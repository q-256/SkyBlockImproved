package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.config.Setting;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SettingCommand extends SubCommand {
    SkyblockImproved main;

    SettingCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        main= mainCommand.main;
        name = "setting";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi setting [setting] [value] §f- Changes your settings";
    }

    @Override
    void processCommand(ICommandSender sender, String[] args) {
        if(args.length==1){
            StringBuilder stringBuilder = new StringBuilder("§7§m---------- §6Settings §7§m----------");
            for(String settingName:main.getConfigValues().getSettingNames()){
                StringBuilder desc = getSettingDescription(settingName);
                if(desc!=null) stringBuilder.append("\n").append(desc);
            }
            stringBuilder.append("\n§7§m------------------------");
            TextUtils.sendClientMessage(stringBuilder.toString());
        }
        else if(args.length==2){
            StringBuilder desc = getSettingDescription(args[1]);
            if(desc==null) TextUtils.sendClientMessage("Could not find a setting by the name §e"+args[1]);
            else TextUtils.sendClientMessage(desc.toString());
        }
        else {
            Setting<?> setting = main.getConfigValues().getSetting(args[1]);
            if(setting==null) TextUtils.sendClientMessage("Could not find a setting by the name §e"+args[1]);
            else {
                if(setting.getType()==Boolean.class){
                    Setting<Boolean> booleanSetting = (Setting<Boolean>)setting;
                    if(args[2].equalsIgnoreCase("true")){
                        booleanSetting.setValue(true);
                        TextUtils.sendClientMessage("Set §b"+args[1]+"§f to §etrue");
                    }
                    else if(args[2].equalsIgnoreCase("false")){
                        booleanSetting.setValue(false);
                        TextUtils.sendClientMessage("Set §b"+args[1]+"§f to §efalse");
                    }
                    else TextUtils.sendClientMessage("§e"+args[2]+"§f is not a valid §dBoolean");
                }
                else if(setting.getType()==Double.class){
                    Setting<Double> doubleSetting = (Setting<Double>)setting;
                    try{
                        doubleSetting.setValue(Double.valueOf(args[2]));
                        TextUtils.sendClientMessage("Set §b"+args[1]+"§f to §e"+doubleSetting.getValue());
                    } catch (NumberFormatException exception){
                        TextUtils.sendClientMessage("§e"+args[2]+"§f is not a valid §dDouble");
                    }
                }
            }
            main.configHandler.saveFile(true);
        }
    }

    private StringBuilder getSettingDescription(String name){
        StringBuilder stringBuilder = new StringBuilder();
        Setting<?> setting = main.getConfigValues().getSetting(name);
        if(setting==null) return null;
        stringBuilder.append("§b").append(name).append(": §e").append(setting.getValue()).append(" §a-§f ").append(setting.getDescription());
        return stringBuilder;
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2) return GeneralUtils.getReducedTabCompletions(main.getConfigValues().getSettingNames(), args[1]);
        if(args.length==3){
            Setting<?> setting = main.getConfigValues().getSetting(args[1]);
            if(setting!=null){
                if(setting.getType() == Boolean.class) return Arrays.asList("false","true");
            }
        }
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText("" +
                "§7§m---------------§b§l /sbi setting [setting] [value] §7§m---------------" + "\n" +
                "§e/sbi setting §fwill print a list of all settings and their current value" + "\n" +
                "§e/sbi setting <setting> §fwill print the specified setting and its current value" + "\n" +
                "§e/sbi setting <setting> <value> §fwill change the value of the specified setting" + "\n" +
                "Example usage: §e/sbi setting showDungeonItemQuality true" + "\n" +
                "§7§m-------------------------------------------------------------"
        );
    }
}
