package com.q256.skyblockImproved.gui;

import net.minecraft.client.gui.GuiButton;

public class MovableGuiButton extends GuiButton implements Movable {
    int originalX;
    int originalY;

    public MovableGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        originalX = x;
        originalY = y;
    }

    @Override
    public void updatePos(int xOffset, int yOffset){
        xPosition = originalX + xOffset;
        yPosition = originalY + yOffset;
    }
}
