package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

public class ReloadConfigCommand extends SubCommand{

    ReloadConfigCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "reloadConfig";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi reloadConfig §f- Attempts to reload the config file";
    }

    @Override
    void processCommand(ICommandSender sender, String[] args) {
        if(mainCommand.main.configHandler.loadFile()) TextUtils.sendClientMessage("Successfully reloaded the config file");
        else TextUtils.sendClientMessage("Failed to reload the config file");
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi reloadConfig §7§m--------" + "\n" +
                "Attempts to reload the config file" +"\n" +
                "§7§m--------------------------------------"
        );
    }
}
