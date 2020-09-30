package com.q256.skyblockImproved.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiSlider;

public class MovableGuiSlider extends GuiSlider implements Movable {
    int originalX;
    int originalY;

    public MovableGuiSlider(int id, int xPos, int yPos, int width, int height, String prefix, String suffix, double minVal, double maxVal, double currentVal, boolean showDec) {
        super(id, xPos, yPos, width-1, height, prefix, suffix, minVal, maxVal, currentVal, showDec, true, null);
        originalX = xPos;
        originalY = yPos;
    }

    @Override
    public void updatePos(int xOffset, int yOffset){
        xPosition = originalX + xOffset;
        yPosition = originalY + yOffset;
    }

    @Override
    protected void mouseDragged(Minecraft par1Minecraft, int par2, int par3) {
        //only difference between this and the super method is the height of rectangle that gets drawn
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (par2 - (this.xPosition + 4)) / (float)(this.width - 8);
                updateSlider();
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, height-1);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, height-1);
        }
    }
}
