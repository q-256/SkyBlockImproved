package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

public class DraggingGuiScreen extends GuiScreen {
    protected int previousMouseX;
    protected int previousMouseY;
    protected GuiButton selectedGuiButton;

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(mouseButton==0) {
            previousMouseX = mouseX;
            previousMouseY = mouseY;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if(selectedGuiButton!=null){
            previousMouseX = mouseX;
            previousMouseY = mouseY;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        selectedGuiButton = button;
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        selectedGuiButton = null;
    }
}
