package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

class HelpCommand extends SubCommand{

    HelpCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "help";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi help [command] §f- Prints additional info about the specified command";

    }

    @Override
    void processCommand(ICommandSender sender, String[] args) {
        if(args.length==1 || args[1].equals("")){
            TextUtils.sendClientMessage(mainCommand.getCommandUsage(sender));
            args = new String[]{args[0], "help"};
        }

        SubCommand subCommand = mainCommand.getSubCommandByName(args[1]);
        if(subCommand==null){
            TextUtils.sendClientMessage("§cCouldn't find a command by the name §e/sbi "+args[1]);
            return;
        }

        TextUtils.sendClientMessage(subCommand.getLongHelpMessage());
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2) {
            ArrayList<String> commandList = new ArrayList<>();
            for (SubCommand subCommand : mainCommand.subCommands) {
                if(!subCommand.requiresDebugMode || mainCommand.main.getConfigValues().debugMode.getValue())commandList.add(subCommand.name);
            }
            return GeneralUtils.getReducedTabCompletions(commandList, args[1]);
        }
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi help [command] §7§m--------" + "\n" +
                "Prints additional info about the specified command" +"\n" +
                "Example: §e/sbi help stats §fwill print info about the §estats §fcommand" +"\n" +
                "§7§m-------------------------------------------"
        );
    }
}
