package com.q256.skyblockImproved.overlay;

import com.q256.skyblockImproved.config.Setting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ImplosionDpsOverlay extends DpsTracker {
    public ImplosionDpsOverlay() {
        super(new ResourceLocation("textures/items/book_writable.png"), 2000, "§e1.92mil§6/641k§d DPS");
    }

    @Override
    public Setting<Boolean> getEnabled() {
        return main.getConfigValues().showImplosionDpsOverlay;
    }
}
