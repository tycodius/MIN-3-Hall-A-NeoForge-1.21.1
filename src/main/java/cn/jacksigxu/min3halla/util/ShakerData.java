package cn.jacksigxu.min3halla.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public final class ShakerData {
    private ShakerData() {}
    public static CompoundTag read(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }
    public static boolean hasResult(ItemStack stack) { return read(stack).contains("Result", 8); }
    public static boolean finished(ItemStack stack) { return read(stack).getBoolean("Finished"); }
    public static void fill(ItemStack stack, ItemStack result, boolean blend, int alcohol, boolean big) {
        if (stack.isEmpty() || result.isEmpty() || hasResult(stack)) return;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(result.getItem());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            tag.putString("Result", id.toString());
            tag.putBoolean("Finished", false);
            tag.putBoolean("Blend", blend);
            tag.putInt("Alcohol", Math.max(0, alcohol));
            tag.putBoolean("Big", big);
        });
    }
    public static ItemStack result(ItemStack stack) {
        String id = read(stack).getString("Result");
        ResourceLocation loc = ResourceLocation.tryParse(id);
        if (loc == null || !BuiltInRegistries.ITEM.containsKey(loc)) return ItemStack.EMPTY;
        var item = BuiltInRegistries.ITEM.get(loc);
        return item == Items.AIR ? ItemStack.EMPTY : new ItemStack(item);
    }
    public static void finish(ItemStack stack) {
        if (!hasResult(stack)) return;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("Finished", true));
    }
    public static void corrupt(ItemStack stack, ItemStack errorDrink) {
        if (!hasResult(stack)) return;
        clear(stack);
        fill(stack, errorDrink, false, 20, false);
        finish(stack);
    }
    public static void clear(ItemStack stack) {
        if (stack.isEmpty()) return;
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
            for (String key : new String[]{"Result", "Finished", "Blend", "Alcohol", "Big"}) tag.remove(key);
        });
    }
}
