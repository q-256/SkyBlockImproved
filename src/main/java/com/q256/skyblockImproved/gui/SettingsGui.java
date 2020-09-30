package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.SkyblockImproved;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class SettingsGui extends GuiScreen {
    SettingsScrollPanel settingsScrollPanel;

    @Override
    public void initGui() {
        super.initGui();
        settingsScrollPanel = new SettingsScrollPanel(fontRendererObj, width/6, height/6, (int)(width*0.8), (int)(height*0.8));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, "SkyBlock Improved Settings", width/2,  6, 16777215);
        settingsScrollPanel.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        settingsScrollPanel.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if(keyCode == Keyboard.KEY_ESCAPE){
            SkyblockImproved.getInstance().configHandler.saveFile(true);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
