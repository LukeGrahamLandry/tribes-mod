package io.github.lukegrahamlandry.tribes.blocks;

import io.github.lukegrahamlandry.tribes.init.BlockInit;
import io.github.lukegrahamlandry.tribes.tribe_data.LandClaimHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AlterBlock extends Block {
    public AlterBlock(AbstractBlock.Properties props) {
        super(props);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        LandClaimHelper.onAlterPlaced(worldIn, pos, placer);
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        removeAdjacentAlters(world, pos);
        LandClaimHelper.onAlterBroken(world, pos, explosion.getExploder());
    }

    // call onBroken when broken by hand

    @Override
    public void onProjectileCollision(World worldIn, BlockState state, BlockRayTraceResult hit, ProjectileEntity projectile) {
        removeAdjacentAlters(worldIn, hit.getPos());
        LandClaimHelper.onAlterBroken(worldIn, hit.getPos(), projectile.func_234616_v_());
        // do something clever with knowing which pos an alter is at
        // because one hit by projectile might not be the one saved as where the alter is
        // probably have to save all alter positions and recheck them on every onBroken call
    }

    private void removeAdjacentAlters(World world, BlockPos pos){
        if (world.getBlockState(pos).getBlock() == BlockInit.ALTER.get()){
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
            for (int i=0;i<6;i++){
                Direction dir = Direction.byIndex(i);
                removeAdjacentAlters(world, pos.offset(dir));
            }
        }
    }

    // TODO: block states for multiblock
    // TODO: check line of sight
    // TODO:
}
