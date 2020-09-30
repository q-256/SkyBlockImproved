package com.q256.skyblockImproved.constants;

import org.apache.commons.lang3.StringUtils;

public enum Rarity {
    COMMON("§f", 75, 75, 75),
    UNCOMMON( "§a", 45, 255, 45),
    RARE( "§9", 45, 45, 255),
    EPIC( "§5", 160, 0, 160),
    LEGENDARY( "§6", 255, 170, 0),
    MYTHIC("§d", 255, 85, 255),
    SUPREME("§4", 127, 0, 0),
    SPECIAL( "§c", 255, 60, 60),
    VERY_SPECIAL( "§c", 255, 60, 60);

    private final String colorCode;
    private final int colorRGB;
    private final String prettyName;

    Rarity(String colorCode, int r, int g, int b){
        this.colorCode = colorCode;
        this.colorRGB = (r<<16)+(g<<8)+b;
        prettyName = StringUtils.capitalize(this.toString().replace('_',' ').toLowerCase());
    }

    public String getColorCode() { return colorCode; }

    public int getColorRGB(){return colorRGB;}

    public String getPrettyName(){return prettyName;}

    public static Rarity[] getPetRarities(){
        Rarity[] outArray = new Rarity[5];
        System.arraycopy(values(), 0, outArray, 0, 5);
        return outArray;
    }

    public static Rarity getRarityByColor(String colorCode){
        for(Rarity rarity:values()){
            if(rarity.colorCode.equals(colorCode)) return rarity;
        }
        return null;
    }
}
