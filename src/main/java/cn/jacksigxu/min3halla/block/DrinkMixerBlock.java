package cn.jacksigxu.min3halla.block;

import com.mojang.serialization.MapCodec;

import cn.jacksigxu.min3halla.block.entity.DrinkMixerBlockEntity;
import cn.jacksigxu.min3halla.init.MHBlockEntityTypes;
import cn.jacksigxu.min3halla.init.MHStats;
import net.minecraft.stats.Stats;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public final class DrinkMixerBlock extends BaseEntityBlock {
    public static final MapCodec<DrinkMixerBlock> CODEC = simpleCodec(DrinkMixerBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public DrinkMixerBlock() {
        this(Properties.of().strength(3f).requiresCorrectToolForDrops());
    }

    public DrinkMixerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction dir = state.getValue(FACING);
        return dir.getAxis() == Direction.Axis.Z ? box(0, 0, 1, 16, 16, 15) : box(1, 0, 0, 15, 16, 16);
    }
    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }
    @Override public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (state.getValue(FACING) != hit.getDirection()) return InteractionResult.PASS;
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof DrinkMixerBlockEntity mixer) {
            player.openMenu(mixer);
            player.awardStat(Stats.CUSTOM.get(MHStats.INTERACT_WITH_DRINK_MIXER.get()));
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) { builder.add(FACING); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new DrinkMixerBlockEntity(pos, state); }
    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moved) {
        if (!state.is(next.getBlock()) && level instanceof ServerLevel server && level.getBlockEntity(pos) instanceof DrinkMixerBlockEntity entity) {
            Containers.dropContents(server, pos, entity);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, next, moved);
    }
    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, MHBlockEntityTypes.DRINK_MIXER.get(), DrinkMixerBlockEntity::serverTick);
    }
}
