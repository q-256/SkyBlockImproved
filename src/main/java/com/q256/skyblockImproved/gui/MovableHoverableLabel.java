package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

import java.util.ArrayList;
import java.util.List;

public class MovableHoverableLabel extends Gui {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected String text;
    protected String hoverText;
    protected ArrayList<String> hoverLines;
    public int textColor = (int)Math.pow(2, 24)-1;

    protected int originalX;
    protected int originalY;

    public MovableHoverableLabel(int x, int y, String text, String hoverText){
        this(x, y, Minecraft.getMinecraft().fontRendererObj.getStringWidth(text), Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT+1, text, hoverText);
    }

    public MovableHoverableLabel(int x, int y, int width, int height, String text, String hoverText){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.hoverText = hoverText;
        this.originalX = x;
        this.originalY = y;
        updateHoverLines(250);
    }

    public void drawText(int mouseX, int mouseY){
        drawString(Minecraft.getMinecraft().fontRendererObj, text, x, y, textColor);
    }

    public void drawHoverText(int mouseX, int mouseY){
        if(isBeingHovered(mouseX, mouseY)) drawHoveringText(hoverLines, mouseX, mouseY, Minecraft.getMinecraft().fontRendererObj);
    }

    public void updatePos(int xOffset, int yOffset){
        x = originalX + xOffset;
        y = originalY + yOffset;
    }

    public void updateHoverLines(int pixelsPerLine){
        hoverLines = TextUtils.splitIntoLines(hoverText, Minecraft.getMinecraft().fontRendererObj, pixelsPerLine);
    }

    protected boolean isBeingHovered(int mouseX, int mouseY){
        return mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height;
    }

    public void setText(String newText){
        setText(newText, true);
    }

    public void setText(String newText, boolean updateWidth){
        text = newText;
        if(updateWidth) width = Minecraft.getMinecraft().fontRendererObj.getStringWidth(newText);
    }

    public String getText(){
        return text;
    }

    public void setHoverText(String newText){
        setHoverText(newText, 50);
    }

    public void setHoverText(String newText, int pixelsPerLine){
        hoverText = newText;
        updateHoverLines(pixelsPerLine);
    }

    public ArrayList<String> getHoverLines(){
        return new ArrayList<>(hoverLines);
    }

    public int getXOffset(){
        return x-originalX;
    }

    public int getYOffset(){
        return y-originalY;
    }

    public void setOriginalX(int newOriginalX){
        int xOffset = getXOffset();
        originalX = newOriginalX;
        x = originalX + xOffset;
    }

    public void setOriginalY(int newOriginalY){
        int yOffset = getYOffset();
        originalY = newOriginalY;
        y = originalY + yOffset;
    }


    //stolen from GuiScreen
    protected void drawHoveringText(List<String> textLines, int x, int y, FontRenderer font)
    {
        if (!textLines.isEmpty())
        {
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            int i = 0;

            for (String s : textLines)
            {
                int j = font.getStringWidth(s);

                if (j > i)
                {
                    i = j;
                }
            }

            int l1 = x + 12;
            int i2 = y - 12;
            int k = 8;

            if (textLines.size() > 1)
            {
                k += 2 + (textLines.size() - 1) * 10;
            }

            /*
            if (l1 + i > this.width)
            {
                l1 -= 28 + i;
            }

            if (i2 + k + 6 > this.height)
            {
                i2 = this.height - k - 6;
            }
            */

            this.zLevel = 300.0F;
            int l = -267386864;
            this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, l, l);
            this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, l, l);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, l, l);
            this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, l, l);
            int i1 = 1347420415;
            int j1 = (i1 & 16711422) >> 1 | i1 & -16777216;
            this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, i1, j1);
            this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, i1, i1);
            this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, j1, j1);

            for (int k1 = 0; k1 < textLines.size(); ++k1)
            {
                String s1 = textLines.get(k1);
                font.drawStringWithShadow(s1, (float)l1, (float)i2, -1);

                if (k1 == 0)
                {
                    i2 += 2;
                }

                i2 += 10;
            }

            this.zLevel = 0.0F;
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();
        }
    }
}
