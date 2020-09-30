package com.q256.skyblockImproved.constants;

import java.util.HashMap;

public class Constants {

    public static final String[] SKILL_NAMES = {"farming", "mining", "combat", "foraging", "fishing", "enchanting", "alchemy", "taming"};
    public static final String[] SKILL_ACHIEVEMENT_NAMES = {"harvester","excavator","combat","gatherer","angler","augmentation","concoctor","domesticator"};
    public static final int[] EXTRA_SKILL_NAME_SPACES = {4,7,5,3,5,0,4,6};
    /**
     * Unique minions required for each minion slot.
     * Doesn't include the first 5 slots as they are free.
     */
    public static final int[] MINION_SLOT_REQS = {5,15,30,50,75,100,125,150,175,200,225,250,275,300,350,400,450,500,550,600};
    public static final int TOTAL_MINIONS = 572;
    public static final String[] REMOVED_ACHIEVEMENT_GAMES = {"bridge"};
    public static final String[] REMOVED_ACHIEVEMENTS = {"blitz_youtuber","arena_utility","walls_canadian_kit","arena_ultimate","copsandcrims_late_to_the_party",
            "vampirez_close_call","buildbattle_buildbattle_points","general_join_vip_lobby","walls3_kill_groopo","walls3_kill_with_groopo","vampirez_herobrine_upgrade",
            "skywars_fortunate","general_buy_golem","general_use_pet","truecombat_wither_slayer","vampirez_wizard","quake_death_chant","blitz_final_destination",
            "blitz_chaotic_madness","blitz_chaos_winner","paintball_killing_spree","general_email","skywars_beastmode","walls3_martyrdom","skywars_disco_play",
            "buildbattle_build_battle_winner","skywars_mega_rich","duels_duels_killer"};
    public static final int[] PET_LEVELS = {100, 110, 120, 130, 145, 160, 175, 190, 210, 230, 250, 275, 300, 330, 360, 400, 440, 490, 540, 600,
            660, 730, 800, 880, 960, 1050, 1150, 1260, 1380, 1510, 1650, 1800, 1960, 2130, 2310, 2500, 2700, 2920, 3160, 3420,
            3700, 4000, 4350, 4750, 5200, 5700, 6300, 7000, 7800, 8700, 9700, 10800, 12000, 13300, 14700, 16200, 17800, 19500, 21300, 23200,
            25200, 27400, 29800, 32400, 35200, 38200, 41400, 44800, 48400, 52200, 56200, 60400, 64800, 69400, 74200, 79200, 84700, 90700, 97200, 104200,
            111700, 119700, 128200, 137200, 146700, 156700, 167700, 179700, 192700, 206700, 221700, 237700, 254700, 272700, 291700, 311700, 333700, 357700, 383700, 411700,
            441700, 476700, 516700, 561700, 611700, 666700, 726700, 791700, 861700, 936700, 1016700, 1101700, 1191700, 1286700, 1386700, 1496700, 1616700, 1746700, 1886700
    };
    public static final int[] PET_RARITY_OFFSETS = {0, 6, 11, 16, 20};
    public static final char[] LIVID_COLORS = {'a','c','d','e','f','2','5','7','9'};
    public static final String[] COLOR_NAMES = {"Black","Dark Blue","Dark Green","Dark Aqua","Dark Red","Purple","Orange","Light Gray","Dark Gray","Blue",
            "Lime","Cyan","Red","Pink","Yellow","White"};
    public static final HashMap<String, String> RANK_PLUS_COLORS = new HashMap<>();
    static {
        RANK_PLUS_COLORS.put("RED","§c");
        RANK_PLUS_COLORS.put("GOLD","§6");
        RANK_PLUS_COLORS.put("GREEN","§a");
        RANK_PLUS_COLORS.put("YELLOW","§e");
        RANK_PLUS_COLORS.put("LIGHT_PURPLE","§d");
        RANK_PLUS_COLORS.put("WHITE","§f");
        RANK_PLUS_COLORS.put("BLUE","§9");
        RANK_PLUS_COLORS.put("DARK_GREEN","§2");
        RANK_PLUS_COLORS.put("DARK_RED","§4");
        RANK_PLUS_COLORS.put("DARK_AQUA","§3");
        RANK_PLUS_COLORS.put("DARK_PURPLE","§5");
        RANK_PLUS_COLORS.put("DARK_GREY","§8");
        RANK_PLUS_COLORS.put("BLACK","§0");
    }
    public static final String[][] TALISMAN_UPGRADES = {
            {"WEDDING_RING_0","WEDDING_RING_1","WEDDING_RING_2","WEDDING_RING_3","WEDDING_RING_4","WEDDING_RING_5",
                    "WEDDING_RING_6","WEDDING_RING_7","WEDDING_RING_8","WEDDING_RING_9"},
            {"CAMPFIRE_TALISMAN_0","CAMPFIRE_TALISMAN_1","CAMPFIRE_TALISMAN_2","CAMPFIRE_TALISMAN_3","CAMPFIRE_TALISMAN_4",
                    "CAMPFIRE_TALISMAN_5","CAMPFIRE_TALISMAN_6","CAMPFIRE_TALISMAN_7","CAMPFIRE_TALISMAN_8","CAMPFIRE_TALISMAN_9",
                    "CAMPFIRE_TALISMAN_10","CAMPFIRE_TALISMAN_11","CAMPFIRE_TALISMAN_12","CAMPFIRE_TALISMAN_13","CAMPFIRE_TALISMAN_14",
                    "CAMPFIRE_TALISMAN_15","CAMPFIRE_TALISMAN_16","CAMPFIRE_TALISMAN_17","CAMPFIRE_TALISMAN_18","CAMPFIRE_TALISMAN_19",
                    "CAMPFIRE_TALISMAN_20","CAMPFIRE_TALISMAN_21","CAMPFIRE_TALISMAN_22","CAMPFIRE_TALISMAN_23","CAMPFIRE_TALISMAN_24",
                    "CAMPFIRE_TALISMAN_25","CAMPFIRE_TALISMAN_26","CAMPFIRE_TALISMAN_27","CAMPFIRE_TALISMAN_28","CAMPFIRE_TALISMAN_29"},
            {"SCARF_STUDIES","SCARF_THESIS","SCARF_GRIMOIRE"},
            {"SPEED_TALISMAN","SPEED_RING","SPEED_ARTIFACT"},
            {"POTION_AFFINITY_TALISMAN","RING_POTION_AFFINITY","ARTIFACT_POTION_AFFINITY"},
            {"FEATHER_TALISMAN","FEATHER_RING","FEATHER_ARTIFACT"},
            {"BAT_TALISMAN","BAT_RING","BAT_ARTIFACT"},
            {"TREASURE_TALISMAN","TREASURE_RING","TREASURE_ARTIFACT"},
            {"RED_CLAW_TALISMAN","RED_CLAW_RING","RED_CLAW_ARTIFACT"},
            {"HEALING_TALISMAN","HEALING_RING"},
            {"SEA_CREATURE_TALISMAN","SEA_CREATURE_RING","SEA_CREATURE_ARTIFACT"},
            {"SPIDER_TALISMAN","SPIDER_RING","SPIDER_ARTIFACT"},
            {"ZOMBIE_TALISMAN","ZOMBIE_RING","ZOMBIE_ARTIFACT"},
            {"SHADY_RING","CROOKED_ARTIFACT","SEAL_OF_THE_FAMILY"},
            {"HUNTER_TALISMAN","HUNTER_RING"},
            {"PERSONAL_COMPACTOR_4000","PERSONAL_COMPACTOR_5000","PERSONAL_COMPACTOR_6000"},
            {"BEASTMASTER_CREST_COMMON","BEASTMASTER_CREST_UNCOMMON","BEASTMASTER_CREST_RARE","BEASTMASTER_CREST_EPIC","BEASTMASTER_CREST_LEGENDARY"},
            {"RAGGEDY_SHARK_TOOTH_NECKLACE","DULL_SHARK_TOOTH_NECKLACE","HONED_SHARK_TOOTH_NECKLACE",
                    "SHARP_SHARK_TOOTH_NECKLACE","RAZOR_SHARP_SHARK_TOOTH_NECKLACE"},
            {"INTIMIDATION_TALISMAN","INTIMIDATION_RING","INTIMIDATION_ARTIFACT"},
            {"WOLF_TALISMAN","WOLF_RING"},
            {"CAT_TALISMAN","LYNX_TALISMAN","CHEETAH_TALISMAN"},
            {"CANDY_TALISMAN","CANDY_RING","CANDY_ARTIFACT"},
            {"BROKEN_PIGGY_BANK","CRACKED_PIGGY_BANK","PIGGY_BANK"}
    };
}
