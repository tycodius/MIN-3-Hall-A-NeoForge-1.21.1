package cn.jacksigxu.min3halla;

import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.init.MHBlocks;
import cn.jacksigxu.min3halla.init.MHBlockEntityTypes;
import cn.jacksigxu.min3halla.init.MHMenuTypes;
import cn.jacksigxu.min3halla.init.MHRecipes;
import cn.jacksigxu.min3halla.init.MHStats;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import cn.jacksigxu.min3halla.init.MHTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import cn.jacksigxu.min3halla.init.MHTrades;
import net.neoforged.fml.common.Mod;

@Mod(MHMod.MOD_ID)
public class MHMod {
    public static final String MOD_ID = "min3halla";
    public MHMod(IEventBus modBus) {
        MHItems.ITEMS.register(modBus);
        MHBlocks.BLOCKS.register(modBus);
        MHBlockEntityTypes.TYPES.register(modBus);
        MHMenuTypes.MENUS.register(modBus);
        MHRecipes.TYPES.register(modBus);
        MHRecipes.SERIALIZERS.register(modBus);
        MHStats.STATS.register(modBus);
        modBus.addListener(MHMod::commonSetup);
        modBus.addListener(MHMod::registerCapabilities);
        MHTabs.TABS.register(modBus);
        NeoForge.EVENT_BUS.addListener(MHTrades::addWandererTrade);
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> Stats.CUSTOM.get(MHStats.INTERACT_WITH_DRINK_MIXER.get(), StatFormatter.DEFAULT));
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                MHBlockEntityTypes.FERMENT_BARREL.get(),
                (entity, side) -> new SidedInvWrapper(entity, side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                MHBlockEntityTypes.DRINK_MIXER.get(),
                (entity, side) -> new SidedInvWrapper(entity, side));
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK,
                MHBlockEntityTypes.BTC_FILLING_MACHINE.get(),
                (entity, side) -> new SidedInvWrapper(entity, side));
    }
}

