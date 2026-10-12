package cn.jacksigxu.min3halla.init;

import cn.jacksigxu.min3halla.MHMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MHStats {
    public static final DeferredRegister<ResourceLocation> STATS =
            DeferredRegister.create(Registries.CUSTOM_STAT, MHMod.MOD_ID);

    public static final DeferredHolder<ResourceLocation, ResourceLocation> INTERACT_WITH_DRINK_MIXER =
            STATS.register("interact_with_drink_mixer", () ->
                    ResourceLocation.fromNamespaceAndPath(MHMod.MOD_ID, "interact_with_drink_mixer"));

    private MHStats() {}
}
