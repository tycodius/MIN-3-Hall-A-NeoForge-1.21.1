package cn.jacksigxu.min3halla.gui.screen;

import cn.jacksigxu.min3halla.block.entity.FermentBarrelBlockEntity;
import cn.jacksigxu.min3halla.gui.menu.FermentBarrelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FermentBarrelScreen extends AbstractContainerScreen<FermentBarrelMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("min3halla", "textures/gui/ferment_barrel.png");
    public FermentBarrelScreen(FermentBarrelMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 176;
        imageHeight = 165;
    }
    @Override protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        int x = leftPos, y = topPos;
        gfx.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        int barWidth = (int)(Math.min(FermentBarrelBlockEntity.MAX_PROGRESS, menu.getProgress()) /
                (float)FermentBarrelBlockEntity.MAX_PROGRESS * 22f);
        if (barWidth > 0) gfx.blit(TEXTURE, x + 73, y + 33, 177, 0, barWidth, 16);
    }
    @Override public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        renderTooltip(gfx, mouseX, mouseY);
    }
    @Override protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 4;
        inventoryLabelX = 8;
        inventoryLabelY = 72;
    }
}
