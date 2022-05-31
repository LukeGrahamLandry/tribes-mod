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
import java.util.List;

public class BannerAccessManager implements AccessManager {
    List<ClaimedCircle> claims = new ArrayList<>();

    public void claim(Tribe tribe, BlockPos pos) {
        claims.add(new ClaimedCircle(tribe.getName(), pos.getX(), pos.getZ()));
    }

    public void unclaim(BlockPos pos) {
        this.claims.removeIf(circle -> circle.x == pos.getX() && circle.z == pos.getZ());
    }

    static class ClaimedCircle {
        private final String tribe;
        private final int x;
        private final int z;

        public ClaimedCircle(String tribe, int x, int z){
            this.tribe = tribe;
            this.x = x;
            this.z = z;
        }

        public double distSq(BlockPos pos){
            return distSq(pos.getX(), pos.getZ());
        }

        public double distSq(int x2, int z2){
            int a = x2 - this.x;
            int b = z2 - this.z;
            return Math.pow(a, 2) + Math.pow(b, 2);
        }

        public boolean isWithin(BlockPos pos, int radius){
            return distSq(pos) < (radius*radius);
        }

        public boolean isWithin(ChunkPos pos, int radius){
            return distSq(pos.getMaxBlockX(), pos.getMaxBlockZ()) < (radius*radius) || distSq(pos.getMinBlockX(), pos.getMinBlockZ()) < (radius*radius);
        }

        @Override
        public String toString() {
            return "ClaimedCircle{" +
                    "tribe='" + tribe + '\'' +
                    ", x=" + x +
                    ", z=" + z +
                    '}';
        }
    }

    @Override
    public Tribe getChunkOwner(World world, BlockPos pos) {
        for (ClaimedCircle circle : this.claims){
            if (circle.isWithin(pos, TribesConfig.getBannerClaimRadius())){
                return TribesManager.getTribe(circle.tribe);
            }
        }
        return null;
    }

    // this kinda doesnt make sense should reqork how compas works because i think thats only thing that uses this
    @Override
    public Tribe getChunkOwner(Long chunkData) {
        ChunkPos chunk = new ChunkPos(chunkData);
        Tribe[] owners = new Tribe[]{
                getChunkOwner(null, new BlockPos(chunk.getMinBlockX() + 8, 0, chunk.getMinBlockZ() + 8)),
                getChunkOwner(null, new BlockPos(chunk.getMaxBlockX(), 0, chunk.getMaxBlockZ())),
                getChunkOwner(null, new BlockPos(chunk.getMinBlockX(), 0, chunk.getMinBlockZ()))
        };
        for (Tribe owner : owners){
            if (owner != null) return owner;
        }

        return null;
    }

    @Override
    public void setChunkOwner(Long chunk, Tribe tribe) {
        TribesMain.LOGGER.error("BannerAccessManager#setChunkOwner(Long, Tribe) should never be called. " + new ChunkPos(chunk) + " " + tribe.getName());
    }

    @Override
    public String getOwnerDisplayFor(PlayerEntity player) {
        Tribe chunkOwner = getChunkOwner(player.level, player.blockPosition());

        if (chunkOwner != null){
            return chunkOwner.getName() + " claimed chunk";
        }
        return "Wilderness";
    }

    @Override
    public boolean canAccessLandAt(PlayerEntity player, BlockPos position) {
        Tribe interactingTribe = TribesManager.getTribeOf(player.getUUID());  // could be null

        for (ClaimedCircle circle : this.claims){
            if (circle.isWithin(position, TribesConfig.getBannerClaimRadius())){
                if (interactingTribe == null) return false;
                if (!interactingTribe.getName().equals(circle.tribe)) return TribesManager.getTribe(circle.tribe).claimDisableTime <= 0;  // respect pvp death penalties;
            }
        }
        return true;
    }

    @Override
    public List<Long> getClaimedChunksOrdered(ChunkPos start) {
        List<Long> centerBannerChunks = new ArrayList<>();
        for (ClaimedCircle circle : this.claims){
            centerBannerChunks.add(new ChunkPos(new BlockPos(circle.x, 0, circle.z)).toLong());
        }

        centerBannerChunks.sort(LandClaimWrapper.makeChunkComparator(start));

        return centerBannerChunks;
    }

    @Override
    public void forgetTribe(Tribe tribe) {
        this.claims.removeIf(circle -> circle.tribe.equals(tribe.getName()));
    }

    @Override
    public boolean isActive() {
        return TribesConfig.bannerClaimsEnabled();
    }

    @Override
    public boolean canClaim(PlayerEntity player, BlockPos pos) {
        if (!LandClaimWrapper.getHemisphereManager().canClaim(player, pos)) {
            TribesMain.LOGGER.info("cant claim in this hemi");
            return false;
        }


        Tribe interactingTribe = TribesManager.getTribeOf(player.getUUID());  // could be null

        int radius = (TribesConfig.getBannerClaimRadius() + TribesConfig.getBannerSafeRadius());
        for (ClaimedCircle circle : this.claims){
            if (circle.isWithin(pos, radius*2) && (interactingTribe == null || !interactingTribe.getName().equals(circle.tribe))){
                TribesMain.LOGGER.info("try place banner at " + pos + " overlaps with banner at " + circle);
                return false;
            }
        }

        List<Long> chunks = LandClaimWrapper.getCommandManager().getClaimedChunksOrdered(new ChunkPos(0,0));
        ClaimedCircle check = new ClaimedCircle(null, pos.getX(), pos.getZ());
        for (long chunkData : chunks){
            ChunkPos chunk = new ChunkPos(chunkData);
            if (check.isWithin(chunk, radius) && (interactingTribe == null || !interactingTribe.getName().equals(LandClaimWrapper.getCommandManager().getChunkOwner(chunkData).getName()))){
                TribesMain.LOGGER.info("try place banner at " + pos + " overlaps with chunk at " + chunk);
                return false;
            }
        }

        return true;
    }
}
