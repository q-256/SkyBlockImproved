package com.q256.skyblockImproved.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiSolidRectButton extends GuiButton {
    protected int[] colors = new int[3];

    public GuiSolidRectButton(int id, int x, int y, int width, int height, int colorInactive, int colorActive){
        this(id, x, y, width, height, colorInactive, colorActive, colorInactive);
    }

    public GuiSolidRectButton(int id, int x, int y, int width, int height, int colorInactive, int colorActive, int colorDisabled){
        this(id, x, y, width, height, colorInactive, colorActive, colorDisabled, "");
    }

    public GuiSolidRectButton(int id, int x, int y, int width, int height, int colorInactive, int colorActive, int colorDisabled, String text){
        super(id, x, y, width, height, text);
        colors[0] = colorDisabled;
        colors[1] = colorInactive;
        colors[2] = colorActive;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        FontRenderer fontRenderer = mc.fontRendererObj;
        hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        int hoverState = getHoverState(hovered);
        drawRect(xPosition, yPosition, xPosition+width, yPosition+height, colors[hoverState]);
        mouseDragged(mc, mouseX, mouseY);

        if(displayString.length()!=0 && !displayString.equals(" ")){
            int textColor = 14737632;

            if (packedFGColour != 0)
            {
                textColor = packedFGColour;
            }
            else
            if (!this.enabled)
            {
                textColor = 10526880;
            }
            else if (this.hovered)
            {
                textColor = 16777120;
            }
            this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, textColor);
        }
    }
}
