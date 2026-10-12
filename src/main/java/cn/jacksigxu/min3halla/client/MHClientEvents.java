package cn.jacksigxu.min3halla.client;

import cn.jacksigxu.min3halla.MHMod;
import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.item.PortDrinkItem;
import cn.jacksigxu.min3halla.client.tooltip.ShakerPreviewTooltip;
import cn.jacksigxu.min3halla.client.tooltip.ClientShakerPreviewTooltip;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import cn.jacksigxu.min3halla.util.ShakerData;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import cn.jacksigxu.min3halla.gui.screen.FermentBarrelScreen;
import cn.jacksigxu.min3halla.gui.screen.DrinkMixerScreen;
import cn.jacksigxu.min3halla.gui.screen.BTCFillingMachineScreen;
import cn.jacksigxu.min3halla.init.MHMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = MHMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class MHClientEvents {

    @SubscribeEvent public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                MHItems.SHAKER_POT.get(),
                ResourceLocation.fromNamespaceAndPath(MHMod.MOD_ID, "type"),
                (stack, level, holder, seed) ->
                        !ShakerData.hasResult(stack) ? 0.0F : ShakerData.finished(stack) ? 2.0F : 1.0F
        ));
    }

    @SubscribeEvent public static void registerShakerPreview(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ShakerPreviewTooltip.class, ClientShakerPreviewTooltip::new);
    }

    @SubscribeEvent public static void registerDrinkRendering(RegisterClientExtensionsEvent event) {
        IClientItemExtensions largeDrinkTransform = new IClientItemExtensions() {
            @Override public boolean applyForgeHandTransform(PoseStack pose, LocalPlayer player, HumanoidArm arm,
                    ItemStack stack, float partialTick, float equipProcess, float swingProcess) {
                if (stack.getItem() instanceof PortDrinkItem drink && drink.getUseDuration(stack, player) > 32) {
                    pose.scale(1.5F, 1.5F, 1.5F);
                }

                return false;
            }
        };
        for (var entry : MHItems.ITEMS.getEntries()) {
            if (entry.get() instanceof PortDrinkItem) event.registerItem(largeDrinkTransform, entry.get());
        }
    }

    @SubscribeEvent public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MHMenuTypes.FERMENT_BARREL.get(), FermentBarrelScreen::new);
        event.register(MHMenuTypes.DRINK_MIXER.get(), DrinkMixerScreen::new);
        event.register(MHMenuTypes.BTC_FILLING_MACHINE.get(), BTCFillingMachineScreen::new);
    }
}
