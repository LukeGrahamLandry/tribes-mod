package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenJoinGUI;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.PacketDistributor;


public class ForceJoinTribe {
    // TODO: also have to check on tick to force you to join a tribe
    @SubscribeEvent
    public static void onjoinworld(PlayerEvent.PlayerLoggedInEvent event){
        PlayerEntity player = event.getPlayer();
        if (!player.getEntityWorld().isRemote()){
            Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
            if (tribe != null){
                if (TribesConfig.isTribeRequired())
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketOpenJoinGUI((ServerPlayerEntity) player));
                else {
                    player.sendStatusMessage(new StringTextComponent("use the \"/tribe join\" command to join a tribe!"), true);
                }
            }
        }
    }
}
