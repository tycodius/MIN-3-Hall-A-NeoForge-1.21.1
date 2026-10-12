package cn.jacksigxu.min3halla.item;

import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.client.tooltip.ShakerPreviewTooltip;
import cn.jacksigxu.min3halla.util.ShakerData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Optional;

public final class ShakerPot extends Item {
    public ShakerPot() { super(new Item.Properties().stacksTo(1)); }
    @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack shaker = player.getItemInHand(hand);
        if (!ShakerData.hasResult(shaker)) return InteractionResultHolder.pass(shaker);
        if (!ShakerData.finished(shaker)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(shaker);
        }
        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack glass = player.getItemInHand(other);
        if (!glass.is(MHItems.WINE_GLASS.get())) return InteractionResultHolder.fail(shaker);
        if (!level.isClientSide()) {
            ItemStack result = ShakerData.result(shaker);
            if (result.isEmpty()) return InteractionResultHolder.fail(shaker);
            var metadata = ShakerData.read(shaker);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, result, tag -> {
                if (metadata.getInt("Alcohol") > 0) tag.putInt("Alcohol", metadata.getInt("Alcohol"));
                if (metadata.getBoolean("Big")) tag.putBoolean("Big", true);
            });
            if (!player.getAbilities().instabuild) glass.shrink(1);
            if (!player.addItem(result)) player.drop(result, false);
            ShakerData.clear(shaker);
        }
        return InteractionResultHolder.sidedSuccess(shaker, level.isClientSide());
    }
    @Override public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remaining) {
        if (level.isClientSide() || !ShakerData.hasResult(stack)) return;
        int held = getUseDuration(stack, entity) - remaining;
        if (held < 100) return;
        if (held >= 200 && !ShakerData.read(stack).getBoolean("Blend"))
            ShakerData.corrupt(stack, new ItemStack(MHItems.ERROR_DRINK.get()));
        else if (held >= 200) ShakerData.finish(stack);
        else if (!ShakerData.read(stack).getBoolean("Blend")) ShakerData.finish(stack);
    }
    @Override public int getUseDuration(ItemStack stack, LivingEntity user) { return 72000; }
    @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.BRUSH; }
    @Override public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (!ShakerData.hasResult(stack)) return Optional.empty();
        ItemStack drink = ShakerData.result(stack);
        if (drink.isEmpty()) return Optional.empty();
        var metadata = ShakerData.read(stack);
        Component status = Component.translatable(ShakerData.finished(stack)
                ? "des.min3halla.shaker_pot.finished"
                : metadata.getBoolean("Blend") ? "des.min3halla.shaker_pot.blend" : "des.min3halla.shaker_pot.mix")
                .withStyle(ShakerData.finished(stack) ? ChatFormatting.GREEN
                        : metadata.getBoolean("Blend") ? ChatFormatting.GOLD : ChatFormatting.YELLOW);
        return Optional.of(new ShakerPreviewTooltip(drink, status));
    }
}
