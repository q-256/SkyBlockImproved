package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.overlay.AlignModeX;
import com.q256.skyblockImproved.overlay.AlignModeY;
import com.q256.skyblockImproved.overlay.Overlay;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;

public class OverlayResizeButton extends GuiSolidRectButton{
    static final int SIZE = 4;
    Overlay overlay;
    OverlayMoveButton overlayMoveButton;
    AlignModeX xAlignment;
    AlignModeY yAlignment;
    //Relative to the center of this button
    int mousePressX;
    int mousePressY;

    public OverlayResizeButton(OverlayMoveButton overlayMoveButton, AlignModeX xAlignment, AlignModeY yAlignment){
        super(overlayMoveButton.id + 1000,
                overlayMoveButton.xPosition + overlayMoveButton.width*xAlignment.ordinal()/2 - SIZE,
                overlayMoveButton.yPosition + overlayMoveButton.height*yAlignment.ordinal()/2 - SIZE,
                SIZE*2, SIZE*2, -1077952577,-1, 0);
        this.overlayMoveButton = overlayMoveButton;
        this.xAlignment = xAlignment;
        this.yAlignment = yAlignment;
        this.overlay = overlayMoveButton.getOverlay();
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if(super.mousePressed(mc, mouseX, mouseY)){
            mousePressX = mouseX - xPosition - SIZE;
            mousePressY = mouseY - yPosition - SIZE;
            return true;
        } else {
            return false;
        }
    }

    public void mouseClickMoved(int newX, int newY){
        int newXCenter = newX - mousePressX;
        int newYCenter = newY - mousePressY;

        if(xAlignment == AlignModeX.CENTER){
            int dY = (newYCenter - (yPosition+SIZE)) * (yAlignment.ordinal()-1);
            if(overlay.getAlignModeY() == AlignModeY.MIDDLE) dY *= 2;
            overlay.setSize((dY + overlayMoveButton.height) / (double) overlay.getUnscaledHeight());
        } else if (yAlignment == AlignModeY.MIDDLE) {
            int dX = (newXCenter - (xPosition+SIZE)) * (xAlignment.ordinal()-1);
            if(overlay.getAlignModeX() == AlignModeX.CENTER) dX *= 2;
            overlay.setSize((dX + overlayMoveButton.width) / (double) overlay.getUnscaledWidth());
        } else {
            int dX = (newXCenter - (xPosition+SIZE)) * (xAlignment.ordinal()-1);
            int dY = (newYCenter - (yPosition+SIZE)) * (yAlignment.ordinal()-1);
            if(overlay.getAlignModeX() == AlignModeX.CENTER) dX *= 2;
            if(overlay.getAlignModeY() == AlignModeY.MIDDLE) dY *= 2;
            if(dX/(double)overlay.getUnscaledWidth() < dY/(double)overlay.getUnscaledHeight()) dX = dY*overlay.getWidth()/overlay.getHeight();
            overlay.setSize((dX + overlayMoveButton.width) / (double) overlay.getUnscaledWidth());
        }

        overlayMoveButton.updatePosAndSize();
    }

    public void updatePos(OverlayMoveButton overlayMoveButton){
        xPosition = overlayMoveButton.xPosition + overlayMoveButton.width*xAlignment.ordinal()/2 - SIZE;
        yPosition = overlayMoveButton.yPosition + overlayMoveButton.height*yAlignment.ordinal()/2 - SIZE;
    }
}
