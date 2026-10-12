package cn.jacksigxu.min3halla.recipe;

import cn.jacksigxu.min3halla.init.MHRecipes;
import com.mojang.serialization.Codec;
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

public final class MixingRecipe implements Recipe<MixerInput> {
    public final int ade, bex, pwd, fla, kar;
    public final boolean ice, age, blend, big;
    public final Ingredient dye, extra;
    private final ItemStack output;

    public MixingRecipe(int ade, int bex, int pwd, int fla, int kar, boolean ice, boolean age,
                        Ingredient dye, Ingredient extra, boolean blend, boolean big, ItemStack output) {
        this.ade = ade; this.bex = bex; this.pwd = pwd; this.fla = fla; this.kar = kar;
        this.ice = ice; this.age = age; this.dye = dye; this.extra = extra;
        this.blend = blend; this.big = big; this.output = output;
    }
    public int required(int index) { return switch (index) { case 0 -> ade; case 1 -> bex; case 2 -> pwd; case 3 -> fla; case 4 -> kar; default -> 0; }; }
    @Override public boolean matches(MixerInput input, Level level) {
        for (int i = 0; i < 5; i++) {
            if (required(i) != -1 && input.getItem(i).getCount() != required(i)) return false;
        }
        return ice == !input.getItem(5).isEmpty() && age == !input.getItem(6).isEmpty()
                && (dye == Ingredient.EMPTY ? input.getItem(7).isEmpty() : dye.test(input.getItem(7)))
                && (extra == Ingredient.EMPTY ? input.getItem(8).isEmpty() : extra.test(input.getItem(8)));
    }
    @Override public ItemStack assemble(MixerInput input, HolderLookup.Provider registries) { return output.copy(); }
    @Override public boolean canCraftInDimensions(int width, int height) { return true; }
    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return output.copy(); }
    @Override public RecipeSerializer<?> getSerializer() { return MHRecipes.MIXING_SERIALIZER.get(); }
    @Override public RecipeType<?> getType() { return MHRecipes.MIXING_TYPE.get(); }

    public static final class Serializer implements RecipeSerializer<MixingRecipe> {
        public static final MapCodec<MixingRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.optionalFieldOf("ade", 0).forGetter(r -> r.ade),
            Codec.INT.optionalFieldOf("bex", 0).forGetter(r -> r.bex),
            Codec.INT.optionalFieldOf("pwd", 0).forGetter(r -> r.pwd),
            Codec.INT.optionalFieldOf("fla", 0).forGetter(r -> r.fla),
            Codec.INT.optionalFieldOf("kar", 0).forGetter(r -> r.kar),
            Codec.BOOL.optionalFieldOf("ice", false).forGetter(r -> r.ice),
            Codec.BOOL.optionalFieldOf("age", false).forGetter(r -> r.age),
            Ingredient.CODEC.optionalFieldOf("dye", Ingredient.EMPTY).forGetter(r -> r.dye),
            Ingredient.CODEC.optionalFieldOf("extra", Ingredient.EMPTY).forGetter(r -> r.extra),
            Codec.BOOL.optionalFieldOf("blend", false).forGetter(r -> r.blend),
            Codec.BOOL.optionalFieldOf("big", false).forGetter(r -> r.big),
            ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
        ).apply(i, MixingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MixingRecipe> STREAM = StreamCodec.of(
            (buf, recipe) -> {
                for (int k = 0; k < 5; k++) buf.writeVarInt(recipe.required(k) + 1);
                buf.writeBoolean(recipe.ice); buf.writeBoolean(recipe.age);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.dye);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.extra);
                buf.writeBoolean(recipe.blend); buf.writeBoolean(recipe.big);
                ItemStack.STREAM_CODEC.encode(buf, recipe.output);
            },
            buf -> {
                int ade = buf.readVarInt() - 1, bex = buf.readVarInt() - 1,
                    pwd = buf.readVarInt() - 1, fla = buf.readVarInt() - 1, kar = buf.readVarInt() - 1;
                boolean ice = buf.readBoolean(), age = buf.readBoolean();
                Ingredient dye = Ingredient.CONTENTS_STREAM_CODEC.decode(buf), extra = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                boolean blend = buf.readBoolean(), big = buf.readBoolean();
                ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                return new MixingRecipe(ade, bex, pwd, fla, kar, ice, age, dye, extra, blend, big, result);
            }
        );
        @Override public MapCodec<MixingRecipe> codec() { return CODEC; }
        @Override public StreamCodec<RegistryFriendlyByteBuf, MixingRecipe> streamCodec() { return STREAM; }
    }
}
