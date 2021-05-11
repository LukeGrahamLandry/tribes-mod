package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// can only be used on the server side
public class LandClaimHelper {
    private static final HashMap<Long, Tribe> claimedChunks = new HashMap<>();
    public static HashMap<Hemi, List<Tribe>> hemispheres = new HashMap<>();

    // important to call this whenever tribes load
    public static void setup(){
        hemispheres.put(Hemi.POSITIVE, new ArrayList<>());
        hemispheres.put(Hemi.NEGATIVE, new ArrayList<>());

        for (Tribe tribe : TribesManager.getTribes()){
            for (Long chunk : tribe.getClaimedChunks()){
                claimedChunks.put(chunk, tribe);
            }
            hemispheres.get(tribe.hemiAccess).add(tribe);
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

        Hemi currentHemi = getHemiAt(player.getPosition());
        switch (currentHemi){
            case NEGATIVE:
                return (TribesConfig.getUseNorthSouthHemisphereDirection() ? "Northern" : "Western") + " Hemisphere";
            case POSITIVE:
                return (TribesConfig.getUseNorthSouthHemisphereDirection() ? "Southern" : "Eastern") + " Hemisphere";
            case NONE:
                return "No Man's Land";
        }

        return "error";
    }


    // considers chunk claims, hemisphere, death punishments
    public static boolean canAccessLandAt(PlayerEntity player, BlockPos position){
        Tribe interactingTribe = TribesManager.getTribeOf(player.getUniqueID());  // could be null

        // claimed chunk
        long chunk = player.getEntityWorld().getChunkAt(position).getPos().asLong();
        Tribe chunkOwner = getChunkOwner(chunk);
        if (chunkOwner != null && !chunkOwner.equals(interactingTribe) && chunkOwner.claimDisableTime <= 0) return false;

        Hemi currentHemi = getHemiAt(position);

        // no mans land
        if (currentHemi == Hemi.NONE) return true;

        // hemisphere
        // TODO: hemiOwner.claimDisableTime >= 0
        if (TribesConfig.getRequireHemiAccess()){
            return hemispheres.get(currentHemi).contains(interactingTribe);
        }

        return true;
    }

    private static Hemi getHemiAt(BlockPos pos){
        int coord = TribesConfig.getUseNorthSouthHemisphereDirection() ? pos.getZ() : pos.getX();
        int limit = TribesConfig.getHalfNoMansLandWidth();
        if (coord < -limit) {
            return Hemi.NEGATIVE;
        } else if (coord > limit) {
            return Hemi.POSITIVE;
        } else {
            return Hemi.NONE;
        }
    }

    enum Hemi {
        POSITIVE, // south or east
        NEGATIVE,  // north or west
        NONE;
    }
}
