package io.github.lukegrahamlandry.tribes.client;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.client.tile.AltarRenderer;
import io.github.lukegrahamlandry.tribes.init.BlockInit;
import io.github.lukegrahamlandry.tribes.init.ItemInit;
import io.github.lukegrahamlandry.tribes.init.TileEntityInit;
import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = TribesMain.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ItemProperties.register(ItemInit.TRIBE_COMPASS.get(), new ResourceLocation("angle"), TribeCompass::getAngle);
        ItemBlockRenderTypes.setRenderLayer(BlockInit.ALTER.get(), RenderType.cutout());

        BlockEntityRenderers.register(TileEntityInit.ALTAR.get(), AltarRenderer::new);
    }
}
