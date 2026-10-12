package cn.jacksigxu.min3halla.client;

import cn.jacksigxu.min3halla.MHMod;
import cn.jacksigxu.min3halla.item.PortDrinkItem;
import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = MHMod.MOD_ID, value = Dist.CLIENT)
public final class MHClientTooltipEvents {
    @SubscribeEvent public static void appendShiftDetails(ItemTooltipEvent event) {
        if (Screen.hasShiftDown() && event.getItemStack().getItem() instanceof PortDrinkItem) {
            PortDrinkItem.appendStyleTooltip(event.getItemStack(), event.getToolTip());
        }
    }
}
