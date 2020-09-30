package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.constants.Sounds;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StopSoundCommand extends SubCommand {
    StopSoundCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "stopSound";
        requiresDebugMode = true;
        shortHelpMessage = "§7/sbi stopSound §f- Stop all sounds";
    }

    @Override
    void processCommand(ICommandSender sender, String[] args) {
        Minecraft.getMinecraft().getSoundHandler().stopSounds();
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi stopSound §7§m--------" + "\n" +
                "Stops all currently playing sounds" + "\n" +
                "§7§m---------------------------------------");
    }
}
