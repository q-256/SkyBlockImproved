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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewInventoryCommand extends SubCommand {
    ViewInventoryCommand(SBICommands sbiCommands){
        mainCommand = sbiCommands;
        name = "viewInventory";
        requiresDebugMode = false;
        shortHelpMessage = "§7/sbi viewInventory <playerName> [inventoryType] [page] §f- View a given player's Skyblock inventories";
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
            String type = "inv_contents";
            String inventoryDisplayName = "Inventory";

            //get inventory type
            if(args.length>2){
                switch (args[2].toLowerCase()){
                    case "inventory":
                        type = "inv_contents";
                        inventoryDisplayName = "Inventory";
                        break;
                    case "enderchest":
                        type = "ender_chest_contents";
                        inventoryDisplayName = "Ender Chest";
                        break;
                    case "talismanbag":
                        type = "talisman_bag";
                        inventoryDisplayName = "Accessory Bag";
                        break;
                    case "potionbag":
                        type = "potion_bag";
                        inventoryDisplayName = "Potion Bag";
                        break;
                    case "fishbag":
                        type = "fishing_bag";
                        inventoryDisplayName = "Fishing Bag";
                        break;
                    case "quiver":
                        type = "quiver";
                        inventoryDisplayName = "Quiver";
                        break;
                    case "wardrobe":
                        type = "wardrobe_contents";
                        inventoryDisplayName = "Wardrobe";
                        break;
                    case "vault":
                        type = "personal_vault_contents";
                        inventoryDisplayName = "Vault";
                        break;
                    default: TextUtils.sendClientMessage("Could not find an inventory type by the name §e"+args[2]);
                }
            }

            //get page
            int page = 1;
            if(args.length>3){
                try{
                    page = Integer.parseInt(args[3]);
                } catch (NumberFormatException exception){
                    TextUtils.sendClientMessage("§e"+args[3]+"§f is not a valid page number");
                }
                if(page<1){
                    TextUtils.sendClientMessage("Page numbers must be positive");
                    page = 1;
                }
            }

            try {
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
                ArrayList<NBTTagCompound> nbtCompounds = ApiParser.getSbInvNBT(sbPlayerProfile, type);
                if(nbtCompounds==null){
                    TextUtils.sendClientMessage("This player doesn't have any inventory data");
                    return;
                }

                //create the inventory
                InventoryBasic inventory;
                switch (type){
                    case "inv_contents":
                        inventory = new InventoryBasic(playerDisplayName+"'s "+inventoryDisplayName, true, 45);
                        for(int ii=0; ii<nbtCompounds.size(); ii++){
                            NBTTagCompound nbtCompound = nbtCompounds.get(ii);
                            if(ii<9)inventory.setInventorySlotContents(ii+36, ItemStack.loadItemStackFromNBT(nbtCompound));
                            else inventory.setInventorySlotContents(ii-9, ItemStack.loadItemStackFromNBT(nbtCompound));
                        }
                        for(int ii=27; ii<36; ii++){
                            inventory.setInventorySlotContents(ii, ItemUtils.getFillerGlassPane());
                        }
                        break;
                    case "ender_chest_contents":
                        int inventorySlots = Math.max(Math.min(nbtCompounds.size()-(page-1)*45, 45), 0);
                        inventory = new InventoryBasic(playerDisplayName+"'s "+inventoryDisplayName+" Page "+page, true, inventorySlots);
                        for(int ii=0; ii<inventorySlots; ii++){
                            NBTTagCompound nbtCompound = nbtCompounds.get(ii+(page-1)*45);
                            inventory.setInventorySlotContents(ii, ItemStack.loadItemStackFromNBT(nbtCompound));
                        }
                        break;
                    case "wardrobe_contents":
                        int inventorySlots1 = Math.max(Math.min(nbtCompounds.size() - (page - 1) * 36, 36), 0);
                        inventory = new InventoryBasic(playerDisplayName+"'s "+inventoryDisplayName+" Page "+page, true, inventorySlots1);
                        for(int ii=0; ii<inventorySlots1; ii++){
                            NBTTagCompound nbtCompound = nbtCompounds.get(ii+(page-1)*36);
                            inventory.setInventorySlotContents(ii, ItemStack.loadItemStackFromNBT(nbtCompound));
                        }
                        break;
                    default:
                        inventory = new InventoryBasic(playerDisplayName+"'s "+inventoryDisplayName, true, (int)Math.ceil(nbtCompounds.size()/9.0)*9);
                        for(int ii=0; ii< nbtCompounds.size(); ii++){
                            NBTTagCompound nbtCompound = nbtCompounds.get(ii);
                            //admins changed what the API returns so this is no longer necessary
                            /*
                            if(ii>=nbtCompounds.size()-9 && nbtCompounds.get(ii).hasNoTags()){
                                if(ii== nbtCompounds.size()-5) inventory.setInventorySlotContents(ii, ItemUtils.getCloseBarrier());
                                else inventory.setInventorySlotContents(ii, ItemUtils.getFillerGlassPane());
                            }
                            else */
                            inventory.setInventorySlotContents(ii, ItemStack.loadItemStackFromNBT(nbtCompound));
                        }
                        break;
                }

                mainCommand.main.openGuiScreen = new NonInteractableInventory(inventory, mainCommand.main.getConfigValues().showRarityBackground.getValue(),
                        (int)(mainCommand.main.getConfigValues().rarityBackgroundAlpha.getValue()*255));
            } catch (IOException exception){
                exception.printStackTrace();
                TextUtils.sendClientMessage("Something went wrong while parsing inventory data");
            }
        });
        thread.start();
    }

    @Override
    List<String> tabCompletions(ICommandSender sender, String[] args, BlockPos pos) {
        if(args.length==2){
            return GeneralUtils.getReducedTabCompletions(GeneralUtils.getPlayersForTabCompletion(), args[1], false);
        }
        if(args.length==3){
            return GeneralUtils.getReducedTabCompletions(new ArrayList<>(Arrays.asList(
                    "inventory","enderChest","talismanBag","wardrobe","potionBag","fishBag","quiver","vault")), args[2]);
        }
        return new ArrayList<>();
    }


    @Override
    ChatComponentText getLongHelpMessage() {
        return new ChatComponentText(""+
                "§7§m----------§b§l /sbi viewInventory <playerName> [inventoryType] [page] §7§m--------" + "\n" +
                "Opens one of the given player's Skyblock inventories" + "\n" +
                "The page argument determines what page of the inventory should be opened if there are multiple" + "\n" +
                "Example usage: §e/sbi viewInventory q256 wardrobe 2" + "\n" +
                "The main purpose of this command is to be run whenever you click the an inventory text after using §e/sbi stats§f" +"\n" +
                "§7§m-------------------------------");
    }
}
