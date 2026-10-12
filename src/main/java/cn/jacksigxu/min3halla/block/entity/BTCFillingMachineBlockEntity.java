package cn.jacksigxu.min3halla.block.entity;

import cn.jacksigxu.min3halla.gui.menu.BTCFillingMachineMenu;
import cn.jacksigxu.min3halla.init.MHBlockEntityTypes;
import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.init.MHRecipes;
import cn.jacksigxu.min3halla.recipe.FillingInput;
import cn.jacksigxu.min3halla.recipe.FillingRecipe;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;

public final class BTCFillingMachineBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int SLOT_FIRST = 0, SLOT_SECOND = 1, SLOT_CAN = 2, SLOT_RESULT = 3;
    public static final int MAX_PROGRESS = 100;
    private static final int[] TOP_SLOTS = {SLOT_FIRST, SLOT_SECOND};
    private static final int[] SIDE_SLOTS = {SLOT_CAN};
    private static final int[] BOTTOM_SLOTS = {SLOT_RESULT};
    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
    private int progress;

    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) { return index == 0 ? progress : 0; }
        @Override public void set(int index, int value) { if (index == 0) progress = Math.clamp(value, 0, MAX_PROGRESS); }
        @Override public int getCount() { return 1; }
    };

    public BTCFillingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(MHBlockEntityTypes.BTC_FILLING_MACHINE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BTCFillingMachineBlockEntity machine) {
        if (level.isClientSide()) return;
        Optional<RecipeHolder<FillingRecipe>> recipe = machine.getCurrentRecipe();
        if (recipe.isEmpty() || !machine.canMake(recipe.get().value().output())) {
            if (machine.progress != 0) { machine.progress = 0; machine.setChanged(); }
            return;
        }
        machine.progress++;
        if (machine.progress >= MAX_PROGRESS) {
            machine.craft(recipe.get().value().output());
            machine.progress = 0;
        }
        if (machine.progress % 20 == 0) machine.setChanged();
    }

    private Optional<RecipeHolder<FillingRecipe>> getCurrentRecipe() {
        if (level == null || items.get(SLOT_FIRST).isEmpty() || items.get(SLOT_SECOND).isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(MHRecipes.FILLING_TYPE.get(),
                new FillingInput(items.get(SLOT_FIRST), items.get(SLOT_SECOND)), level);
    }

    private boolean canMake(ItemStack result) {
        if (result.isEmpty() || items.get(SLOT_CAN).isEmpty() || !items.get(SLOT_CAN).is(MHItems.BTC_CAN.get())) return false;
        ItemStack existing = items.get(SLOT_RESULT);
        return existing.isEmpty() ? result.getCount() <= Math.min(getMaxStackSize(), result.getMaxStackSize())
                : ItemStack.isSameItemSameComponents(existing, result)
                    && existing.getCount() + result.getCount() <= Math.min(getMaxStackSize(), existing.getMaxStackSize());
    }

    private void craft(ItemStack result) {
        if (!canMake(result)) return;
        ItemStack output = items.get(SLOT_RESULT);
        if (output.isEmpty()) items.set(SLOT_RESULT, result.copy());
        else output.grow(result.getCount());
        items.get(SLOT_FIRST).shrink(1);
        items.get(SLOT_SECOND).shrink(1);
        items.get(SLOT_CAN).shrink(1);
        setChanged();
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(4, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items, registries);
        progress = Math.clamp(tag.getInt("Progress"), 0, MAX_PROGRESS);
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, items, registries);
        tag.putInt("Progress", progress);
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
    @Override public void clearContent() { items = NonNullList.withSize(4, ItemStack.EMPTY); progress = 0; setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == SLOT_CAN) return stack.is(MHItems.BTC_CAN.get());
        return slot == SLOT_FIRST || slot == SLOT_SECOND;
    }
    @Override public int[] getSlotsForFace(Direction side) {
        return side == Direction.UP ? TOP_SLOTS : side == Direction.DOWN ? BOTTOM_SLOTS : SIDE_SLOTS;
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) {
        if (side == Direction.DOWN) return false;
        if (side == Direction.UP) return (slot == SLOT_FIRST || slot == SLOT_SECOND) && canPlaceItem(slot, stack);
        return slot == SLOT_CAN && canPlaceItem(slot, stack);
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) {
        return side == Direction.DOWN && slot == SLOT_RESULT;
    }
    @Override public Component getDisplayName() { return Component.translatable("menu.min3halla.btc_filling_machine"); }
    @Override @Nullable public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new BTCFillingMachineMenu(id, inventory, this, data);
    }
}
