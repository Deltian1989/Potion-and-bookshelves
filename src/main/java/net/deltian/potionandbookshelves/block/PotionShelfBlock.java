package net.deltian.potionandbookshelves.block;

import net.deltian.potionandbookshelves.PotionAndBookshelves;
import net.deltian.potionandbookshelves.block.entity.PotionShelfBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PotionShelfBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING =  HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHELF1 = Block.box(1, 11.5, 0.5, 15, 12, 7);
    private static final VoxelShape SHELF2 = Block.box(1, 6, 0.5, 15, 6.5, 7);
    private static final VoxelShape SHELF3 = Block.box(1, 0.5, 0.5, 15, 1, 7);
    private static final VoxelShape SIDE1 = Block.box(15, 0, 0, 16, 16, 8);
    private static final VoxelShape SIDE2 = Block.box(0, 0, 0, 1, 16, 8);

    // Combine all the parts into one VoxelShape
    private static final VoxelShape SHAPE_NORTH = Shapes.or(SHELF1, SHELF2, SHELF3, SIDE1, SIDE2);
    private static final VoxelShape SHAPE_EAST = rotateShape(SHAPE_NORTH, Direction.EAST);
    private static final VoxelShape SHAPE_SOUTH = rotateShape(SHAPE_NORTH, Direction.SOUTH);
    private static final VoxelShape SHAPE_WEST = rotateShape(SHAPE_NORTH, Direction.WEST);

    protected final Supplier<BlockEntityType<? extends PotionShelfBlockEntity>> blockEntityType;

    public PotionShelfBlock(Properties pProperties, Supplier<BlockEntityType<? extends PotionShelfBlockEntity>> blockEntityType) {
        super(pProperties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
        this.blockEntityType = blockEntityType;
    }

    private static VoxelShape rotateShape(VoxelShape shape, Direction direction) {
        switch (direction) {
            case EAST:
                return shapeRotateY(shape, 90);
            case SOUTH:
                return shapeRotateY(shape, 180);
            case WEST:
                return shapeRotateY(shape, 270);
            default:
                return shape;
        }
    }

    private static VoxelShape shapeRotateY(VoxelShape shape, int angle) {
        VoxelShape[] buffer = { shape, Shapes.empty() };

        int rotations = angle / 90;
        for (int i = 0; i < rotations; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                double newMinX = 1.0 - maxZ;
                double newMaxX = 1.0 - minZ;
                double newMinZ = minX;
                double newMaxZ = maxX;
                buffer[1] = Shapes.or(buffer[1], Shapes.box(newMinX, minY, newMinZ, newMaxX, maxY, newMaxZ));
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    @Override
    @Deprecated
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            MenuProvider menuProvider = this.getMenuProvider(blockState, level, blockPos);

            BlockEntity blockentity = level.getBlockEntity(blockPos);
            if (blockentity instanceof PotionShelfBlockEntity) {
                player.openMenu((PotionShelfBlockEntity) blockentity);
            }

            return InteractionResult.CONSUME;
        }
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {

        Direction direction = pState.getValue(FACING);

        switch (direction){
            case SOUTH:
                return SHAPE_SOUTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
            default:{
                return SHAPE_NORTH;
            }
        }

    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof Container) {
                Containers.dropContents(pLevel, pPos, (Container)blockentity);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new PotionShelfBlockEntity(pPos, pState);
    }
}
