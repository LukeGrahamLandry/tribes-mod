package io.github.lukegrahamlandry.tribes.tribe_data.claim;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribeSuccessType;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

// can only be used on the server side
public class LandClaimWrapper {
    public static String WILDERNESS = "Wilderness";

    private static HemiAccessManager hemisphereManager;
    private static ChunkAccessManager commandManager;
    private static BannerAccessManager bannerManager;
    private static List<AccessManager> managers;
    // important to call this whenever tribes load
    public static void setup(){
        hemisphereManager = new HemiAccessManager();
        commandManager = new ChunkAccessManager();
        bannerManager = new BannerAccessManager();
        managers = Arrays.asList(commandManager, bannerManager, hemisphereManager);
    }

    public static Tribe getChunkOwner(World world, BlockPos pos){
        for (AccessManager manager : managers){
            Tribe result = manager.getChunkOwner(world, pos);
            if (result != null) return result;
        }
        return null;
    }

    public static Tribe getChunkOwner(Long chunk){
        for (AccessManager manager : managers){
            Tribe result = manager.getChunkOwner(chunk);
            if (result != null) return result;
        }
        return null;
    }

    public static ChunkAccessManager getCommandManager(){
        return commandManager;
    }

    public static BannerAccessManager getBannerManager(){
        return bannerManager;
    }

    public static String getOwnerDisplayFor(PlayerEntity player){
        for (AccessManager manager : managers){
            String result = manager.getOwnerDisplayFor(player);
            if (!WILDERNESS.equals(result)) return result;
        }
        return WILDERNESS;
    }


    // considers chunk claims, hemisphere, death punishments
    public static boolean canAccessLandAt(PlayerEntity player, BlockPos position){
        for (AccessManager manager : managers){
            boolean result = manager.canAccessLandAt(player, position);
            if (!result) return false;
        }
        return true;
    }

    public static HemiAccessManager getHemisphereManager() {
        return hemisphereManager;
    }

    public static List<Long> getClaimedChunksOrdered(ChunkPos start){
        List<Long> chunks = new ArrayList<>();
        for (AccessManager manager : managers){
            chunks.addAll(manager.getClaimedChunksOrdered(start));
        }
        chunks.sort(makeChunkComparator(start));

        return chunks;
    }

    public static Comparator<Long> makeChunkComparator(ChunkPos start){
        return (o1, o2) -> {
            ChunkPos a = new ChunkPos(o1);
            ChunkPos b = new ChunkPos(o2);

            double distA = Math.sqrt(Math.pow(start.x - a.x, 2) + Math.pow(start.z - a.z, 2));
            double distB = Math.sqrt(Math.pow(start.x - b.x, 2) + Math.pow(start.z - b.z, 2));

            return (int) (distA - distB);
        };
    }

    // important to call this whenever a tribe is deleted
    public static void forgetTribe(Tribe tribe){
        if (tribe == null) return;

        for (AccessManager manager : managers){
            manager.forgetTribe(tribe);
        }

        TribesMain.LOGGER.debug("forge tribe: " + tribe.getName());
    }

    public static void onBannerBrokenAt(BlockPos pos){
        Tribe tribe = bannerManager.getChunkOwner(null, pos);
        if (tribe != null){
            tribe.broadcastMessageNoCause(TribeSuccessType.BANNER_BROKEN, pos.getX(), pos.getY(), pos.getZ());
            tribe.bannerUnclaim(pos);
        }
        bannerManager.unclaim(pos);
    }
}
