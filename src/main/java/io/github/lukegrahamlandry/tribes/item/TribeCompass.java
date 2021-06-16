package io.github.lukegrahamlandry.tribes.item;

import io.github.lukegrahamlandry.tribes.TribesMain;
import io.github.lukegrahamlandry.tribes.tribe_data.LandClaimHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nullable;
import java.util.*;

public class TribeCompass extends Item {
    public static HashMap<UUID, BlockPos> toLookAt = new HashMap<>();

    public TribeCompass(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("Right click while holding this and standing in a chunk that hasn't been claimed to get a rough estimate of the distance to the nearest claimed chunk."));
        tooltip.add(new StringTextComponent("Right click while holding this in a chunk that *has* been claimed to exclude it from compass searches, which makes it easier to hunt for more claimed chunks. Right click again in excluded chunks to resume including them in compass searches."));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public static BlockPos caclulateTargetPosition(ServerPlayerEntity player, ItemStack compass){
        BlockPos posToLook = null;
        ChunkPos start = new ChunkPos(player.getPosition().getX() >> 4, player.getPosition().getZ() >> 4);

        List<Long> chunks = LandClaimHelper.getClaimedChunksOrdered(start);  // closest first
        if (chunks.size() > 0){
            for (long chunk : chunks){
                if (TribeCompass.isChunkIgnored(compass, chunk)) continue;

                // spin if you're in the chunk to point to
                if (chunk == start.asLong()) break;

                ChunkPos lookchunk = new ChunkPos(chunk);
                posToLook = new BlockPos((lookchunk.x << 4) + 7, 63 , (lookchunk.z << 4) + 7);
                break;
            }
        }

        return posToLook;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);
        if (!worldIn.isRemote()){
            long chunk = worldIn.getChunkAt(playerIn.getPosition()).getPos().asLong();
            if (LandClaimHelper.getChunkOwner(chunk) != null){
                if (isChunkIgnored(stack, chunk)){
                    removeIgnoredChunk(stack, chunk);
                    playerIn.sendStatusMessage(new StringTextComponent("This chunk will be included in tribe compass searches!"), false);
                } else {
                    addIgnoredChunk(stack, chunk);
                    playerIn.sendStatusMessage(new StringTextComponent("This chunk will be excluded from tribe compass searches!"), false);
                }
            } else {
                BlockPos target = caclulateTargetPosition((ServerPlayerEntity) playerIn, stack);

                if (target == null){
                    playerIn.sendStatusMessage(new StringTextComponent("There are no claimed chunks to find."), false);
                } else {
                    double myX = playerIn.getPosX();
                    double myZ = playerIn.getPosZ();

                    double dist = Math.pow(Math.pow(target.getX() - myX, 2) + Math.pow(target.getZ() - myZ, 2), 0.5D);
                    int digits = (int) (Math.floor(Math.log10(dist)) + 1);
                    StringBuilder x = new StringBuilder();
                    for (int i=0;i<digits;i++){
                        x.append("x");
                    }
                    playerIn.sendStatusMessage(new StringTextComponent("The next claimed chunk is " + x.toString() + " blocks away."), false);
                }
            }
        }
        return ActionResult.resultSuccess(stack);
    }

    private static List<Long> getIgnoredChunks(CompoundNBT tag){
        long[] savedIgnoredChunks = tag.contains("ignore") ? tag.getLongArray("ignore") : new long[]{};
        List<Long> ignoredChunks = new ArrayList<>();
        for (long c : savedIgnoredChunks) ignoredChunks.add(c);
        return ignoredChunks;
    }

    public static void addIgnoredChunk(ItemStack stack, long chunk){
        CompoundNBT tag = stack.getOrCreateTag();
        List<Long> ignoredChunks = getIgnoredChunks(tag);
        if (!ignoredChunks.contains(chunk)){
            ignoredChunks.add(chunk);
            tag.putLongArray("ignore", ignoredChunks);
            stack.setTag(tag);
        }
    }

    public static void removeIgnoredChunk(ItemStack stack, long chunk){
        CompoundNBT tag = stack.getOrCreateTag();
        List<Long> ignoredChunks = getIgnoredChunks(tag);
        ignoredChunks.remove(chunk);
        tag.putLongArray("ignore", ignoredChunks);
        stack.setTag(tag);
    }

    public static boolean isChunkIgnored(ItemStack stack, long chunk){
        CompoundNBT tag = stack.getOrCreateTag();
        List<Long> ignoredChunks = getIgnoredChunks(tag);
        return ignoredChunks.contains(chunk);
    }

    private static final Angle field_239439_a_ = new Angle();
    private static final Angle field_239440_b_ = new Angle();

    public static float getAngle(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity player) {
        Entity entity = (Entity)(player != null ? player : stack.getAttachedEntity());
        if (world == null) return 0F;
        long i = world.getGameTime();
        if (!(entity instanceof PlayerEntity)) return (Math.floorDiv(i, 20) % 40) * 0.25F;

            if (world == null && entity.world instanceof ClientWorld) {
                world = (ClientWorld)entity.world;
            }

            BlockPos blockpos = getCloseClaimedChunk(entity);
            if (blockpos != null) {
                boolean flag = player != null && ((PlayerEntity)player).isUser();
                double d1 = 0.0D;
                if (flag) {
                    d1 = (double)player.rotationYaw;
                } else if (player != null) {
                    d1 = (double)player.renderYawOffset;
                }

                d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                double d2 = posToAngle(Vector3d.copyCentered(blockpos), entity) / (double)((float)Math.PI * 2F);
                double d3;
                if (flag) {
                    if (field_239439_a_.func_239448_a_(i)) {
                        field_239439_a_.func_239449_a_(i, 0.5D - (d1 - 0.25D));
                    }

                    d3 = d2 + field_239439_a_.field_239445_a_;
                } else {
                    d3 = 0.5D - (d1 - 0.25D - d2);
                }

                return MathHelper.positiveModulo((float)d3, 1.0F);
            } else {
                // spin
                return (float) (((i * 10) % 360)  / 360.0D);
            }

    }

    private static BlockPos getCloseClaimedChunk(Entity entity) {
        BlockPos pos = toLookAt.get(entity.getUniqueID());
        if (pos == null || pos.getY() == 0) return null;
        return pos;
    }

    private static double posToAngle(Vector3d p_239443_1_, Entity p_239443_2_) {
        return Math.atan2(p_239443_1_.getZ() - p_239443_2_.getPosZ(), p_239443_1_.getX() - p_239443_2_.getPosX());
    }

    static class Angle {
        private double field_239445_a_;
        private double field_239446_b_;
        private long field_239447_c_;

        private Angle() {
        }

        private boolean func_239448_a_(long p_239448_1_) {
            return this.field_239447_c_ != p_239448_1_;
        }

        private void func_239449_a_(long p_239449_1_, double p_239449_3_) {
            this.field_239447_c_ = p_239449_1_;
            double d0 = p_239449_3_ - this.field_239445_a_;
            d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
            this.field_239446_b_ += d0 * 0.1D;
            this.field_239446_b_ *= 0.8D;
            this.field_239445_a_ = MathHelper.positiveModulo(this.field_239445_a_ + this.field_239446_b_, 1.0D);
        }
    }
}
