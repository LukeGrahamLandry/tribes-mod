package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AngryDogs {
    @SubscribeEvent
    public static void addTribeSlayGoal(EntityJoinWorldEvent event){
        Entity dog = event.getEntity();
        if (dog.getCommandSenderWorld().isClientSide() || !(dog instanceof Wolf)) return;
        ((Wolf) dog).targetSelector.addGoal(0, new NearestAttackableTargetGoal<>((Mob) dog, Player.class, 10, true, false, (entity) -> {
            if (!(entity instanceof ServerPlayer)) return false;  // just to make sure

            UUID ownerID = ((Wolf) dog).getOwnerUUID();
            if (ownerID == null) return false;
            Tribe ownerTribe = TribesManager.getTribeOf(ownerID);
            Tribe checkTribe = TribesManager.getTribeOf(entity.getUUID());
            if (ownerTribe == null || checkTribe == null) return false;

            return ownerTribe.relationToOtherTribes.containsKey(checkTribe.getName()) && ownerTribe.relationToOtherTribes.get(checkTribe.getName()) == Tribe.Relation.ENEMY;
        }));
    }
}
