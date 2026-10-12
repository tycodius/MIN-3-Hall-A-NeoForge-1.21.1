package cn.jacksigxu.min3halla.client.tooltip;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public record ShakerPreviewTooltip(ItemStack drink, Component status) implements TooltipComponent { }
