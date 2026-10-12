package cn.jacksigxu.min3halla.compat.jei;

import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.recipe.FermentingRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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

public final class FermentingCategory implements IRecipeCategory<FermentingRecipe> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("min3halla", "textures/gui/ferment_barrel.png");
    public static final RecipeType<FermentingRecipe> TYPE = new RecipeType<>(
            ResourceLocation.fromNamespaceAndPath("min3halla", "fermenting"), FermentingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public FermentingCategory(IGuiHelper helper) {
        background = helper.drawableBuilder(TEXTURE, 30, 22, 116, 38)
                .setTextureSize(256, 256).build();
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(MHItems.FERMENT_BARREL.get()));
        arrow = helper.drawableBuilder(TEXTURE, 177, 0, 22, 16)
                .setTextureSize(256, 256)
                .buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override public RecipeType<FermentingRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() {
        return Component.translatable("block.min3halla.ferment_barrel");
    }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 116; }
    @Override public int getHeight() { return 38; }

    @Override
    public void draw(FermentingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
        arrow.draw(graphics, 43, 11);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FermentingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 11).addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 11).addItemStack(recipe.getResultItem(null));
    }
}
