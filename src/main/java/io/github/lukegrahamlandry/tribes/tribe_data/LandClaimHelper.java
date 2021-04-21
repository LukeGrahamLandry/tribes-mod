package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

// can only be used on the server side
public class LandClaimHelper {
    private static HashMap<Long, Tribe> claimedChunks;

    // important to call this whenever
    public static void setup(){
        for (Tribe tribe : TribesManager.getTribes()){
            for (Long chunk : tribe.getClaimedChunks()){
                claimedChunks.put(chunk, tribe);
            }
        }
    }

    public static Tribe getChunkOwner(Long chunk){
        return claimedChunks.get(chunk);
    }

    // do not call directly, use the version in Tribe
    // important that anything that should update the map calls it
    public static void setChunkOwner(Long chunk, Tribe tribe){
        if (tribe == null) claimedChunks.remove(chunk);
        else claimedChunks.put(chunk, tribe);
    }

    public static String getOwnerDisplayFor(PlayerEntity player){
        long chunk = player.getEntityWorld().getChunkAt(player.getPosition()).getPos().asLong();
        Tribe chunkOwner = getChunkOwner(chunk);

        if (chunkOwner != null){
            return chunkOwner.getName() + " claimed chunk";
        }

        if (player.getPosition().getZ() < -500) {
            return "Northern Hemisphere";
        } else if (player.getPosition().getZ() > 500) {
            return "Southern Hemisphere";
        } else {
            return "No Man's Land";
        }

        // TODO: congig for north vs east & middle width
        // TODO: take in to account hemisphere access
    }

    public static boolean canAccessLandAt(PlayerEntity player, BlockPos position){
        Tribe interactingTribe = TribesManager.getTribeOf(player.getUniqueID());

        // claimed chunk
        long chunk = player.getEntityWorld().getChunkAt(position).getPos().asLong();
        Tribe chunkOwner = getChunkOwner(chunk);
        if (chunkOwner != null && !chunkOwner.equals(interactingTribe)) return false;

        // no mans land
        if (position.getZ() > -500 && position.getZ() < 500) return true;


        // TODO: take in to account hemisphere access

        return false;
    }
}
