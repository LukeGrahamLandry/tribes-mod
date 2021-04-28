package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttackHandler {
    @SubscribeEvent
    public static void blockFriendlyFire(LivingDamageEvent event){
        if (TribesConfig.getFriendlyFireEnabled()) return;

        Entity source = event.getSource().getTrueSource();
        Entity target = event.getEntityLiving();

        if (source instanceof PlayerEntity && !source.getEntityWorld().isRemote()){
            Tribe sourceTribe = TribesManager.getTribeOf(source.getUniqueID());
            Tribe targetTribe = TribesManager.getTribeOf(target.getUniqueID());

            if (sourceTribe != null && targetTribe != null){
                if (sourceTribe.equals(targetTribe)) event.setAmount(0);
            }
        }
    }

    @SubscribeEvent
    public static void punishDeath(LivingDeathEvent event){
        if (!(event.getEntity() instanceof PlayerEntity) || event.getEntity().getEntityWorld().isRemote()) return;

        Tribe tribe = TribesManager.getTribeOf(event.getEntityLiving().getUniqueID());
        if (tribe == null) return;

        if (event.getSource().getTrueSource() instanceof PlayerEntity) tribe.deathWasPVP = true;

        tribe.claimDisableTime = TribesConfig.getDeathClaimDisableTime(tribe.deathIndex, tribe.deathWasPVP);
        tribe.deathIndex++;
    }
}
