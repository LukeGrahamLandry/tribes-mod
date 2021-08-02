package io.github.lukegrahamlandry.tribes.blocks;

import io.github.lukegrahamlandry.tribes.tile.AltarTileEntity;
import io.github.lukegrahamlandry.tribes.tribe_data.DeitiesManager;
import io.github.lukegrahamlandry.tribes.tribe_data.Tribe;
import io.github.lukegrahamlandry.tribes.tribe_data.TribesManager;
import net.minecraft.world.level.block.*;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.EnumProperty;
import net.minecraft.world.level.block.state.StateContainer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import javax.annotation.Nullable;

public class AlterBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;

    public AlterBlock(AbstractBlock.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, ChestType.SINGLE));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world) {
        return new AltarTileEntity();
    }

    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (placer instanceof ServerPlayer){
            Tribe tribe = TribesManager.getTribeOf(placer.getUUID());
            if (tribe != null && tribe.deity != null){
                DeitiesManager.DeityData deity = DeitiesManager.deities.get(tribe.deity);
                AltarTileEntity tile = (AltarTileEntity) worldIn.getBlockEntity(pos);
                tile.setBannerKey(deity.bannerKey);
            }
        }
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facingState.is(this) && facing.getAxis().isHorizontal()) {
            ChestType chesttype = facingState.getValue(TYPE);
            if (stateIn.getValue(TYPE) == ChestType.SINGLE && chesttype != ChestType.SINGLE && stateIn.getValue(FACING) == facingState.getValue(FACING) && getDirectionToAttached(facingState) == facing.getOpposite()) {
                return stateIn.setValue(TYPE, chesttype.getOpposite());
            }
        } else if (getDirectionToAttached(stateIn) == facing) {
            return stateIn.setValue(TYPE, ChestType.SINGLE);
        }

        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    // todo: export different ones for each facing direction. need the bbmodel file
    protected static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D);

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    /*
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(TYPE) == ChestType.SINGLE) {
            return SHAPE_SINGLE;
        } else {
            switch(getDirectionToAttached(state)) {
                case NORTH:
                default:
                    return SHAPE_NORTH;
                case SOUTH:
                    return SHAPE_SOUTH;
                case WEST:
                    return SHAPE_WEST;
                case EAST:
                    return SHAPE_EAST;
            }
        }
    }
     */

    /**
     * Returns a facing pointing from the given state to its attached double chest
     */
    public static Direction getDirectionToAttached(BlockState state) {
        Direction direction = state.getValue(FACING);
        return state.getValue(TYPE) == ChestType.LEFT ? direction.getClockWise() : direction.getCounterClockWise();
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        ChestType chesttype = ChestType.SINGLE;
        Direction direction = context.getHorizontalDirection().getOpposite();
        boolean flag = context.isSecondaryUseActive();
        Direction direction1 = context.getClickedFace();
        if (direction1.getAxis().isHorizontal() && flag) {
            Direction direction2 = this.getDirectionToAttach(context, direction1.getOpposite());
            if (direction2 != null && direction2.getAxis() != direction1.getAxis()) {
                direction = direction2;
                chesttype = direction2.getCounterClockWise() == direction1.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
            }
        }

        if (chesttype == ChestType.SINGLE && !flag) {
            if (direction == this.getDirectionToAttach(context, direction.getClockWise())) {
                chesttype = ChestType.LEFT;
            } else if (direction == this.getDirectionToAttach(context, direction.getCounterClockWise())) {
                chesttype = ChestType.RIGHT;
            }
        }

        return this.defaultBlockState().setValue(FACING, direction).setValue(TYPE, chesttype);
    }

    /**
     * Returns facing pointing to a chest to form a double chest with, null otherwise
     */
    @Nullable
    private Direction getDirectionToAttach(BlockPlaceContext context, Direction direction) {
        BlockState blockstate = context.getLevel().getBlockState(context.getClickedPos().relative(direction));
        return blockstate.is(this) && blockstate.getValue(TYPE) == ChestType.SINGLE ? blockstate.getValue(FACING) : null;
    }

    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }
}
