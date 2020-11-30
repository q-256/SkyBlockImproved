package com.q256.skyblockImproved.listeners;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.constants.Constants;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LividListener {
    SkyblockImproved main = SkyblockImproved.getInstance();
    boolean scanForLivids = false;
    HashMap<Character, Integer> predictedColors = new HashMap<>();
    int ticksSinceChatMsg = 0;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event){
        if(!main.getConfigValues().showLividMessage.getValue()) return;
        String unformattedText = event.message.getUnformattedText();

        if(unformattedText.equals("[BOSS] Livid: I respect you for making it to here, but I'll be your undoing.")){
            predictedColors.clear();
            scanForLivids = true;
            ticksSinceChatMsg = 0;
            Timer timer = new Timer();
            TimerTask stopScanning = new TimerTask() {
                @Override
                public void run() {
                    scanForLivids = false;
                }
            };
            timer.schedule(stopScanning, 5000);
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(scanForLivids && event.phase == TickEvent.Phase.END){
            ticksSinceChatMsg++;
            System.out.println("New Tick ------------------------------------------");
            World theWorld = Minecraft.getMinecraft().theWorld;
            ArrayList<Character> seenColors = new ArrayList<>();
            for(Entity entity:theWorld.loadedEntityList){
                String displayName = entity.getDisplayName().getFormattedText();
                if(entity instanceof EntityArmorStand) {
                    if(displayName.contains("Livid"))System.out.println("Armor Stand Name: "+displayName);
                    Matcher matcher1 = Pattern.compile("§(\\w). §\\1§lLivid§r (§a|§e)\\d(\\.\\d)?M§c. §\\1.§r").matcher(displayName);
                    if(matcher1.find()){
                        seenColors.add(matcher1.group(1).charAt(0));
                    }
                } /*else {
                    if(displayName.contains("Livid"))System.out.println(entity.getClass() + " name: " + displayName);
                }*/
            }

            //System.out.println("Seen colors: "+seenColors);
            //System.out.println("Predicted Colors: "+predictedColors);
            if(seenColors.size()==8){
                for(char color: Constants.LIVID_COLORS){
                    if(!seenColors.contains(color)) predictedColors.merge(color, 1, Integer::sum);
                }
            }
            for(Map.Entry<Character, Integer> colorFrequency:predictedColors.entrySet()){
                if(colorFrequency.getValue()>5){
                    if(ticksSinceChatMsg<50) {
                        TextUtils.sendClientMessage("Target the §" + colorFrequency.getKey() + TextUtils.getColorCodeName(colorFrequency.getKey()) + "§f Livid!", true);
                    } else {
                        ChatComponentText cct = new ChatComponentText("Target the §" + colorFrequency.getKey() + TextUtils.getColorCodeName(colorFrequency.getKey()) + "§f Livid §c(?)");
                        cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("You are seeing the question mark for one of two reasons: " +
                                "\n1. Hypixel was being laggy during Livid detection but everything is fine" +
                                "\n2. Something went wrong while detecting the real Livid" +
                                "\n" +
                                "\nIf this did end up showing the incorrect color, please report this to a developer" +
                                "\nalong with a copy of your log file (or just all lines from your log file which contain \"lividListener\"")));
                        TextUtils.sendClientMessage(cct, true);
                    }
                    scanForLivids = false;
                }
            }
        }
    }
}