package com.q256.skyblockImproved.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.q256.skyblockImproved.constants.Constants;
import com.q256.skyblockImproved.constants.SkillLevels;
import com.q256.skyblockImproved.constants.PetItem;
import com.q256.skyblockImproved.constants.Rarity;
import com.q256.skyblockImproved.utils.*;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.StatusReply;
import net.hypixel.api.util.GameType;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ReportedException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * A class for formatting the Hypixel API's responses
 */
public class ApiParser {

    private HypixelAPI api;
    private JsonObject achievements = null;
    private long achievementsLastQueried = 0;

    /**
     * Creates a new ApiParser, gets constant resources from the Hypixel API
     * @param api Reference to the Hypixel API
     */
    public ApiParser(HypixelAPI api){
        this.api = api;
        refreshAchievements();
    }

    /**
     * Imports a list of achievements
     * Returns before the values are actually imported
     */
    private void refreshAchievements(){
        api.getResource("achievements").whenComplete((response, error) -> {
            if(error!=null){
                TextUtils.sendClientMessage("Couldn't load achievements");
                TextUtils.sendClientMessage("API error: "+error.getMessage());
                error.printStackTrace();
            } else {
                achievements = response.getResponse().getAsJsonObject("achievements");
            }
            achievementsLastQueried = System.currentTimeMillis();
        });
    }

    public void setApi(HypixelAPI api){this.api = api;}

