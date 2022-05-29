package io.github.lukegrahamlandry.tribes.tribe_data.claim;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// can only be used on the server side
public class ChunkAccessManager implements AccessManager {
    private final HashMap<Long, Tribe> claimedChunks = new HashMap<>();

    // important to call this whenever tribes load
    public ChunkAccessManager(){
        claimedChunks.clear();
        for (Tribe tribe : TribesManager.getTribes()){
            for (Long chunk : tribe.getClaimedChunks()){
                claimedChunks.put(chunk, tribe);
            }
        }
    }

    public Tribe getChunkOwner(World world, BlockPos pos){
        long chunk = world.getChunkAt(pos).getPos().toLong();
        return getChunkOwner(chunk);
    }

    public Tribe getChunkOwner(Long chunk){
        return claimedChunks.get(chunk);
    }

    // do not call directly, use the version in Tribe
    // important that anything that should update the map calls it
    public void setChunkOwner(Long chunk, Tribe tribe){
        if (tribe == null) claimedChunks.remove(chunk);
        else claimedChunks.put(chunk, tribe);
    }

    public String getOwnerDisplayFor(PlayerEntity player){
        long chunk = player.getCommandSenderWorld().getChunkAt(player.blockPosition()).getPos().toLong();
        Tribe chunkOwner = getChunkOwner(chunk);

        if (chunkOwner != null){
            return chunkOwner.getName() + " claimed chunk";
        }

        return LandClaimWrapper.WILDERNESS;
    }


    // considers chunk claims, death punishments
    public boolean canAccessLandAt(PlayerEntity player, BlockPos position){
        Tribe interactingTribe = TribesManager.getTribeOf(player.getUUID());  // could be null

        // claimed chunk
        long chunk = player.getCommandSenderWorld().getChunkAt(position).getPos().toLong();
        Tribe chunkOwner = getChunkOwner(chunk);
        if (chunkOwner != null && !chunkOwner.equals(interactingTribe)) {
            return chunkOwner.claimDisableTime <= 0;  // respect pvp death penalties
        }

        return true;
    }


    public List<Long> getClaimedChunksOrdered(ChunkPos start){
        List<Long> chunks = new ArrayList<>(claimedChunks.keySet());
        chunks.sort((o1, o2) -> {
            ChunkPos a = new ChunkPos(o1);
            ChunkPos b = new ChunkPos(o2);

            double distA = Math.sqrt(Math.pow(start.x - a.x, 2) + Math.pow(start.z - a.z, 2));
            double distB = Math.sqrt(Math.pow(start.x - b.x, 2) + Math.pow(start.z - b.z, 2));

            return (int) (distA - distB);
        });

        return chunks;
    }

    // important to call this whenever a tribe is deleted
    public void forgetTribe(Tribe tribe){
        tribe.chunks.forEach((chunk) -> setChunkOwner(chunk, null));
    }

    @Override
    public boolean isActive() {
        return TribesConfig.commandClaimsEnabled();
    }
}
