package com.q256.skyblockImproved;

import com.q256.skyblockImproved.api.ApiHandler;
import com.q256.skyblockImproved.commands.SBICommands;
import com.q256.skyblockImproved.config.ConfigHandler;
import com.q256.skyblockImproved.config.ConfigValues;
import com.q256.skyblockImproved.listeners.Listener;
import com.q256.skyblockImproved.listeners.LividListener;
import com.q256.skyblockImproved.listeners.WithermancerListener;
import com.q256.skyblockImproved.overlay.Overlay;
import com.q256.skyblockImproved.party.PartyHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = SkyblockImproved.MODID, name = SkyblockImproved.NAME, version = SkyblockImproved.VERSION, clientSideOnly = true)
public class SkyblockImproved {
    public static final String MODID = "skyblockImproved";
    public static final String VERSION = "@VERSION@";
    public static final String NAME = "SkyBlockImproved";

    private static SkyblockImproved instance;
    public ConfigHandler configHandler;
    public ApiHandler apiHandler;
    public PartyHandler partyHandler;
    public String fakeChatGuiMsg;

    private ArrayList<KeyBinding> keyBindings = new ArrayList<>();

    /**
     * Keeps track of the GuiScreen that will open in the next tick
     */
    public GuiScreen openGuiScreen = null;

    public SkyblockImproved(){
        instance = this;
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        configHandler = new ConfigHandler(this, event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        addKeyBinding(new KeyBinding("Open Settings", Keyboard.KEY_K, NAME));
        addKeyBinding(new KeyBinding("Edit Overlays", Keyboard.KEY_M, NAME));

        apiHandler = new ApiHandler();
        partyHandler = new PartyHandler();
        MinecraftForge.EVENT_BUS.register(new Listener());
        MinecraftForge.EVENT_BUS.register(new LividListener());
        MinecraftForge.EVENT_BUS.register(new WithermancerListener());

        ClientCommandHandler.instance.registerCommand(new SBICommands());
    }

    public ConfigValues getConfigValues(){
        return configHandler.configValues;
    }

    public ArrayList<KeyBinding> getKeyBindings(){return keyBindings;}

    public static SkyblockImproved getInstance(){
        return instance;
    }

    private void addKeyBinding(KeyBinding keyBinding){
        keyBindings.add(keyBinding);
        ClientRegistry.registerKeyBinding(keyBinding);
    }

    public List<Overlay> getOverlays() {
        return getConfigValues().getOverlays();
    }
}
