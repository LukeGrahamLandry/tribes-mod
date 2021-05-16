package io.github.lukegrahamlandry.tribes.item;

import io.github.lukegrahamlandry.tribes.TribesMain;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class TribeCompass extends Item {
    public static HashMap<UUID, BlockPos> toLookAt = new HashMap<>();

    public TribeCompass(Properties properties) {
        super(properties);

        ItemModelsProperties.registerProperty(this, new ResourceLocation("angle"), TribeCompass::getAngle);
    }

    private static final Angle field_239439_a_ = new Angle();
    private static final Angle field_239440_b_ = new Angle();

    public static float getAngle(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity player) {
        Entity entity = (Entity)(player != null ? player : stack.getAttachedEntity());

        if (entity == null) return 0.0F;
        if (!(entity instanceof PlayerEntity)) return 0;

            if (world == null && entity.world instanceof ClientWorld) {
                world = (ClientWorld)entity.world;
            }

            BlockPos blockpos = getCloseClaimedChunk(entity);
            long i = world.getGameTime();
            if (blockpos != null && !(entity.getPositionVec().squareDistanceTo((double)blockpos.getX() + 0.5D, entity.getPositionVec().getY(), (double)blockpos.getZ() + 0.5D) < (double)1.0E-5F)) {
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
                // spin randomlly
                return (float) ((i % 360)  / 360.0D);
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

    @OnlyIn(Dist.CLIENT)
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
