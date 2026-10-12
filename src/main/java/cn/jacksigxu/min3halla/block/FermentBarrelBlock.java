package cn.jacksigxu.min3halla.block;

import cn.jacksigxu.min3halla.block.entity.FermentBarrelBlockEntity;
import cn.jacksigxu.min3halla.init.MHBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class FermentBarrelBlock extends Block implements EntityBlock {
    public FermentBarrelBlock() { super(BlockBehaviour.Properties.of().strength(2.0f)); }
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FermentBarrelBlockEntity barrel) {
            player.openMenu(barrel);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FermentBarrelBlockEntity(pos, state); }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (!level.isClientSide() && level.getBlockEntity(pos) instanceof FermentBarrelBlockEntity barrel) {
                Containers.dropContents(level, pos, barrel);
            }
        }
        super.onRemove(state, level, pos, newState, moved);
    }
    @Nullable @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide() || type != MHBlockEntityTypes.FERMENT_BARREL.get()) return null;
        return (l, p, s, entity) -> FermentBarrelBlockEntity.serverTick(l, p, s, (FermentBarrelBlockEntity) entity);
    }
}
