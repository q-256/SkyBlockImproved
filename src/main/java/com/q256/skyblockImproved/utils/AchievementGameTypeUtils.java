package com.q256.skyblockImproved.utils;

public class AchievementGameTypeUtils {
    private static final String[] dbNames = {"arcade","arena","bedwars","blitz","buildbattle","christmas2017","copsandcrims",
            "duels","easter","general","gingerbread","halloween2017","housing","murdermystery",
            "paintball","pit","quake","skyblock","skyclash","skywars","speeduhc","summer","supersmash",
            "tntgames","truecombat","uhc","vampirez","walls","walls3","warlords"};
    private static final String[] displayNames = {"Arcade","Arena Brawl","BedWars","Blitz SG","Build Battle","Christmas","CvC",
            "Duels","Easter","General","TKR","Halloween","Housing","Murder Mystery",
            "Paintball","Pit","Quake","SkyBlock","SkyClash","SkyWars","Speed UHC","Summer","Smash Heroes",
            "TNT Games","Crazy Walls","UHC","VampireZ","Walls","Mega Walls","Warlords"};

    public static String dbToDisplay(String dbName){
        for(int i=0; i<dbNames.length; i++){
            if(dbNames[i].equals(dbName))return displayNames[i];
        }
        return dbName;
    }

    public static String displayToDb(String displayName){
        for(int i=0; i<displayNames.length; i++){
            if(dbNames[i].equals(displayName))return dbNames[i];
        }
        return displayName;
    }

    public static String[] getDbNames(){return dbNames;}

    public static String[] getDisplayNames(){return displayNames;}
}
