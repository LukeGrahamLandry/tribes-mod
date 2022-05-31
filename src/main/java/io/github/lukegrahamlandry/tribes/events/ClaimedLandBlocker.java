package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.claim.LandClaimWrapper;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.block.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClaimedLandBlocker {
    @SubscribeEvent
    public static void onBlockInteract(PlayerInteractEvent.RightClickBlock event){
        if (event.getPlayer().getCommandSenderWorld().isClientSide()) return;
        if (!LandClaimWrapper.canAccessLandAt(event.getPlayer(), event.getPos())){
            boolean blockAll = true;

            if (TribesConfig.canEnemiesInteract()){
                Tribe interacting = TribesManager.getTribeOf(event.getPlayer().getUUID());
                Tribe owner = LandClaimWrapper.getChunkOwner(event.getWorld(), event.getPos());
                if (interacting != null && interacting.getRelationTo(owner) == Tribe.Relation.ENEMY){
                    blockAll = false;
                    if (event.getItemStack().getItem() instanceof BlockItem){
                        event.setUseItem(Event.Result.DENY);
                    }
                    Block block = event.getWorld().getBlockState(event.getHitVec().getBlockPos()).getBlock();
                    if (block instanceof BedBlock || block instanceof RespawnAnchorBlock){
                        event.setUseBlock(Event.Result.DENY);
                    }
                }
            }

            if (TribesConfig.canAlliesInteract()){
                Tribe interacting = TribesManager.getTribeOf(event.getPlayer().getUUID());
                Tribe owner = LandClaimWrapper.getChunkOwner(event.getWorld(), event.getPos());
                if (owner.getRelationTo(interacting) == Tribe.Relation.ALLY){
                    blockAll = false;
                    event.setUseItem(Event.Result.DENY);
                    Block block = event.getWorld().getBlockState(event.getHitVec().getBlockPos()).getBlock();
                    if (block instanceof ContainerBlock){
                        event.setUseBlock(Event.Result.DENY);
                    }
                }
            }

            if (blockAll){
                // not directly using value of blockAll in case other mods wanted to cancel
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockLeftInteract(PlayerInteractEvent.LeftClickBlock event){
        if (event.getPlayer().getCommandSenderWorld().isClientSide()) return;
        if (!LandClaimWrapper.canAccessLandAt(event.getPlayer(), event.getPos())){
            if (TribesConfig.bannerClaimsEnabled() && event.getPlayer().level.getBlockState(event.getPos()).is(BlockTags.BANNERS)){
                // allow breaking banners to stop claims
            } else {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event){
        if (event.getPlayer().getCommandSenderWorld().isClientSide()) return;
        if (event.getState().is(BlockTags.BANNERS)){
            BlockPos[] possiblePositions = new BlockPos[]{
                    event.getPos(), event.getPos().above(), event.getPos().below()
            };
            for (BlockPos pos : possiblePositions){
                LandClaimWrapper.onBannerBrokenAt(pos);
            }
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event){
        if (event.getPlayer().getCommandSenderWorld().isClientSide()) return;
        if (!TribesConfig.allowRespawnOnClaimed()){
            BlockPos pos = ((ServerPlayerEntity)event.getPlayer()).getRespawnPosition();
            Tribe tribe = TribesManager.getTribeOf(event.getPlayer().getUUID());
            Tribe ownerTribe = LandClaimWrapper.getChunkOwner(event.getPlayer().level, pos);
            if (ownerTribe != null){
                boolean notAllies = tribe == null || tribe.getRelationTo(ownerTribe) != Tribe.Relation.ALLY;
                if (notAllies){
                    event.getPlayer().displayClientMessage(new StringTextComponent("spawning on claimed land. todo: block this"), false);
                }
            }
        }
    }
}
