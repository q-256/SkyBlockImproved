package com.q256.skyblockImproved.listeners;

import com.q256.skyblockImproved.SkyblockImproved;
import com.q256.skyblockImproved.gui.SettingsGui;
import com.q256.skyblockImproved.utils.GeneralUtils;
import com.q256.skyblockImproved.utils.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class Listener {
    private final SkyblockImproved main = SkyblockImproved.getInstance();
    
    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if(event.toolTip==null) return;

        int insertAt = getToolTipInsertPos(event);
        if(insertAt<0) return;

        List<String> toolTip = event.toolTip;

        int dungeonQuality = -1;
        int dungeonFloor = -1;
        int expertiseKills = -1;
        if(main.getConfigValues().showDungeonItemQuality.getValue()) dungeonQuality = getBaseStatBoostPercentage(event);
        if(main.getConfigValues().showDungeonItemQuality.getValue()) dungeonFloor = getDungeonFloor(event);
        if(main.getConfigValues().showExpertiseKillCount.getValue()) expertiseKills = getExpertiseKillCount(event);

        boolean insertingSomething = dungeonQuality!=-1 || dungeonFloor!=-1 || expertiseKills!=-1;

        if(insertingSomething && !toolTip.get(insertAt).equals("§5§o") && !toolTip.get(insertAt).startsWith("Anvil Uses:"))toolTip.add(insertAt, "");

        if(dungeonQuality!=-1){
            String color = TextUtils.colorNumber(dungeonQuality, new double[]{25,30,35,40,45,50});
            toolTip.add(insertAt, "Dungeon Quality: "+color+dungeonQuality+"§f§r/§b50");
        }
        if(dungeonFloor!=-1){
            toolTip.add(insertAt, "Obtained From: §6Floor "+dungeonFloor);
        }
        if(expertiseKills!=-1){
            toolTip.add(insertAt, "Expertise Kills: §6"+TextUtils.formatNumberLong(expertiseKills));
        }

        if(insertingSomething && insertAt!=0 && !toolTip.get(insertAt-1).equals("§5§o")) toolTip.add(insertAt, "");
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
        //type 2 means its a chat message above the action bar
        if(event.type==2) return;

        main.partyHandler.chatReceived(event.message);

        String unformattedMessage = event.message.getUnformattedText();

        if(unformattedMessage.equals("This creature is immune to this kind of magic!")){
            if(main.getConfigValues().playSoundOnMagicResist.getValue()) GeneralUtils.playSound("note.bass", main.getConfigValues().magicResistSoundVolume.getValue(), 2);
            if(main.getConfigValues().hideMagicResistMessages.getValue()) event.setCanceled(true);
        }

        if(unformattedMessage.matches("Your Bat Staff hit ((1 enemy)|(\\d+ enemies)) for \\d{1,3}(,\\d{3})*(\\.\\d)? damage.")){
            if(main.getConfigValues().playSoundOnBatStaffHit.getValue()) GeneralUtils.playSound("random.orb", main.getConfigValues().batStaffHitVolume.getValue(), 1);
            if(main.getConfigValues().hideBatStaffHitMessages.getValue())event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onKeyEvent(InputEvent.KeyInputEvent event){
        for(KeyBinding keyBinding:main.getKeyBindings()){
            if(keyBinding.getKeyDescription().equals("Open Settings") && keyBinding.isPressed()){
                main.openGuiScreen = new SettingsGui();
            }
        }
    }


    //Increases the width of the chat texfield from 100 to 256, unfortunately, this isn't enough to send long chat messages
    //To send long chat messages you would also need to modify C01PacketChatMessage which I don't feel like doing rn
    /*
    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.InitGuiEvent.Post event){
        if(main.getConfigValues().longChat.getValue() && event.gui instanceof GuiChat){
            if(main.getConfigValues().debugMode.getValue())TextUtils.sendClientMessage("Increasing chat limit", true);
            GuiChat guiChat = (GuiChat)event.gui;

            try {
                Field chatTextField;

                try {
                    chatTextField = GuiChat.class.getDeclaredField("inputField");
                } catch (NoSuchFieldException exception){
                    try {
                        chatTextField = GuiChat.class.getDeclaredField("field_146415_a");
                    } catch (NoSuchFieldException exception1){
                        Field[] fields = GuiChat.class.getDeclaredFields();
                        chatTextField = fields[7];
                    }
                }

                chatTextField.setAccessible(true);
                GuiTextField guiTextField = (GuiTextField) chatTextField.get(guiChat);
                guiTextField.setMaxStringLength(256);
                if(main.getConfigValues().debugMode.getValue())TextUtils.sendClientMessage("Increased chat limit", true);
            } catch (IllegalAccessException | NullPointerException | ClassCastException | IndexOutOfBoundsException exception){
                exception.printStackTrace();
                System.out.println("Declared fields: "+Arrays.toString(GuiChat.class.getDeclaredFields()));
                System.out.println("Fields: "+Arrays.toString(GuiChat.class.getFields()));
                if(main.getConfigValues().debugMode.getValue())TextUtils.sendClientMessage("Failed to increase chat limit", true);
            }
        }
    }
    */


    //before any major releases, make sure the client doesn't send any weird packets
    /*
    @SubscribeEvent
    public void clientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event){
        ChannelDuplexHandler handler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                // here is where you intercept incoming packets
                super.channelRead(ctx, packet);
            }

            @Override
            public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
                // here is where you intercept outgoing packets
                super.write(ctx, packet, promise);
                if(!(packet instanceof C00PacketKeepAlive || packet instanceof C03PacketPlayer)){
                    System.out.println(packet.toString());
                }
            }
        };
        String name = Minecraft.getSessionInfo().get("X-Minecraft-Username");
        event.manager.channel().pipeline().addBefore("packet_handler", name, handler);
    }
    */
}
