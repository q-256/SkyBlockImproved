package com.q256.skyblockImproved.party;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IChatComponent;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartyHandler {
    private HashSet<String> partyMembers = new HashSet<>();
    SkyblockImproved main = SkyblockImproved.getInstance();

    public PartyHandler(){
        clearMembers();
    }

    public void chatReceived(IChatComponent message){
        String unformattedMessage = message.getFormattedText().replaceAll("§.","");


        //clear all members
        if(unformattedMessage.matches("^(\\[\\w*\\+*] )?\\w{3,16} has disbanded the party! *") ||
                unformattedMessage.equals("The party was disbanded because all invites expired and the party was empty") ||
                unformattedMessage.equals("You left the party.") ||
                unformattedMessage.matches("^You have been kicked from the party by (\\[\\w*\\+*] )?\\w{3,16} *") ||
                unformattedMessage.equals("You must be in a party to join the party channel!") ||
                unformattedMessage.equals("You are not in a party and were moved to the ALL channel.") ||
                unformattedMessage.equals("You are not currently in a party."))
        {
            clearMembers();
            return;
        }


        //add member
        Matcher matcher1 = Pattern.compile("^(\\[\\w*\\+*] )?(\\w{3,16}) joined the party.").matcher(unformattedMessage);
        Matcher matcher2 = Pattern.compile("^Dungeon Finder > (\\w{3,16}) joined the dungeon group!").matcher(unformattedMessage);
        Matcher matcher3 = Pattern.compile("^Party > (\\[\\w*\\+*] )?(\\w{3,16}): ").matcher(unformattedMessage);

        if(matcher1.find()){
            addMember(matcher1.group(2));
            return;
        }
        if(matcher2.find()){
            addMember(matcher2.group(1));
            return;
        }
        if(matcher3.find()){
            addMember(matcher3.group(2));
            return;
        }


        //remove member
        Matcher matcher4 = Pattern.compile("^(\\[\\w*\\+*] )?(\\w{3,16}) has left the party.").matcher(unformattedMessage);
        Matcher matcher5 = Pattern.compile("^(\\[\\w*\\+*] )?(\\w{3,16}) has been removed from the party.").matcher(unformattedMessage);
        Matcher matcher6 = Pattern.compile("^Kicked (\\[\\w*\\+*] )?(\\w{3,16}) because they were offline.").matcher(unformattedMessage);
        Matcher matcher7 = Pattern.compile("^(\\[\\w*\\+*] )?(\\w{3,16}) was removed from your party because they disconnected").matcher(unformattedMessage);
        Matcher matcher8 = Pattern.compile("^The party was transferred to (\\[\\w*\\+*] )?\\w{3,16} because (\\[\\w*\\+*] )?(\\w{3,16}) left").matcher(unformattedMessage);

        if(matcher4.find()){
            removeMember(matcher4.group(2));
            return;
        }
        if(matcher5.find()){
            removeMember(matcher5.group(2));
            return;
        }
        if(matcher6.find()){
            removeMember(matcher6.group(2));
            return;
        }
        if(matcher7.find()){
            removeMember(matcher7.group(2));
            return;
        }
        if(matcher8.find()){
            removeMember(matcher8.group(3));
            return;
        }


        //override party members
        Matcher matcher9 = Pattern.compile("^Party Leader: (\\[\\w*\\+*] )?(\\w{3,16}) \\W").matcher(unformattedMessage);
        Matcher matcher10 = Pattern.compile("(\\[\\w*\\+*] )?(\\w{3,16}) \\W").matcher(unformattedMessage);
        if(matcher9.find()){
            clearMembers();
            addMember(matcher9.group(2));
            return;
        }
        if(unformattedMessage.startsWith("Party Moderators: ") || unformattedMessage.startsWith("Party Members: ")){
            while (matcher10.find()){
                addMember(matcher10.group(2));
            }
        }
    }


    private void addMember(String name){
        partyMembers.add(name);
        if(main.getConfigValues().debugMode.getValue()) TextUtils.sendClientMessage("Added §e"+name+"§f to the list of party members", true);
    }

    private void removeMember(String name){
        partyMembers.remove(name);
        if(main.getConfigValues().debugMode.getValue()) TextUtils.sendClientMessage("Removed §e"+name+"§f from the list of party members", true);
    }

    private void clearMembers(){
        partyMembers.clear();
        partyMembers.add(Minecraft.getSessionInfo().get("X-Minecraft-Username"));
        if(main.getConfigValues().debugMode.getValue()) TextUtils.sendClientMessage("Cleared the list of party members", true);
    }

    public HashSet<String> getPartyMembers(){
        return new HashSet<>(partyMembers);
    }
}
