package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.SkyblockImproved;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class SkyblockImprovedGui extends GuiScreen {
    SkyblockImproved main = SkyblockImproved.getInstance();
    @Override
    public void initGui() {
        super.initGui();
        addScaledButton(0, 30, 15, 40,7, "Settings");
        addScaledButton(1, 30, 25, 40,7, "Edit Overlays");
        addScaledButton(2, 30, 35, 40,7, "Fake Chat Editor");
    }

    @Override
    protected void actionPerformed(GuiButton button){
        int id = button.id;
        switch (id){
            case 0:
                main.openGuiScreen = new SettingsGui();
                break;
            case 1:
                main.openGuiScreen = new OverlayEditGui();
                break;
            case 2:
                main.openGuiScreen = new FakeChatGui();
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.25, 1.25, 1);
        drawCenteredString(fontRendererObj,"SkyBlock Improved", (int)(width/2.5), 5, (int)(Math.pow(2,24)-1));
        GlStateManager.popMatrix();

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
