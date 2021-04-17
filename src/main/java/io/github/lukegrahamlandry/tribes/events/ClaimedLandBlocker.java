package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClaimedLandBlocker {
    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event){
        if (event.getPlayer().getEntityWorld().isRemote()) return;
        if (!canAccessLand(event.getPlayer(), event.getPos())){
            event.setCanceled(true);
            // TODO: return placed block to the player
        }
    }

    @SubscribeEvent
    public static void onBlockLeftInteract(PlayerInteractEvent.LeftClickBlock event){
        if (event.getPlayer().getEntityWorld().isRemote()) return;
        if (!canAccessLand(event.getPlayer(), event.getPos())){
            event.setCanceled(true);
        }
    }


    private static boolean canAccessLand(PlayerEntity placer, BlockPos position){
        Tribe placerTribe = TribesManager.getTribeOf(placer.getUniqueID());

        long chunk = placer.getEntityWorld().getChunkAt(position).getPos().asLong();
        Tribe chunkOwner = TribesManager.getChunkOwner(chunk);
        if (chunkOwner != null && !chunkOwner.equals(placerTribe)) return false;


        if (position.getZ() > -500 && position.getZ() < 500) return true;


        // TODO: take in to account hemisphere access

        return false;
    }
}
