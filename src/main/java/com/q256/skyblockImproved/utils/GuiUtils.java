package com.q256.skyblockImproved.utils;

import com.q256.skyblockImproved.constants.Rarity;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import java.awt.*;

public class GuiUtils {
    public static final Color slotBackgroundColor = new Color(139, 139, 139);
    /*
    This method doesn't actually draw a translucent rectangle like it's supposed to (I couldn't get that to work),
    but instead simulates what a translucent rectangle would look like assuming the vanilla texture pack.
     */
    public static void drawRarityBackgrounds(GuiContainer container, int backgroundAlpha){
        GlStateManager.colorMask(true, true, true, false);

        for (int i = 0; i < container.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = container.inventorySlots.getSlot(i);
            int x = slot.xDisplayPosition;
            int y = slot.yDisplayPosition;

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

                int red = slotBackgroundColor.getRed() + (itemRarity.getColor().getRed() - slotBackgroundColor.getRed()) * backgroundAlpha/255;
                int green = slotBackgroundColor.getGreen() + (itemRarity.getColor().getGreen() - slotBackgroundColor.getGreen()) * backgroundAlpha/255;
                int blue = slotBackgroundColor.getBlue() + (itemRarity.getColor().getBlue() - slotBackgroundColor.getBlue()) * backgroundAlpha/255;

                Gui.drawRect(x, y, x + 16, y + 16, (255<<24)+(red<<16)+(green<<8)+(blue));
            } catch (NullPointerException ignored) {

            }
        }

        GlStateManager.colorMask(true, true, true, true);
    }
}
