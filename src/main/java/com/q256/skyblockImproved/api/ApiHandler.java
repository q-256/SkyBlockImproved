package com.q256.skyblockImproved.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.StatusReply;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

//I know this class is quite messy, gonna clean it up once Hypixel allows us to access the skyblock/profiles endpoint through their API wrapper

public class ApiHandler {
    SkyblockImproved main;
    UUID apiKey;
    HypixelAPI api;
    ApiCache apiCache;
    UuidCache uuidCache;
    ApiParser apiParser;

    public ApiHandler(){
        main = SkyblockImproved.getInstance();
        apiKey = main.getConfigValues().apiKey;
        api = new HypixelAPI(apiKey);
        apiCache = new ApiCache();
        uuidCache = new UuidCache();
        apiParser = new ApiParser(api);
    }

    public void printPlayerInfo(String playerName) {

        //find the player json
        JsonObject player = getPlayer(playerName);
        if (player == null) {
            TextUtils.sendClientMessage("Could not find player: " + playerName);
            return;
        }
        String uuid = player.get("uuid").getAsString();


        //find the sb and session jsons
        AtomicReference<JsonObject> sbProfile = new AtomicReference<>();
        AtomicReference<StatusReply.Session> status = new AtomicReference<>();

        AtomicBoolean sbLookupFinished = new AtomicBoolean(false);
        AtomicBoolean statusLookupFinished = new AtomicBoolean(false);

        if (main.getConfigValues().showSkyblockInfo.getValue()) {
            Thread thread = new Thread(() -> {
                sbProfile.set(getMostRecentSbProfile(player, playerName, uuid));
                sbLookupFinished.set(true);
            });
            thread.start();
        } else sbLookupFinished.set(true);

        if (main.getConfigValues().showSessionInfo.getValue()) {
            Thread thread1 = new Thread(() -> {
                status.set(findStatus(uuid));
                statusLookupFinished.set(true);
            });
            thread1.start();
        } else statusLookupFinished.set(true);


        ArrayList<IChatComponent> messageTextComponents = new ArrayList<>();
        try {
            //print info that only needs player json
            messageTextComponents.add(new ChatComponentText("§7§m---------------------------------------------"));
            messageTextComponents.add(TextUtils.mergeChatTexts("§aName: " + ApiParser.getRank(player) + " ", ApiParser.getNames(player, 4)));
            messageTextComponents.add(TextUtils.mergeChatTexts("\n" + "§aNetwork Lvl: §f", ApiParser.getNetworkLvl(player)));
            messageTextComponents.add(TextUtils.mergeChatTexts("§aQuests Completed: §f", ApiParser.getQuestCount(player)));
            messageTextComponents.add(TextUtils.mergeChatTexts("§aAchievement Points: §f", apiParser.getAchievementPoints
                    (player, main.getConfigValues().showLegacyAchievements.getValue(), main.getConfigValues().debugMode.getValue(), 3)));
            messageTextComponents.add(TextUtils.mergeChatTexts("§aLast Online: §f", ApiParser.getLastOnline(player)));


            if (main.getConfigValues().showSkyblockInfo.getValue()) {
                GeneralUtils.waitUntilBooleanIsTrue(sbLookupFinished);

                //check if sb json exists
                if (sbProfile.get() == null) {
                    messageTextComponents.add(new ChatComponentText("\n" + "§cCould not find any Skyblock Profiles"));
                } else {
                    JsonObject sbPlayerProfile = sbProfile.get().getAsJsonObject("members").getAsJsonObject(uuid);
                    //print info that needs sb json
                    messageTextComponents.add(new ChatComponentText("\n§b§lSkyblock:"));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bSkill Average: §f", ApiParser.getSkillAvg(sbPlayerProfile, player, false)));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bSlayer Exp: §f", ApiParser.getSlayerXp(sbPlayerProfile)));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bMinion Slots: §f", ApiParser.getMinionCount(sbProfile.get())));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bFairy Souls: §f", ApiParser.getFairySoulCount(sbPlayerProfile)));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bUnique Talismans: §f", ApiParser.getTalismanCount(sbPlayerProfile)));
                    ArrayList<Pet> pets = ApiParser.getPets(sbPlayerProfile);
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bActive Pet: §f", ApiParser.getActivePet(pets)));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bPet Score: §f", ApiParser.getPetScore(pets, !main.getConfigValues().showAllPets.getValue(), 4)));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bDungeons completed: §f", ApiParser.getDungeonCompletions(sbPlayerProfile, player)));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bCatacombs Xp: §f", ApiParser.getDungeonXp(sbPlayerProfile)));
                    messageTextComponents.add(TextUtils.mergeChatTexts("§bDungeon Class: §f", ApiParser.getDungeonClass(sbPlayerProfile)));
                    if (main.getConfigValues().showAllPets.getValue())
                        messageTextComponents.add(TextUtils.mergeChatTexts("\n§bAll Pets: \n", ApiParser.getPets(ApiParser.getPets(sbPlayerProfile))));
                    messageTextComponents.add(new ChatComponentText("\n§bArmor:"));

                    try {
                        ArrayList<NBTTagCompound> armorPieces = ApiParser.getSbInvNBT(sbPlayerProfile, "inv_armor");
                        if (armorPieces == null) messageTextComponents.add(new ChatComponentText("None"));
                        else {
                            Collections.reverse(armorPieces);
                            for (ChatComponentText armorPiece : ApiParser.getSbArmorChatMessages(armorPieces)) {
                                messageTextComponents.add(TextUtils.mergeChatTexts("-", armorPiece));
                            }
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        TextUtils.sendClientMessage("Couldn't parse armor data");
                        messageTextComponents.add(new ChatComponentText("NaN"));
                    }

                    if (main.getConfigValues().showAllInventories.getValue()) {
                        ArrayList<ChatComponentText> inventoryMessages = ApiParser.getInventoryMessages(sbPlayerProfile, playerName);
                        if (inventoryMessages.size() != 0) {
                            messageTextComponents.add(new ChatComponentText(""));
                            messageTextComponents.addAll(inventoryMessages);
                        }
                    } else {
                        messageTextComponents.add(new ChatComponentText(""));
                        messageTextComponents.add(ApiParser.getShortInventoryMessage(sbPlayerProfile, playerName));
                    }
                }
            }

            if (main.getConfigValues().showSessionInfo.getValue()) {
                GeneralUtils.waitUntilBooleanIsTrue(statusLookupFinished);

                //check if session json exists
                if (status.get() == null) {
                    messageTextComponents.add(new ChatComponentText("\n" + "§cCould not find the player's status"));
                } else {
                    //print session info
                    messageTextComponents.add(new ChatComponentText("\n§e§lSesssion:"));
                    messageTextComponents.addAll(ApiParser.getSessionMessages(status.get()));
                }
            }
        }
        catch(Exception exception){
            exception.printStackTrace();
            TextUtils.sendClientMessage("§cSomething went wrong while parsing this player's API data! Printing everything that has already been processed." +
                    " A more detailed error message has been printed to your logs. Please report it to a developer.");
        }

        messageTextComponents.add(new ChatComponentText("§7§m---------------------------------------------"));

        TextUtils.sendClientMessages(messageTextComponents);
    }

    public void refreshApiKey(){
        apiKey = main.getConfigValues().apiKey;
        api = new HypixelAPI(apiKey);
        apiParser.setApi(api);
    }

    /**
     * Attempts to find the given player by first checking in the cache, then looking them up in the Hypixel API.
     * Puts the found player in cache.
     * Automatically prints an error message if the look-up fails.
     * @return Returns the JsonObject of the player if found, returns null in case of an error
     */
    public JsonObject getPlayer(String name){
        //check if player is cached
        JsonObject playerJson = apiCache.getItem(name, CacheableJson.TYPE_PLAYER);

        //if not, look up the player from the Hypixel API and cache them

        //first look them up by UUID
        String uuid = getUuid(name);
        if(uuid!=null){
            return getPlayer(name, uuid);
        }

        //then by name
        if(playerJson==null) {
            AtomicBoolean lookUpFinished = new AtomicBoolean(false);

            api.getPlayerByName(name).whenComplete((response, error) -> {
                if(error!=null){
                    TextUtils.sendClientMessage("An error occurred in the Hypixel API: " + error.getMessage() +
                            (error.getMessage().equals("Connection reset") ? ".§b Try again in a few seconds" : ""), true);
                    error.printStackTrace();
                } else {
                    apiCache.addItem(name, CacheableJson.TYPE_PLAYER, response.getPlayer());
                }

                lookUpFinished.set(true);
            });
            GeneralUtils.waitUntilBooleanIsTrue(lookUpFinished);
            playerJson = apiCache.getItem(name, CacheableJson.TYPE_PLAYER);
        }

        return playerJson;
    }

    /**
     * Attempts to find the given player by first checking in the cache, then looking them up in the Hypixel API.
     * Puts the found player in cache.
     * Automatically prints an error message if the look-up fails.
     * @return Returns the JsonObject of the player if found, returns null in case of an error
     */
    JsonObject getPlayer(String name, String uuid){
        //check if player is cached
        JsonObject playerJson = apiCache.getItem(name, CacheableJson.TYPE_PLAYER);

        //if not, look up the player from the Hypixel API and cache them
        if(playerJson==null) {
            AtomicBoolean lookUpFinished = new AtomicBoolean(false);

            api.getPlayerByUuid(uuid).whenComplete((response, error) -> {
                if(error!=null){
                    TextUtils.sendClientMessage("An error occurred in the Hypixel API: " + error.getMessage() +
                            (error.getMessage().equals("Connection reset") ? ".§b Try again in a few seconds" : ""), true);
                    error.printStackTrace();
                } else {
                    apiCache.addItem(name, CacheableJson.TYPE_PLAYER, response.getPlayer());
                }

                lookUpFinished.set(true);
            });
            GeneralUtils.waitUntilBooleanIsTrue(lookUpFinished);
            playerJson = apiCache.getItem(name, CacheableJson.TYPE_PLAYER);
        }

        return playerJson;
    }

    /**
     * Returns the given player's most recent Skyblock profile.
     * First checks in the cache, if the profile is not found, checks in the Hypixel API.
     * Puts the found profile in cache.
     * Automatically prints error messages.
     */
    JsonObject getMostRecentSbProfile(JsonObject playerJson, String playerName, String uuid){

        //check whether the profile is already cached
        JsonObject mostRecentProfile = apiCache.getItem(playerName, CacheableJson.TYPE_SKYBLOCK_PROFILE);
        if(mostRecentProfile!=null)return mostRecentProfile;

        //check if the person has played skyblock
        if(!playerJson.has("stats") || !playerJson.getAsJsonObject("stats").has("SkyBlock")){
            //TextUtils.sendClientMessage("This player has not played Skyblock");
            return null;
        }

        //if they have, get the id of all of their profiles
        ArrayList<String> profileIds = new ArrayList<>();
        Set<Map.Entry<String, JsonElement>> profilesEntrySet = playerJson.getAsJsonObject("stats").getAsJsonObject("SkyBlock").getAsJsonObject("profiles").entrySet();
        for(Map.Entry<String, JsonElement> ee:profilesEntrySet){
            profileIds.add(ee.getKey());
        }

        //turn the profile IDs into jsons using the Hypixel API
        ArrayList<JsonObject> profileJsons = new ArrayList<>();
        AtomicInteger numberOfFinishedLookUps = new AtomicInteger(0);

        for(String ss:profileIds){
            api.getSkyBlockProfile(ss).whenComplete((response, error)->{
                if(error != null){
                    error.printStackTrace();
                    TextUtils.sendClientMessage("Error with Skyblock profile "+ss);
                    TextUtils.sendClientMessage("API error: "+error.getMessage());
                } else {
                    if(response.getProfile()==null) TextUtils.sendClientMessage("This player has an empty profile");
                    else profileJsons.add(response.getProfile());
                }
                numberOfFinishedLookUps.addAndGet(1);
            });
        }

        GeneralUtils.waitUntilIntIsEqualTo(numberOfFinishedLookUps, profileIds.size());

        //find the most recent profile
        long last_save = 0;
        for(JsonObject profile:profileJsons){
            long profile_lastSave = 0;
            if(profile.getAsJsonObject("members").getAsJsonObject(uuid).has("last_save"))
                profile_lastSave = profile.getAsJsonObject("members").getAsJsonObject(uuid).get("last_save").getAsLong();
            if(profile_lastSave > last_save){
                last_save = profile_lastSave;
                mostRecentProfile = profile;
            }
        }

        String a = playerJson.get("playername").getAsString();
        if(mostRecentProfile!=null)apiCache.addItem(a, CacheableJson.TYPE_SKYBLOCK_PROFILE, mostRecentProfile);
        return mostRecentProfile;
    }

    public String getUuid(String name){
        String uuid = uuidCache.getUuid(name);
        if(uuid!=null) return uuid;

        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/"+name);
            URLConnection request = url.openConnection();
            JsonElement root = new JsonParser().parse(new InputStreamReader(request.getInputStream()));
            if(root==null || root.isJsonNull())return null;
            uuid = root.getAsJsonObject().get("id").getAsString();
            uuidCache.addItem(name, uuid);
            return uuid;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    StatusReply.Session findStatus(String uuid){
        AtomicBoolean lookUpFinished = new AtomicBoolean(false);
        AtomicReference<StatusReply.Session> session = new AtomicReference<>();
        uuid = uuid.substring(0, 8)+"-"+uuid.substring(8,12)+"-"+uuid.substring(12,16)+"-"+uuid.substring(16,20)+"-"+uuid.substring(20,32);

        api.getStatus(UUID.fromString(uuid)).whenComplete((response, error) -> {
            if(error!=null){
                TextUtils.sendClientMessage("An error occurred in the Hypixel API: " + error.getMessage(), true);
                error.printStackTrace();
            } else {
                session.set(response.getSession());
            }
            lookUpFinished.set(true);
        });
        GeneralUtils.waitUntilBooleanIsTrue(lookUpFinished);

        return session.get();
    }

    public JsonObject getSbPlayerProfile(JsonObject playerJson, String playerName, String uuid){
        JsonObject sbProfile = getMostRecentSbProfile(playerJson, playerName, uuid);
        if(sbProfile==null){
            TextUtils.sendClientMessage("Could not find that player's Skyblock profiles");
            return null;
        }
        return sbProfile.getAsJsonObject("members").getAsJsonObject(uuid);
    }
}
