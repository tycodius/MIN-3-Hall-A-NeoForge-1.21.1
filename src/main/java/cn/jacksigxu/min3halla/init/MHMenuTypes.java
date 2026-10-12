package cn.jacksigxu.min3halla.init;

import cn.jacksigxu.min3halla.MHMod;
import cn.jacksigxu.min3halla.gui.menu.FermentBarrelMenu;
import cn.jacksigxu.min3halla.gui.menu.DrinkMixerMenu;
import cn.jacksigxu.min3halla.gui.menu.BTCFillingMachineMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MHMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MHMod.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<FermentBarrelMenu>> FERMENT_BARREL =
            MENUS.register("ferment_barrel_menu", () -> new MenuType<>(FermentBarrelMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<DrinkMixerMenu>> DRINK_MIXER =
            MENUS.register("drink_mixer_menu", () -> new MenuType<>(DrinkMixerMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final DeferredHolder<MenuType<?>, MenuType<BTCFillingMachineMenu>> BTC_FILLING_MACHINE =
            MENUS.register("btc_filling_machine_menu", () -> new MenuType<>(BTCFillingMachineMenu::new, FeatureFlags.DEFAULT_FLAGS));
    private MHMenuTypes() {}
}
