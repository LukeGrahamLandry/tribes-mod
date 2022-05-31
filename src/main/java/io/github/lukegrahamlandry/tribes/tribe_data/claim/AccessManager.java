package io.github.lukegrahamlandry.tribes.tribe_data.claim;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public interface AccessManager {
    Tribe getChunkOwner(World world, BlockPos pos);

    Tribe getChunkOwner(Long chunk);

    // do not call directly, use the version in Tribe
    // important that anything that should update the map calls it
    void setChunkOwner(Long chunk, Tribe tribe);

    String getOwnerDisplayFor(PlayerEntity player);


    // considers chunk claims, hemisphere, death punishments
    boolean canAccessLandAt(PlayerEntity player, BlockPos position);

    List<Long> getClaimedChunksOrdered(ChunkPos start);

    // important to call this whenever a tribe is deleted
    void forgetTribe(Tribe tribe);

    boolean isActive();

    boolean canClaim(PlayerEntity player, BlockPos pos);
}
