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

public class PartyListCommand extends SubCommand{

    PartyListCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "partyList";
        requiresDebugMode = true;
        shortHelpMessage = "§7/sbi partyList §f- Prints the list of currently tracked party members";
    }


    @Override
    void processCommand(ICommandSender sender, String[] args) {
        StringBuilder stringBuilder = new StringBuilder("§bParty members: §f");
        for(String string:mainCommand.main.partyHandler.getPartyMembers()){
            stringBuilder.append(string).append(", ");
        }
        if(stringBuilder.length()>1) stringBuilder.deleteCharAt(stringBuilder.length()-2);
        TextUtils.sendClientMessage(stringBuilder.toString());
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi partyList §7§m--------" + "\n" +
                "Prints the list of currently tracked party members" +"\n" +
                "§7§m-------------------------------------------------"
        );
    }
}
