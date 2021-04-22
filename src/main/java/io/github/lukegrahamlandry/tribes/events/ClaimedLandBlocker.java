package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.tribe_data.LandClaimHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClaimedLandBlocker {
    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event){
        if (event.getPlayer().getEntityWorld().isRemote()) return;
        if (!LandClaimHelper.canAccessLandAt(event.getPlayer(), event.getPos())){
            event.setCanceled(true);
            // TODO: return placed block to the player
        }
    }

    @SubscribeEvent
    public static void onBlockLeftInteract(PlayerInteractEvent.LeftClickBlock event){
        if (event.getPlayer().getEntityWorld().isRemote()) return;
        if (!LandClaimHelper.canAccessLandAt(event.getPlayer(), event.getPos())){
            event.setCanceled(true);
        }
    }
}
