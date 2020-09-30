package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentScore;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;

class StatsCommand extends SubCommand{

    StatsCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "stats";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi stats <name> §f- Prints the stats of the given player";
    }

    @Override
    void processCommand(ICommandSender sender, String[] args){
        if(args.length==1){
            TextUtils.sendClientMessage("§cUsage: "+shortHelpMessage);
            return;
        }
        if(mainCommand.main.getConfigValues().apiKey == null){
            TextUtils.sendClientMessage("You do not have an API key set. Please type §e/sbi help apiKey §fto see how to set your API key.");
            return;
        }
        for(int i=1; i<args.length; i++){
            if (!GeneralUtils.isUserNameValid(args[1])) {
                TextUtils.sendClientMessage("§cInvalid username: §f"+args[i]);
            } else {
                final int iCopy = i;
                Thread playerInfoPrinter = new Thread(() -> mainCommand.main.apiHandler.printPlayerInfo(args[iCopy]));
                playerInfoPrinter.start();
            }
        }
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2) return GeneralUtils.getReducedTabCompletions(GeneralUtils.getPlayersForTabCompletion(), args[1], false);
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi stats <name> §7§m--------" + "\n" +
                "Prints the stats of the given player" +"\n" +
                "A lot of the printed text is hoverable, meaning you can hover over it for extra information" +"\n" +
                "Example usage: §e/sbi stats q256" +"\n" +
                "§7§m-----------------------------------------------------------"
        );
    }
}
