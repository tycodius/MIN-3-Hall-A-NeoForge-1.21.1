package cn.jacksigxu.min3halla.block.entity;

import cn.jacksigxu.min3halla.gui.menu.DrinkMixerMenu;
import cn.jacksigxu.min3halla.init.MHBlockEntityTypes;
import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.init.MHTags;
import cn.jacksigxu.min3halla.init.MHRecipes;
import cn.jacksigxu.min3halla.recipe.MixerInput;
import cn.jacksigxu.min3halla.recipe.MixingRecipe;
import cn.jacksigxu.min3halla.util.ShakerData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class DrinkMixerBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SLOT_SHAKER = 0;
    private static final int[] ABOVE = {0};
    private static final int[] BELOW = {0};
    private static final int[] SIDES = {1,2,3,4,5,6,7,8,9};
    private static final int MAX_COMPONENTS = 20;
    private NonNullList<ItemStack> items = NonNullList.withSize(10, ItemStack.EMPTY);

    private final int[] selection = new int[10];
    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) { return selection[index]; }
        @Override public void set(int index, int value) { selection[index] = value; }
        @Override public int getCount() { return 10; }
    };
    public DrinkMixerBlockEntity(BlockPos pos, BlockState state) { super(MHBlockEntityTypes.DRINK_MIXER.get(), pos, state); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, DrinkMixerBlockEntity entity) {
        boolean changed = false;
        boolean available = entity.hasCleanShaker();
        if (entity.selection[9] != (available ? 1 : 0)) { entity.selection[9] = available ? 1 : 0; changed = true; }
        for (int i = 0; i < 5; i++) {
            int desired = available ? Math.min(entity.items.get(i + 1).getCount(), entity.selection[i]) : 0;
            if (entity.selection[i] != desired) { entity.selection[i] = desired; changed = true; }
        }
        for (int i = 5; i < 9; i++) {
            if ((!available || entity.items.get(i + 1).isEmpty()) && entity.selection[i] != 0) {
                entity.selection[i] = 0; changed = true;
            }
        }
        if (changed) entity.setChanged();
    }
    public boolean hasCleanShaker() {
        ItemStack stack = items.get(SLOT_SHAKER);
        return stack.is(MHItems.SHAKER_POT.get()) && !ShakerData.hasResult(stack);
    }
    public boolean adjust(int index, int delta) {
        if (index < 0 || index > 4 || !hasCleanShaker() || Math.abs(delta) != 1
                || !canPlaceItem(index + 1, items.get(index + 1))) return false;
        int total = selection[0]+selection[1]+selection[2]+selection[3]+selection[4];
        int next = selection[index] + delta;
        if (next < 0 || next > items.get(index + 1).getCount() || next > MAX_COMPONENTS ||
                (delta > 0 && total >= MAX_COMPONENTS)) return false;
        selection[index] = next;
        setChanged();
        return true;
    }
    public boolean toggle(int index) {
        if (index < 5 || index > 8 || !hasCleanShaker() || !canPlaceItem(index + 1, items.get(index + 1))) return false;
        selection[index] = selection[index] == 0 ? 1 : 0;
        setChanged();
        return true;
    }
    public void reset() {
        for (int i = 0; i < 9; i++) selection[i] = 0;
        setChanged();
    }
    public boolean mix() {
        if (level == null || level.isClientSide() || !hasCleanShaker()) return false;
        int total = 0;
        for (int i = 0; i < 5; i++) {
            if (items.get(i + 1).getCount() < selection[i]) return false;
            total += selection[i];
        }
        if (total > MAX_COMPONENTS || (total == 0 && selection[8] == 0)) return false;
        for (int i = 0; i < 5; i++) {
            if (selection[i] > 0 && !canPlaceItem(i + 1, items.get(i + 1))) return false;
        }
        for (int i = 5; i < 9; i++) {
            if (selection[i] != 0 && (items.get(i + 1).isEmpty()
                    || !canPlaceItem(i + 1, items.get(i + 1)))) return false;
        }
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 5; i++) inputs.add(selection[i] > 0 ? new ItemStack(items.get(i + 1).getItem(), selection[i]) : ItemStack.EMPTY);
        for (int i = 5; i < 9; i++) inputs.add(selection[i] == 0 ? ItemStack.EMPTY : items.get(i + 1).copyWithCount(1));
        Optional<RecipeHolder<MixingRecipe>> found = level.getRecipeManager().getRecipeFor(MHRecipes.MIXING_TYPE.get(), new MixerInput(inputs), level);
        ItemStack produced = found.map(r -> r.value().getResultItem(level.registryAccess())).orElseGet(() -> new ItemStack(MHItems.ERROR_DRINK.get()));
        boolean blend = found.isPresent() && found.get().value().blend;
        boolean big = found.isPresent() && found.get().value().big;
        int alcohol = found.isPresent() && found.get().value().kar == -1 ? selection[4] : 0;
        for (int i = 0; i < 5; i++) items.get(i + 1).shrink(selection[i]);
        for (int i = 5; i < 9; i++) if (selection[i] != 0) {
            ItemStack reagent = items.get(i + 1);
            ItemStack remainder = reagent.getCraftingRemainingItem();
            reagent.shrink(1);
            if (i == 8 && !remainder.isEmpty()) {
                if (reagent.isEmpty()) items.set(i + 1, remainder);
                else net.minecraft.world.Containers.dropItemStack(level, worldPosition.getX()+0.5, worldPosition.getY()+0.5, worldPosition.getZ()+0.5, remainder);
            }
        }
        ShakerData.fill(items.get(SLOT_SHAKER), produced, blend, alcohol, big);
        reset();
        return true;
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(10, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        String[] keys = {"Ade","Bex","Pwd","Fla","Kar","Ice","Aged","Dye","Extra"};
        for (int i = 0; i < keys.length; i++) selection[i] = i < 5 ? Math.max(0, tag.getInt(keys[i])) : (tag.getBoolean(keys[i]) ? 1 : 0);
        selection[9] = 0;

        for (int i = 0; i < 5; i++) selection[i] = Math.min(MAX_COMPONENTS, selection[i]);
        int remaining = MAX_COMPONENTS;
        for (int i = 0; i < 5; i++) {
            selection[i] = Math.min(selection[i], remaining);
            remaining -= selection[i];
        }
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        String[] keys = {"Ade","Bex","Pwd","Fla","Kar","Ice","Aged","Dye","Extra"};
        for (int i = 0; i < keys.length; i++) if (i < 5) tag.putInt(keys[i], selection[i]); else tag.putBoolean(keys[i], selection[i] == 1);
    }
    @Override public int getContainerSize() { return items.size(); }
    @Override public boolean isEmpty() { return items.stream().allMatch(ItemStack::isEmpty); }
    @Override public ItemStack getItem(int slot) { return items.get(slot); }
    @Override public ItemStack removeItem(int slot, int count) {
        ItemStack removed = ContainerHelper.removeItem(items, slot, count);
        if (!removed.isEmpty()) setChanged();
        return removed;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {

        return ContainerHelper.takeItem(items, slot);
    }
    @Override public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case 0 -> stack.is(MHItems.SHAKER_POT.get());
            case 1 -> stack.is(MHItems.ADELHYDE.get());
            case 2 -> stack.is(MHItems.BRONSON_EXTRACT.get());
            case 3 -> stack.is(MHItems.POWDERED_DELTA.get());
            case 4 -> stack.is(MHItems.FLANERGIDE.get());
            case 5 -> stack.is(MHItems.KARMOTRINE.get());
            case 6 -> stack.is(Items.ICE);
            case 7 -> stack.is(Items.REDSTONE);
            case 8 -> stack.is(net.neoforged.neoforge.common.Tags.Items.DYES);
            case 9 -> stack.is(MHTags.MIXER_EXTRAS);
            default -> false;
        };
    }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.UP ? ABOVE : side == Direction.DOWN ? BELOW : SIDES; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        return side != Direction.DOWN && (side != Direction.UP || slot == 0) && canPlaceItem(slot, stack);
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return side == Direction.DOWN ? slot == 0 && ShakerData.hasResult(stack) : true;
    }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { items = NonNullList.withSize(10, ItemStack.EMPTY); reset(); }
    @Override public Component getDisplayName() { return Component.translatable("menu.min3halla.drink_mixer"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) { return new DrinkMixerMenu(id, inventory, this, data); }
}
