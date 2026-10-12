package cn.jacksigxu.min3halla.init;

import cn.jacksigxu.min3halla.MHMod;
import cn.jacksigxu.min3halla.block.entity.FermentBarrelBlockEntity;
import cn.jacksigxu.min3halla.block.entity.DrinkMixerBlockEntity;
import cn.jacksigxu.min3halla.block.entity.BTCFillingMachineBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MHBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MHMod.MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermentBarrelBlockEntity>> FERMENT_BARREL =
            TYPES.register("ferment_barrel_block_entity", () -> BlockEntityType.Builder.of(
                    FermentBarrelBlockEntity::new, MHBlocks.FERMENT_BARREL.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DrinkMixerBlockEntity>> DRINK_MIXER =
            TYPES.register("drink_mixer_block_entity", () -> BlockEntityType.Builder.of(
                    DrinkMixerBlockEntity::new, MHBlocks.DRINK_MIXER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BTCFillingMachineBlockEntity>> BTC_FILLING_MACHINE =
            TYPES.register("btc_filling_machine_block_entity", () -> BlockEntityType.Builder.of(
                    BTCFillingMachineBlockEntity::new, MHBlocks.BTC_FILLING_MACHINE.get()).build(null));
    private MHBlockEntityTypes() {}
}
