package com.q256.skyblockImproved.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.List;

abstract class SubCommand {
    String name;
    String shortHelpMessage;
    boolean requiresDebugMode;

    protected SBICommands mainCommand;

    abstract void processCommand(ICommandSender sender, String[] args);
    abstract List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos);
    abstract ChatComponentText getLongHelpMessage();
}
