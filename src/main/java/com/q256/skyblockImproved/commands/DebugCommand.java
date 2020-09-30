package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

class DebugCommand extends SubCommand{

    DebugCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "debug";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi debug §f- Toggles debug mode";
    }

    @Override
    void processCommand(ICommandSender sender, String[] args){
        mainCommand.main.getConfigValues().debugMode.setValue(!mainCommand.main.getConfigValues().debugMode.getValue());
        TextUtils.sendClientMessage("Debug mode in now " + (mainCommand.main.getConfigValues().debugMode.getValue() ? "enabled" : "disabled"));
        mainCommand.main.configHandler.saveFile(true);
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi debug §7§m--------" + "\n" +
                "Toggles debug mode" +"\n" +
                "Debug mode provides additional commands and info helpful for development" +"\n" +
                "§7§m--------------------------------------"
        );
    }
}
