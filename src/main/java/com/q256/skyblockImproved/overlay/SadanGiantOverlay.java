package com.q256.skyblockImproved.overlay;

import com.q256.skyblockImproved.config.Setting;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SadanGiantOverlay extends Overlay{
    private static final transient String[] GIANT_NAMES = {"§d§lJolly Pink Giant", "§4§lL.A.S.R.", "§c§lBigfoot", "§3§lThe Diamond Giant"};
    private static final transient Pattern SADAN_REGEX = Pattern.compile("§e﴾ §c§lSadan§r §[aec](\\d+(\\.\\d)?M|\\d{1,3}k)§c❤ §e﴿§r");
    //todo change this
    private static final transient Pattern WATCHER_GIANT_REGEX = Pattern.compile(".+ Giant §[aec]\\d+§c❤§r");

    private transient ArrayList<String> visibleGiantNames = new ArrayList<>();
    private transient boolean scanForGiants = false;
    private transient int tickCounter = 0;
    private transient long lastGiantFoundTime;
    private transient String[] previewNames = {"§c" + GIANT_NAMES[0] + " §c1.6M§c❤", "§c" + GIANT_NAMES[1] + " §a25M§c❤",
            "§c" + GIANT_NAMES[2] + " §e11M§c❤", "§c" + GIANT_NAMES[3] + " §a20M§c❤"};

    public SadanGiantOverlay(){
        super(1, 1, 1, AlignModeX.RIGHT, AlignModeY.BOTTOM);
    }

    public void onChatReceived(String message){
        switch (message){
            case "[BOSS] Sadan: My giants! Unleashed!":
            case "The BLOOD DOOR has been opened!":
                scanForGiants = true; break;
            case "[BOSS] Sadan: It was inevitable. You are fighting forces beyond your imagination.":
            case "[BOSS] Sadan: NOOOOOOOOO!!! THIS IS IMPOSSIBLE!!":
                scanForGiants = false; break;
        }
    }

    @Override
    public void drawOverlay() {
        tick();

        if(!visibleGiantNames.isEmpty()){
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            GlStateManager.pushMatrix();
            GlStateManager.scale(size, size, 1);

            int yCorner = (int)(y*scaledResolution.getScaledHeight()/size - (alignModeY.ordinal()*visibleGiantNames.size()*(fontRenderer.FONT_HEIGHT+1)-1)/2);
            for(int i=0; i<visibleGiantNames.size(); i++){
                String giantName = visibleGiantNames.get(i);
                int xCorner = (int)(x*scaledResolution.getScaledWidth()/size - alignModeX.ordinal()* TextUtils.getStringWidth(giantName, fontRenderer)/2);
                fontRenderer.drawStringWithShadow(giantName, xCorner, yCorner + i*(fontRenderer.FONT_HEIGHT+1), 0);
            }
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void drawPreview() {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        GlStateManager.pushMatrix();
        GlStateManager.scale(size, size, 1);

        int yCorner = (int)(y*scaledResolution.getScaledHeight()/size - (alignModeY.ordinal()* previewNames.length*(fontRenderer.FONT_HEIGHT+1)-1)/2);
        for(int i=0; i< previewNames.length; i++){
            String giantName = previewNames[i];
            int xCorner = (int)(x*scaledResolution.getScaledWidth()/size - alignModeX.ordinal()* TextUtils.getStringWidth(giantName, fontRenderer)/2);
            fontRenderer.drawStringWithShadow(giantName, xCorner, yCorner + i*(fontRenderer.FONT_HEIGHT+1), 0);
        }
        GlStateManager.popMatrix();
    }

    private void tick(){
        tickCounter++;

        if (scanForGiants || tickCounter % 100 == 0) {
            visibleGiantNames.clear();
            World theWorld = Minecraft.getMinecraft().theWorld;
            if (theWorld != null) {
                for (Entity entity : theWorld.loadedEntityList) {
                    String displayName = entity.getDisplayName().getFormattedText();

                    //Use this for testing in singleplayer worlds
                    //displayName = displayName.replace('&','§');

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

                    Matcher matcher = WATCHER_GIANT_REGEX.matcher(displayName);
                    if(matcher.matches()){
                        visibleGiantNames.add(displayName);
                        lastGiantFoundTime = tickCounter;
                    }
                }
            }
        }

        if (scanForGiants && lastGiantFoundTime + 300 < tickCounter) scanForGiants = false;
        if (!scanForGiants && lastGiantFoundTime + 5 > tickCounter) scanForGiants = true;
    }

    @Override
    public Setting<Boolean> getEnabled() {
        return main.getConfigValues().showSadanGiantOverlay;
    }

    @Override
    public void updateDimensions() {
        width = 0;
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        for(String previewName: previewNames){
            int nameWidth = fontRenderer.getStringWidth(previewName);
            if(nameWidth > width) width = nameWidth;
        }

        height = (fontRenderer.FONT_HEIGHT+1) * previewNames.length;
    }

    @Override
    public void resetPos() {
        x = 1;
        y = 1;
        size = 1;
        alignModeX = AlignModeX.RIGHT;
        alignModeY = AlignModeY.BOTTOM;
    }
}
