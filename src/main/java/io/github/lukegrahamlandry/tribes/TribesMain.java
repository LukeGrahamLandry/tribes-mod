package io.github.lukegrahamlandry.tribes;

import io.github.lukegrahamlandry.tribes.init.*;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeActionResult;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("tribes")
public class TribesMain {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "tribes";

    public TribesMain() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BlockInit.BLOCKS.register(eventBus);
        MinecraftForge.EVENT_BUS.register(this);
    }

    /*
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvent{
        @SubscribeEvent
        public static void save(WorldEvent.Save event){
            WorldDataSave data = WorldDataSave.getInstance((World) event.getWorld());
            data.markDirty();
        }

        @SubscribeEvent
        public static void load(WorldEvent.Load event){
            WorldDataSave data = WorldDataSave.getInstance((World) event.getWorld());
        }
    }

     */
}
