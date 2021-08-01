package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.init.NetworkHandler;
import io.github.lukegrahamlandry.tribes.network.PacketOpenJoinGUI;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LoginHandler {
    @SubscribeEvent
    public static void remindLeaderOfSetup(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (!player.getCommandSenderWorld().isClientSide()) {
            Tribe tribe = TribesManager.getTribeOf(player.getUUID());

            // join a tribe
            if (tribe == null){
                if (TribesConfig.isTribeRequired())
                    NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new PacketOpenJoinGUI((ServerPlayerEntity) player));
                else {
                    player.displayClientMessage(TribeSuccessType.ALERT_JOIN.getBlueText(), true);
                }
                return;
            }

            if (!tribe.isLeader(player.getUUID())) return;

            // choose effects
            // todo: config to force choosing your effects
            if (tribe.effects.isEmpty()){
                player.displayClientMessage(TribeSuccessType.ALERT_EFFECTS.getBlueText(), false);
            }

            // choose vice leader
            boolean hasViceLeader = false;
            for (String id : tribe.getMembers()){
                if (tribe.getRankOf(id) == Tribe.Rank.VICE_LEADER) hasViceLeader = true;
            }
            if (!hasViceLeader){
                player.displayClientMessage(TribeSuccessType.ALERT_VICE_LEADER.getBlueText(), false);
            }

            // choose deity 
            // todo: config to force choosing a deity
            if (tribe.deity == null){
                player.displayClientMessage(TribeSuccessType.ALERT_DEITY.getBlueText(), false);
            }

            RemoveInactives.recordActive(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide()) {
            RemoveInactives.recordActive(event.getPlayer().getUUID());
        }

    }
}
