package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.network.LandOwnerPacket;
import io.github.lukegrahamlandry.tribes.network.NetworkHandler;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TickHandler {
    private static Random rand = new Random();
    @SubscribeEvent
    public static void updateLandOwner(TickEvent.PlayerTickEvent event){
        if (event.player.getEntityWorld().isRemote() || rand.nextInt(10) != 0) return;

        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.player),
                new LandOwnerPacket(event.player.getUniqueID(), getLandOwnerDisplay(event.player)));
    }

    private static String getLandOwnerDisplay(PlayerEntity player){
        long chunk = player.getEntityWorld().getChunkAt(player.getPosition()).getPos().asLong();
        Tribe chunkOwner = TribesManager.getChunkOwner(chunk);

        if (chunkOwner != null){
            return chunkOwner.getName() + " claimed chunk";
        }


        if (player.getPosition().getZ() < -500) {
            return "Northern Hemisphere";
        } else if (player.getPosition().getZ() > 500) {
            return "Southern Hemisphere";
        } else {
            return "No Man's Land";
        }

        // TODO: congig for north vs east & middle width
        // TODO: take in to account hemisphere access
    }
}
