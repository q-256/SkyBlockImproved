package com.q256.skyblockImproved.gui;

import com.q256.skyblockImproved.constants.Rarity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * An inventory which the user cannot interact with.
 * Useful for displaying inventories gotten from the Hypixel API
 */
public class NonInteractableInventory extends GuiChest {
    boolean showRarityBackground;
    int backgroundAlpha;

    /**
     *
     * @param inventory The inventory to be displayed.
     * @param showRarityBackground Whether items with rarities should get a colored background.
     * @param backgroundAlpha How much alpha color these backgrounds should have.
     */
    public NonInteractableInventory(IInventory inventory, boolean showRarityBackground, int backgroundAlpha) {
        super(Minecraft.getMinecraft().thePlayer.inventory, inventory);
        allowUserInput = true;
        this.showRarityBackground = showRarityBackground;
        this.backgroundAlpha = backgroundAlpha;
    }


    //The next three methods are what make this inventory "non-interactable"
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton){

    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(Keyboard.KEY_ESCAPE == keyCode || keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()){
            Minecraft.getMinecraft().thePlayer.closeScreenAndDropStack();
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {

    }
    


    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        if(showRarityBackground) {
            GlStateManager.enableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);

            for (int i = 0; i < inventorySlots.inventorySlots.size(); i++) {
                Slot slot = inventorySlots.getSlot(i);
                int x = slot.xDisplayPosition + guiLeft;
                int y = slot.yDisplayPosition + guiTop;

                ItemStack itemStack = slot.getStack();
                try {
                    NBTTagList lore = itemStack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                    String lastLoreLine = lore.getStringTagAt(lore.tagCount() - 1);
                    Rarity itemRarity = null;

                    //Note that this only works because unCOMMON is tested after COMMON and very_SPECIAL is tested after SPECIAL
                    //This section of code may also work incorrectly if the item in question is bugged and has no rarity
                    for (Rarity rarity : Rarity.values()) {
                        if (lastLoreLine.contains(rarity.toString().replace('_', ' '))) {
                            itemRarity = rarity;
                        }
                    }
                    if (itemRarity == null) continue;

                    drawRect(x, y, x + 16, y + 16, (backgroundAlpha << 24) + itemRarity.getColorRGB());
                } catch (NullPointerException ignored) {

                }
            }

            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }
}
