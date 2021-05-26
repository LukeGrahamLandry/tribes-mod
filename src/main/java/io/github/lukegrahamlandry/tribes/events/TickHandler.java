package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.item.TribeCompass;
import io.github.lukegrahamlandry.tribes.network.CompassChunkPacket;
import io.github.lukegrahamlandry.tribes.network.LandOwnerPacket;
import io.github.lukegrahamlandry.tribes.network.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenJoinGUI;
import io.github.lukegrahamlandry.tribes.tribe_data.LandClaimHelper;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
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
    public static void updateLandOwnerAndCompassAndEffects(TickEvent.PlayerTickEvent event){
        if (event.player.getEntityWorld().isRemote() || timer % 10 != 0) return;

        // land owner display
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.player),
                new LandOwnerPacket(event.player.getUniqueID(), LandClaimHelper.getOwnerDisplayFor(event.player)));


        // tribe compass direction
        ItemStack stack = event.player.getHeldItem(Hand.MAIN_HAND);
        if (stack.getItem() instanceof TribeCompass){
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.player),
                    new CompassChunkPacket(event.player.getUniqueID(), TribeCompass.caclulateTargetPosition((ServerPlayerEntity) event.player, stack)));
        }


        // apply tribe effects
        Tribe tribe = TribesManager.getTribeOf(event.player.getUniqueID());
        if (tribe != null){
            tribe.effects.forEach((effect, level) -> {
                event.player.addPotionEffect(new EffectInstance(effect, 15*20, level-1));
            });
        } else if (TribesConfig.isTribeRequired()){
            // no tribe and force tribes
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.player), new PacketOpenJoinGUI((ServerPlayerEntity) event.player));
        }
    }

    @SubscribeEvent
    public static void tickDeathPunishments(TickEvent.WorldTickEvent event){
        if (event.world.isRemote()) return;
        timer++;

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
