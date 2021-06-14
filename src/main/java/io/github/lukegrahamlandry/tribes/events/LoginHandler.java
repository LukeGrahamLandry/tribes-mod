package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LoginHandler {
    @SubscribeEvent
    public static void remindLeaderOfSetup(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        if (player instanceof ServerPlayerEntity) {
            Tribe tribe = TribesManager.getTribeOf(player.getUniqueID());
            if (tribe == null || !tribe.isLeader(player.getUniqueID())) return;

            // todo: config to force choosing your effects
            if (tribe.effects.isEmpty()){
                player.sendStatusMessage(new StringTextComponent("Use '/tribe effects' to select always active potion effects for your tribe"), false);
            }

            boolean hasViceLeader = false;
            for (String id : tribe.getMembers()){
                if (tribe.isViceLeader(UUID.fromString(id))) hasViceLeader = true;
            }
            if (!hasViceLeader){
                player.sendStatusMessage(new StringTextComponent("Use `/tribe promote PlayerNameHere` to promote a member to an officer, and again to promote an officer to vice leader. If you leave your tribe, the vice leader will inherit your tribe. There can be any number of members and officers but only 1 vice leader."), false);
            }

            // todo: config to force choosing a deity
            if (tribe.deity == null){
                player.sendStatusMessage(new StringTextComponent("Use '/tribe deity list' see options of deities to follow. Then use '/tribe deity choose deitykey'"), false);
            }
        }
    }
}
