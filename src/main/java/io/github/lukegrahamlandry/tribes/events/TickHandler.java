package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.network.LandOwnerPacket;
import io.github.lukegrahamlandry.tribes.network.NetworkHandler;
import io.github.lukegrahamlandry.tribes.tribe_data.LandClaimHelper;
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
                new LandOwnerPacket(event.player.getUniqueID(), LandClaimHelper.getOwnerDisplayFor(event.player)));
    }
}
