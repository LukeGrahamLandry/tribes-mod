package io.github.lukegrahamlandry.tribes.mixin;

import io.github.lukegrahamlandry.tribes.events.ClaimedLandBlocker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {
    @Shadow public abstract BlockState defaultBlockState();

    @Inject(at = @At("HEAD"), method = "wasExploded")
    private void tribesWasExploded(World p_180652_1_, BlockPos p_180652_2_, Explosion p_180652_3_, CallbackInfo ci) {
        ClaimedLandBlocker.onBlockBreakCheckBanner(p_180652_1_, p_180652_2_, defaultBlockState());
    }
}