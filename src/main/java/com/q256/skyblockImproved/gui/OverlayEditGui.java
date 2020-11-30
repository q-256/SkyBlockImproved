package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.overlay.AlignModeX;
import com.q256.skyblockImproved.overlay.AlignModeY;
import com.q256.skyblockImproved.overlay.Overlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OverlayEditGui extends DraggingGuiScreen {
    private final SkyblockImproved main = SkyblockImproved.getInstance();

    private int menuXCorner;
    private int menuYCorner;
    private int menuWidth;
    private int menuHeight;
    private static final int ALIGN_BUTTON_SIZE = 24;
    private static final int MENU_BUTTON_GAP_SIZE = 6;

    private ArrayList<GuiButton> menuButtons = new ArrayList<>();
    private ArrayList<OverlayMoveButton> overlayMoveButtons = new ArrayList<>();
    private ArrayList<OverlayResizeButton> overlayResizeButtons = new ArrayList<>();
    private OverlayMoveButton selectedOverlay;
    private boolean deselectOverlay;

    /*
    Button ids:
    0 - menu background
    10-99 - menu features
        10-15 - alignment buttons
        20-21 - exit buttons
        22 - reset overlay locations
    1000-1999 - overlay move buttons
    2000-2999 - overlay resize buttons
     */

    @Override
    public void initGui() {
        int fontHeight = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;

        menuWidth = ALIGN_BUTTON_SIZE*3 + MENU_BUTTON_GAP_SIZE *4;
        menuXCorner = (width-menuWidth)/2;
        menuHeight = ALIGN_BUTTON_SIZE*2 + MENU_BUTTON_GAP_SIZE *5 + fontHeight*2 + 40;
        menuYCorner = height/2;

        //Add menu buttons
        buttonList.add(new GuiSolidRectButton(0, menuXCorner, menuYCorner, menuWidth, menuHeight, 1431677695, -1437226241));
        menuButtons.clear();

        int currentY = menuYCorner + MENU_BUTTON_GAP_SIZE + fontHeight;
        menuButtons.add(new AlignModeButton(10, menuXCorner + MENU_BUTTON_GAP_SIZE, currentY, 0));
        menuButtons.add(new AlignModeButton(11, menuXCorner + MENU_BUTTON_GAP_SIZE *2 + ALIGN_BUTTON_SIZE, currentY, 1));
        menuButtons.add(new AlignModeButton(12, menuXCorner + MENU_BUTTON_GAP_SIZE *3 + ALIGN_BUTTON_SIZE*2, currentY, 2));

        currentY += ALIGN_BUTTON_SIZE + MENU_BUTTON_GAP_SIZE + fontHeight;
        menuButtons.add(new AlignModeButton(13, menuXCorner + MENU_BUTTON_GAP_SIZE, currentY, 3));
        menuButtons.add(new AlignModeButton(14, menuXCorner + MENU_BUTTON_GAP_SIZE *2 + ALIGN_BUTTON_SIZE, currentY, 4));
        menuButtons.add(new AlignModeButton(15, menuXCorner + MENU_BUTTON_GAP_SIZE *3 + ALIGN_BUTTON_SIZE*2, currentY, 5));

        currentY += ALIGN_BUTTON_SIZE + MENU_BUTTON_GAP_SIZE;
        menuButtons.add(new GuiButton(20, menuXCorner + MENU_BUTTON_GAP_SIZE, currentY, menuWidth/2 - MENU_BUTTON_GAP_SIZE *3/2, 20, "Save"));
        menuButtons.add(new GuiButton(21, menuXCorner + menuWidth/2 + MENU_BUTTON_GAP_SIZE /2, currentY,
                menuWidth/2 - MENU_BUTTON_GAP_SIZE *3/2, 20, "Cancel"));

        currentY += 20 + MENU_BUTTON_GAP_SIZE;
        menuButtons.add(new GuiButton(22, menuXCorner + MENU_BUTTON_GAP_SIZE, currentY, menuWidth - MENU_BUTTON_GAP_SIZE*2, 20, "Reset locations"));

        buttonList.addAll(menuButtons);
        updateAlignButtonsEnabled();

        //Add overlays
        overlayMoveButtons.clear();
        overlayResizeButtons.clear();
        List<Overlay> overlays = main.getOverlays();
        for (int i = 0; i < overlays.size(); i++) {
            Overlay overlay = overlays.get(i);
            if(overlay.getEnabled().getValue()) {
                overlay.updateDimensions();
                OverlayMoveButton overlayMoveButton = new OverlayMoveButton(1000 + i, overlay);
                overlayMoveButtons.add(overlayMoveButton);
                overlayResizeButtons.addAll(overlayMoveButton.createResizeButtons());
            }
        }
        buttonList.addAll(overlayMoveButtons);
        buttonList.addAll(overlayResizeButtons);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        if(overlayMoveButtons.size() == 0){
            drawCenteredString(fontRendererObj, "§cYou don't have any overlays enabled", width/2, height/3, 0);
            drawCenteredString(fontRendererObj, "§cYou can enable overlays in settings -> overlays", width/2, height/3 + fontRendererObj.FONT_HEIGHT+3, 0);
        }

        for (GuiButton guiButton:buttonList) {
            if(selectedGuiButton == guiButton) guiButton.drawButton(mc, guiButton.xPosition+1, guiButton.yPosition+1);
            else if(selectedGuiButton != null) guiButton.drawButton(mc, -100, -100);
            else guiButton.drawButton(mc, mouseX, mouseY);
        }

        for (GuiLabel guiLabel:labelList) {
            guiLabel.drawLabel(mc, mouseX, mouseY);
        }

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;
        drawCenteredString(fontRenderer, "§6X Alignment", menuXCorner + menuWidth/2, menuYCorner + MENU_BUTTON_GAP_SIZE /2, 0);
        drawCenteredString(fontRenderer, "§6Y Alignment", menuXCorner + menuWidth/2, menuYCorner + ALIGN_BUTTON_SIZE + MENU_BUTTON_GAP_SIZE *3/2 + fontRenderer.FONT_HEIGHT, 0);

        if(selectedOverlay!=null) selectedOverlay.drawOutline(2130771967);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton==0){
            deselectOverlay = true;
            super.mouseClicked(mouseX, mouseY, mouseButton);
            if(deselectOverlay) setSelectedOverlay(null);
        }
        else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        deselectOverlay = false;

        int id = button.id;
        switch (id){
            case 20:
                exit(true);
                break;
            case 21:
                exit(false);
                break;
            case 22:
                for(OverlayMoveButton overlayMoveButton:overlayMoveButtons){
                    overlayMoveButton.resetPos();
                }
                break;
            default:
                if(10<=id && id<16 && selectedOverlay != null){
                    switch (id){
                        case 10: selectedOverlay.setAlignX(AlignModeX.LEFT); break;
                        case 11: selectedOverlay.setAlignX(AlignModeX.CENTER); break;
                        case 12: selectedOverlay.setAlignX(AlignModeX.RIGHT); break;
                        case 13: selectedOverlay.setAlignY(AlignModeY.TOP); break;
                        case 14: selectedOverlay.setAlignY(AlignModeY.MIDDLE); break;
                        case 15: selectedOverlay.setAlignY(AlignModeY.BOTTOM); break;
                    }
                    updateAlignButtonsEnabled();
                } else if(1000<=id && id<2000){
                    setSelectedOverlay ((OverlayMoveButton) button);
                } else if(2000<=id && id<3000){
                    setSelectedOverlay(overlayMoveButtons.get(id-2000));
                }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(selectedGuiButton!=null){
            int id = selectedGuiButton.id;
            if((id==0) || (1000 <= id && id < 2000)) {
                int dX = mouseX - previousMouseX;
                int dY = mouseY - previousMouseY;
                if (selectedGuiButton.xPosition + dX < 0) dX = -selectedGuiButton.xPosition;
                if (selectedGuiButton.yPosition + dY < 0) dY = -selectedGuiButton.yPosition;
                if (selectedGuiButton.xPosition + selectedGuiButton.width + dX > width) dX = width - selectedGuiButton.xPosition - selectedGuiButton.width;
                if (selectedGuiButton.yPosition + selectedGuiButton.height + dY > height) dY = height - selectedGuiButton.yPosition - selectedGuiButton.height;

                if (id == 0) {
                    selectedGuiButton.xPosition += dX;
                    selectedGuiButton.yPosition += dY;
                    menuXCorner = selectedGuiButton.xPosition;
                    menuYCorner = selectedGuiButton.yPosition;

                    for (GuiButton menuButton : menuButtons) {
                        menuButton.xPosition += dX;
                        menuButton.yPosition += dY;
                    }
                } else {
                    ((OverlayMoveButton)selectedGuiButton).mouseClickMoved(dX, dY);
                }
            } else if (2000 <= id && id < 3000){
                ((OverlayResizeButton)selectedGuiButton).mouseClickMoved(mouseX, mouseY);
            }
        }

        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void updateAlignButtonsEnabled(){
        if(selectedOverlay==null){
            for(int i=0; i<6; i++){
                menuButtons.get(i).enabled = false;
            }
        } else {
            for(int i=0; i<3; i++){
                menuButtons.get(i).enabled = selectedOverlay.getOverlay().getAlignModeX().ordinal() != i;
            }
            for(int i=3; i<6; i++){
                menuButtons.get(i).enabled = selectedOverlay.getOverlay().getAlignModeY().ordinal() != i-3;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) exit(true);
    }

    void exit(boolean save){
        if(save) main.configHandler.saveFile(true);
        else main.configHandler.loadFile();

        mc.displayGuiScreen(null);
        if (mc.currentScreen == null) {
            mc.setIngameFocus();
        }
    }

    private void setSelectedOverlay(OverlayMoveButton selectedOverlay) {
        this.selectedOverlay = selectedOverlay;
        updateAlignButtonsEnabled();
    }
}
