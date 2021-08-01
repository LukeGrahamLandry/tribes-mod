package io.github.lukegrahamlandry.tribes.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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

    public static List<ServerPlayerEntity> getPlayers(){
        List<ServerPlayerEntity> allPlayers = new ArrayList<>();
        server.getAllLevels().forEach((world) -> {
            allPlayers.addAll(world.players());
        });
        return allPlayers;
    }

    public static PlayerEntity getPlayerByUuid(UUID id){
        for (ServerWorld world : server.getAllLevels()){
            if (world.getPlayerByUUID(id) != null) return world.getPlayerByUUID(id);
        }
        return null;
    }
}
