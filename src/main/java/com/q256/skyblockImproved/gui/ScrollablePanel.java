package com.q256.skyblockImproved.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class ScrollablePanel extends GuiScreen {
    protected FontRenderer fontRendererObj;
    protected int x;
    protected int y;
    protected int smallWidth;
    protected int smallHeight;
    protected int totalHeight;
    protected int scrollPos = 0;
    ArrayList<MovableHoverableLabel> movableLabels = new ArrayList<>();

    public ScrollablePanel(FontRenderer fontRendererObj, int x, int y, int width, int height, int totalHeight){
        mc = Minecraft.getMinecraft();
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        this.width = scaledResolution.getScaledWidth();
        this.height = scaledResolution.getScaledHeight();
        this.itemRender = mc.getRenderItem();
        this.fontRendererObj = fontRendererObj;
        this.x = x;
        this.y = y;
        this.smallWidth = width;
        this.smallHeight = height;
        this.totalHeight = totalHeight;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for(GuiButton button:buttonList){
            if(isButtonVisible(button)){
                button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY);
            }
        }
        for(MovableHoverableLabel label:movableLabels){
            if(isLabelVisible(label)){
                label.drawText(mouseX, mouseY);
            }
        }
        for(MovableHoverableLabel label:movableLabels){
            if(isLabelVisible(label)){
                label.drawHoverText(mouseX, mouseY);
            }
        }
    }

    protected void addButton(int id, int x, int y, int width, int height, String text){
        buttonList.add(new MovableGuiButton(id, x + this.x, y + this.y, width, height, text));
    }

    protected void addLabel(int x, int y, String text, String hoverText){
        movableLabels.add(new MovableHoverableLabel(x + this.x, y + this.y, text, hoverText));
    }

    protected void addSlider(int id, int x, int y, int width, int height, String preifx, String suffix, double minValue, double maxValue, double currentValue, boolean showDec){
        buttonList.add(new MovableGuiSlider(id, x + this.x, y + this.y, width, height, preifx, suffix, minValue, maxValue, currentValue, showDec));
    }

    protected boolean isButtonVisible(GuiButton button){
        return !(button.xPosition < x || button.yPosition < y || button.xPosition+button.width > x+smallWidth || button.yPosition+ button.height > y+smallHeight);
    }

    protected boolean isLabelVisible(MovableHoverableLabel label){
        return !(label.x < x || label.y < y || label.x+label.width > x+smallWidth || label.y+ label.height > y+smallHeight);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dScroll = Integer.signum(Mouse.getDWheel());
        scrollPos -= dScroll*15;
        if(scrollPos+smallHeight > totalHeight) scrollPos = totalHeight-smallHeight;
        if(scrollPos<0) scrollPos=0;

        for(GuiButton button:buttonList){
            if(button instanceof Movable){
                ((Movable) button).updatePos(0, -scrollPos);
            }
        }

        for(MovableHoverableLabel label:movableLabels){
            label.updatePos(0, -scrollPos);
        }
    }
}
