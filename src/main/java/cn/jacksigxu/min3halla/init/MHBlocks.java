package cn.jacksigxu.min3halla.init;

import cn.jacksigxu.min3halla.MHMod;
import cn.jacksigxu.min3halla.block.FermentBarrelBlock;
import cn.jacksigxu.min3halla.block.DrinkMixerBlock;
import cn.jacksigxu.min3halla.block.BTCFillingMachineBlock;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MHBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MHMod.MOD_ID);
    public static final DeferredBlock<FermentBarrelBlock> FERMENT_BARREL =
            BLOCKS.register("ferment_barrel", FermentBarrelBlock::new);
    public static final DeferredBlock<DrinkMixerBlock> DRINK_MIXER =
            BLOCKS.register("drink_mixer", () -> new DrinkMixerBlock());
    public static final DeferredBlock<BTCFillingMachineBlock> BTC_FILLING_MACHINE =
            BLOCKS.register("btc_filling_machine", () -> new BTCFillingMachineBlock());
    private MHBlocks() {}
}