    //general stats methods
    public static ChatComponentText getNames(JsonObject playerJson, int namesBeforeWrapping){
        ChatComponentText cct = new ChatComponentText(playerJson.get("displayname").getAsString());
        StringBuilder hoverString = new StringBuilder("Previous names:" + "\n");

        int wordsThisLine = 0;
        if(!playerJson.has("knownAliases")) return cct;
        for(JsonElement name:playerJson.getAsJsonArray("knownAliases")){
            hoverString.append(name.getAsString()).append(", ");
            wordsThisLine++;
            if(wordsThisLine==namesBeforeWrapping){
                wordsThisLine=0;
                hoverString.append('\n');
            }
        }
        if(hoverString.charAt(hoverString.length()-1) == '\n') hoverString.deleteCharAt(hoverString.length()-1);
        hoverString.deleteCharAt(hoverString.length()-2);

        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverString.toString())));
        return cct;
    }

    public static String getRank(JsonObject playerJson){
        if(playerJson.has("prefix")){
            return playerJson.get("prefix").getAsString();
        }

        //if(playerJson.has("buildTeamAdmin")) return "§3[BUILD TEAM§c+§3]";
        //if(playerJson.has("buildTeam")) return "§3[BUILD TEAM]";

        if(playerJson.has("rank")){
            String rank = playerJson.get("rank").getAsString();
            switch (rank){
                case "YOUTUBER": return "§c[§fYOUTUBE§c]";
                case "ADMIN": return "§c[ADMIN]";
                case "MODERATOR": return "§2[MODERATOR]";
                case "HELPER": return "§9[HELPER]";
            }
        }

        String rankPlusColor = "§c";
        if(playerJson.has("rankPlusColor")){
            rankPlusColor = Constants.RANK_PLUS_COLORS.getOrDefault(playerJson.get("rankPlusColor").getAsString(), "§1");
        }

        if(playerJson.has("monthlyPackageRank")){
            if(playerJson.get("monthlyPackageRank").getAsString().equals("SUPERSTAR")){
                String monthlyRankColor = "GOLD";
                if(playerJson.has("monthlyRankColor")) monthlyRankColor = playerJson.get("monthlyRankColor").getAsString();
                String monthlyRankColorCode = "";
                if(monthlyRankColor.equals("GOLD")) monthlyRankColorCode = "§6";
                else if(monthlyRankColor.equals("AQUA")) monthlyRankColorCode = "§b";

                return monthlyRankColorCode + "[MVP" + rankPlusColor + "++" + monthlyRankColorCode + "]";
            }
        }

        if(playerJson.has("newPackageRank")){
            String packageRank = playerJson.get("newPackageRank").getAsString();
            switch (packageRank){
                case "MVP_PLUS": return "§b[MVP"+rankPlusColor+"+§b]";
                case "MVP": return "§b[MVP]";
                case "VIP_PLUS": return "§a[VIP§6+§a]";
                case "VIP": return "§a[VIP]";
            }
        }

        if(playerJson.has("packageRank")){
            String packageRank = playerJson.get("packageRank").getAsString();
            switch (packageRank){
                case "MVP_PLUS": return "§b[MVP"+rankPlusColor+"+§b]";
                case "MVP": return "§b[MVP]";
                case "VIP_PLUS": return "§a[VIP§6+§a]";
                case "VIP": return "§a[VIP]";
            }
        }

        return "§7";
    }

    public static ChatComponentText getNetworkLvl(JsonObject playerJson){
        long networkExp = playerJson.has("networkExp") ? playerJson.get("networkExp").getAsInt() : 0;
        double networkLvl = (Math.sqrt(2*networkExp + 30625) - 125)/50;

        ChatComponentText cct = new ChatComponentText(Integer.toString((int)networkLvl));

        ChatComponentText hoverText = new ChatComponentText("Level: "+ TextUtils.formatNumberLong(networkLvl) + "\n" +
                "Total Exp: " + TextUtils.formatNumberLong(networkExp));

        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));
        return cct;
    }

    public static ChatComponentText getQuestCount(JsonObject playerJson){
        if(!playerJson.has("quests"))return new ChatComponentText("0");

        int totalQuests = 0;
        for(Map.Entry<String, JsonElement> ee:playerJson.getAsJsonObject("quests").entrySet()){
            if(ee.getValue().getAsJsonObject().has("completions")){
                totalQuests += ee.getValue().getAsJsonObject().getAsJsonArray("completions").size();
            }
        }
        return new ChatComponentText(TextUtils.formatNumberLong(totalQuests));
    }

    public ChatComponentText getAchievementPoints(JsonObject playerJson, boolean includeLegacy, boolean printNotFoundOnes, int maxItemsPerHoverTextLine){
        //makes sure that achievements are imported
        if(achievements==null) {
            if(System.currentTimeMillis()-10000<achievementsLastQueried)return new ChatComponentText("NaN");
            refreshAchievements();
            long time = System.currentTimeMillis();
            GeneralUtils.waitUntil(o -> achievements!=null || System.currentTimeMillis()-3000 > time);
            if(achievements==null){
                if(playerJson.has("achievementPoints")) return new ChatComponentText(playerJson.get("achievementPoints").getAsString());
                return new ChatComponentText("NaN");
            }
        }


        int totalPoints = 0;
        HashMap<String, AchievementGameType> pointsPerGameMap = new HashMap<>();
        ArrayList<AchievementGameType> pointsPerGameList = new ArrayList<>();

        Iterable<JsonElement> achivsOneTime = playerJson.has("achievementsOneTime") ? playerJson.getAsJsonArray("achievementsOneTime") : new ArrayList<>();
        Set<Map.Entry<String, JsonElement>> achivsTiered = playerJson.has("achievements") ?
                playerJson.getAsJsonObject("achievements").entrySet() : new HashSet<>();


        //go through all normal achievements
        for(JsonElement achiv:achivsOneTime){
            if(achiv.isJsonArray())continue;

            int points = getAchievementOneTime(achiv.getAsString(), includeLegacy, printNotFoundOnes);
            totalPoints += points;
            String gameType = achiv.getAsString().split("_", 2)[0];

            if(!pointsPerGameMap.containsKey(gameType)) pointsPerGameMap.put(gameType, new AchievementGameType(gameType));
            pointsPerGameMap.get(gameType).addPoints(points);
        }

        //go through all tiered achievements
        for(Map.Entry<String, JsonElement> ee:achivsTiered){
            int points = getAchievementTiered(ee.getKey(), ee.getValue().getAsInt(), includeLegacy, printNotFoundOnes);
            totalPoints += points;
            String gameType = ee.getKey().split("_", 2)[0];

            if(!pointsPerGameMap.containsKey(gameType)) pointsPerGameMap.put(gameType, new AchievementGameType(gameType));
            pointsPerGameMap.get(gameType).addPoints(points);
        }

        //fill in missing games
        for(String gameType: AchievementGameTypeUtils.getDbNames()){
            if(!pointsPerGameMap.containsKey(gameType)) pointsPerGameMap.put(gameType, new AchievementGameType(gameType));
        }


        StringBuilder hoverText = new StringBuilder("Achievement points per game: \n");
        int itemsThisLine = 0;

        //move the games from the hashSet to the ArrayList
        for(Map.Entry<String, AchievementGameType> ee:pointsPerGameMap.entrySet()) {
            String gameType = ee.getKey();

            if(Arrays.asList(Constants.REMOVED_ACHIEVEMENT_GAMES).contains(gameType))continue;
            if(!achievements.has(gameType)){
                if(printNotFoundOnes) TextUtils.sendClientMessage("Could not find achievement game type: §c" + gameType);
                continue;
            }

            AchievementGameType game = ee.getValue();
            int maxPointsThisGame = achievements.getAsJsonObject(gameType).get("total_points").getAsInt();
            if(includeLegacy) maxPointsThisGame += achievements.getAsJsonObject(gameType).get("total_legacy_points").getAsInt();
            game.setMaxPoints(maxPointsThisGame);

            if(maxPointsThisGame!=0){
                game.calcAchievedPointsFraction();
                pointsPerGameList.add(game);
            }
        }

        sortAchivGameTypes(pointsPerGameList);

        for(AchievementGameType game:pointsPerGameList){
            itemsThisLine++;
            if(itemsThisLine>maxItemsPerHoverTextLine){
                itemsThisLine=1;
                hoverText.append("\n");
            }

            int pointsThisGame = game.achievedPoints;
            int maximumPointsThisGame = game.maxPoints;

            String color = TextUtils.colorNumber(game.achievedPointsFraction, new double[]{0.000001,0.2,0.4,0.6,0.8,1});

            hoverText.append(AchievementGameTypeUtils.dbToDisplay(game.dbName)).append(": ").append(color).append(TextUtils.formatNumberLong(pointsThisGame))
                    .append("§f§r/§b").append(TextUtils.formatNumberLong(maximumPointsThisGame)).append("§f, ");
        }
        hoverText.deleteCharAt(hoverText.length()-2);

        ChatComponentText cct = new ChatComponentText(TextUtils.formatNumberLong(totalPoints));
        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));

        return cct;
    }

    private int getAchievementOneTime(String name, boolean includeLegacy, boolean printIfNotFound){
        String[] splitName = name.split("_", 2);
        String gameType = splitName[0].toLowerCase();
        String achivName = splitName[1].toUpperCase();

        if(Arrays.asList(Constants.REMOVED_ACHIEVEMENT_GAMES).contains(gameType)) return 0;
        if(Arrays.asList(Constants.REMOVED_ACHIEVEMENTS).contains(name.toLowerCase())) return 0;

        JsonObject achievement = JsonUtils.getJsonObject(achievements, gameType+".one_time."+achivName);
        if(achievement==null){
            if(printIfNotFound) TextUtils.sendClientMessage("Achievement not found: " + gameType + "§c§l_" + achivName);
            return 0;
        }

        if(!includeLegacy && achievement.has("legacy") && achievement.get("legacy").getAsBoolean()){
            return 0;
        }

        return achievement.get("points").getAsInt();
    }

    private int getAchievementTiered(String name, int score, boolean includeLegacy, boolean printIfNotFound){
        String[] splitName = name.split("_", 2);
        String gameType = splitName[0].toLowerCase();
        String achivName = splitName[1].toUpperCase();

        if(Arrays.asList(Constants.REMOVED_ACHIEVEMENT_GAMES).contains(gameType)) return 0;
        if(Arrays.asList(Constants.REMOVED_ACHIEVEMENTS).contains(name.toLowerCase())) return 0;

        JsonObject achievement = JsonUtils.getJsonObject(achievements, gameType+".tiered."+achivName);
        if(achievement==null){
            if(printIfNotFound) TextUtils.sendClientMessage("Achievement not found: " + gameType + "§c§l_" + achivName);
            return 0;
        }

        if(!includeLegacy && achievement.has("legacy") && achievement.get("legacy").getAsBoolean()){
            return 0;
        }

        JsonArray tiers = achievement.getAsJsonArray("tiers");
        int totalPoints = 0;

        for(JsonElement tier:tiers){
            if(tier.getAsJsonObject().get("amount").getAsInt() <= score) totalPoints += tier.getAsJsonObject().get("points").getAsInt();
        }

        return totalPoints;
    }

    private static void sortAchivGameTypes(ArrayList<AchievementGameType> inAl){
        for(int i=0; i<inAl.size(); i++){
            AchievementGameType tempObj = inAl.get(i);
            double number = tempObj.achievedPointsFraction;
            for(int jj=i; jj>0; jj--){
                AchievementGameType tempObj2 = inAl.get(jj-1);
                if(tempObj2.achievedPointsFraction < number || (tempObj2.achievedPointsFraction==number && tempObj2.achievedPoints < tempObj.achievedPoints)){
                    inAl.set(jj, tempObj2);
                    if(jj==1)inAl.set(0, tempObj);
                }
                else{
                    inAl.set(jj, tempObj);
                    break;
                }
            }
        }
    }

    static class AchievementGameType{
        String dbName;
        int achievedPoints = 0;
        int maxPoints;
        double achievedPointsFraction = 0;

        public AchievementGameType(String dbName){
            this.dbName = dbName;
        }

        void addPoints(int amount){achievedPoints+=amount;}

        void setMaxPoints(int amount){maxPoints=amount;}

        void calcAchievedPointsFraction(){achievedPointsFraction=(double)achievedPoints/maxPoints;}
    }

    public static ChatComponentText getLastOnline(JsonObject playerJson){
        long lastLogin = 0;
        long lastLogout = 0;

        if(playerJson.has("lastLogin")) lastLogin = playerJson.get("lastLogin").getAsLong();
        if(playerJson.has("lastLogout")) lastLogout = playerJson.get("lastLogout").getAsLong();

        String absoluteTime = null;
        String relativeTime = null;

        if(lastLogin!=0 && lastLogout!=0){
            if(lastLogin>lastLogout) return new ChatComponentText("Online now");
            absoluteTime = new Date(lastLogout).toString();
            relativeTime = TextUtils.millisToString(System.currentTimeMillis()-lastLogout) + " ago";
        }
        if(lastLogin!=0){
            absoluteTime = new Date(lastLogin).toString();
            relativeTime = TextUtils.millisToString(System.currentTimeMillis()-lastLogin) + " ago";
        }
        if(lastLogout!=0){
            absoluteTime = new Date(lastLogout).toString();
            relativeTime = TextUtils.millisToString(System.currentTimeMillis()-lastLogout) + " ago";
        }
        if(lastLogin==0 && lastLogout==0){
            return new ChatComponentText("NaN");
        }

        ChatComponentText cct = new ChatComponentText(relativeTime);
        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(absoluteTime)));
        return cct;
    }

    //SkyBlock methods
    public static ChatComponentText getSkillAvg(JsonObject sbPlayerJson, JsonObject playerJson, boolean alignHoverText){
        int[] xpPerSkill = new int[Constants.SKILL_NAMES.length];
        boolean foundAtLeastOneSkill = false;

        for(int i = 0; i < Constants.SKILL_NAMES.length; i++){
            if(sbPlayerJson.has("experience_skill_"+ Constants.SKILL_NAMES[i])) {
                xpPerSkill[i] = sbPlayerJson.get("experience_skill_"+ Constants.SKILL_NAMES[i]).getAsInt();
                foundAtLeastOneSkill = true;
            } else {
                xpPerSkill[i] = 0;
            }
        }
        if(!foundAtLeastOneSkill) return getSkillAvgFromAchivs(playerJson, alignHoverText);
        return getSkillAvgChatComponent(xpPerSkill, alignHoverText);
    }

    private static ChatComponentText getSkillAvgFromAchivs(JsonObject playerJson, boolean alignHoverText){
        int[] skillLvls = new int[Constants.SKILL_NAMES.length];
        int[] xpPerSkill = new int[Constants.SKILL_NAMES.length];

        for(int i = 0; i < Constants.SKILL_NAMES.length; i++){
            if(!playerJson.getAsJsonObject("achievements").has("skyblock_"+ Constants.SKILL_ACHIEVEMENT_NAMES[i])) {
                xpPerSkill[i] = 0;
            } else {
                skillLvls[i] = playerJson.getAsJsonObject("achievements").get("skyblock_"+ Constants.SKILL_ACHIEVEMENT_NAMES[i]).getAsInt();
                if(skillLvls[i]!=0) xpPerSkill[i] = SkillLevels.STANDARD_SKILL_LEVELS[skillLvls[i]-1];
            }
        }

        ChatComponentText cct = getSkillAvgChatComponent(xpPerSkill, alignHoverText);
        cct.getChatStyle().getChatHoverEvent().getValue().appendText("\nSkill info pulled from achievements");
        return cct;
    }

    /**
     * Only call if {@code xpPerSkill.length == Constants.SKILL_NAMES.length}
     */
    private static ChatComponentText getSkillAvgChatComponent(int[] xpPerSkill, boolean alignHoverText){
        double[] skillLvls = new double[Constants.SKILL_NAMES.length];
        int totalXp = 0;
        for (int i = 0; i < xpPerSkill.length; i++) {
            skillLvls[i] = skillXpToLvl(xpPerSkill[i], SkillLevels.STANDARD_SKILL_LEVELS);
            totalXp += xpPerSkill[i];
        }

        double skillAvg = 0;
        for(double dd:skillLvls){
            skillAvg+=dd;
        }
        skillAvg /= Constants.SKILL_NAMES.length;

        ChatComponentText cct = new ChatComponentText(TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(skillAvg)));

        StringBuilder hoverText = new StringBuilder("§aTotal §9Exp: §6"+ TextUtils.formatNumberLong(totalXp)+"\n");
        if(alignHoverText) {
            for (int i = 0; i < Constants.SKILL_NAMES.length; i++) {
                hoverText.append("§a").append(StringUtils.capitalize(Constants.SKILL_NAMES[i])).append(TextUtils.getWhiteSpaces(Constants.EXTRA_SKILL_NAME_SPACES[i]))
                        .append(" §bLevel: §6").append(TextUtils.formatNumberLong(skillLvls[i])).append("\n");
            }
            for (int i = 0; i < Constants.SKILL_NAMES.length; i++) {
                hoverText.append("§a").append(StringUtils.capitalize(Constants.SKILL_NAMES[i])).append(TextUtils.getWhiteSpaces(Constants.EXTRA_SKILL_NAME_SPACES[i]))
                        .append(" §9Exp: §6").append(TextUtils.formatNumberLong(xpPerSkill[i])).append("\n");
            }
        } else {
            for (int i = 0; i < Constants.SKILL_NAMES.length; i++) {
                hoverText.append("§a").append(StringUtils.capitalize(Constants.SKILL_NAMES[i])).append(" §bLevel: §6")
                        .append(TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(skillLvls[i]))).append("\n");
            }
            for (int i = 0; i < Constants.SKILL_NAMES.length; i++) {
                hoverText.append("§a").append(StringUtils.capitalize(Constants.SKILL_NAMES[i])).append(" §9Exp: §6")
                        .append(TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(xpPerSkill[i]))).append("\n");
            }
        }

        hoverText.deleteCharAt(hoverText.length()-1);

        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));
        return cct;
    }

    public static double skillXpToLvl(int xpAmount, int[] xpRequirements){
        for(int i=0; i<xpRequirements.length; i++){
            if(xpAmount < xpRequirements[i]){
                if(i==0) return (double)xpAmount/xpRequirements[0];
                return i + ((double)xpAmount- xpRequirements[i-1])/(xpRequirements[i]- xpRequirements[i-1]);
            }
        }
        return xpRequirements.length;
    }

    public static ChatComponentText getSlayerXp(JsonObject sbPlayerJson){
        if(!sbPlayerJson.has("slayer_bosses"))return new ChatComponentText("0");

        int totalXp = 0;
        StringBuilder hoverText = new StringBuilder("Slayer xp:");

        Set<Map.Entry<String, JsonElement>> allSlayers = sbPlayerJson.getAsJsonObject("slayer_bosses").entrySet();
        for(Map.Entry<String, JsonElement> entry:allSlayers){
            String name = StringUtils.capitalize(entry.getKey());
            int xp = 0;
            if(entry.getValue().getAsJsonObject().has("xp")) xp = entry.getValue().getAsJsonObject().get("xp").getAsInt();
            totalXp += xp;
            hoverText.append("\n").append(name).append(": ").append(TextUtils.formatNumberLong(xp));
        }

        ChatComponentText cct = new ChatComponentText(TextUtils.formatNumberLong(totalXp));
        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));

        return cct;
    }

    public static ChatComponentText getMinionCount(JsonObject sbProfileJson){
        ArrayList<String> minionNames = new ArrayList<>();

        //calculate the number of unique minions across the co-op
        Set<Map.Entry<String, JsonElement>> players = sbProfileJson.getAsJsonObject("members").entrySet();
        for(Map.Entry<String, JsonElement> entry:players){
            JsonObject jsonObject = entry.getValue().getAsJsonObject();
            if(jsonObject.has("crafted_generators")){
                for(JsonElement minion:jsonObject.getAsJsonArray("crafted_generators")){
                    String minionName = minion.getAsString();
                    if(!minionNames.contains(minionName)) minionNames.add(minionName);
                }
            }
        }
        int uniqueMinions = minionNames.size();

        //calculate the number of minion slots
        int minionSlots = 5;

        for(int i: Constants.MINION_SLOT_REQS){
            if(uniqueMinions>=i) minionSlots++;
            else break;
        }

        //get how many minion slots the profile has from community upgrades
        int bonusSlots = 0;
        JsonArray finishedUpgrades = JsonUtils.getJsonArray(sbProfileJson, "community_upgrades.upgrade_states");
        if(finishedUpgrades!=null) {
            for (JsonElement jsonElement : finishedUpgrades) {
                if(jsonElement instanceof JsonObject){
                    JsonObject finishedUpgrade = jsonElement.getAsJsonObject();
                    if(finishedUpgrade.has("upgrade") && finishedUpgrade.get("upgrade").getAsString().equals("minion_slots") && finishedUpgrade.has("tier")){
                        int tier = finishedUpgrade.get("tier").getAsInt();
                        if(tier>bonusSlots) bonusSlots = tier;
                    }
                }
            }
        }

        //format it nicely
        double fractionOfMinions = (double)uniqueMinions/Constants.TOTAL_MINIONS;

        String color = TextUtils.colorNumber(fractionOfMinions, new double[]{0.25,0.5,0.65,0.8,0.9,1});

        StringBuilder hoverText = new StringBuilder("Unique minions: ").append(color).append(uniqueMinions).append("§f§r/§b").append(Constants.TOTAL_MINIONS);
        if(bonusSlots!=0){
            hoverText.append("\n§6").append(minionSlots).append("§f \"normal\" minion slots");
            hoverText.append("\n§6").append(bonusSlots).append("§f minion slots from community upgrades");
        }

        ChatComponentText cct = new ChatComponentText(minionSlots + (bonusSlots==0 ? "" : " §7("+(minionSlots+bonusSlots)+")"));
        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));
        return cct;
    }

    public static ChatComponentText getFairySoulCount(JsonObject sbPlayerJson){
        int fairies = 0;
        if(sbPlayerJson.has("fairy_souls_collected")) fairies = sbPlayerJson.get("fairy_souls_collected").getAsInt();
        return new ChatComponentText(String.valueOf(fairies));
    }

    public static ArrayList<Pet> getPets(JsonObject sbPlayerJson){
        ArrayList<Pet> outAl = new ArrayList<>();
        if(!sbPlayerJson.has("pets") || sbPlayerJson.getAsJsonArray("pets").size()==0) return outAl;

        for(JsonElement jsonElement:sbPlayerJson.getAsJsonArray("pets")) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            Pet pet = new Pet();

            pet.name = StringUtils.capitalize(jsonObject.get("type").getAsString().replace('_',' ').toLowerCase());

            String tier = jsonObject.get("tier").getAsString();
            for(Rarity rarity:Rarity.getPetRarities()){
                if(tier.equals(rarity.toString())){
                    pet.rarity = rarity;
                    break;
                }
            }
            if(pet.rarity==null){
                TextUtils.sendClientMessage("Unexpected pet rarity: "+tier+". Expected: Common-Legendary");
                continue;
            }

            pet.active = jsonObject.get("active").getAsBoolean();

            pet.candyUsed = jsonObject.get("candyUsed").getAsInt();

            pet.xp = jsonObject.get("exp").getAsInt();

            String heldItem = jsonObject.get("heldItem").isJsonNull() ? "null" : jsonObject.get("heldItem").getAsString();
            for(PetItem petItem:PetItem.values()){
                if(heldItem.equals(petItem.toString())) pet.heldItem = petItem;
            }
            pet.heldItemId = heldItem;

            int lvl = 1;
            int offset = Constants.PET_RARITY_OFFSETS[pet.rarity.ordinal()];
            int totalXpRequired = 0;
            for(int i = offset; i<offset+99; i++){
                if(pet.xp < totalXpRequired + Constants.PET_LEVELS[i]) break;
                lvl ++;
                totalXpRequired += Constants.PET_LEVELS[i];
            }
            pet.lvl = lvl;

            pet.extraXp = pet.xp - totalXpRequired;

            if(lvl==100) pet.xpUntilNextLvl=0;
            else pet.xpUntilNextLvl = Constants.PET_LEVELS[lvl+offset-1];

            outAl.add(pet);
        }
        outAl.sort(Collections.reverseOrder());
        return outAl;
    }

    public static ChatComponentText getActivePet(ArrayList<Pet> allPets){
        for(Pet pet:allPets){
            if(pet.active){
                ChatComponentText cct = new ChatComponentText(pet.getNameLong().toString());
                cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(pet.getHoverText())));
                return cct;
            }
        }
        return new ChatComponentText("None");
    }

    public static ChatComponentText getPets(ArrayList<Pet> allPets){
        ChatComponentText cct = new ChatComponentText("");
        for(int i=0; i<allPets.size(); i++){
            Pet pet = allPets.get(i);

            StringBuilder baseText = new StringBuilder(pet.getNameShort());
            if(i+1<allPets.size()) baseText.append("§f, ");
            ChatComponentText petTextComponent = new ChatComponentText(baseText.toString());

            petTextComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(pet.getHoverText())));
            cct.appendSibling(petTextComponent);
        }
        return cct;
    }

    public static ChatComponentText getPetScore(ArrayList<Pet> allPets, boolean showListOfPets, int petsPerHoverTextLine){
        ArrayList<String> usedPetNames = new ArrayList<>();
        Rarity[] petRarities = Rarity.getPetRarities();
        int[] petsPerRarity = new int[petRarities.length];
        for(Pet pet:allPets){
            if(usedPetNames.contains(pet.name)) continue;
            usedPetNames.add(pet.name);
            petsPerRarity[pet.rarity.ordinal()]++;
        }

        int petScore = 0;
        for(int i=0; i<petsPerRarity.length; i++){
            petScore += (i+1)*petsPerRarity[i];
        }


        StringBuilder hoverText = new StringBuilder();
        if(!showListOfPets) {
            hoverText.append("Pets of each rarity:");
            for (Rarity rarity : petRarities) {
                hoverText.append("\n").append(rarity.getColorCode()).append(rarity.getPrettyName()).append(": §f").append(petsPerRarity[rarity.ordinal()]);
            }
        } else {
            hoverText.append("All pets:");
            for(int i=0; i<allPets.size(); i++){
                if(i%petsPerHoverTextLine==0) hoverText.append("\n");
                hoverText.append(allPets.get(i).getNameShort()).append("§f, ");
            }
            hoverText.deleteCharAt(hoverText.length()-2);
        }

        ChatComponentText cct = new ChatComponentText(String.valueOf(petScore));
        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));
        return cct;
    }

    public static ArrayList<NBTTagCompound> getSbInvNBT(JsonObject sbPlayerJson, String type) throws IOException {
        JsonElement rawInvData = JsonUtils.get(sbPlayerJson, type+".data");
        if(rawInvData==null) return null;

        //decode from base64
        byte[] decodedBytes = Base64.getDecoder().decode(rawInvData.getAsString());

        return ItemUtils.parseNbt(decodedBytes);
    }

    public static ArrayList<ChatComponentText> getSbArmorChatMessages(ArrayList<NBTTagCompound> armorNbt){
        ArrayList<ChatComponentText> outAl = new ArrayList<>();

        for(NBTTagCompound armorPiece:armorNbt){
            if(armorPiece.hasNoTags()){
                outAl.add(new ChatComponentText("None"));
                continue;
            }

            ChatComponentText cct = new ChatComponentText(armorPiece.getCompoundTag("tag").getCompoundTag("display").getString("Name"));
            cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ChatComponentText(armorPiece.toString())));
            outAl.add(cct);
        }
        return outAl;
    }

    public static ChatComponentText getShortInventoryMessage(JsonObject sbPlayerJson, String playerName){
        ChatComponentText result = new ChatComponentText("§6§nAll Inventories");
        result.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi listInventories " + playerName));
        result.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to list all of " + playerName + "'s inventories")));
        return result;
    }

    public static ArrayList<ChatComponentText> getInventoryMessages(JsonObject sbPlayerJson, String playerName){
        ArrayList<ChatComponentText> result = new ArrayList<>();

        if(sbPlayerJson.has("inv_contents")) {
            ChatComponentText inventoryMsg = new ChatComponentText("§e§nInventory");
            inventoryMsg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName));
            inventoryMsg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s inventory")));
            result.add(inventoryMsg);
        }

        if(sbPlayerJson.has("talisman_bag")) {
            ChatComponentText talismanBagMsg = new ChatComponentText("§a§nAccessory Bag");
            talismanBagMsg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " talismanBag"));
            talismanBagMsg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s accessory bag")));
            result.add(talismanBagMsg);
        }

        if(sbPlayerJson.has("potion_bag")) {
            ChatComponentText potionBagMsg = new ChatComponentText("§d§nPotion Bag");
            potionBagMsg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " potionBag"));
            potionBagMsg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s potion bag")));
            result.add(potionBagMsg);
        }

        if(sbPlayerJson.has("fishing_bag")) {
            ChatComponentText fishBagMsg = new ChatComponentText("§b§nFishing Bag");
            fishBagMsg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " fishBag"));
            fishBagMsg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s fishing bag")));
            result.add(fishBagMsg);
        }

        if(sbPlayerJson.has("quiver")) {
            ChatComponentText quiverMsg = new ChatComponentText("§8§nQuiver");
            quiverMsg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " quiver"));
            quiverMsg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s quiver")));
            result.add(quiverMsg);
        }

        if(sbPlayerJson.has("personal_vault_contents")) {
            ChatComponentText quiverMsg = new ChatComponentText("§7§nVault");
            quiverMsg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " vault"));
            quiverMsg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s vault")));
            result.add(quiverMsg);
        }


        if(sbPlayerJson.has("ender_chest_contents")) {
            ChatComponentText enderChestMsg = new ChatComponentText("§5Ender chest: ");

            try {
                ArrayList<NBTTagCompound> slots = getSbInvNBT(sbPlayerJson, "ender_chest_contents");
                if(slots==null) enderChestMsg.appendText("§fNaN");
                else {
                    int numberOfPages = slots.size()/45;
                    for(int i=1; i<=numberOfPages; i++){
                        ChatComponentText pageCct;
                        if(i==numberOfPages) pageCct = new ChatComponentText("§5§nPage "+i);
                        else pageCct = new ChatComponentText("§5§nPage "+i+",§r ");

                        pageCct.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " enderChest "+i));
                        pageCct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s ender chest page "+i)));

                        enderChestMsg.appendSibling(pageCct);
                    }
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                enderChestMsg.appendText("§cError");
            }

            result.add(enderChestMsg);
        }


        if(sbPlayerJson.has("wardrobe_contents")) {
            ChatComponentText wardrobeMsg = new ChatComponentText("§9Wardrobe: ");

            ChatComponentText wardrobePage1Msg = new ChatComponentText("§9§nPage 1,§r ");
            wardrobePage1Msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " wardrobe 1"));
            wardrobePage1Msg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s wardrobe page 1")));

            ChatComponentText wardrobePage2Msg = new ChatComponentText("§9§nPage 2");
            wardrobePage2Msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sbi viewInventory " + playerName + " wardrobe 2"));
            wardrobePage2Msg.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to view " + playerName + "'s wardrobe page 2")));

            wardrobeMsg.appendSibling(wardrobePage1Msg);
            wardrobeMsg.appendSibling(wardrobePage2Msg);

            result.add(wardrobeMsg);
        }

        return result;
    }

    public static ChatComponentText getTalismanCount(JsonObject sbPlayerJson){
        try {
            ArrayList<NBTTagCompound> inventoryContents = getSbInvNBT(sbPlayerJson, "inv_contents");
            ArrayList<NBTTagCompound> talismanBag = getSbInvNBT(sbPlayerJson, "talisman_bag");
            ArrayList<NBTTagCompound> enderChest = getSbInvNBT(sbPlayerJson, "ender_chest_contents");
            if(inventoryContents==null && talismanBag==null && enderChest==null) return new ChatComponentText("NaN");
            if(inventoryContents==null) inventoryContents = new ArrayList<>();
            if(talismanBag==null) talismanBag = new ArrayList<>();
            if(enderChest==null) enderChest = new ArrayList<>();

            ArrayList<NBTTagCompound> backpackContainer = new ArrayList<>(inventoryContents);
            backpackContainer.addAll(enderChest);

            ArrayList<NBTTagCompound> activeSlots = new ArrayList<>(talismanBag);
            activeSlots.addAll(inventoryContents);

            ArrayList<NBTTagCompound> inactiveSlots = new ArrayList<>(inventoryContents);
            inactiveSlots.addAll(enderChest);


            for(NBTTagCompound inventorySlot:backpackContainer){
                try{
                    String id = inventorySlot.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getString("id");
                    if(id.endsWith("_BACKPACK")){
                        String backpackType = id.substring(0, id.length()-9).toLowerCase();
                        byte[] backpackData = inventorySlot.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getByteArray(backpackType+"_backpack_data");
                        ArrayList<NBTTagCompound> compoundTags = ItemUtils.parseNbt(backpackData);
                        inactiveSlots.addAll(compoundTags);
                    }
                } catch (NullPointerException | ReportedException ignored){}
            }

            HashMap<String, Rarity> activeAccessories = new HashMap<>();
            HashMap<String, Rarity> inactiveAccessories = new HashMap<>();

            //add active accessories
            for(NBTTagCompound item:activeSlots){
                if(isAccessory(item)){
                    String id = item.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getString("id");
                    NBTTagList lore = item.getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8);
                    String lastLoreLine = lore.getStringTagAt(lore.tagCount()-1);
                    Rarity rarity = Rarity.getRarityByColor(lastLoreLine.substring(0,2));
                    if(rarity==null) continue;

                    if(activeAccessories.containsKey(id)){
                        if(activeAccessories.get(id).ordinal()<rarity.ordinal()){
                            activeAccessories.remove(id);
                        } else {
                            continue;
                        }
                    }

                    boolean talismanInserted = false;
                    for(String oldTalismanId:activeAccessories.keySet()){
                        for(String[] talismanUpgrade:Constants.TALISMAN_UPGRADES){
                            boolean oldTalismanFound = false;
                            boolean newTalismanFound = false;
                            for(String talismanName:talismanUpgrade){
                                if(oldTalismanId.equals(talismanName)){
                                    if(newTalismanFound){
                                        talismanInserted = true;
                                        break;
                                    }
                                    else oldTalismanFound = true;
                                }
                                if(id.equals(talismanName)){
                                    if(oldTalismanFound){
                                        activeAccessories.remove(oldTalismanId);
                                        activeAccessories.put(id, rarity);
                                        talismanInserted = true;
                                        break;
                                    } else newTalismanFound = true;
                                }
                            }
                            if(talismanInserted) break;
                        }
                        if(talismanInserted) break;
                    }
                    if(!talismanInserted) activeAccessories.put(id, rarity);
                }
            }

            //add inactive accessories
            for(NBTTagCompound item:inactiveSlots){
                if(isAccessory(item)){
                    String id = item.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getString("id");
                    NBTTagList lore = item.getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8);
                    String lastLoreLine = lore.getStringTagAt(lore.tagCount()-1);
                    Rarity rarity = Rarity.getRarityByColor(lastLoreLine.substring(0,2));
                    if(rarity==null) continue;

                    if(activeAccessories.containsKey(id)) continue;
                    if(inactiveAccessories.containsKey(id)){
                        if(inactiveAccessories.get(id).ordinal()<rarity.ordinal()){
                            inactiveAccessories.remove(id);
                        } else {
                            continue;
                        }
                    }


                    boolean skip = false;
                    for(String oldTalismanId:activeAccessories.keySet()){
                        for(String[] talismanUpgrade:Constants.TALISMAN_UPGRADES){
                            boolean oldTalismanFound = false;
                            boolean newTalismanFound = false;
                            for(String talismanName:talismanUpgrade){
                                if(oldTalismanId.equals(talismanName)){
                                    oldTalismanFound = true;
                                }
                                if(id.equals(talismanName)){
                                    newTalismanFound = true;
                                }
                                if(oldTalismanFound && newTalismanFound){
                                    skip = true;
                                    break;
                                }
                            }
                            if(skip) break;
                        }
                        if(skip) break;
                    }
                    if(skip) continue;


                    boolean talismanInserted = false;
                    for(String oldTalismanId:inactiveAccessories.keySet()){
                        for(String[] talismanUpgrade:Constants.TALISMAN_UPGRADES){
                            boolean oldTalismanFound = false;
                            boolean newTalismanFound = false;
                            for(String talismanName:talismanUpgrade){
                                if(oldTalismanId.equals(talismanName)){
                                    if(newTalismanFound){
                                        talismanInserted = true;
                                        break;
                                    }
                                    else oldTalismanFound = true;
                                }
                                if(id.equals(talismanName)){
                                    if(oldTalismanFound){
                                        inactiveAccessories.remove(oldTalismanId);
                                        inactiveAccessories.put(id, rarity);
                                        talismanInserted = true;
                                        break;
                                    } else newTalismanFound = true;
                                }
                            }
                            if(talismanInserted) break;
                        }
                        if(talismanInserted) break;
                    }
                    if(!talismanInserted) inactiveAccessories.put(id, rarity);
                }
            }


            HashMap<Rarity, Integer> activeTalismanCounts = new HashMap<>();
            for(Rarity rarity:activeAccessories.values()){
                activeTalismanCounts.merge(rarity, 1, Integer::sum);
            }

            HashMap<Rarity, Integer> inactiveTalismanCounts = new HashMap<>();
            for(Rarity rarity:inactiveAccessories.values()){
                inactiveTalismanCounts.merge(rarity, 1, Integer::sum);
            }


            ChatComponentText cct = new ChatComponentText(String.valueOf(activeAccessories.size()+inactiveAccessories.size()));
            StringBuilder hoverText = new StringBuilder();
            Rarity[] rarities = Rarity.values();

            if(activeTalismanCounts.size()!=0) {
                hoverText.append("§7Active unique talismans: ");
                for (int i = rarities.length - 1; i >= 0; i--) {
                    Rarity rarity = rarities[i];
                    Integer amount = activeTalismanCounts.get(rarity);
                    if (amount == null) continue;
                    hoverText.append('\n').append(rarity.getColorCode()).append(rarity.getPrettyName()).append("§f: ").append(amount);
                }
            }
            if(inactiveTalismanCounts.size()!=0) {
                if(activeTalismanCounts.size()!=0) hoverText.append("\n \n");
                hoverText.append("§7Inactive unique talismans: ");
                for (int i = rarities.length - 1; i >= 0; i--) {
                    Rarity rarity = rarities[i];
                    Integer amount = inactiveTalismanCounts.get(rarity);
                    if (amount == null) continue;
                    hoverText.append('\n').append(rarity.getColorCode()).append(rarity.getPrettyName()).append("§f: ").append(amount);
                }
            }
            cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));

            return cct;
        } catch (IOException exception){
            exception.printStackTrace();
            return new ChatComponentText("NaN");
        }
    }

    private static boolean isAccessory(NBTTagCompound item){
        try {
            NBTTagList lore = item.getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8);
            String lastLoreLine = lore.getStringTagAt(lore.tagCount()-1);
            return !item.getCompoundTag("tag").getCompoundTag("ExtraAttributes").getString("id").equals("") && (lastLoreLine.contains(" ACCESSORY") || lastLoreLine.contains(" HATCCESSORY"));
        } catch (ReportedException exception) {
            return false;
        }
    }

    public static ChatComponentText getDungeonXp(JsonObject sbPlayerProfile){
        JsonElement catacombsXp = JsonUtils.get(sbPlayerProfile, "dungeons.dungeon_types.catacombs.experience");
        if(catacombsXp==null || catacombsXp.isJsonNull()) return new ChatComponentText("0");
        ChatComponentText cct = new ChatComponentText(TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(skillXpToLvl(catacombsXp.getAsInt(), SkillLevels.DUNGEON_LEVELS))));

        //JsonElement selectedClassJson = JsonUtils.get(sbPlayerProfile, "dungeons.selected_dungeon_class");
        //String selectedClass = selectedClassJson==null || selectedClassJson.isJsonNull() ? null : selectedClassJson.getAsString();

        StringBuilder hoverText = new StringBuilder();
        JsonElement classes = JsonUtils.get(sbPlayerProfile, "dungeons.player_classes");
        if(classes==null || classes.isJsonNull()) return cct;

        hoverText.append("§cCatacombs").append("§b Level: §6");
        hoverText.append(TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(skillXpToLvl(catacombsXp.getAsInt(), SkillLevels.DUNGEON_LEVELS))));

        for(Map.Entry<String, JsonElement> classEntry:classes.getAsJsonObject().entrySet()){
            hoverText.append('\n');
            //if(classEntry.getKey().equals(selectedClass)) hoverText.append("§b");
            hoverText.append("§c").append(StringUtils.capitalize(classEntry.getKey())).append("§b Level: §6");
            JsonElement experience = classEntry.getValue().getAsJsonObject().get("experience");
            hoverText.append(experience==null ? 0 : TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(skillXpToLvl(experience.getAsInt(), SkillLevels.DUNGEON_LEVELS))));
        }
        hoverText.append("\n§cCatacombs").append("§9 Exp: §6");
        hoverText.append(TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(catacombsXp.getAsInt())));

        for(Map.Entry<String, JsonElement> classEntry:classes.getAsJsonObject().entrySet()){
            hoverText.append('\n');
            //if(classEntry.getKey().equals(selectedClass)) hoverText.append("§b");
            hoverText.append("§c").append(StringUtils.capitalize(classEntry.getKey())).append("§9 Exp: §6");
            JsonElement experience = classEntry.getValue().getAsJsonObject().get("experience");
            hoverText.append(experience==null ? 0 : TextUtils.formatNumberLong(experience.getAsInt()));
        }

        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));
        return cct;
    }

    public static ChatComponentText getDungeonClass(JsonObject sbPlayerProfile){
        JsonElement selectedClass = JsonUtils.get(sbPlayerProfile, "dungeons.selected_dungeon_class");
        if(selectedClass==null || selectedClass.isJsonNull()) return new ChatComponentText("None");
        String className = StringUtils.capitalize(selectedClass.getAsString());

        int classXp = 0;
        JsonElement classes = JsonUtils.get(sbPlayerProfile, "dungeons.player_classes");
        if(classes!=null && !classes.isJsonNull()){
            JsonElement classXpJson = JsonUtils.get(classes.getAsJsonObject(), className.toLowerCase()+".experience");
            classXp = classXpJson==null ? 0:classXpJson.getAsInt();
        }

        return new ChatComponentText(className + " §6" +TextUtils.grayOutAfterDecimal(TextUtils.formatNumberLong(skillXpToLvl(classXp, SkillLevels.DUNGEON_LEVELS))));
    }

    public static ChatComponentText getDungeonCompletions(JsonObject sbPlayerJson, JsonObject playerJson){
        HashMap<Integer, Integer> floorCompletions = new HashMap<>();
        JsonElement completionsElement = JsonUtils.get(sbPlayerJson, "dungeons.dungeon_types.catacombs.tier_completions");
        if(completionsElement==null || completionsElement.isJsonNull()) return new ChatComponentText("NaN");

        for(Map.Entry<String, JsonElement> entry:completionsElement.getAsJsonObject().entrySet()){
            try{
                floorCompletions.put(Integer.parseInt(entry.getKey()), entry.getValue().getAsInt());
            } catch (NumberFormatException exception){
                exception.printStackTrace();
            }
        }

        int totalCompletions = 0;
        for(int i: floorCompletions.values()) totalCompletions+=i;
        ChatComponentText cct = new ChatComponentText(String.valueOf(totalCompletions));

        StringBuilder hoverText = new StringBuilder("Floor completions: ");
        for(Map.Entry<Integer, Integer> entry:floorCompletions.entrySet()){
            hoverText.append("\nFloor ").append(entry.getKey()).append(": §6").append(entry.getValue());
        }

        JsonElement secretsFound = JsonUtils.get(playerJson, "achievements.skyblock_treasure_hunter");
        if(secretsFound!=null && !secretsFound.isJsonNull()){
            hoverText.append("\n\nTotal secrets found: §b").append(secretsFound.getAsInt());
        }

        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(hoverText.toString())));
        return cct;
    }

    //session method
    public static ArrayList<ChatComponentText> getSessionMessages(StatusReply.Session session){
        ArrayList<ChatComponentText> outAl = new ArrayList<>();
        if(!session.isOnline()){
            outAl.add(new ChatComponentText("§cOffline"));
            return outAl;
        }

        GameType gameType = session.getGameType();
        String mode = session.getMode();
        String map = session.getMap();

        if(gameType!=null)outAl.add(new ChatComponentText("§eGame type: §f" + gameType.getName()));
        if(mode!=null)outAl.add(new ChatComponentText("§eGame mode: §f" + mode));
        if(map!=null)outAl.add(new ChatComponentText("§eMap: §f" + map));

        return outAl;
    }
}
