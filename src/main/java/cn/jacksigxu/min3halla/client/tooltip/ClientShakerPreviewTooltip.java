package cn.jacksigxu.min3halla.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public final class ClientShakerPreviewTooltip implements ClientTooltipComponent {
    private final ShakerPreviewTooltip preview;
    public ClientShakerPreviewTooltip(ShakerPreviewTooltip preview) { this.preview = preview; }
    @Override public int getHeight() { return 22; }
    @Override public int getWidth(Font font) {
        return 22 + Math.max(font.width(preview.drink().getHoverName()), font.width(preview.status()));
    }
    @Override public void renderImage(Font font, int x, int y, GuiGraphics graphics) {
        graphics.renderItem(preview.drink(), x, y + 2);
        graphics.drawString(font, preview.drink().getHoverName(), x + 22, y, 0x55FF7F, false);
        graphics.drawString(font, preview.status(), x + 22, y + 11, 0xFFFFFF, false);
    }
}
