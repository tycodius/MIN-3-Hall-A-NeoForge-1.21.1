package cn.jacksigxu.min3halla.block;

import com.mojang.serialization.MapCodec;

import cn.jacksigxu.min3halla.block.entity.BTCFillingMachineBlockEntity;
import cn.jacksigxu.min3halla.init.MHBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
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
import org.jetbrains.annotations.Nullable;

public final class BTCFillingMachineBlock extends BaseEntityBlock {
    public static final MapCodec<BTCFillingMachineBlock> CODEC = simpleCodec(BTCFillingMachineBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BTCFillingMachineBlock() {
        this(Properties.of().strength(2f));
    }

    public BTCFillingMachineBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override public RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                        Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BTCFillingMachineBlockEntity entity) {
            player.openMenu(entity);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BTCFillingMachineBlockEntity(pos, state);
    }

    @Override protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState next, boolean moved) {
        if (!state.is(next.getBlock()) && level instanceof ServerLevel server
                && level.getBlockEntity(pos) instanceof BTCFillingMachineBlockEntity machine) {
            Containers.dropContents(server, pos, machine);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, next, moved);
    }

    @Override @Nullable public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, MHBlockEntityTypes.BTC_FILLING_MACHINE.get(),
                BTCFillingMachineBlockEntity::serverTick);
    }
}
