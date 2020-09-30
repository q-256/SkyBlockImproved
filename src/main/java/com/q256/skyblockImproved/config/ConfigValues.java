package com.q256.skyblockImproved.config;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.utils.TextUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ConfigValues {
    private final transient SkyblockImproved main = SkyblockImproved.getInstance();

    private HashMap<String, Object> settingValues = new HashMap<>();

    public UUID apiKey;

    public transient Setting<Boolean> showDungeonItemQuality = new Setting<>("Show dungeon item quality",true, "Shows dungeon item quality when hovering over a dungeon item",
            "General");
    public transient Setting<Boolean> showExpertiseKillCount = new Setting<>("Show expertise kill count",true,
            "Shows number of sea creatures killed by a fishing rod with the expertise enchant", "General");
    public transient Setting<Boolean> hideMagicResistMessages = new Setting<>("Hide magic resist messages", false, "Hides any messages that say "+
            "\"§cThis creature is immune to this kind of magic!§f\"");
    public transient Setting<Boolean> playSoundOnMagicResist = new Setting<>("Play sound on magic resist",false, "Plays a sound when an opponent resists your magic damage",
            "General");
    public transient Setting<Double> magicResistSoundVolume = new Setting<>("Magic resist sound volume", 1.0, "Determines the volume of the sound that gets played " +
            "when an opponent resists your magic damage (if enabled)");
    public transient Setting<Boolean> showLividDisplay = new Setting<>("Show correct Livid color", false,
            "Tells you what colored Livid is the real one", "General");
    public transient Setting<Boolean> hideBatStaffHitMessages = new Setting<>("Hide spirit sceptre hit messages", false,
            "Hides messages that tell you how many opponents your spirit sceptre hit", "General");

    //Re-add this if you can properly increase the chat character limit from 100 to 256
    /*
    public transient Setting<Boolean> longChat = new Setting<>("Higher chat character limit", false, "Increases the number of characters you can type in chat from 100 to 256. " +
            "May not work on other servers");
    */

    public transient Setting<Boolean> printCommandHelpOnBaseCommand = new Setting<>("Print command help on base command",true,
            "Prints a list of available commands when using §e/sbi", "/sbi command");

    public transient Setting<Boolean> showRarityBackground = new Setting<>("Show rarity background",true,
            "Shows colored backgrounds when using the §e/sbi viewInventory§f command", "/sbi viewInventory command");
    public transient Setting<Double> rarityBackgroundAlpha = new Setting<>("Rarity background intensity",0.5,
            "Determines the intensity of the background colors when using the §e/sbi viewInventory§f command §7(0-1)", "/sbi viewInventory command");

    public transient Setting<Boolean> showSkyblockInfo = new Setting<>("Show Skyblock info",true, "Shows Skyblock info when using the §e/sbi stats §fcommand",
            "/sbi stats command");
    public transient Setting<Boolean> showSessionInfo = new Setting<>("Show session info",false, "Shows session info when using the §e/sbi stats §fcommand",
            "/sbi stats command");
    public transient Setting<Boolean> showLegacyAchievements = new Setting<>("Show legacy achievements",false, "Shows legacy achievements when using the §e/sbi stats §fcommand",
            "/sbi stats command");
    public transient Setting<Boolean> showAllPets = new Setting<>("List all pets",false, "Lists all pets when using the §e/sbi stats §fcommand",
            "/sbi stats command");
    public transient Setting<Boolean> showAllInventories = new Setting<>("List all inventories",false, "Lists all inventories when using the §e/sbi stats §fcommand",
            "/sbi stats command");

    public transient Setting<Boolean> debugMode = new Setting<>("Debug mode",false, "Provides additional tools for development", "Misc");

    public ArrayList<String> getSettingNames(){
        Field[] fields = this.getClass().getDeclaredFields();
        ArrayList<String> outAl = new ArrayList<>();
        for(Field field:fields){
            if(field.getType() == Setting.class) outAl.add(field.getName());
        }
        return outAl;
    }

    public Setting<?> getSetting(String name){
        try {
            return (Setting<?>)this.getClass().getDeclaredField(name).get(this);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException exception){
            return null;
        }
    }

    /**
     * Always call this immediately after deserialization!
     */
    public void postDeserialization(){
        for(Map.Entry<String, Object> entry:settingValues.entrySet()){
            Setting setting = getSetting(entry.getKey());
            try {
                setting.setValue((entry.getValue()));
            } catch (ClassCastException exception){
                exception.printStackTrace();
                TextUtils.sendClientMessage("Failed to load "+entry.getKey()+" setting, using default value instead");
            }
        }
    }

    /**
     * Always call this immediately before serialization!
     */
    public void preSerialization(){
        settingValues.clear();
        for(String settingName:getSettingNames()){
            Setting<?> setting = getSetting(settingName);
            settingValues.put(settingName, setting.getValue());
        }
    }
}
