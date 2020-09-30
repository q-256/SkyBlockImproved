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

public class PlaySoundCommand extends SubCommand {
    PlaySoundCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "playSound";
        requiresDebugMode = true;
        shortHelpMessage = "§7/sbi playSound <soundName> [volume] [pitch] §f- Plays a sound";
    }

    @Override
    void processCommand(ICommandSender sender, String[] args) {
        if(args.length==1){
            TextUtils.sendClientMessage("§cUsage: "+shortHelpMessage);
            return;
        }

        String soundName = args[1];
        double volume = 1;
        double pitch = 1;
        if(args.length>2){
            try{
                volume = Double.parseDouble(args[2]);
            }catch (NumberFormatException exception){
                TextUtils.sendClientMessage("§cError: §e"+args[2] + "§f is not a valid number");
            }
        }
        if(args.length>3){
            try{
                pitch = Double.parseDouble(args[3]);
            }catch (NumberFormatException exception){
                TextUtils.sendClientMessage("§cError: §e"+args[3] + "§f is not a valid number");
            }
        }

        GeneralUtils.playSound(soundName, volume, pitch);
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2) return GeneralUtils.getReducedTabCompletions(Sounds.ALL_SOUNDS, args[1]);
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi playSound <soundName> [volume] [pitch] §7§m--------" + "\n" +
                "Plays a sound at your current location" + "\n" +
                "§7§m----------------------------------------------------------------");
    }
}
