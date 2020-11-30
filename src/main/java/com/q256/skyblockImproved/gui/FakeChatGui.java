package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class FakeChatGui extends GuiScreen {
    private static final int COLOR_WHITE = (255<<24) - 1;
    GuiTextField textField;
    String[][] texts;
    String currentMsg;

    public FakeChatGui(){
        currentMsg = SkyblockImproved.getInstance().fakeChatGuiMsg;

        texts = new String[4][];
        texts[0] = new String[10];
        texts[1] = new String[6];
        texts[2] = new String[5];
        texts[3] = new String[5];

        for (int i = 0; i < 10; i++) {
            texts[0][i] = "&" +i+i+i+i+ " §e->§f §" +i+i+i+i;
        }
        for (char i = 0; i < 6; i++) {
            char ch = (char)('a'+i);
            texts[1][i] = "&" +ch+ch+ch+ch+ " §e->§f §" +ch+ch+ch+ch;
        }
        for (char i = 0; i < 5; i++) {
            char ch = (char)('k'+i);
            texts[2][i] = "§r&" +ch+ch+ch+ch+ " §e->§f §" +ch+ch+ch+ch;
        }

        texts[3][0] = TextUtils.addRainbowFormat("&wwwwwwwwww §e->§f §wwwwwwwwww");
        texts[3][1] = "\\nL1\\nL2\\nL3 §e->§f ";
        texts[3][2] = "L1";
        texts[3][3] = "L2";
        texts[3][4] = "L3";
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        if(textField != null) currentMsg = textField.getText();
        textField = new GuiTextField(0, fontRendererObj, 20, height*2/3, width-40, 20);
        textField.setMaxStringLength(5000);
        if(currentMsg != null) textField.setText(currentMsg);
        buttonList.add(new GuiButton(1, (width-200)/2, height*2/3 + 25, "Send"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        int fontHeight = fontRendererObj.FONT_HEIGHT;

        GlStateManager.pushMatrix();
        GlStateManager.scale(1.25, 1.25, 1);
        drawCenteredString(fontRendererObj, "Fake Chat Editor", (int)(width/2.5), fontHeight+2, COLOR_WHITE);
        GlStateManager.popMatrix();

        for (int i = 0; i < 4; i++) {
            String[] strArr = texts[i];
            for (int j = 0; j < strArr.length; j++) {
                drawString(fontRendererObj, strArr[j], scaleX(5 + i*20), (fontHeight+2) * (3 + j), COLOR_WHITE);
            }
        }

        textField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 1){
            TextUtils.sendClientMessage(TextUtils.addRainbowFormat(textField.getText().replace('&','§')).replace("\\n", "\n"));
        }
    }

    @Override
    public void updateScreen() {
        textField.updateCursorCounter();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        textField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == 1) SkyblockImproved.getInstance().fakeChatGuiMsg = textField.getText();
        super.keyTyped(typedChar, keyCode);
        textField.textboxKeyTyped(typedChar, keyCode);
    }

    int scaleX(double x){
        return (int)(x/100 * width);
    }
}
