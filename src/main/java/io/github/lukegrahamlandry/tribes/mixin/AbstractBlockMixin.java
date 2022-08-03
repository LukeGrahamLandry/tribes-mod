package io.github.lukegrahamlandry.tribes.mixin;

import io.github.lukegrahamlandry.tribes.events.ClaimedLandBlocker;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BannerBlock.class)
public abstract class AbstractBlockMixin {
    @Inject(at = @At("RETURN"), method = "updateShape")
    private void tribesUpdateShape(BlockState selfState, Direction p_196271_2_, BlockState checkState, IWorld world, BlockPos selfPos, BlockPos checkPos, CallbackInfoReturnable<BlockState> cir) {
        if (cir.getReturnValue().is(Blocks.AIR)) ClaimedLandBlocker.onBlockBreakCheckBanner(world, selfPos, selfState);
    }
}