package io.github.lukegrahamlandry.tribes;

import io.github.lukegrahamlandry.tribes.config.Config;
import io.github.lukegrahamlandry.tribes.init.*;
import io.github.lukegrahamlandry.tribes.tribe_data.SaveHandler;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("tribes")
public class TribesMain {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "tribes";

    public TribesMain() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Registering of both Client and Server Configs
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.client_config);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.server_config);

        //Loading of both Client and Server Config Files
        Config.loadConfig(Config.client_config, FMLPaths.CONFIGDIR.get().resolve(MOD_ID+"-client.toml").toString());
        Config.loadConfig(Config.server_config, FMLPaths.CONFIGDIR.get().resolve(MOD_ID+"-server.toml").toString());

        ItemInit.init(eventBus);
        BlockInit.BLOCKS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvent{
        // TODO: seems to exits in /data /DIM1/data and /DIM-1/data
        // why does it fire 3 times?
        @SubscribeEvent
        public static void doLoad(WorldEvent.Load event){
            if (event.getWorld().isRemote()) return;
            File dataFile = ((ServerChunkProvider)event.getWorld().getChunkProvider()).getSavedData().folder;
            SaveHandler.load(dataFile);
        }

        @SubscribeEvent
        public static void doSave(WorldEvent.Unload event){
            if (event.getWorld().isRemote()) return;
            File dataFile = ((ServerChunkProvider)event.getWorld().getChunkProvider()).getSavedData().folder;
            SaveHandler.save(dataFile);
        }
    }
}
