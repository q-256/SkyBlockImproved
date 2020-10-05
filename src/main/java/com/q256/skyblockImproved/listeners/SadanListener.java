package com.q256.skyblockImproved.listeners;

import com.q256.skyblockImproved.SkyblockImproved;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SadanListener {
    private SkyblockImproved main = SkyblockImproved.getInstance();
    private static final String[] GIANT_NAMES = {"§d§lJolly Pink Giant", "§4§lL.A.S.R.", "§c§lBigfoot", "§3§lThe Diamond Giant"};
    private static final Pattern SADAN_REGEX = Pattern.compile("§e. §c§lSadan§r §[aec](\\d+(\\.\\d)?M|\\d{1,3}k)§c. §e.§r");
    private ArrayList<String> visibleGiantNames = new ArrayList<>();
    boolean scanForGiants = false;
    int tickCounter = 0;
    long lastGiantFoundTime;

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        String unformattedText = event.message.getUnformattedText();

        switch (unformattedText){
            case "[BOSS] Sadan: My giants! Unleashed!": scanForGiants = true; break;
            case "[BOSS] Sadan: It was inevitable. You are fighting forces beyond your imagination.":
            case "[BOSS] Sadan: NOOOOOOOOO!!! THIS IS IMPOSSIBLE!!": scanForGiants = false; break;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (main.getConfigValues().showSadanGiantOverlay.getValue()) {
                tickCounter++;

                if (scanForGiants || tickCounter % 100 == 0) {
                    visibleGiantNames.clear();
                    World theWorld = Minecraft.getMinecraft().theWorld;
                    if (theWorld != null) {
                        for (Entity entity : theWorld.loadedEntityList) {
                            String displayName = entity.getDisplayName().getFormattedText();

                            if (entity instanceof EntityArmorStand && displayName.startsWith("§c§")) {
                                for (String giantName : GIANT_NAMES) {
                                    if (displayName.startsWith("§c" + giantName)) {
                                        visibleGiantNames.add(displayName);
                                        lastGiantFoundTime = tickCounter;
                                    }
                                }
                            } else if (visibleGiantNames.isEmpty()) {
                                Matcher matcher = SADAN_REGEX.matcher(displayName);
                                if(matcher.matches()){
                                    visibleGiantNames.add(displayName);
                                    lastGiantFoundTime = tickCounter;
                                }
                            }
                        }
                    }
                }

                if (scanForGiants && lastGiantFoundTime + 300 < tickCounter) scanForGiants = false;
                if (!scanForGiants && lastGiantFoundTime + 5 > tickCounter) scanForGiants = true;
            } else {
                visibleGiantNames.clear();
            }
        }
    }

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent event){
        if(!event.isCancelable() &&  (event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.type == RenderGameOverlayEvent.ElementType.JUMPBAR)){
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            for(int i=0; i<visibleGiantNames.size(); i++){
                fontRenderer.drawString(visibleGiantNames.get(i), (int)(main.getConfigValues().sadanGiantOverlayX.getValue() * scaledResolution.getScaledWidth()),
                        (int)(main.getConfigValues().sadanGiantOverlayY.getValue() * scaledResolution.getScaledHeight()) + i*(fontRenderer.FONT_HEIGHT+1), 0);
            }
        }
    }
}
