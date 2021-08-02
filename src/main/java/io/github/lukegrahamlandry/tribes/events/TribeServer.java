package io.github.lukegrahamlandry.tribes.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TribeServer {
    public static MinecraftServer server;
    @SubscribeEvent
    public static void saveServer(FMLServerStartedEvent event){
        server = event.getServer();
    }

    public static List<ServerPlayer> getPlayers(){
        List<ServerPlayer> allPlayers = new ArrayList<>();
        server.getAllLevels().forEach((world) -> {
            allPlayers.addAll(world.players());
        });
        return allPlayers;
    }

    public static Player getPlayerByUuid(UUID id){
        for (ServerWorld world : server.getAllLevels()){
            if (world.getPlayerByUUID(id) != null) return world.getPlayerByUUID(id);
        }
        return null;
    }
}
