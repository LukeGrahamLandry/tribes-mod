package io.github.lukegrahamlandry.tribes.client;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.client.tile.AltarRenderer;
import io.github.lukegrahamlandry.tribes.init.BlockInit;
import io.github.lukegrahamlandry.tribes.init.ItemInit;
import io.github.lukegrahamlandry.tribes.init.TileEntityInit;
import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TribesMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ItemModelsProperties.register(ItemInit.TRIBE_COMPASS.get(), new ResourceLocation("angle"), TribeCompass::getAngle);
        RenderTypeLookup.setRenderLayer(BlockInit.ALTER.get(), RenderType.cutout());

        ClientRegistry.bindTileEntityRenderer(TileEntityInit.ALTAR.get(), AltarRenderer::new);
    }
}
