package cn.jacksigxu.min3halla.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.neoforge.event.village.WandererTradesEvent;

public final class MHTrades {
    private MHTrades() {}
    public static void addWandererTrade(WandererTradesEvent event) {
        event.getGenericTrades().add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 32),
                new ItemStack(MHItems.MULAN_TEA.get()), 4, 0, 0.15F));
        event.getGenericTrades().add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 32),
                new ItemStack(MHItems.A_FEDORA.get()), 4, 0, 0.15F));
        event.getGenericTrades().add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 32),
                new ItemStack(MHItems.RUM.get()), 4, 0, 0.15F));
        event.getGenericTrades().add((trader, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 32),
                new ItemStack(MHItems.ABSINTHE.get()), 4, 0, 0.15F));
    }
}
