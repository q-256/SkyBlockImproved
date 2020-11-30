package com.q256.skyblockImproved.overlay;

import com.q256.skyblockImproved.config.Setting;
import net.minecraft.util.ResourceLocation;

public class BatStaffDpsOverlay extends DpsTracker {

    public BatStaffDpsOverlay() {
        super(new ResourceLocation("textures/blocks/flower_allium.png"), 2000, "§e1.92mil§6/641k§d DPS");
    }

    @Override
    public Setting<Boolean> getEnabled() {
        return main.getConfigValues().showBatStaffDpsOverlay;
    }
}
