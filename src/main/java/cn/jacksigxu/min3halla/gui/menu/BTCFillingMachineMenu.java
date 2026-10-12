package cn.jacksigxu.min3halla.gui.menu;

import cn.jacksigxu.min3halla.init.MHItems;
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

public final class BTCFillingMachineMenu extends AbstractContainerMenu {
    private final Container machine;
    private final ContainerData data;
    public BTCFillingMachineMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(4), new SimpleContainerData(1));
    }
    public BTCFillingMachineMenu(int id, Inventory inventory, Container machine, ContainerData data) {
        super(MHMenuTypes.BTC_FILLING_MACHINE.get(), id);
        checkContainerSize(machine, 4);
        checkContainerDataCount(data, 1);
        this.machine = machine;
        this.data = data;
        addSlot(new Slot(machine, 0, 28, 30));
        addSlot(new Slot(machine, 1, 56, 30));
        addSlot(new Slot(machine, 2, 124, 12) {
            @Override public boolean mayPlace(ItemStack stack) { return stack.is(MHItems.BTC_CAN.get()); }
        });
        addSlot(new Slot(machine, 3, 124, 51) {
            @Override public boolean mayPlace(ItemStack stack) { return false; }
        });
        addDataSlots(data);
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 142));
    }
    @Override public boolean stillValid(Player player) { return machine.stillValid(player); }
    public int getProgress() { return data.get(0); }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack current = slot.getItem();
        ItemStack original = current.copy();
        if (index < 4) {
            if (!moveItemStackTo(current, 4, 40, true)) return ItemStack.EMPTY;
        } else if (current.is(MHItems.BTC_CAN.get())) {
            if (!moveItemStackTo(current, 2, 3, false)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(current, 0, 2, false)) {
            if (index < 31) {
                if (!moveItemStackTo(current, 31, 40, false)) return ItemStack.EMPTY;
            } else if (!moveItemStackTo(current, 4, 31, false)) return ItemStack.EMPTY;
        }
        if (current.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        if (current.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, current);
        return original;
    }
}
