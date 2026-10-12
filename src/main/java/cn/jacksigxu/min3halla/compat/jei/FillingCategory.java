package cn.jacksigxu.min3halla.compat.jei;

import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.recipe.FillingRecipe;
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

public final class FillingCategory implements IRecipeCategory<FillingRecipe> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("min3halla", "textures/gui/btc_filling_machine.png");
    public static final RecipeType<FillingRecipe> TYPE = new RecipeType<>(
            ResourceLocation.fromNamespaceAndPath("min3halla", "filling"), FillingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public FillingCategory(IGuiHelper helper) {
        background = helper.drawableBuilder(TEXTURE, 20, 8, 134, 67)
                .setTextureSize(256, 256).build();
        icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(MHItems.BTC_FILLING_MACHINE.get()));
        arrow = helper.drawableBuilder(TEXTURE, 177, 0, 22, 16)
                .setTextureSize(256, 256)
                .buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override public RecipeType<FillingRecipe> getRecipeType() { return TYPE; }
    @Override public Component getTitle() {
        return Component.translatable("block.min3halla.btc_filling_machine");
    }
    @Override public IDrawable getIcon() { return icon; }
    @Override public int getWidth() { return 134; }
    @Override public int getHeight() { return 67; }

    @Override
    public void draw(FillingRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics,
                     double mouseX, double mouseY) {
        background.draw(graphics, 0, 0);
        arrow.draw(graphics, 64, 22);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FillingRecipe recipe, IFocusGroup focuses) {
        var inputs = recipe.inputs();
        if (!inputs.isEmpty())
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 22).addIngredients(inputs.get(0));
        if (inputs.size() > 1)
            builder.addSlot(RecipeIngredientRole.INPUT, 36, 22).addIngredients(inputs.get(1));
        builder.addSlot(RecipeIngredientRole.INPUT, 104, 4)
                .addItemStack(new ItemStack(MHItems.BTC_CAN.get()));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 104, 43)
                .addItemStack(recipe.getResultItem(null));
    }
}
