package com.q256.skyblockImproved.commands;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.gui.SettingsGui;
import com.q256.skyblockImproved.gui.SkyblockImprovedGui;
import com.q256.skyblockImproved.utils.TextUtils;
import com.q256.skyblockImproved.utils.GeneralUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

import java.util.*;

public class SBICommands extends CommandBase {
    ArrayList<SubCommand> subCommands = new ArrayList<>();
    final SkyblockImproved main;

    public SBICommands(){
        main = SkyblockImproved.getInstance();

        subCommands.add(new ApiKeyCommand(this));
        subCommands.add(new FakeChatCommand(this));
        subCommands.add(new HelpCommand(this));
        subCommands.add(new ListInventoriesCommand(this));
        subCommands.add(new SettingCommand(this));
        subCommands.add(new StatsCommand(this));
        subCommands.add(new ViewInventoryCommand(this));
        subCommands.add(new ReloadConfigCommand(this));
        subCommands.add(new DebugCommand(this));
        subCommands.add(new ScoreboardCommand(this));
        subCommands.add(new PartyListCommand(this));
        subCommands.add(new PlaySoundCommand(this));
        subCommands.add(new StopSoundCommand(this));
    }

    public String getCommandName() {
        return "skyblockimproved";
    }

    public List<String> getCommandAliases(){
        return Collections.singletonList("sbi");
    }

    public int getRequiredPermissionLevel() {
        return 0;
    }

    public String getCommandUsage(ICommandSender sender) {
        StringBuilder stringBuilder = new StringBuilder("§7§m----------§b§l SkyblockImproved commands §7§m-------- \n");
        for(SubCommand subCommand:subCommands){
            if(!subCommand.requiresDebugMode || main.getConfigValues().debugMode.getValue()) stringBuilder.append(subCommand.shortHelpMessage).append("\n");
        }
        stringBuilder.append("§7§m---------------------------------------------");
        return stringBuilder.toString();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        if(args!=null && args.length>0){
            SubCommand subCommand = getSubCommandByName(args[0]);
            if (subCommand != null) {
                subCommand.processCommand(sender, args);
                return;
            }
            TextUtils.sendClientMessage(getCommandUsage(sender));
        }
        else{
            main.openGuiScreen = new SettingsGui();
            if(main.getConfigValues().printCommandHelpOnBaseCommand.getValue())TextUtils.sendClientMessage(getCommandUsage(sender));
        }
    }

    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            ArrayList<String> outAl = new ArrayList<>();
            for(SubCommand subCommand:subCommands){
                if(!subCommand.requiresDebugMode || main.getConfigValues().debugMode.getValue()) outAl.add(subCommand.name);
            }
            return GeneralUtils.getReducedTabCompletions(outAl, args[0]);
        }
        if(args.length > 1){
            SubCommand typedCommand = getSubCommandByName(args[0]);
            if(typedCommand==null) return null;
            else return typedCommand.tabCompletions(sender, args, pos);
        }
        return null;
    }

    SubCommand getSubCommandByName(String name){
        for(SubCommand subCommand:subCommands){
            if(subCommand.name.equalsIgnoreCase(name)) return subCommand;
        }
        return null;
    }
}
