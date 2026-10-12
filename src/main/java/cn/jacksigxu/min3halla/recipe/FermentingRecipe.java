package cn.jacksigxu.min3halla.recipe;

import cn.jacksigxu.min3halla.init.MHRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

public final class FermentingRecipe implements Recipe<SingleRecipeInput> {
    private final Ingredient input;
    private final ItemStack output;
    public FermentingRecipe(Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
    }
    public Ingredient input() { return input; }
    public ItemStack output() { return output; }
    @Override public boolean matches(SingleRecipeInput inventory, Level level) { return input.test(inventory.item()); }
    @Override public ItemStack assemble(SingleRecipeInput inventory, HolderLookup.Provider registries) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return output.copy(); }
    @Override public RecipeSerializer<?> getSerializer() { return MHRecipes.FERMENTING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MHRecipes.FERMENTING_TYPE.get(); }

    public static final class Serializer implements RecipeSerializer<FermentingRecipe> {
        private static final MapCodec<FermentingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.fieldOf("input").forGetter(FermentingRecipe::input),
                ItemStack.CODEC.fieldOf("output").forGetter(FermentingRecipe::output)
        ).apply(instance, FermentingRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> STREAM_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC, FermentingRecipe::input,
                ItemStack.STREAM_CODEC, FermentingRecipe::output,
                FermentingRecipe::new
        );
        @Override public MapCodec<FermentingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, FermentingRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
