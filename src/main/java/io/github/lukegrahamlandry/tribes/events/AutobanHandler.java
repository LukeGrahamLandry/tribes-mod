package io.github.lukegrahamlandry.tribes.events;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AutobanHandler {
    static String NBT_KEY = "tribesdeaths";
    @SubscribeEvent
    public static void autobanOnDeath(LivingDeathEvent event){
        if (event.getEntityLiving().getEntityWorld().isRemote() || !(event.getEntityLiving() instanceof PlayerEntity)) return;

        Tribe tribe = TribesManager.getTribeOf(event.getEntityLiving().getUniqueID());
        if (tribe == null || !tribe.autobanRank.get(tribe.getRankOf(event.getEntityLiving().getUniqueID().toString()))) return;

        long now = System.currentTimeMillis();
        long threshold = tribe.autobanDaysThreshold * 24 * 60 * 60 * 1000;

        CompoundNBT nbt = event.getEntityLiving().getPersistentData();
        long[] pastDeaths = new long[0];
        if (nbt.contains(NBT_KEY)) pastDeaths = nbt.getLongArray(NBT_KEY);

        List<Long> recentDeaths = new ArrayList<>();
        recentDeaths.add(now);

        for (long deathTime : pastDeaths){
            long timeSince = now - deathTime;
            if (timeSince < threshold){
                recentDeaths.add(deathTime);
            }
        }

        int numDeathsWithinThreshold = recentDeaths.size();
        TribesMain.LOGGER.debug(event.getEntityLiving().getUniqueID() + " has died " + numDeathsWithinThreshold + " within their tribe's autoban threshold");
        if (numDeathsWithinThreshold >= tribe.autobanDeathThreshold){
            // todo; specify that its because they died too often
            tribe.broadcastMessageNoCause(TribeSuccessType.BAN_FOR_DEATHS, (PlayerEntity) event.getEntityLiving());
            tribe.banPlayer(UUID.fromString(tribe.getOwner()), event.getEntityLiving().getUniqueID());
        } else {
            nbt.putLongArray(NBT_KEY, recentDeaths);
            // hopefully it was passed by reference and not copied
        }
    }
}