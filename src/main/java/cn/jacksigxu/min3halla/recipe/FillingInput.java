package cn.jacksigxu.min3halla.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record FillingInput(ItemStack first, ItemStack second) implements RecipeInput {
    @Override public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> first;
            case 1 -> second;
            default -> throw new IndexOutOfBoundsException(index);
        };
    }
    @Override public int size() { return 2; }
}
