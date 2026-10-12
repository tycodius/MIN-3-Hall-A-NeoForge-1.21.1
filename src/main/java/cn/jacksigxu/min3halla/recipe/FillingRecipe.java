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
import net.minecraft.world.level.Level;
import java.util.List;

public final class FillingRecipe implements Recipe<FillingInput> {
    private final List<Ingredient> inputs;
    private final ItemStack output;

    public FillingRecipe(List<Ingredient> inputs, ItemStack output) {
        this.inputs = List.copyOf(inputs);
        this.output = output;
    }

    public List<Ingredient> inputs() { return inputs; }
    public ItemStack output() { return output; }

    @Override public boolean matches(FillingInput input, Level level) {
        if (inputs.size() != 2 || input.first().isEmpty() || input.second().isEmpty()) return false;
        return (inputs.get(0).test(input.first()) && inputs.get(1).test(input.second()))
            || (inputs.get(1).test(input.first()) && inputs.get(0).test(input.second()));
    }
    @Override public ItemStack assemble(FillingInput input, HolderLookup.Provider registries) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return output.copy(); }
    @Override public RecipeSerializer<?> getSerializer() { return MHRecipes.FILLING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MHRecipes.FILLING_TYPE.get(); }

    public static final class Serializer implements RecipeSerializer<FillingRecipe> {
        private static final MapCodec<FillingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC.listOf().fieldOf("inputs").forGetter(FillingRecipe::inputs),
                ItemStack.CODEC.fieldOf("output").forGetter(FillingRecipe::output)
        ).apply(instance, FillingRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, FillingRecipe> STREAM = StreamCodec.of(
                (buffer, recipe) -> {
                    buffer.writeVarInt(recipe.inputs.size());
                    for (Ingredient ingredient : recipe.inputs) Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);
                    ItemStack.STREAM_CODEC.encode(buffer, recipe.output);
                },
                buffer -> {
                    int size = buffer.readVarInt();
                    if (size != 2) throw new IllegalArgumentException("BTC filling requires 2 inputs, got " + size);
                    java.util.ArrayList<Ingredient> ingredients = new java.util.ArrayList<>(2);
                    for (int i = 0; i < size; i++) ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
                    return new FillingRecipe(ingredients, ItemStack.STREAM_CODEC.decode(buffer));
                }
        );
        @Override public MapCodec<FillingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, FillingRecipe> streamCodec() { return STREAM; }
    }
}
