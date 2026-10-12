package cn.jacksigxu.min3halla.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

public class PortFoodItem extends Item {
    public PortFoodItem(FoodProperties food) {
        super(new Item.Properties().food(food).stacksTo(16));
    }
}
