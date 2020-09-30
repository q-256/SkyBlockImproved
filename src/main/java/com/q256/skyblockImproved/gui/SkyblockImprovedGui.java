package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.SkyblockImproved;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;

//Currently unused
//This is going to be a central menu for opening other GUIs (once other GUIs exist)

public class SkyblockImprovedGui extends GuiScreen {
    SkyblockImproved main = SkyblockImproved.getInstance();
    @Override
    public void initGui() {
        super.initGui();
        addScaledButton(0, 30, 15, 40,7, "Settings");
    }

    @Override
    protected void actionPerformed(GuiButton button){
        int id = button.id;
        switch (id){
            case 0:
                main.openGuiScreen = new SettingsGui();
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj,"SkyBlock Improved", width/2, 5, (int)(Math.pow(2,24)-1));

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void addScaledButton(int id, double x, double y, double width, double height, String text){
        int scaledX = (int)(x*this.width/100);
        int scaledY = (int)(y*this.height/100);
        int scaledWidth = (int)(width*this.width/100);
        int scaledHeight = (int)(height*this.height/100);

        buttonList.add(new GuiButtonExt(id, scaledX, scaledY, scaledWidth, scaledHeight, text));
    }
}
