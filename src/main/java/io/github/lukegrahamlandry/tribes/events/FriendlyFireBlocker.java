package io.github.lukegrahamlandry.tribes.events;


import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FriendlyFireBlocker {
    @SubscribeEvent
    public static void onDamaged(LivingDamageEvent event){
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
}
