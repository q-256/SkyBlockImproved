package com.q256.skyblockImproved.utils;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.constants.Sounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class GeneralUtils {
    /**
     * Halts the program until the input boolean is true
     */
    public static void waitUntilBooleanIsTrue(AtomicBoolean b){
        while(!b.get()){
            try{
                Thread.sleep(5);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Halts the program until the input int is equal to the target int
     */
    public static void waitUntilIntIsEqualTo(AtomicInteger i, int target){
        while(!(i.get()==target)){
            try{
                Thread.sleep(5);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public static void waitUntil(Predicate predicate){
        while (!predicate.test(new Object())){
            try{
                Thread.sleep(5);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    public static ArrayList<String> getScoreboardText(){
        ArrayList<String> outAl = new ArrayList<>();

        try {
            Scoreboard scoreboard = Minecraft.getMinecraft().theWorld.getScoreboard();
            ScoreObjective scoreboardObjective = scoreboard.getObjectiveInDisplaySlot(1);
            List<Score> scores = (List<Score>) scoreboard.getSortedScores(scoreboardObjective);
            scores = scores.stream().filter(input -> input.getPlayerName() != null && !input.getPlayerName().startsWith("#"))
                    .skip(Math.max(scores.size() - 15, 0)).collect(Collectors.toList());
            Collections.reverse(scores);

            for(Score ss:scores){
                ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(ss.getPlayerName());
                String playerName = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, ss.getPlayerName());
                outAl.add(playerName);
            }

            return outAl;
        } catch (NullPointerException exception){
            return outAl;
        }
    }
    public static ArrayList<String> getPlayersForTabCompletion(){
        ArrayList<String> names = new ArrayList<>(SkyblockImproved.getInstance().partyHandler.getPartyMembers());

        //get lists of all player entities and the names of players visible in tab
        //both are necessary to make sure you are in the same game as the other player and not just in the same server (Hypixel hosts up to 20 games/server)
        ArrayList<EntityPlayer> players = new ArrayList<>();
        ArrayList<NetworkPlayerInfo> playerInfoMap = new ArrayList<>();

        try {
            players = new ArrayList<>(Minecraft.getMinecraft().thePlayer.worldObj.playerEntities);
            playerInfoMap = new ArrayList<>(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap());
        } catch (NullPointerException ignored){}

        ArrayList<String> playersInTab = new ArrayList<>();
        for(NetworkPlayerInfo playerInfo:playerInfoMap){
            playersInTab.add(Minecraft.getMinecraft().ingameGUI.getTabList().getPlayerName(playerInfo));
        }

        for(EntityPlayer player:players){
            if(player==null || player.getName()==null || player.getDisplayName()==null) continue;
            if(player.getDisplayName().getFormattedText().contains("§k")) continue;

            boolean visibleInTab = false;

            String playerDisplayName = player.getDisplayName().getFormattedText();
            playerDisplayName = playerDisplayName.substring(2, playerDisplayName.length()-2);

            for(String playerInTab:playersInTab){
                if (playerDisplayName.equals(playerInTab)) {
                    visibleInTab = true;
                    break;
                }
            }

            if(visibleInTab && !names.contains(player.getName())){
                names.add(player.getName());
            }
        }

        return names;
    }
    public static ArrayList<String> getReducedTabCompletions(ArrayList<String> allSuggestions, String alreadyTyped, boolean matchMidWord) {
        if (alreadyTyped.equals("")) return allSuggestions;
        alreadyTyped = alreadyTyped.toLowerCase();

        ArrayList<String> result = new ArrayList<>();
        if (matchMidWord) {
            ArrayList<StringInt> sortAl = new ArrayList<>();

            for (String ss : allSuggestions) {
                int indexOf = ss.toLowerCase().indexOf(alreadyTyped);
                if (indexOf != -1) {
                    for (int i = sortAl.size() - 1; i >= 0; i--) {
                        if (sortAl.get(i).integer <= indexOf) {
                            sortAl.add(i + 1, new StringInt(ss, indexOf));
                            break;
                        }
                        if (i == 0) sortAl.add(0, new StringInt(ss, indexOf));
                    }
                    if (sortAl.size() == 0) sortAl.add(new StringInt(ss, indexOf));
                }
            }

            for (StringInt strInt : sortAl) result.add(strInt.string);
        }
        else {
            for(String suggestion:allSuggestions){
                if(suggestion.toLowerCase().startsWith(alreadyTyped)) result.add(suggestion);
            }
        }

        return result;
    }
    public static ArrayList<String> getReducedTabCompletions(ArrayList<String> allSuggestions, String alreadyTyped){
        return getReducedTabCompletions(allSuggestions, alreadyTyped, true);
    }

    /**
     * Plays the given sound at the player's current location
     * @param soundName The sounds name. A list of sound names can be found in the {@link Sounds} class
     * @param volume Volume of the sound. For short sounds, all values above 1 will cause the same effect.
     *               However, if the sound is long enough so that the player has moved a decent distance away from their current position,
     *               it may be required to use higher values.
     * @param pitch Pitch of the sound. Between 0 and 2.
     */
    public static void playSound(String soundName, double volume, double pitch){
        try {
            Minecraft.getMinecraft().thePlayer.playSound(soundName, (float) volume, (float) pitch);
        } catch (NullPointerException exception){
            exception.printStackTrace();
        }
    }

    /**
     * Stops all currently playing sounds
     */
    public static void stopSounds(){
        try {
            Minecraft.getMinecraft().getSoundHandler().stopSounds();
        } catch (NullPointerException exception){
            exception.printStackTrace();
        }
    }
    public static boolean isUserNameValid(String name){
        return name.matches("^\\w{3,16}$");
    }
    public static int parseInt(String string){
        return string.equals("")||string.equals(" ") ? 0:Integer.parseInt(string);
    }
    private static class StringInt{
        String string;
        int integer;
        StringInt(String string, int integer){
            this.string = string;
            this.integer = integer;
        }
    }
}
