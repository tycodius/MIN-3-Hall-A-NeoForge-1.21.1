package cn.jacksigxu.min3halla.compat.jei;

import cn.jacksigxu.min3halla.gui.screen.BTCFillingMachineScreen;
import cn.jacksigxu.min3halla.gui.screen.DrinkMixerScreen;
import cn.jacksigxu.min3halla.gui.screen.FermentBarrelScreen;
import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.init.MHRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;

@JeiPlugin
public final class MHJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("min3halla", "jei_plugin");
    }
    @Override public void registerCategories(IRecipeCategoryRegistration registration) {
        var helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new MixingCategory(helper), new FermentingCategory(helper), new FillingCategory(helper),
                new WanderingTraderCategory(helper));
    }
    @Override public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MHItems.DRINK_MIXER.get()), MixingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MHItems.FERMENT_BARREL.get()), FermentingCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MHItems.BTC_FILLING_MACHINE.get()), FillingCategory.TYPE);
    }
    @Override public void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) return;
        var manager = level.getRecipeManager();
        registration.addRecipes(MixingCategory.TYPE, manager.getAllRecipesFor(MHRecipes.MIXING_TYPE.get()).stream()

                .map(RecipeHolder::value)
                .filter(recipe -> !recipe.big)
                .filter(recipe -> !recipe.getResultItem(null).is(MHItems.RUM.get())
                        && !recipe.getResultItem(null).is(MHItems.ABSINTHE.get()))
                .toList());
        registration.addRecipes(FermentingCategory.TYPE, manager.getAllRecipesFor(MHRecipes.FERMENTING_TYPE.get()).stream()
                .map(RecipeHolder::value).toList());
        registration.addRecipes(FillingCategory.TYPE, manager.getAllRecipesFor(MHRecipes.FILLING_TYPE.get()).stream()
                .map(RecipeHolder::value).toList());

        registration.addRecipes(WanderingTraderCategory.TYPE, java.util.List.of(
                new WanderingTraderCategory.Trade(new ItemStack(MHItems.MULAN_TEA.get()), 32),
                new WanderingTraderCategory.Trade(new ItemStack(MHItems.A_FEDORA.get()), 32),
                new WanderingTraderCategory.Trade(new ItemStack(MHItems.RUM.get()), 32),
                new WanderingTraderCategory.Trade(new ItemStack(MHItems.ABSINTHE.get()), 32)
        ));
    }
    @Override public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(DrinkMixerScreen.class, 98, 88, 46, 13, MixingCategory.TYPE);
        registration.addRecipeClickArea(FermentBarrelScreen.class, 73, 34, 22, 15, FermentingCategory.TYPE);
        registration.addRecipeClickArea(BTCFillingMachineScreen.class, 84, 31, 22, 15, FillingCategory.TYPE);
    }
}
