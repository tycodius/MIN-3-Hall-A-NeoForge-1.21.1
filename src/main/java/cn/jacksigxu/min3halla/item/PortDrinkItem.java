package cn.jacksigxu.min3halla.item;

import cn.jacksigxu.min3halla.init.MHItems;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.ArrayList;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class PortDrinkItem extends Item {
    private final boolean big;
    private final boolean bottled;

    private final int alcoholMode;

    public PortDrinkItem(FoodProperties food, boolean big, boolean bottled) {
        this(food, big, bottled, 0);
    }
    public PortDrinkItem(FoodProperties food, boolean big, boolean bottled, int alcoholMode) {
        super(new Item.Properties().food(food).stacksTo(16));
        this.big = big;
        this.bottled = bottled;
        this.alcoholMode = alcoholMode;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) { return true; }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return bottled ? stack.copyWithCount(1) : new ItemStack(MHItems.WINE_GLASS.get());
    }

    @Override
    public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity user) {
        FoodProperties base = super.getFoodProperties(stack, user);
        if (base == null || !cn.jacksigxu.min3halla.util.ShakerData.read(stack).getBoolean("Big")) return base;
        var builder = new FoodProperties.Builder()
                .nutrition(base.nutrition() * 2)
                .saturationModifier(base.nutrition() == 0 ? 0.0F : base.saturation() / (2.0F * base.nutrition()));
        if (base.canAlwaysEat()) builder.alwaysEdible();
        for (FoodProperties.PossibleEffect effect : base.effects()) {
            builder.effect(effect.effectSupplier(), Math.min(1.0F, effect.probability() * 2.0F));
        }
        return builder.build();
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return (big || cn.jacksigxu.min3halla.util.ShakerData.read(stack).getBoolean("Big")) ? 64 : 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide && alcoholMode == -1) {
            int alcohol = cn.jacksigxu.min3halla.util.ShakerData.read(stack).getInt("Alcohol");
            if (alcohol > 0 && level.random.nextDouble() < Math.min(1.0, alcohol * 0.1)) {
                user.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
            }
        }
        ItemStack remaining = super.finishUsingItem(stack, level, user);

        if (user instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack glass = new ItemStack(MHItems.WINE_GLASS.get());
            if (remaining.isEmpty()) return glass;
            if (!player.getInventory().add(glass)) player.drop(glass, false);
        }
        return remaining;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String descriptionKey = "des.min3halla." + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        tooltip.add(Component.translatable(descriptionKey).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        var metadata = cn.jacksigxu.min3halla.util.ShakerData.read(stack);
        if (metadata.getBoolean("Big")) tooltip.add(Component.translatable("des.min3halla.big"));
        if (alcoholMode == -1 && metadata.getInt("Alcohol") > 0) tooltip.add(Component.translatable("des.min3halla.alcohol", metadata.getInt("Alcohol")));
    }

    public static void appendStyleTooltip(ItemStack stack, List<Component> tooltip) {
        List<MutableComponent> parts = new ArrayList<>(3);
        addMatchingTag(stack, parts, "flavor", "sweet", "spicy", "sour", "bitter", "bubbly");
        addMatchingTag(stack, parts, "type", "classic", "classy", "girly", "manly", "promo");
        addMatchingTag(stack, parts, "secondary", "bland", "burning", "happy", "sobering", "soft", "strong", "vintage");
        if (parts.isEmpty()) return;
        MutableComponent line = parts.getFirst();
        for (int i = 1; i < parts.size(); i++) line.append(", ").append(parts.get(i));
        tooltip.add(line.withStyle(ChatFormatting.YELLOW));
    }
    private static void addMatchingTag(ItemStack stack, List<MutableComponent> parts, String category, String... choices) {
        for (String choice : choices) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM,
                    ResourceLocation.fromNamespaceAndPath("min3halla", category + "/" + choice));
            if (stack.is(tag)) {
                parts.add(Component.translatable("des.min3halla." + category + "." + choice));
                return;
            }
        }
    }
}
