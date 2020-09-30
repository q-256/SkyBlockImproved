package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;

import java.util.ArrayList;
import java.util.List;

class FakeChatCommand extends SubCommand {

    FakeChatCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "fakeChat";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi fakeChat <message> §f- Sends a fake chat message only you can see";
    }

    @Override
    void processCommand(ICommandSender sender, String[] args) {
        StringBuilder message = new StringBuilder();
        for(int i=1; i<args.length; i++){
            message.append(args[i]);
            message.append(" ");
        }
        TextUtils.sendClientMessage(TextUtils.addRainbowFormat(message.toString().replace('&','§')));
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        ChatComponentText cct = new ChatComponentText(""+
                "§7§m----------§b§l /sbi fakeChat <message> §7§m--------" + "\n" +
                "Sends a fake chat message only you can see" +"\n" +
                "You may use the §e&§f symbol for color codes" +"\n" +
                "You can find a list of default color codes at:" +"\n");
        ChatComponentText cct2 = new ChatComponentText(
                "§3§nhttps://minecraft.gamepedia.com/Formatting_codes#Color_codes" +"\n");
        cct2.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://minecraft.gamepedia.com/Formatting_codes#Color_codes"));
        ChatComponentText cct3 = new ChatComponentText(
                "You may use §e&w§f as a rainbow color code" +"\n" +
                "Example: §e/sbi fakeChat &b&lHello, &6this &2text is &wcolorful &kkkkk" +"\n" +
                TextUtils.addRainbowFormat("Will output: §b§lHello, §6this §2text is §wcolorful §kkkkk") +"\n" +
                "§7§m--------------------------------------------"
        );

        cct.appendSibling(cct2).appendSibling(cct3);
        return cct;
    }
}
