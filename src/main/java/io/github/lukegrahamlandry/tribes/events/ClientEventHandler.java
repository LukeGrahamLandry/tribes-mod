package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.init.BlockInit;
import io.github.lukegrahamlandry.tribes.init.ItemInit;
import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = TribesMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event){
        ItemModelsProperties.registerProperty(ItemInit.TRIBE_COMPASS.get(), new ResourceLocation("angle"), TribeCompass::getAngle);
        RenderTypeLookup.setRenderLayer(BlockInit.ALTER.get(), RenderType.getCutout());
    }
}
