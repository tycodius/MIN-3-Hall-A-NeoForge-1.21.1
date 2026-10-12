package cn.jacksigxu.min3halla.block.entity;

import cn.jacksigxu.min3halla.gui.menu.FermentBarrelMenu;
import cn.jacksigxu.min3halla.init.MHBlockEntityTypes;
import cn.jacksigxu.min3halla.init.MHRecipes;
import cn.jacksigxu.min3halla.recipe.FermentingRecipe;
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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FermentBarrelBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int MAX_PROGRESS = 1800; 
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 1;
    private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private int progress;
    private final ContainerData data = new ContainerData() {
        @Override public int get(int index) { return index == 0 ? progress : 0; }
        @Override public void set(int index, int value) { if (index == 0) progress = Math.max(0, value); }
        @Override public int getCount() { return 1; }
    };

    public FermentBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(MHBlockEntityTypes.FERMENT_BARREL.get(), pos, state);
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, FermentBarrelBlockEntity barrel) {
        if (level.isClientSide()) return;
        Optional<RecipeHolder<FermentingRecipe>> recipe = barrel.getRecipe();
        if (recipe.isEmpty() || !barrel.canOutput(recipe.get().value().getResultItem(level.registryAccess()))) {
            if (barrel.progress != 0) {
                barrel.progress = 0;
                barrel.setChanged();
            }
            return;
        }
        barrel.progress++;
        if (barrel.progress >= MAX_PROGRESS) {
            barrel.progress = 0;

            ItemStack result = recipe.get().value().assemble(new SingleRecipeInput(barrel.items.get(0)), level.registryAccess());
            if (barrel.canOutput(result) && !result.isEmpty()) {
                barrel.items.get(0).shrink(1);
                ItemStack output = barrel.items.get(1);
                if (output.isEmpty()) barrel.items.set(1, result);
                else output.grow(result.getCount());
            }
        }
        if (barrel.progress % 20 == 0) barrel.setChanged();
    }
    private Optional<RecipeHolder<FermentingRecipe>> getRecipe() {
        if (level == null || items.get(0).isEmpty()) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(MHRecipes.FERMENTING_TYPE.get(), new SingleRecipeInput(items.get(0)), level);
    }
    private boolean canOutput(ItemStack result) {
        if (result.isEmpty() || result.getCount() > Math.min(result.getMaxStackSize(), getMaxStackSize())) return false;
        ItemStack out = items.get(1);
        return out.isEmpty() || (ItemStack.isSameItemSameComponents(out, result) &&
                out.getCount() + result.getCount() <= Math.min(out.getMaxStackSize(), getMaxStackSize()));
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        items = NonNullList.withSize(2, ItemStack.EMPTY);
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
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == SLOT_INPUT; }
    @Override public void clearContent() { items = NonNullList.withSize(2, ItemStack.EMPTY); progress = 0; setChanged(); }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public int[] getSlotsForFace(Direction side) { return side == Direction.DOWN ? new int[]{SLOT_OUTPUT} : new int[]{SLOT_INPUT}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction side) { return slot == SLOT_INPUT && side != Direction.DOWN; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == SLOT_OUTPUT && side != Direction.UP; }
    @Override public Component getDisplayName() { return Component.translatable("menu.min3halla.ferment_barrel"); }
    @Override @Nullable public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FermentBarrelMenu(id, inventory, this, data);
    }
}
