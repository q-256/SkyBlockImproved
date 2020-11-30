package com.q256.skyblockImproved.gui;

import net.minecraftforge.fml.client.config.GuiButtonExt;

public class MovableGuiButton extends GuiButtonExt implements Movable {
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
