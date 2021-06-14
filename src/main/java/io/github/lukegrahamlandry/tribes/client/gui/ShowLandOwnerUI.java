package io.github.lukegrahamlandry.tribes.client.gui;


import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid=TribesMain.MOD_ID, value=Dist.CLIENT)
public class ShowLandOwnerUI {
    public static HashMap<UUID, String> chunkOwnerDisplayForPlayer = new HashMap<>();

    @SubscribeEvent
    public static void renderUI(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            PlayerController controller = Minecraft.getInstance().playerController;
            PlayerEntity player = Minecraft.getInstance().player;
            if (controller == null || player == null ) {
                return;
            }

            String location = chunkOwnerDisplayForPlayer.get(player.getUniqueID());
            if (!Minecraft.getInstance().gameSettings.showDebugInfo && location != null) {  // && controller.shouldDrawHUD()
                Minecraft.getInstance().fontRenderer.drawString(event.getMatrixStack(), location, 5, 5, calcColour(0, 255, 0));
            }
        }
    }

    private static int calcColour(int r, int g, int b){
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;

        return rgb;
    }
}
