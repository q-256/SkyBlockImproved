package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class AlignModeButton extends GuiButton {
    protected static final ResourceLocation ALIGN_BUTTON_TEXTURES = new ResourceLocation("skyblockimproved", "gui/alignButtons.png");
    /*
    0 - LEFT
    1 - CENTER
    2 - RIGHT
    3 - TOP
    4 - MIDDLE
    5 - BOTTOM
     */
    protected int alignType;

    public AlignModeButton(int buttonId, int x, int y, int alignType) {
        super(buttonId, x, y, 24, 24, "");
        this.alignType = alignType;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if(visible){
            mc.getTextureManager().bindTexture(ALIGN_BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.hovered);
            drawTexturedModalRect(xPosition, yPosition, hoverState*24, alignType*24, width, height);
            mouseDragged(mc, mouseX, mouseY);
        }
    }
}
