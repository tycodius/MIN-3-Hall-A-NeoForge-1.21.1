package cn.jacksigxu.min3halla.gui.screen;

import cn.jacksigxu.min3halla.gui.menu.DrinkMixerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public final class DrinkMixerScreen extends AbstractContainerScreen<DrinkMixerMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("min3halla", "textures/gui/drink_mixer.png");
    private static final int[][] METER_POSITIONS = {{58, 19}, {118, 19}, {178, 19}, {58, 57}, {178, 57}};
    private static final int[][] BUTTON_POSITIONS = {{83, 21}, {143, 21}, {203, 21}, {83, 59}, {203, 59}};
    private static final int[][] TOGGLE_POSITIONS = {{10, 10}, {10, 48}, {216, 10}, {216, 48}};
    private static final String[] TOGGLE_LABELS = {"Ice", "Age", "Dye", "Extra"};

    public DrinkMixerScreen(DrinkMixerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageWidth = 244;
        imageHeight = 203;
    }

    private void send(int id) {
        if (Minecraft.getInstance().gameMode != null) {
            Minecraft.getInstance().gameMode.handleInventoryButtonClick(menu.containerId, id);
        }
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 42;
        titleLabelY = 4;
        inventoryLabelX = 42;
        inventoryLabelY = 109;

        addRenderableWidget(new SpriteButton(leftPos + 157, topPos + 86, 50, 16,
                Component.literal("Mix"), DrinkMixerMenu.MIX, ButtonKind.MIX, 0));
        addRenderableWidget(new SpriteButton(leftPos + 37, topPos + 86, 50, 16,
                Component.literal("Reset"), DrinkMixerMenu.RESET, ButtonKind.RESET, 0));

        for (int i = 0; i < BUTTON_POSITIONS.length; i++) {
            int x = leftPos + BUTTON_POSITIONS[i][0];
            int y = topPos + BUTTON_POSITIONS[i][1];
            addRenderableWidget(new SpriteButton(x, y, 5, 5,
                    Component.literal("Add ingredient " + (i + 1)), 10 + i * 2, ButtonKind.PLUS, i));
            addRenderableWidget(new SpriteButton(x, y + 11, 5, 5,
                    Component.literal("Remove ingredient " + (i + 1)), 11 + i * 2, ButtonKind.MINUS, i));
        }
        for (int i = 0; i < TOGGLE_POSITIONS.length; i++) {
            addRenderableWidget(new SpriteButton(leftPos + TOGGLE_POSITIONS[i][0],
                    topPos + TOGGLE_POSITIONS[i][1], 18, 10,
                    Component.literal(TOGGLE_LABELS[i]), 20 + i, ButtonKind.TOGGLE, i));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float tick, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        for (int i = 0; i < METER_POSITIONS.length; i++) {
            renderIngredientBars(graphics, leftPos + METER_POSITIONS[i][0],
                    topPos + METER_POSITIONS[i][1], i * 5, menu.count(i));
        }
        renderTotalBlocks(graphics, leftPos + 100, topPos + 89, menu.total());
    }

    private static void renderIngredientBars(GuiGraphics graphics, int x, int y, int u, int count) {
        for (int i = 0; i < 4; i++) {
            int n = Math.min(5, Math.max(0, count - 5 * i));
            for (int j = 0; j < n; j++) {
                graphics.blit(TEXTURE, x + j * 5, y + ((i & 1) * 11),
                        u, 204 + (i >= 2 ? 9 : 0), 4, 9);
            }
        }
    }

    private static void renderTotalBlocks(GuiGraphics graphics, int x, int y, int count) {
        for (int i = 0; i < 4; i++) {
            int n = Math.min(5, Math.max(0, count - 5 * i));
            for (int j = 0; j < n; j++) {
                graphics.blit(TEXTURE, x + j * 9, y + ((i & 1) * 6),
                        i >= 2 ? 9 : 0, 222, 8, 5);
            }
        }
    }

    @Override protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) { }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float tick) {
        super.render(graphics, mouseX, mouseY, tick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    private enum ButtonKind { MIX, RESET, PLUS, MINUS, TOGGLE }

    private final class SpriteButton extends AbstractButton {
        private final int id;
        private final ButtonKind kind;
        private final int index;
        private SpriteButton(int x, int y, int width, int height, Component label,
                             int id, ButtonKind kind, int index) {
            super(x, y, width, height, label);
            this.id = id;
            this.kind = kind;
            this.index = index;
        }
        @Override public void onPress() { send(id); }
        @Override protected void updateWidgetNarration(NarrationElementOutput narration) {
            defaultButtonNarrationText(narration);
        }
        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float tick) {
            switch (kind) {
                case MIX -> {
                    if (!menu.ready()) graphics.blit(TEXTURE, getX(), getY(), isHovered() ? 146 : 44, 204, 50, 16);
                    else if (isHovered()) graphics.blit(TEXTURE, getX(), getY(), 95, 204, 50, 16);
                }
                case RESET -> {
                    if (isHovered()) graphics.blit(TEXTURE, getX(), getY(), 44, 221, 50, 16);
                }
                case PLUS, MINUS -> {
                    if (isHovered()) graphics.blit(TEXTURE, getX(), getY(), kind == ButtonKind.PLUS ? 25 : 31, 204, 5, 5);
                }
                case TOGGLE -> {
                    if (menu.selected(index + 5)) graphics.blit(TEXTURE, getX(), getY(), 25, 221, 18, 10);
                    if (isHovered()) graphics.blit(TEXTURE, getX(), getY(), 25, 210, 18, 10);
                }
            }
        }
    }
}
