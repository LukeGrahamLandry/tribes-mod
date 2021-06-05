package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AngryDogs {
    @SubscribeEvent
    public static void addTribeSlayGoal(EntityJoinWorldEvent event){
        Entity dog = event.getEntity();
        if (dog.getEntityWorld().isRemote() || !(dog instanceof WolfEntity)) return;
        ((WolfEntity) dog).targetSelector.addGoal(0, new NearestAttackableTargetGoal<>((MobEntity) dog, PlayerEntity.class, 10, true, false, (entity) -> {
            if (!(entity instanceof ServerPlayerEntity)) return false;  // just to make sure

            UUID ownerID = ((WolfEntity) dog).getOwnerId();
            if (ownerID == null) return false;
            Tribe ownerTribe = TribesManager.getTribeOf(ownerID);
            Tribe checkTribe = TribesManager.getTribeOf(entity.getUniqueID());
            if (ownerTribe == null || checkTribe == null) return false;

            return ownerTribe.relationToOtherTribes.containsKey(checkTribe.getName()) && ownerTribe.relationToOtherTribes.get(checkTribe.getName()) == Tribe.Relation.ENEMY;
        }));
    }
}
