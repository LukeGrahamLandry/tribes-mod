package io.github.lukegrahamlandry.tribes.client.gui;

import com.mojang.datafixers.util.Pair;
import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid=TribesMain.MOD_ID, value=Dist.CLIENT)
public class ShowLandOwnerUI {
    public static HashMap<UUID, String> chunkOwnerDisplayForPlayer = new HashMap<>();
    public static HashMap<UUID, Boolean> playerCanAccess = new HashMap<>();

    public static Pair<Integer, Integer> position;

    @SubscribeEvent
    public static void renderUI(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
            Player player = Minecraft.getInstance().player;
            if (controller == null || player == null ) {
                return;
            }

            String location = chunkOwnerDisplayForPlayer.get(player.getUUID());
            int color = playerCanAccess.containsKey(player.getUUID()) && playerCanAccess.get(player.getUUID()) ? GREEN : RED;

            if (!Minecraft.getInstance().options.renderDebug && location != null && !TribesConfig.getLandOwnerDisplayPosition().equals("none")) {  // && controller.shouldDrawHUD()
                int x = getX(location);
                int y = getY();
                Minecraft.getInstance().font.draw(event.getMatrixStack(), location, x, y, color);
            }
        }
    }

    private static int getX(String text){
        int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        switch (TribesConfig.getLandOwnerDisplayPosition()){
            case "top_left":
            case "bottom_left":
                return 5;
            case "top_right":
            case "bottom_right":
                return w - 5 - Minecraft.getInstance().font.width(text);
            case "top_middle":
            case "bottom_middle":
                return (w / 2) - (Minecraft.getInstance().font.width(text) / 2) - 5;
        }

        return 0;
    }

    private static int getY(){
        int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        switch (TribesConfig.getLandOwnerDisplayPosition()){
            case "top_left":
            case "top_right":
            case "top_middle":
                return 5;
            case "bottom_left":
            case "bottom_right":
                return h - 5 - Minecraft.getInstance().font.lineHeight;
            case "bottom_middle":
                return h - 45 - Minecraft.getInstance().font.lineHeight;

        }

        return 0;
    }


    private static final int RED = calcColour(255, 0, 0);
    private static final int GREEN = calcColour(0, 255, 0);

    private static int calcColour(int r, int g, int b){
        int rgb = r;
        rgb = (rgb << 8) + g;
        rgb = (rgb << 8) + b;

        return rgb;
    }
}
