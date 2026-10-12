package cn.jacksigxu.min3halla.gui.menu;

import cn.jacksigxu.min3halla.block.entity.DrinkMixerBlockEntity;
import cn.jacksigxu.min3halla.init.MHItems;
import cn.jacksigxu.min3halla.init.MHTags;
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
import net.minecraft.world.item.Items;

public final class DrinkMixerMenu extends AbstractContainerMenu {
    public static final int MIX = 0, RESET = 1;
    private final Container mixer;
    private final ContainerData data;

    public DrinkMixerMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(10), new SimpleContainerData(10));
    }
    public DrinkMixerMenu(int id, Inventory inventory, Container container, ContainerData data) {
        super(MHMenuTypes.DRINK_MIXER.get(), id);
        checkContainerSize(container, 10);
        checkContainerDataCount(data, 10);
        this.mixer = container;
        this.data = data;

        int[][] coords = {{114,59},{39,21},{99,21},{159,21},{39,59},{159,59},{11,22},{11,60},{217,22},{217,60}};
        for (int slot = 0; slot < 10; slot++) {
            final int index = slot;
            addSlot(new Slot(container, index, coords[index][0], coords[index][1]) {
                @Override public boolean mayPlace(ItemStack stack) {
                    return switch (index) {
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
            });
        }
        addDataSlots(data);
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                addSlot(new Slot(inventory, col + row * 9 + 9, 42 + col * 18, 121 + row * 18));
        for (int col = 0; col < 9; col++)
            addSlot(new Slot(inventory, col, 42 + col * 18, 179));
    }
    public int count(int index) { return index >= 0 && index < 5 ? data.get(index) : 0; }
    public boolean selected(int index) { return index >= 5 && index <= 8 && data.get(index) != 0; }
    public boolean ready() { return data.get(9) > 0; }
    public int total() { int total = 0; for (int i = 0; i < 5; i++) total += count(i); return total; }
    @Override public boolean stillValid(Player player) { return mixer.stillValid(player); }
    @Override public boolean clickMenuButton(Player player, int buttonId) {
        if (!(mixer instanceof DrinkMixerBlockEntity entity) || !stillValid(player)) return false;
        if (buttonId == MIX) return entity.mix();
        if (buttonId == RESET) { entity.reset(); return true; }
        if (buttonId >= 10 && buttonId < 20) return entity.adjust((buttonId - 10) / 2, buttonId % 2 == 0 ? 1 : -1);
        if (buttonId >= 20 && buttonId < 24) return entity.toggle(buttonId - 15);
        return false;
    }
    @Override public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack current = slot.getItem(), previous = current.copy();
        if (index < 10) {
            if (!moveItemStackTo(current, 10, 46, true)) return ItemStack.EMPTY;
        } else {
            boolean inserted = false;
            for (int input = 0; input < 10; input++) {
                if (slots.get(input).mayPlace(current) && moveItemStackTo(current, input, input + 1, false)) {
                    inserted = true; break;
                }
            }
            if (!inserted) {
                if (index < 37 ? !moveItemStackTo(current, 37, 46, false) : !moveItemStackTo(current, 10, 37, false)) return ItemStack.EMPTY;
            }
        }
        if (current.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        if (current.getCount() == previous.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, current);
        return previous;
    }
}
