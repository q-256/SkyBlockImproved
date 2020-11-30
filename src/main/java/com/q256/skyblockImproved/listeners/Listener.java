package com.q256.skyblockImproved.listeners;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.gui.NonInteractableInventory;
import com.q256.skyblockImproved.gui.OverlayEditGui;
import com.q256.skyblockImproved.gui.SettingsGui;
import com.q256.skyblockImproved.overlay.Overlay;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.GuiUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Listener {
    private final SkyblockImproved main = SkyblockImproved.getInstance();

    private static final Pattern abilityHitPattern = Pattern.compile("Your ([\\w ]+) hit (1 enemy|\\d+ enemies) for \\d{1,3}(,\\d{3})*(\\.\\d)? damage.");
    
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        addExtraInfoLines(event);
        //todo remove this
        //if(!addExtraInfoLines(event)) addPartyFinderHighlights(event);
    }

    /**
     * Adds dungeon item quality and expertise/fel sword kill counts to the given tooltip
     * @return Whether any text was added
     */
    private boolean addExtraInfoLines(ItemTooltipEvent event){
        if(event.toolTip==null) return false;

        int insertAt = getToolTipInsertPos(event);
        if(insertAt<0) return false;

        List<String> toolTip = event.toolTip;

        int dungeonQuality = -1;
        int dungeonFloor = -1;
        int expertiseKills = -1;
        int swordKills = -1;

        if(main.getConfigValues().showDungeonItemQuality.getValue()) dungeonQuality = getBaseStatBoostPercentage(event);
        if(main.getConfigValues().showDungeonItemQuality.getValue()) dungeonFloor = getDungeonFloor(event);
        if(main.getConfigValues().showExpertiseKillCount.getValue()) expertiseKills = getExpertiseKillCount(event);
        if(main.getConfigValues().showFelSwordKillCount.getValue()) swordKills = getSwordKillCount(event);

        boolean insertingSomething = dungeonQuality!=-1 || dungeonFloor!=-1 || expertiseKills!=-1 || swordKills!=-1;

        if(insertingSomething && !toolTip.get(insertAt).equals("§5§o") && !toolTip.get(insertAt).startsWith("Anvil Uses:"))toolTip.add(insertAt, "");

        if(dungeonQuality!=-1 || dungeonFloor!=-1){
            String qualityColor = TextUtils.colorNumber(dungeonQuality, new double[]{25,30,35,40,45,50});
            StringBuilder stringBuilder;
            if(dungeonQuality!=-1){
                stringBuilder = new StringBuilder("Dungeon Quality: "+qualityColor+dungeonQuality+"§f§r/§b50");
                if(dungeonFloor!=-1) stringBuilder.append(" §6[Floor ").append(dungeonFloor).append("]");
            }
            else {
                stringBuilder = new StringBuilder("Obtained From: §6Floor "+dungeonFloor);
            }
            toolTip.add(insertAt, stringBuilder.toString());
        }
        if(expertiseKills!=-1){
            toolTip.add(insertAt, "Expertise Kills: §6"+TextUtils.formatNumberLong(expertiseKills));
        }
        if(swordKills!=-1){
            toolTip.add(insertAt, "Sword Kills: §6"+TextUtils.formatNumberLong(swordKills));
        }

        if(insertingSomething && insertAt!=0 && !toolTip.get(insertAt-1).equals("§5§o")) toolTip.add(insertAt, "");

        return insertingSomething;
    }

    private int getToolTipInsertPos(ItemTooltipEvent event){
        List<String> toolTip = event.toolTip;

        int insertAt = 0;

        //check if it's an auction
        boolean isAuction = false;
        for(int i=toolTip.size()-1; i>0; i--){
            if(toolTip.get(i).startsWith("§5§o§7Ends in:")){
                //if it is, put the texts right below "Ends in:"
                insertAt=i+1;
                isAuction = true;
                break;
            }
        }

        //if it's not an auction, put the texts near the bottom of the tooltip
        if(!isAuction) {
            insertAt = toolTip.size() - 1; // 1 line for the rarity
            if (event.showAdvancedItemTooltips) {
                insertAt -= 2; // 1 line for the item name, and 1 line for the nbt
                if (event.itemStack.isItemDamaged()) {
                    insertAt--; // 1 line for damage
                }
            }

            if(insertAt<0) return insertAt;

            //check if SBA's anvil use text is present
            for (int i = toolTip.size() - 1; i >= insertAt; i--) {
                if (toolTip.get(i).startsWith("Anvil Uses:")) {
                    return i;
                }
            }
        }

        return insertAt;
    }
    private int getBaseStatBoostPercentage(ItemTooltipEvent event){
        try {
            NBTTagCompound extraAttributes = event.itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            return extraAttributes.hasKey("baseStatBoostPercentage") ? extraAttributes.getInteger("baseStatBoostPercentage") : -1;
        } catch (NullPointerException exception){
            return -1;
        }
    }
    private int getDungeonFloor(ItemTooltipEvent event){
        try {
            NBTTagCompound extraAttributes = event.itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            return extraAttributes.hasKey("item_tier") ? extraAttributes.getInteger("item_tier") : -1;
        } catch (NullPointerException exception){
            return -1;
        }
    }
    private int getExpertiseKillCount(ItemTooltipEvent event){
        try {
            NBTTagCompound extraAttributes = event.itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            return extraAttributes.hasKey("expertise_kills") ? extraAttributes.getInteger("expertise_kills") : -1;
        } catch (NullPointerException exception){
            return -1;
        }
    }
    private int getSwordKillCount(ItemTooltipEvent event){
        try {
            NBTTagCompound extraAttributes = event.itemStack.getTagCompound().getCompoundTag("ExtraAttributes");
            return extraAttributes.hasKey("sword_kills") ? extraAttributes.getInteger("sword_kills") : -1;
        } catch (NullPointerException exception){
            return -1;
        }
    }

    //todo finish party finder highlights
    /**
     * Colors different classes differently in the party finder
     * @return Whether anything was added
     */
    private boolean addPartyFinderHighlights(ItemTooltipEvent event){
        ItemStack itemStack = event.itemStack;
        try {
            if (itemStack.getDisplayName().endsWith("'s Party") && itemStack.getItem() == Item.getItemById(397)) {
                int membersIndex = event.toolTip.indexOf("§5§o§f§7Members: ");
                if (membersIndex == -1) return false;
                for (int i = membersIndex + 1; i < membersIndex + 6; i++) {
                    String loreLine = event.toolTip.get(i);
                    if(loreLine.equals("§5§o§8 Empty")) break;

                    int firstColon = loreLine.indexOf(':');
                    String className = loreLine.substring(firstColon+4, loreLine.indexOf('§', firstColon+4));
                    char colorCode = 'e';
                    switch (className){
                        case "Healer": colorCode= 'e'; break; //d
                        case "Mage": colorCode= 'e'; break; //5
                        case "Berserk": colorCode= 'e'; break; //c
                        case "Archer": colorCode= 'e'; break; //9
                        case "Tank": colorCode= 'c'; break; //a
                    }
                    event.toolTip.set(i, loreLine.substring(0, firstColon+3)+colorCode+loreLine.substring(firstColon+4));
                }
            }
        } catch (NullPointerException | IndexOutOfBoundsException exception){
            exception.printStackTrace();
            System.out.println("Item Stack: " + itemStack.getTagCompound());
            System.out.println("Lore: " + event.toolTip);
            TextUtils.sendClientMessage("§cAn exception occurred while reading that item's tooltip! Please report this to a developer.", true);
        }
        return false;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();

        //open a GUI
        if(main.openGuiScreen!=null){
            mc.displayGuiScreen(main.openGuiScreen);
            main.openGuiScreen=null;
        }
    }

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event){
        //type 2 means it's a message above the action bar
        if(event.type==2) return;

        main.partyHandler.chatReceived(event.message);

        String unformattedMessage = event.message.getUnformattedText();

        if(unformattedMessage.equals("This creature is immune to this kind of magic!")){
            if(main.getConfigValues().playSoundOnMagicResist.getValue()) GeneralUtils.playSound("note.bass", main.getConfigValues().magicResistSoundVolume.getValue(), 2);
            if(main.getConfigValues().hideMagicResistMessages.getValue()) event.setCanceled(true);
        }

        boolean calcBatStaff = false;
        boolean calcMidasStaff = false;
        boolean calcImplosion = false;

        Matcher matcher = abilityHitPattern.matcher(unformattedMessage);
        if(matcher.matches()){
            String abilityName = matcher.group(1);
            switch (abilityName){
                case "Spirit Sceptre":
                    if (main.getConfigValues().showBatStaffDpsOverlay.getValue()) calcBatStaff = true;
                    if (main.getConfigValues().playSoundOnBatStaffHit.getValue()) GeneralUtils.playSound("random.orb", main.getConfigValues().batStaffHitVolume.getValue(), 1);
                    if (main.getConfigValues().hideBatStaffHitMessages.getValue()) event.setCanceled(true);
                    break;
                case "Molten Wave":
                    if (main.getConfigValues().showMidasStaffDpsOverlay.getValue()) calcMidasStaff = true;
                    if (main.getConfigValues().playSoundOnMidasStaffHit.getValue()) GeneralUtils.playSound("random.orb", main.getConfigValues().midasStaffHitVolume.getValue(), 1);
                    if (main.getConfigValues().hideMidasStaffHitMessages.getValue()) event.setCanceled(true);
                    break;
                case "Implosion":
                    if (main.getConfigValues().showImplosionDpsOverlay.getValue()) calcImplosion = true;
                    if (main.getConfigValues().playSoundOnImplosionHit.getValue()) GeneralUtils.playSound("random.orb", main.getConfigValues().midasStaffHitVolume.getValue(), 1);
                    if (main.getConfigValues().hideImplosionHitMessages.getValue()) event.setCanceled(true);
            }
        }

        if(calcBatStaff || calcMidasStaff || calcImplosion){
            int dmgStartIndex = unformattedMessage.indexOf("for")+4;
            int dmgEndIndex = unformattedMessage.indexOf("damage")-1;
            String dmgString = unformattedMessage.substring(dmgStartIndex, dmgEndIndex).replace(",","");

            int enemyCountStartIndex = unformattedMessage.indexOf("hit")+4;
            int enemyCountEndIndex = unformattedMessage.indexOf(' ', enemyCountStartIndex);
            String enemyCountString = unformattedMessage.substring(enemyCountStartIndex, enemyCountEndIndex).replace(",","");

            try{
                if(calcBatStaff) main.getConfigValues().batStaffDpsOverlay.dmgDealt(Integer.parseInt(enemyCountString), Double.parseDouble(dmgString));
                else if(calcMidasStaff) main.getConfigValues().midasStaffDpsOverlay.dmgDealt(Integer.parseInt(enemyCountString), Double.parseDouble(dmgString));
                else main.getConfigValues().implosionDpsOverlay.dmgDealt(Integer.parseInt(enemyCountString), Double.parseDouble(dmgString));
            } catch (NumberFormatException exception){
                exception.printStackTrace();
            }
        }

        main.getConfigValues().sadanGiantOverlay.onChatReceived(unformattedMessage);
    }

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event){
        for(KeyBinding keyBinding:main.getKeyBindings()){
            if(keyBinding.isPressed()){
                switch (keyBinding.getKeyDescription()){
                    case "Open Settings":
                        main.openGuiScreen = new SettingsGui();
                        break;
                    case "Edit Overlays":
                        main.openGuiScreen = new OverlayEditGui();
                        break;
                }
            }
        }
    }

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent event){
        if(!event.isCancelable() && !(Minecraft.getMinecraft().currentScreen instanceof OverlayEditGui)  &&
                (event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE || event.type == RenderGameOverlayEvent.ElementType.JUMPBAR)){
            for(Overlay overlay:main.getOverlays()){
                if(overlay.getEnabled().getValue()) overlay.drawOverlay();
            }
        }
    }

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Pre event){
        if(main.getConfigValues().showRarityInAllInv.getValue() && event.gui instanceof GuiContainer && !(event.gui instanceof NonInteractableInventory)){
            GuiContainer guiContainer = (GuiContainer) event.gui;
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiContainer.guiLeft, guiContainer.guiTop, 100);
            GuiUtils.drawRarityBackgrounds(guiContainer, (int)(main.getConfigValues().allInvRarityAlpha.getValue()*255));
            GlStateManager.popMatrix();
        }
    }
}
