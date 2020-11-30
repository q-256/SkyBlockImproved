package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.overlay.AlignModeX;
import com.q256.skyblockImproved.overlay.AlignModeY;
import com.q256.skyblockImproved.overlay.Overlay;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class OverlayMoveButton extends GuiSolidRectButton {
    private Overlay overlay;
    private ArrayList<OverlayResizeButton> overlayResizeButtons;

    public OverlayMoveButton(int buttonId, Overlay overlay) {
        super(buttonId, overlay.getXCorner(), overlay.getYCorner(), overlay.getWidth(), overlay.getHeight(), 1434419071, -1430274113);
        this.overlay = overlay;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        super.drawButton(mc, mouseX, mouseY);
        overlay.drawPreview();
    }

    public Overlay getOverlay() {
        return overlay;
    }

    public void drawOutline(int color){
        drawRect(xPosition, yPosition, xPosition+width, yPosition+2, color);
        drawRect(xPosition, yPosition+height-2, xPosition+width, yPosition+height, color);
        drawRect(xPosition, yPosition+2, xPosition+2, yPosition+height-2, color);
        drawRect(xPosition+width-2, yPosition+2, xPosition+width, yPosition+height-2, color);
    }

    public List<OverlayResizeButton> createResizeButtons(){
        overlayResizeButtons = new ArrayList<>();
        for(AlignModeX alignModeX:AlignModeX.values()){
            for(AlignModeY alignModeY:AlignModeY.values()){
                overlayResizeButtons.add(new OverlayResizeButton(this, alignModeX, alignModeY));
            }
        }
        //Remove the button that has both alignment x and y in the middle
        overlayResizeButtons.remove(4);
        updateRescaleButtonEnabled();
        return overlayResizeButtons;
    }

    public void mouseClickMoved(int dX, int dY){
        xPosition += dX;
        yPosition += dY;
        overlay.moveX(dX);
        overlay.moveY(dY);
        for(OverlayResizeButton overlayResizeButton:overlayResizeButtons){
            overlayResizeButton.xPosition += dX;
            overlayResizeButton.yPosition += dY;
        }
    }

    public void updatePosAndSize(){
        xPosition = overlay.getXCorner();
        yPosition = overlay.getYCorner();
        width = overlay.getWidth();
        height = overlay.getHeight();
        updateRescaleButtonPos();
    }

    public void updateRescaleButtonPos() {
        for(OverlayResizeButton overlayResizeButton:overlayResizeButtons){
            overlayResizeButton.updatePos(this);
        }
    }

    public void updateRescaleButtonEnabled(){
        for(OverlayResizeButton overlayResizeButton:overlayResizeButtons){
            boolean enable;

            if(overlayResizeButton.xAlignment == AlignModeX.CENTER) enable = overlay.getAlignModeX() == AlignModeX.CENTER && overlayResizeButton.yAlignment != overlay.getAlignModeY();
            else if(overlayResizeButton.yAlignment == AlignModeY.MIDDLE) enable = overlay.getAlignModeY() == AlignModeY.MIDDLE && overlayResizeButton.xAlignment != overlay.getAlignModeX();
            else enable = overlayResizeButton.xAlignment != overlay.getAlignModeX() && overlayResizeButton.yAlignment != overlay.getAlignModeY();

            overlayResizeButton.enabled = enable;
        }
    }

    public void setAlignX(AlignModeX alignX){
        overlay.setAlignModeX(alignX);
        updateRescaleButtonEnabled();
    }

    public void setAlignY(AlignModeY alignY){
        overlay.setAlignModeY(alignY);
        updateRescaleButtonEnabled();
    }

    /**
     * Resets the position of this button's overlay, this button, and all of its resize buttons
     */
    public void resetPos(){
        overlay.resetPos();
        updatePosAndSize();
        updateRescaleButtonEnabled();
    }
}
