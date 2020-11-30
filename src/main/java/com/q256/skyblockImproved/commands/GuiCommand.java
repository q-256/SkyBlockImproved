package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.gui.*;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentScore;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.ArrayList;
import java.util.List;

class GuiCommand extends SubCommand {
    ArrayList<String> tabCompletions = new ArrayList<>();

    GuiCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "gui";
        requiresDebugMode = true;
        shortHelpMessage = "§7/sbi stats <name> §f- Opens the specified GUI";

        tabCompletions.add("main");
        tabCompletions.add("settings");
        tabCompletions.add("overlayEdit");
        tabCompletions.add("fakeChat");
    }

    @Override
    void processCommand(ICommandSender sender, String[] args){
        if(args.length==1){
            mainCommand.main.openGuiScreen = new SkyblockImprovedGui();
            return;
        }
        switch (args[1].toLowerCase()){
            case "main":
                mainCommand.main.openGuiScreen = new SkyblockImprovedGui();
                break;
            case "settings":
                mainCommand.main.openGuiScreen = new SettingsGui();
                break;
            case "overlayedit":
                mainCommand.main.openGuiScreen = new OverlayEditGui();
                break;
            case "fakechat":
                mainCommand.main.openGuiScreen = new FakeChatGui();
                break;
            default:
                TextUtils.sendClientMessage("§cUnknown GUI type");
        }
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2) return GeneralUtils.getReducedTabCompletions(tabCompletions, args[1], false);
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi gui [guiType] §7§m--------" + "\n" +
                "Opens the specified GUI" +"\n" +
                "§7§m-----------------------------------------------------------"
        );
    }
}
