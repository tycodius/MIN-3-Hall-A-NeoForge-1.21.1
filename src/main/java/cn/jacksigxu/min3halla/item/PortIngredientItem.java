package cn.jacksigxu.min3halla.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

public class PortIngredientItem extends Item {
    private final String description;
    public PortIngredientItem(String description) {
        super(new Item.Properties());
        this.description = description;
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(description).withStyle(ChatFormatting.RED));
    }
}
