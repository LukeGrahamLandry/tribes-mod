package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import io.github.lukegrahamlandry.tribes.network.CompassChunkPacket;
import io.github.lukegrahamlandry.tribes.network.LandOwnerPacket;
import io.github.lukegrahamlandry.tribes.network.NetworkHandler;
import io.github.lukegrahamlandry.tribes.tribe_data.LandClaimHelper;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TickHandler {
    private static int timer = 0;
    static int ONE_MINUTE = 60 * 20;
    @SubscribeEvent
    public static void updateLandOwner(TickEvent.PlayerTickEvent event){
        timer++;
        if (event.player.getEntityWorld().isRemote() || timer % 10 != 0) return;

        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.player),
                new LandOwnerPacket(event.player.getUniqueID(), LandClaimHelper.getOwnerDisplayFor(event.player)));

        ItemStack stack = event.player.getHeldItem(Hand.MAIN_HAND);
        if (stack.getItem() instanceof TribeCompass){
            BlockPos posToLook = null;
            ChunkPos start = new ChunkPos(event.player.getPosition().getX() >> 4, event.player.getPosition().getZ() >> 4);

            List<Long> chunks = LandClaimHelper.getClaimedChunksOrdered(start);  // closest first
            if (chunks.size() > 0){
                for (long chunk : chunks){
                    if (TribeCompass.isChunkIgnored(stack, chunk)) continue;

                    // spin if you're in the chunk to point to
                    if (chunk == start.asLong()) break;

                    ChunkPos lookchunk = new ChunkPos(chunk);
                    posToLook = new BlockPos((lookchunk.x << 4) + 7, 63 , (lookchunk.z << 4) + 7);
                    break;
                }
            }

            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.player),
                    new CompassChunkPacket(event.player.getUniqueID(), posToLook));
        }

        if (timer >= ONE_MINUTE){
            timer = 0;
            for (Tribe tribe : TribesManager.getTribes()){
                if (tribe.claimDisableTime > 0){
                    tribe.claimDisableTime -= 1;
                    if (tribe.claimDisableTime < 0){
                        tribe.deathWasPVP = false;
                        tribe.deathIndex = 0;
                    }
                }
            }
        }
    }
}
