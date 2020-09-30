package com.q256.skyblockImproved.api;

import com.q256.skyblockImproved.constants.PetItem;
import com.q256.skyblockImproved.constants.Rarity;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class Pet implements Comparable<Pet>{
    Rarity rarity;
    String name;
    int lvl;
    int xp;
    int extraXp;
    int xpUntilNextLvl;
    String heldItemId;
    PetItem heldItem;
    boolean active;
    int candyUsed;

    public String getRarityColor(){
        String rarityColor;
        if(heldItem == PetItem.PET_ITEM_TIER_BOOST) rarityColor = Rarity.values()[rarity.ordinal()+1].getColorCode();
        else rarityColor = rarity.getColorCode();
        return rarityColor;
    }

    public StringBuilder getNameShort(){
        return new StringBuilder(getRarityColor()).append("[").append(lvl).append("] ").append(name);
    }

    public StringBuilder getNameLong(){
        return new StringBuilder ("§7[LVL ").append(lvl).append("] ").append(getRarityColor()).append(name);
    }

    public String getHoverText(){
        StringBuilder hoverText = new StringBuilder(getNameLong());
        hoverText.append("\n\n§e").append(TextUtils.formatNumberLong(extraXp)).append("§6/§e").append(TextUtils.formatNumberShort(xpUntilNextLvl)).append(" §7Exp");
        if(candyUsed!=0)hoverText.append("\n\n§a(").append(candyUsed).append("/10) Candy Used");
        if(heldItem!=null){
            hoverText.append("\n\n§6Held Item: ").append(heldItem.getRarity().getColorCode())
                    .append(heldItem.getName());
            ArrayList<String> petItemLines = TextUtils.splitIntoLines(heldItem.getDescription(), Minecraft.getMinecraft().fontRendererObj, 180);
            for(String string:petItemLines){
                hoverText.append('\n').append(string);
            }
        } else if (!heldItemId.equals("null")){
            hoverText.append("\n\n§6Held Item: §f").append(heldItemId);
        }

        return hoverText.toString();
    }

    @Override
    public int compareTo(Pet pet) {
        if(this.rarity.compareTo(pet.rarity)!=0) return this.rarity.compareTo(pet.rarity);
        if(this.xp - pet.xp != 0) return this.xp-pet.xp;
        return this.name.compareTo(pet.name);
    }
}
