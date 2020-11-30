package com.q256.skyblockImproved.overlay;

import com.q256.skyblockImproved.config.Setting;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.concurrent.DelayQueue;

public abstract class DpsTracker extends Overlay {
    transient ResourceLocation iconResource;
    transient ItemStack iconItem;
    transient boolean has3dIcon;

    /** This is how long each chat message will be cached for */
    private transient long dmgCacheTime;
    private transient DelayQueue<DelayedDamage> damageAmounts = new DelayQueue<>();
    private transient double totalDmg = 0;
    private transient double totalDmgPerEnemy = 0;
    private transient String previewMsg;

    protected DpsTracker(ResourceLocation icon, long dmgCacheTime, String previewMsg) {
        super(0, 0.1, 1, AlignModeX.LEFT, AlignModeY.TOP);
        iconResource = icon;
        has3dIcon = false;
        this.dmgCacheTime = dmgCacheTime;
        this.previewMsg = previewMsg;
    }

    protected DpsTracker(ItemStack icon, long dmgCacheTime, String previewMsg){
        super(0, 0.1, 1, AlignModeX.LEFT, AlignModeY.TOP);
        iconItem = icon;
        has3dIcon = true;
        this.dmgCacheTime = dmgCacheTime;
        this.previewMsg = previewMsg;
    }

    public void dmgDealt(int enemyCount, double dmgAmount){
        totalDmg += dmgAmount;
        totalDmgPerEnemy += dmgAmount/enemyCount;
        damageAmounts.add(new DelayedDamage(enemyCount, dmgAmount, dmgCacheTime));
    }

    @Override
    public void drawOverlay() {
        tick();

        if(totalDmg!=0){
            String totalDps = TextUtils.formatNumberShort(totalDmg/(dmgCacheTime/1000.0));
            String dpsPerEnemy = TextUtils.formatNumberShort(totalDmgPerEnemy /(dmgCacheTime/1000.0));
            drawOverlay("§e" + totalDps + "§6/" + dpsPerEnemy + "§d DPS");
        }
    }

    @Override
    public void drawPreview() {
        drawOverlay(previewMsg);
    }

    protected void drawOverlay(String stringToDisplay){
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = getScaledResolution();
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.scale(size, size, 1);

        FontRenderer fontRenderer = mc.fontRendererObj;

        int xCorner = (int)(x*scaledResolution.getScaledWidth()/size - alignModeX.ordinal()*(TextUtils.getStringWidth(stringToDisplay, fontRenderer) + 19)/2);
        int yCorner = (int)(y*scaledResolution.getScaledHeight()/size - alignModeY.ordinal()*8);

        if(has3dIcon){
            RenderItem renderItem = mc.getRenderItem();
            renderItem.renderItemIntoGUI(iconItem, xCorner, yCorner);
        } else {
            mc.getTextureManager().bindTexture(iconResource);
            drawModalRectWithCustomSizedTexture(xCorner, yCorner, 0, 0, 16, 16, 16, 16);
        }


        fontRenderer.drawString(stringToDisplay, xCorner + 19, yCorner + 16 - fontRenderer.FONT_HEIGHT, 0);

        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    protected void tick(){
        DelayedDamage nextDmgObj = damageAmounts.poll();
        while (nextDmgObj != null){
            totalDmg -= nextDmgObj.amount;
            totalDmgPerEnemy -= nextDmgObj.amount/nextDmgObj.enemyCount;
            nextDmgObj = damageAmounts.poll();
        }

        if(-1<totalDmg && totalDmg<1){
            //Round to 0
            totalDmg = 0;
        }
        if(-1<totalDmgPerEnemy && totalDmgPerEnemy<1){
            //round to 0
            totalDmgPerEnemy = 0;
        }
        if(totalDmg<0 || totalDmgPerEnemy<0){
            //totalDmg should never be negative
            TextUtils.sendClientMessage("§cDPS is negative?");
        }
    }

    @Override
    public abstract Setting<Boolean> getEnabled();

    @Override
    public void updateDimensions() {
        width = 19 + Minecraft.getMinecraft().fontRendererObj.getStringWidth(previewMsg);
        height = 16;
    }

    @Override
    public void resetPos() {
        x = 0;
        y = 0.1;
        size = 1;
        alignModeX = AlignModeX.LEFT;
        alignModeY = AlignModeY.TOP;
    }
}
