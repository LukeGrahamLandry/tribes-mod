package io.github.lukegrahamlandry.tribes.tribe_data.claim;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class BannerAccessManager implements AccessManager {
    @Override
    public Tribe getChunkOwner(World world, BlockPos pos) {
        return null;
    }

    @Override
    public Tribe getChunkOwner(Long chunk) {
        return null;
    }

    @Override
    public void setChunkOwner(Long chunk, Tribe tribe) {

    }

    @Override
    public String getOwnerDisplayFor(PlayerEntity player) {
        return "Wilderness";
    }

    @Override
    public boolean canAccessLandAt(PlayerEntity player, BlockPos position) {
        return false;
    }

    @Override
    public List<Long> getClaimedChunksOrdered(ChunkPos start) {
        return new ArrayList<>();
    }

    @Override
    public void forgetTribe(Tribe tribe) {

    }

    @Override
    public boolean isActive() {
        return TribesConfig.bannerClaimsEnabled();
    }
}
