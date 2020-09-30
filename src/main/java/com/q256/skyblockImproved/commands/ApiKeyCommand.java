package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ApiKeyCommand extends SubCommand {

    ApiKeyCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "apiKey";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi apiKey [key] §f- Sets your API key";

    }
    
    @Override
    void processCommand(ICommandSender sender, String[] args) {
        if(args.length==1){
            UUID apiKey = mainCommand.main.getConfigValues().apiKey;
            if(apiKey==null) TextUtils.sendClientMessage("§aYou don't have an API key set." + "\n" +
                    "§fTip: type §b/api new §fwhile playing on Hypixel to generate a new API key.");
            else{
                ChatComponentText cct = new ChatComponentText("Your current API key is: §e"+apiKey.toString());
                cct.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to copy")));
                cct.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, apiKey.toString()));
                TextUtils.sendClientMessage(cct);
            }
            return;
        }
        try{
            mainCommand.main.getConfigValues().apiKey = UUID.fromString(args[1]);
            mainCommand.main.apiHandler.refreshApiKey();
            TextUtils.sendClientMessage("§aSuccessfully set your API key to §e"+ mainCommand.main.getConfigValues().apiKey.toString());
            mainCommand.main.configHandler.saveFile(true);
        } catch (IllegalArgumentException exception){
            TextUtils.sendClientMessage("§cCould not set your API key. You entered an invalid UUID" + "\n" +
                    "§cKeys are supposed to be of format §exxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" + "\n" +
                    "§fTip: type §b/api new §fwhile playing on Hypixel to generate a new API key.");
        }
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi apiKey [key] §7§m--------" + "\n" +
                "Sets or gets you API key." +"\n" +
                "Use §e/sbi apiKey §fto print the key you currently have set." +"\n" +
                "You can click the text to copy it." + "\n" +
                "Use §e/sbi apiKey <key> §fto set your key for the Hypixel API." +"\n" +
                "You can type §e/api new §fwhile playing on Hypixel to generate a new API key." + "\n" +
                "This key should be of format §exxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" + "\n" +
                "This key will be used to access the Hypixel API whenever you use the §e/sbi stats §fcommand." + "\n" +
                "§7§m-----------------------------------"
        );
    }
}
