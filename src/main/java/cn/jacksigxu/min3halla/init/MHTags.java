package cn.jacksigxu.min3halla.init;

import cn.jacksigxu.min3halla.MHMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class MHTags {
    private MHTags() { }
    public static final TagKey<Item> MIXER_EXTRAS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(MHMod.MOD_ID, "mixer_extras"));
}
