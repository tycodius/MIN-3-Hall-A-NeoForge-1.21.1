package cn.jacksigxu.min3halla.gui.screen;

import cn.jacksigxu.min3halla.block.entity.BTCFillingMachineBlockEntity;
import cn.jacksigxu.min3halla.gui.menu.BTCFillingMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class BTCFillingMachineScreen extends AbstractContainerScreen<BTCFillingMachineMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("min3halla", "textures/gui/btc_filling_machine.png");

    public BTCFillingMachineScreen(BTCFillingMachineMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 176;
        imageHeight = 165;
    }
    @Override protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int pixels = Math.min(22, (int)(menu.getProgress() / (float)BTCFillingMachineBlockEntity.MAX_PROGRESS * 22));
        if (pixels > 0) graphics.blit(TEXTURE, leftPos + 84, topPos + 30, 177, 0, pixels, 16);
    }
    @Override public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }
    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 4;
        inventoryLabelX = 8;
        inventoryLabelY = 72;
    }
}
