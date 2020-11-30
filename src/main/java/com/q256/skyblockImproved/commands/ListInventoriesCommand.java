package com.q256.skyblockImproved.commands;

import com.google.gson.JsonObject;
import com.q256.skyblockImproved.api.ApiParser;
import com.q256.skyblockImproved.gui.NonInteractableInventory;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.ItemUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListInventoriesCommand extends SubCommand{

    ListInventoriesCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "listInventories";
        requiresDebugMode = true;
        shortHelpMessage = "§7/sbi listInventories <player> §f- Lists all the SkyBlock inventories of the specified player";
    }


    @Override
    void processCommand(ICommandSender sender, String[] args) {
        if(args.length==1){
            TextUtils.sendClientMessage("§cUsage: "+shortHelpMessage);
            return;
        }

        if(!GeneralUtils.isUserNameValid(args[1])){
            TextUtils.sendClientMessage("§cInvalid username: §f"+args[1]);
            return;
        }
        Thread thread = new Thread(() -> {
                //get inventory data from Hypixel API
                JsonObject playerJson = mainCommand.main.apiHandler.getPlayer(args[1]);
                if(playerJson==null){
                    TextUtils.sendClientMessage("Could not find player: §e"+args[1]);
                    return;
                }
                JsonObject sbPlayerProfile = mainCommand.main.apiHandler.getSbPlayerProfile(playerJson, args[1], playerJson.get("uuid").getAsString());
                String playerDisplayName = playerJson.get("displayname").getAsString();
                if(sbPlayerProfile==null){
                    TextUtils.sendClientMessage("Could not find any Skyblock profiles for player: §e"+playerDisplayName);
                    return;
                }

                ChatComponentText lineSeparator = new ChatComponentText("§7§m----------------------------------");
                ArrayList<IChatComponent> messagesToSend = new ArrayList<>();
                messagesToSend.add(lineSeparator);
                messagesToSend.add(new ChatComponentText(playerDisplayName+"'s inventories:"));
                messagesToSend.addAll(ApiParser.getInventoryMessages(sbPlayerProfile, args[1]));
                messagesToSend.add(lineSeparator);
                TextUtils.sendClientMessages(messagesToSend);
        });
        thread.start();
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2) return GeneralUtils.getReducedTabCompletions(GeneralUtils.getPlayersForTabCompletion(), args[1], false);
        return new ArrayList<>();
    }

    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l §7/sbi listInventories <player>§7§m--------" + "\n" +
                "Lists all the SkyBlock inventories of the specified player" +"\n" +
                "The main purpose of this command is to be run whenever you click the \"Show All Inventories\" text after using §e/sbi stats§f" +"\n" +
                "§7§m-------------------------------------------------"
        );
    }
}
