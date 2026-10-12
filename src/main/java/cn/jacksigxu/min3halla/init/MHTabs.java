package cn.jacksigxu.min3halla.init;

import cn.jacksigxu.min3halla.MHMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MHTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MHMod.MOD_ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEM_TAB = TABS.register("mh_tab", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.min3halla_tab"))
                    .icon(() -> new ItemStack(MHItems.SUGAR_RUSH.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(MHItems.FERMENT_BARREL.get());
                        output.accept(MHItems.DRINK_MIXER.get());
                        output.accept(MHItems.BTC_FILLING_MACHINE.get());
                        output.accept(MHItems.SHAKER_POT.get());
                        output.accept(MHItems.WINE_GLASS.get());
                        output.accept(MHItems.ADELHYDE.get());
                        output.accept(MHItems.BRONSON_EXTRACT.get());
                        output.accept(MHItems.POWDERED_DELTA.get());
                        output.accept(MHItems.FLANERGIDE.get());
                        output.accept(MHItems.KARMOTRINE.get());
                        output.accept(MHItems.BTC_CAN.get());
                        output.accept(MHItems.HYPER_ACID_X.get());
                        output.accept(MHItems.GLITCH_LIQUID.get());
                        output.accept(MHItems.BAD_TOUCH.get());
                        output.accept(MHItems.BEER.get());
                        output.accept(MHItems.BLEEDING_JANE.get());
                        output.accept(MHItems.BLOOM_LIGHT.get());
                        output.accept(MHItems.BLUE_FAIRY.get());
                        output.accept(MHItems.BRANDTINI.get());
                        output.accept(MHItems.COBALT_VELVET.get());
                        output.accept(MHItems.CREVICE_SPIKE.get());
                        output.accept(MHItems.FLAMING_MOAI.get());
                        output.accept(MHItems.FLUFFY_DREAM.get());
                        output.accept(MHItems.FRINGE_WEAVER.get());
                        output.accept(MHItems.FROTHY_WATER.get());
                        output.accept(MHItems.GRIZZLY_TEMPLE.get());
                        output.accept(MHItems.GUT_PUNCH.get());
                        output.accept(MHItems.MARSBLAST.get());
                        output.accept(MHItems.MERCURYBLAST.get());
                        output.accept(MHItems.MOONBLAST.get());
                        output.accept(MHItems.PIANO_MAN.get());
                        output.accept(MHItems.PIANO_WOMAN.get());
                        output.accept(MHItems.PILE_DRIVER.get());
                        output.accept(MHItems.SPARKLE_STAR.get());
                        output.accept(MHItems.SUGAR_RUSH.get());
                        output.accept(MHItems.SUNSHINE_CLOUD.get());
                        output.accept(MHItems.SUPLEX.get());
                        output.accept(MHItems.ZEN_STAR.get());
                        output.accept(MHItems.MULAN_TEA.get());
                        output.accept(MHItems.A_FEDORA.get());
                        output.accept(MHItems.RUM.get());
                        output.accept(MHItems.ABSINTHE.get());
                        output.accept(MHItems.ERROR_DRINK.get());
                        output.accept(MHItems.SUPERB_BASKETBALL.get());
                        output.accept(MHItems.ICED_TEA.get());
                        output.accept(MHItems.SEPTWOLVES.get());
                        output.accept(MHItems.ALARM_LAMP.get());
                        output.accept(MHItems.AFTERGLOW.get());
                        output.accept(MHItems.FRIED_RICE.get());
                    }).build());
}
