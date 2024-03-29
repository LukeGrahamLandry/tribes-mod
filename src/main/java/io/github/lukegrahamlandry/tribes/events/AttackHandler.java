package io.github.lukegrahamlandry.tribes.events;


import com.mojang.authlib.GameProfile;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttackHandler {
    @SubscribeEvent
    public static void blockFriendlyFire(LivingDamageEvent event){
        if (TribesConfig.getFriendlyFireEnabled()) return;

        Entity source = event.getSource().getEntity();
        Entity target = event.getEntityLiving();

        if (source instanceof PlayerEntity && !source.getCommandSenderWorld().isClientSide()){
            Tribe sourceTribe = TribesManager.getTribeOf(source.getUUID());
            Tribe targetTribe = TribesManager.getTribeOf(target.getUUID());

            if (sourceTribe != null && targetTribe != null){
                if (sourceTribe.equals(targetTribe)) event.setAmount(0);
            }
        }
    }

    @SubscribeEvent
    public static void punishDeath(LivingDeathEvent event){
        Entity dead = event.getEntity();
        if (!(dead instanceof PlayerEntity) || dead.getCommandSenderWorld().isClientSide()) return;

        Entity killer = event.getSource().getEntity();
        if (killer instanceof PlayerEntity){
            tryDropHead(dead, killer);
        }

        Tribe tribe = TribesManager.getTribeOf(event.getEntityLiving().getUUID());
        if (tribe == null) return;

        if (event.getSource().getEntity() instanceof PlayerEntity) tribe.deathWasPVP = true;

        tribe.claimDisableTime = TribesConfig.getDeathClaimDisableTime(tribe.deathIndex, tribe.deathWasPVP);
        tribe.deathIndex++;
    }

    private static void tryDropHead(Entity dead, Entity killer) {
        Tribe deadTribe = TribesManager.getTribeOf(dead.getUUID());
        Tribe killerTribe = TribesManager.getTribeOf(killer.getUUID());
        if (killerTribe == null && deadTribe == null) return; // redundant
        if (killerTribe == deadTribe) return;

        boolean areAllies = deadTribe != null && killerTribe.relationToOtherTribes.get(deadTribe.getName()) == Tribe.Relation.ALLY;
        if (areAllies) return;


        // actually drop the head
        GameProfile gameprofile = ((PlayerEntity)dead).getGameProfile();
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        stack.getOrCreateTag().put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
        ItemEntity itementity = new ItemEntity(dead.getCommandSenderWorld(), dead.blockPosition().getX(), dead.blockPosition().getY(), dead.blockPosition().getZ(), stack);
        dead.getCommandSenderWorld().addFreshEntity(itementity);
    }
}
