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
public class HemiAccessManager implements AccessManager {
    public HashMap<Hemi, List<Tribe>> hemispheres = new HashMap<>();

    public HemiAccessManager(){
        hemispheres.put(Hemi.POSITIVE, new ArrayList<>());
        hemispheres.put(Hemi.NEGATIVE, new ArrayList<>());
        hemispheres.put(Hemi.NONE, new ArrayList<>());  // never actaully used but would crash without unless add a check

        for (Tribe tribe : TribesManager.getTribes()){
            hemispheres.get(tribe.hemiAccess).add(tribe);
        }
    }

    public Tribe getChunkOwner(World world, BlockPos pos){
       return null;
    }

    public Tribe getChunkOwner(Long chunk){
        return null;
    }

    public void setChunkOwner(Long chunk, Tribe tribe){
        
    }

    public String getOwnerDisplayFor(PlayerEntity player){
        Hemi currentHemi = getHemiAt(player.blockPosition());
        switch (currentHemi){
            case NEGATIVE:
                return (TribesConfig.getUseNorthSouthHemisphereDirection() ? "Northern" : "Western") + " Hemisphere";
            case POSITIVE:
                return (TribesConfig.getUseNorthSouthHemisphereDirection() ? "Southern" : "Eastern") + " Hemisphere";
            default:
                return LandClaimWrapper.WILDERNESS;
        }
    }


    // considers chunk claims, hemisphere, death punishments
    public boolean canAccessLandAt(PlayerEntity player, BlockPos position){
        Tribe interactingTribe = TribesManager.getTribeOf(player.getUUID());  // could be null
        Hemi currentHemi = getHemiAt(position);

        // no mans land
        if (currentHemi == Hemi.NONE) return true;

        // hemisphere
        if (TribesConfig.getRequireHemiAccess()){
            return hemispheres.get(currentHemi).contains(interactingTribe);
        }

        return true;
    }

    @Override
    public List<Long> getClaimedChunksOrdered(ChunkPos start) {
        return new ArrayList<>();
    }

    private Hemi getHemiAt(BlockPos pos){
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

    public enum Hemi {
        POSITIVE, // south or east
        NEGATIVE,  // north or west
        NONE;
    }

    // important to call this whenever a tribe is deleted
    public void forgetTribe(Tribe tribe){
        hemispheres.forEach((hemi, theTribes) -> {
            theTribes.remove(tribe);
        });
    }

    @Override
    public boolean isActive() {
        return TribesConfig.getRequireHemiAccess() && TribesConfig.getHalfNoMansLandWidth() > 0;
    }
}
