package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScoreboardCommand extends SubCommand{

    ScoreboardCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "scoreboard";
        requiresDebugMode = true;
        shortHelpMessage = "§7/sbi scoreboard [keepColors] §f- Prints the current scoreboard";
    }


    @Override
    void processCommand(ICommandSender sender, String[] args) {
        boolean keepColors = true;
        if(args.length>1 && "false".equalsIgnoreCase(args[1])) keepColors = false;
        TextUtils.sendClientMessage("Scoreboard:");
        for(String ss: GeneralUtils.getScoreboardText()){
            if(keepColors) TextUtils.sendClientMessage(ss + "END");
            else TextUtils.sendClientMessage(ss.replace('§','&') + "END");
        }
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2) return Arrays.asList("false","true");
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi scoreboard [keepColors] §7§m--------" + "\n" +
                "Prints the current scoreboard" +"\n" +
                "The word §e\"END\"§f will mark the end of each line" + "\n" +
                "§ekeepColors §fdetermines whether color codes will be printed as normal or will be replaced by '&'" +"\n" +
                "§7§m-------------------------------------------------"
        );
    }
}
