package cn.jacksigxu.min3halla.compat.jei;

import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.recipe.MixingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class MixingCategory implements IRecipeCategory<MixingRecipe> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("min3halla", "textures/gui/drink_mixer_jei.png");

    public static final RecipeType<MixingRecipe> TYPE = new RecipeType<>(
            ResourceLocation.fromNamespaceAndPath("min3halla", "mixing"), MixingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public MixingCategory(IGuiHelper helper) {
        background = helper.drawableBuilder(TEXTURE, 0, 0, 180, 100)
                .setTextureSize(256, 256).build();
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(MHItems.DRINK_MIXER.get()));
    }

    @Override public RecipeType<MixingRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() {
        return Component.translatable("block.min3halla.drink_mixer");
    }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 180; }
    @Override public int getHeight() { return 100; }

    @Override
    public void draw(MixingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics,
                     double mouseX, double mouseY) {

        background.draw(graphics, 0, 0);
        renderIngredientMeter(graphics, 26, 13, 0, 119, recipe.ade);
        renderIngredientMeter(graphics, 86, 13, 5, 126, recipe.bex);
        renderIngredientMeter(graphics, 146, 13, 10, 133, recipe.pwd);
        renderIngredientMeter(graphics, 26, 51, 15, 140, recipe.fla);
        renderIngredientMeter(graphics, 146, 51, 20, 147, recipe.kar);
    }

    private static void renderIngredientMeter(GuiGraphics graphics, int x, int y,
                                               int u, int optionalV, int count) {
        if (count == -1) {

            graphics.blit(TEXTURE, x + 5, y + 7, 0, optionalV, 14, 7);
            return;
        }

        for (int i = 0; i < Math.min(5, count); i++)
            graphics.blit(TEXTURE, x + i * 5, y, u, 101, 4, 9);
        for (int i = 0; i < Math.min(5, count - 5); i++)
            graphics.blit(TEXTURE, x + i * 5, y + 11, u, 101, 4, 9);
        for (int i = 0; i < Math.min(5, count - 10); i++)
            graphics.blit(TEXTURE, x + i * 5, y, u, 110, 4, 9);
        for (int i = 0; i < Math.min(5, count - 15); i++)
            graphics.blit(TEXTURE, x + i * 5, y + 11, u, 110, 4, 9);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MixingRecipe recipe, IFocusGroup focuses) {
        addMeasuredInput(builder, MHItems.ADELHYDE.get().getDefaultInstance(), recipe.ade, 7, 15);
        addMeasuredInput(builder, MHItems.BRONSON_EXTRACT.get().getDefaultInstance(), recipe.bex, 67, 15);
        addMeasuredInput(builder, MHItems.POWDERED_DELTA.get().getDefaultInstance(), recipe.pwd, 127, 15);
        addMeasuredInput(builder, MHItems.FLANERGIDE.get().getDefaultInstance(), recipe.fla, 7, 53);
        addMeasuredInput(builder, MHItems.KARMOTRINE.get().getDefaultInstance(), recipe.kar, 127, 53);

        if (recipe.ice)
            builder.addSlot(RecipeIngredientRole.INPUT, 5, 81).addItemStack(new ItemStack(Items.ICE));
        if (recipe.age)
            builder.addSlot(RecipeIngredientRole.INPUT, 41, 81).addItemStack(new ItemStack(Items.REDSTONE));
        if (!recipe.dye.isEmpty())
            builder.addSlot(RecipeIngredientRole.INPUT, 123, 81).addIngredients(recipe.dye);
        if (!recipe.extra.isEmpty())
            builder.addSlot(RecipeIngredientRole.INPUT, 161, 81).addIngredients(recipe.extra);

        builder.addSlot(RecipeIngredientRole.INPUT, 82, 79)
                .addItemStack(new ItemStack(MHItems.SHAKER_POT.get()))
                .addTooltipCallback((slot, tooltip) -> {
                    if (recipe.blend) {
                        tooltip.add(Component.translatable("des.min3halla.shaker_pot.blend")
                                .append(" (10s+)").withStyle(ChatFormatting.GOLD));
                    } else {
                        tooltip.add(Component.translatable("des.min3halla.shaker_pot.mix")
                                .append(" (5 ~ 10s)").withStyle(ChatFormatting.YELLOW));
                    }
                });
        builder.addSlot(RecipeIngredientRole.OUTPUT, 82, 53)
                .addItemStack(recipe.getResultItem(null));
    }

    private static void addMeasuredInput(IRecipeLayoutBuilder builder, ItemStack stack,
                                         int amount, int x, int y) {
        if (amount == 0) return;
        stack.setCount(Math.max(1, amount));
        builder.addSlot(RecipeIngredientRole.INPUT, x, y)
                .addItemStack(stack)
                .addTooltipCallback((slot, tooltip) -> {
                    if (amount == -1)
                        tooltip.add(Component.translatable("des.min3halla.recipe.optional")
                                .withStyle(ChatFormatting.GOLD));
                });
    }
}
