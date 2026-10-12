package cn.jacksigxu.min3halla.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class WanderingTraderCategory implements IRecipeCategory<WanderingTraderCategory.Trade> {
    public record Trade(ItemStack result, int emeraldCost) {}

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("min3halla", "textures/gui/wandering_trader_jei.png");

    public static final RecipeType<Trade> TYPE = new RecipeType<>(
            ResourceLocation.fromNamespaceAndPath("min3halla", "wandering_trader"), Trade.class);

    private final IDrawable background;
    private final IDrawable icon;

    public WanderingTraderCategory(IGuiHelper helper) {
        background = helper.drawableBuilder(TEXTURE, 0, 0, 160, 70)
                .setTextureSize(160, 70)
                .build();
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.EMERALD));
    }

    @Override public RecipeType<Trade> getRecipeType() { return TYPE; }
    @Override public Component getTitle() { return Component.translatable("jei.min3halla.wandering_trader.title"); }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 160; }
    @Override public int getHeight() { return 70; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Trade recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 24, 25)
                .addItemStack(new ItemStack(Items.EMERALD, recipe.emeraldCost()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 25)
                .addItemStack(recipe.result().copy());
    }

    @Override
    public void draw(Trade recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
    }
}
