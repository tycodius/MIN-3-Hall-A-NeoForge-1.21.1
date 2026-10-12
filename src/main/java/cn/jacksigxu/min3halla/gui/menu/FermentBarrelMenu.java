package cn.jacksigxu.min3halla.gui.menu;

import cn.jacksigxu.min3halla.block.entity.FermentBarrelBlockEntity;
import cn.jacksigxu.min3halla.init.MHMenuTypes;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FermentBarrelMenu extends AbstractContainerMenu {
    private final Container barrel;
    private final ContainerData data;
    public FermentBarrelMenu(int id, Inventory inv) {
        this(id, inv, new SimpleContainer(2), new SimpleContainerData(1));
    }
    public FermentBarrelMenu(int id, Inventory inv, Container barrel, ContainerData data) {
        super(MHMenuTypes.FERMENT_BARREL.get(), id);
        checkContainerSize(barrel, 2);
        checkContainerDataCount(data, 1);
        this.barrel = barrel;
        this.data = data;
        this.addSlot(new Slot(barrel, 0, 40, 33));
        this.addSlot(new Slot(barrel, 1, 116, 33) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addDataSlots(data);
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 83 + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inv, col, 8 + col * 18, 141));
    }
    @Override public boolean stillValid(Player player) { return barrel.stillValid(player); }
    public int getProgress() { return data.get(0); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return empty;
        ItemStack current = slot.getItem();
        ItemStack copy = current.copy();
        if (index < 2) {
            if (!moveItemStackTo(current, 2, 38, true)) return empty;
        } else if (!moveItemStackTo(current, 0, 1, false)) {

            if (index < 29) {
                if (!moveItemStackTo(current, 29, 38, false)) return empty;
            } else if (!moveItemStackTo(current, 2, 29, false)) return empty;
        }
        if (current.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        if (current.getCount() == copy.getCount()) return empty;
        slot.onTake(player, current);
        return copy;
    }
}
