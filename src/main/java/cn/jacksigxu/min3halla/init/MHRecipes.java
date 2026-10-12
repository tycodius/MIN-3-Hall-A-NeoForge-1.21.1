package cn.jacksigxu.min3halla.init;

import cn.jacksigxu.min3halla.MHMod;
import cn.jacksigxu.min3halla.recipe.FermentingRecipe;
import cn.jacksigxu.min3halla.recipe.MixingRecipe;
import cn.jacksigxu.min3halla.recipe.FillingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MHRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MHMod.MOD_ID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MHMod.MOD_ID);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FermentingRecipe>> FERMENTING_TYPE =
            TYPES.register("fermenting", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MHMod.MOD_ID, "fermenting")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FermentingRecipe>> FERMENTING_SERIALIZER =
            SERIALIZERS.register("fermenting", FermentingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<MixingRecipe>> MIXING_TYPE =
            TYPES.register("mixing", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MHMod.MOD_ID, "mixing")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MixingRecipe>> MIXING_SERIALIZER =
            SERIALIZERS.register("mixing", MixingRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<FillingRecipe>> FILLING_TYPE =
            TYPES.register("filling", () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(MHMod.MOD_ID, "filling")));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FillingRecipe>> FILLING_SERIALIZER =
            SERIALIZERS.register("filling", FillingRecipe.Serializer::new);
    private MHRecipes() {}
}
