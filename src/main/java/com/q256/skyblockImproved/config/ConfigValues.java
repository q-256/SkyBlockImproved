package com.q256.skyblockImproved.config;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.overlay.*;
import com.q256.skyblockImproved.utils.TextUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ConfigValues {
    private final transient SkyblockImproved main = SkyblockImproved.getInstance();

    public UUID apiKey;

    private HashMap<String, Object> settingValues = new HashMap<>();

    //settings

    public transient Setting<Boolean> compactWithermancerMessages = new Setting<>("Compact Withermancer chat messages", false,
            "When you get hit by multiple withermancer skulls at the same time, you will only receive one chat message.", "General");
    public transient Setting<Boolean> countWithermancerSkulls = new Setting<>("Count withermancer skulls", false,
            "Counts how many withermancer skulls there are in a general area and displays a name tag above them.", "General");

    public transient Setting<Boolean> showDungeonItemQuality = new Setting<>("Show dungeon item quality",true,
            "Shows dungeon item quality when hovering over a dungeon item", "Tooltips");
    public transient Setting<Boolean> showExpertiseKillCount = new Setting<>("Show expertise kill count",true,
            "Shows number of sea creatures killed by a fishing rod with the expertise enchant", "Tooltips");
    public transient Setting<Boolean> showFelSwordKillCount = new Setting<>("Show fel sword kill count",true,
            "Shows number of enemies killed by a fel sword", "Tooltips");
    //Re-add this if you can properly increase the chat character limit from 100 to 256
    /*
    public transient Setting<Boolean> longChat = new Setting<>("Higher chat character limit", false, "Increases the number of characters you can type in chat from 100 to 256. " +
            "May not work on other servers");
    */

    public transient Setting<Boolean> showLividMessage = new Setting<>("Show correct Livid color", false,
            "Tells you what colored Livid is the real one", "Dungeon boss fights");

    public transient Setting<Boolean> hideMagicResistMessages = new Setting<>("Hide magic resist messages", false, "Hides any messages that say "+
            "\"§cThis creature is immune to this kind of magic!§f\"", "Abilities");
    public transient Setting<Boolean> playSoundOnMagicResist = new Setting<>("Play sound on magic resist",false,
            "Plays a sound when an opponent resists your magic damage", "Abilities");
    public transient Setting<Double> magicResistSoundVolume = new Setting<>("Magic resist sound volume", 1.0,
            "Determines the volume of the sound that gets played when an opponent resists your magic damage (if enabled)", "Abilities");
    public transient Setting<Boolean> hideBatStaffHitMessages = new Setting<>("Hide spirit sceptre hit messages", false,
            "Hides messages that tell you how many opponents your spirit sceptre hit", "Abilities");
    public transient Setting<Boolean> playSoundOnBatStaffHit = new Setting<>("Play sound on spirit sceptre hit", false,
            "Plays a sound whenever your spirit sceptre hits an opponent", "Abilities");
    public transient Setting<Double> batStaffHitVolume = new Setting<>("Spirit sceptre hit volume", 1.0,
            "Determines the volume of the sound that gets played when an your spirit sceptre hits an opponent (if enabled)", "Abilities");
    public transient Setting<Boolean> hideMidasStaffHitMessages = new Setting<>("Hide Midas Staff hit messages", false,
            "Hides messages that tell you how many opponents your Midas Staff hit", "Abilities");
    public transient Setting<Boolean> playSoundOnMidasStaffHit = new Setting<>("Play sound on Midas Staff hit", false,
            "Plays a sound whenever your Midas Staff hits an opponent", "Abilities");
    public transient Setting<Double> midasStaffHitVolume = new Setting<>("Midas Staff hit volume", 1.0,
            "Determines the volume of the sound that gets played when an your Midas Staff hits an opponent (if enabled)", "Abilities");
    public transient Setting<Boolean> hideImplosionHitMessages = new Setting<>("Hide Implosion hit messages", false,
            "Hides messages that tell you how many opponents your Necron's Sword's Implosion ability hit", "Abilities");
    public transient Setting<Boolean> playSoundOnImplosionHit = new Setting<>("Play sound on Implosion hit", false,
            "Plays a sound whenever your Necron's Sword's Implosion ability hits an opponent", "Abilities");
    public transient Setting<Double> implosionHitVolume = new Setting<>("Implosion hit volume", 1.0,
            "Determines the volume of the sound that gets played when an your Necron's Sword's Implosion ability hits an opponent (if enabled)", "Abilities");

    public transient Setting<Boolean> printCommandHelpOnBaseCommand = new Setting<>("Print command help on base command",true,
            "Prints a list of available commands when using §e/sbi", "/sbi command");

    public transient Setting<Boolean> showRarityInCustomInv = new Setting<>("Show rarity background in custom inventories",true,
            "Shows colored backgrounds when viewing others' inventories", "Inventories");
    public transient Setting<Double> customInvRarityAlpha = new Setting<>("Rarity background intensity in custom inventories",0.5,
            "Determines the intensity of the rarity background colors when viewing others' inventories", "Inventories");
    public transient Setting<Boolean> showRarityInAllInv = new Setting<>("Show rarity background in all inventories",false,
            "Shows colored backgrounds when in any inventory", "Inventories");
    public transient Setting<Double> allInvRarityAlpha = new Setting<>("Rarity background intensity in all inventories",0.5,
            "Determines the intensity of the rarity background colors when in any inventory", "Inventories");

    public transient Setting<Boolean> showSkyblockInfo = new Setting<>("Show Skyblock info",true, "Shows Skyblock info when using the §e/sbi stats §fcommand",
            "/sbi stats command");
    public transient Setting<Boolean> showSessionInfo = new Setting<>("Show session info",false, "Shows session info when using the §e/sbi stats §fcommand",
            "/sbi stats command");
    public transient Setting<Boolean> showLegacyAchievements = new Setting<>("Show legacy achievements",false,
            "Shows legacy achievements when using the §e/sbi stats §fcommand", "/sbi stats command");
    public transient Setting<Boolean> showAllPets = new Setting<>("List all pets",false, "Lists all pets when using the §e/sbi stats §fcommand",
            "/sbi stats command");
    public transient Setting<Boolean> showAllInventories = new Setting<>("List all inventories",false, "Lists all inventories when using the §e/sbi stats §fcommand",
            "/sbi stats command");

    //overlay settings
    public transient Setting<Boolean> showSadanGiantOverlay = new Setting<>("Show Sadan giant overlay", false,
            "Shows an overlay that tells you the health of Sadan's giants", "Overlays");
    public transient Setting<Boolean> showBatStaffDpsOverlay = new Setting<>("Show spirit scetre DPS overlay", false,
            "Shows an overlay that tells you how much DPS you are doing with your spirit sceptre. " +
                    "The first number in the overlay tells you how much total DPS you are doing, " +
                    "and the second number tells you how much DPS you are doing per enemy. " +
                    "For example, if the overlay says §e1.92mil§6/641k§d DPS§r, then you are doing a total of " +
                    "1.92mil DPS and you are hitting about 3 opponents.", "Overlays");
    public transient Setting<Boolean> showMidasStaffDpsOverlay = new Setting<>("Show Midas Staff DPS overlay", false,
            "Shows an overlay that tells you how much DPS you are doing with your Midas Staff. " +
                    "The first number in the overlay tells you how much total DPS you are doing, " +
                    "and the second number tells you how much DPS you are doing per enemy. " +
                    "For example, if the overlay says §e1.92mil§6/641k§d DPS§r, then you are doing a total of " +
                    "1.92mil DPS and you are hitting about 3 opponents.", "Overlays");
    public transient Setting<Boolean> showImplosionDpsOverlay = new Setting<>("Show Implosion DPS overlay", false,
            "Shows an overlay that tells you how much DPS you are doing with your Necron's Sword's Implosion ability. " +
                    "The first number in the overlay tells you how much total DPS you are doing, " +
                    "and the second number tells you how much DPS you are doing per enemy. " +
                    "For example, if the overlay says §e1.92mil§6/641k§d DPS§r, then you are doing a total of " +
                    "1.92mil DPS and you are hitting about 3 opponents.", "Overlays");

    public transient Setting<Boolean> debugMode = new Setting<>("Debug mode",false, "Provides additional tools for development", "Misc");

    //overlays
    public final SadanGiantOverlay sadanGiantOverlay = new SadanGiantOverlay();
    public final BatStaffDpsOverlay batStaffDpsOverlay = new BatStaffDpsOverlay();
    public final MidasStaffDpsOverlay midasStaffDpsOverlay = new MidasStaffDpsOverlay();
    public final ImplosionDpsOverlay implosionDpsOverlay = new ImplosionDpsOverlay();

    private transient List<Overlay> overlays = new ArrayList<>();

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
            } catch (NullPointerException exception){
                exception.printStackTrace();
                TextUtils.sendClientMessage("Setting "+entry.getKey()+" does not exist");
            }
        }

        Field[] fields = this.getClass().getDeclaredFields();
        for(Field field:fields){
            try {
                if(field.get(this) instanceof Overlay) overlays.add(((Overlay) field.get(this)));
            } catch (IllegalAccessException e) {
                //this line should never be reached
                e.printStackTrace();
            }
        }
        overlays = Collections.unmodifiableList(overlays);
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

    public List<Overlay> getOverlays() {
        return overlays;
    }
}
