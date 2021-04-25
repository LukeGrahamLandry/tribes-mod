package io.github.lukegrahamlandry.tribes.tribe_data;

import io.github.lukegrahamlandry.tribes.config.TribesConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;

// can only be used on the server side
public class LandClaimHelper {
    private static final HashMap<Long, Tribe> claimedChunks = new HashMap<>();
    private static Tribe negativeHemisphereOwner;  // north or west
    private static Tribe positiveHemisphereOwner;  // south or east

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

        int coord = TribesConfig.getUseNorthSouthHemisphereDirection() ? player.getPosition().getZ() : player.getPosition().getX();
        int limit = TribesConfig.getHalfNoMansLandWidth();
        String ownerDisplay = getHemisphereOwner(player.getPosition()) == null ? " unclaimed" : " claimed by " + getHemisphereOwner(player.getPosition()).getName();
        if (coord < -limit) {
            return getHemiName(player.getPosition()) + ownerDisplay;
        } else if (coord > limit) {
            return getHemiName(player.getPosition()) + ownerDisplay;
        } else {
            return "No Man's Land";
        }
    }

    private static String getHemiName(BlockPos pos){
        int coord = TribesConfig.getUseNorthSouthHemisphereDirection() ? pos.getZ() : pos.getX();
        int limit = TribesConfig.getHalfNoMansLandWidth();
        if (coord < -limit) {
            return (TribesConfig.getUseNorthSouthHemisphereDirection() ? "Northern" : "Western") + " Hemisphere";
        } else if (coord > limit) {
            return (TribesConfig.getUseNorthSouthHemisphereDirection() ? "Southern" : "Eastern") + " Hemisphere";
        }

        return "";
    }

    public static boolean canAccessLandAt(PlayerEntity player, BlockPos position){
        Tribe interactingTribe = TribesManager.getTribeOf(player.getUniqueID());  // could be null

        // claimed chunk
        long chunk = player.getEntityWorld().getChunkAt(position).getPos().asLong();
        Tribe chunkOwner = getChunkOwner(chunk);
        if (chunkOwner != null && !chunkOwner.equals(interactingTribe)) return false;

        // no mans land
        int coord = TribesConfig.getUseNorthSouthHemisphereDirection() ? player.getPosition().getZ() : player.getPosition().getX();
        int limit = TribesConfig.getHalfNoMansLandWidth();
        if (coord > -limit && coord < limit) return true;

        // hemisphere
        Tribe hemiOwner = getHemisphereOwner(position);
        return hemiOwner == null || hemiOwner.equals(interactingTribe);
    }

    public static void setNegativeHemisphereOwner(Tribe tribe){ negativeHemisphereOwner = tribe; }
    public static void setPositiveHemisphereOwner(Tribe tribe){ positiveHemisphereOwner = tribe; }

    public static Tribe getHemisphereOwner(BlockPos pos){
        int coord = TribesConfig.getUseNorthSouthHemisphereDirection() ? pos.getZ() : pos.getX();
        int limit = TribesConfig.getHalfNoMansLandWidth();

        if (coord < -limit) {
            return negativeHemisphereOwner;
        } else if (coord > limit) {
            return positiveHemisphereOwner;
        } else {
            return null;
        }
    }

    public static void onAlterPlaced(World worldIn, BlockPos pos, LivingEntity placer) {
        Tribe tribe = TribesManager.getTribeOf(placer.getUniqueID());
        if (tribe == null || getHemisphereOwner(pos) != null || worldIn.isRemote()) return;
        // TODO: check needs leader config and placer rank

        int coord = TribesConfig.getUseNorthSouthHemisphereDirection() ? pos.getZ() : pos.getX();
        int limit = TribesConfig.getHalfNoMansLandWidth();
        if (coord < -limit) {
            setNegativeHemisphereOwner(tribe);
        } else if (coord > limit) {
            setPositiveHemisphereOwner(tribe);
        } else {
            return;
        }

        tribe.broadcastMessage("Your tribe has claimed the " + getHemiName(pos), (PlayerEntity) placer);
    }

    public static void onAlterBroken(World worldIn, BlockPos pos, Entity breaker) {
        // figure out if thats the last alter and remove claim
    }
}
