package net.luckystudios.gui.crossbowgolem;

import com.mojang.blaze3d.systems.RenderSystem;
import net.luckystudios.BlockySiege;
import net.luckystudios.blocks.custom.turret.crossbowgolem.CrossbowGolemBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import org.jetbrains.annotations.Nullable;

// This is the image
public class CrossbowGolemScreen extends AbstractContainerScreen<CrossbowGolemMenu> {

    Level level;
    BlockPos blockPos;
    Player player;
    CrossbowGolemBlockEntity crossbow_golemBlockEntity;

    private static final ResourceLocation GUI_TEXTURE =
            BlockySiege.id("textures/gui/container/crossbow_golem_gui.png");

    public CrossbowGolemScreen(CrossbowGolemMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.player = playerInventory.player;
        this.level = player.level();
        this.blockPos = menu.blockEntity.getBlockPos();
        this.crossbow_golemBlockEntity = menu.blockEntity;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        // Only the panel (176x197); the filled health bar sprite lives further down the texture.
        guiGraphics.blit(GUI_TEXTURE, x, y - 31, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        renderHealthBar(guiGraphics, x, y - 31);
        renderTeamIcons(guiGraphics, x, y - 31);
        RenderSystem.disableBlend();
    }


    // Health bar: the empty bar is part of the background at (BAR_X, BAR_Y); the filled sprite sits at (BAR_U, BAR_V).
    private static final int PANEL_WIDTH = 176;
    private static final int PANEL_HEIGHT = 197;
    private static final int BAR_X = 8;
    private static final int BAR_Y = 24;
    private static final int BAR_U = 8;
    private static final int BAR_V = 200;
    private static final int BAR_WIDTH = 160;
    private static final int BAR_HEIGHT = 3;

    private void renderHealthBar(GuiGraphics guiGraphics, int originX, int originY) {
        int filled = Mth.ceil(BAR_WIDTH * this.menu.getHealthFraction());
        if (filled > 0) {
            guiGraphics.blit(GUI_TEXTURE, originX + BAR_X, originY + BAR_Y, BAR_U, BAR_V, filled, BAR_HEIGHT);
        }
    }

    private boolean isHoveringHealthBar(int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2 + BAR_X;
        int y = (height - imageHeight) / 2 - 31 + BAR_Y;
        return mouseX >= x - 1 && mouseX < x + BAR_WIDTH + 1 && mouseY >= y - 1 && mouseY < y + BAR_HEIGHT + 1;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(guiGraphics, pMouseX, pMouseY);
        if (isHoveringTeamIcon(pMouseX, pMouseY)) {
            PlayerTeam team = getGolemTeam();
            Component name = team == null
                    ? Component.translatable("gui.blockysiege.crossbow_golem.no_team")
                    : team.getDisplayName();
            guiGraphics.renderTooltip(this.font, name, pMouseX, pMouseY);
        } else if (isHoveringHealthBar(pMouseX, pMouseY)) {
            String health = String.format("%.1f / %.1f", this.menu.getHealth(), this.menu.getMaxHealth());
            guiGraphics.renderTooltip(this.font, Component.translatable("gui.blockysiege.crossbow_golem.health", health), pMouseX, pMouseY);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Center the title at the top of the GUI
        int titleX = (this.imageWidth - this.font.width(this.title)) / 2;
        guiGraphics.drawString(this.font, this.title, titleX, this.titleLabelY - 32, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
    }

    private static final ResourceLocation TEAM_ICON =
            BlockySiege.id("textures/gui/container/team_icon.png");
    private static final int ICON_SIZE = 5;
    private static final int ICON_LEFT_X = 7;
    private static final int ICON_RIGHT_X = 164;
    private static final int ICON_Y = 6;

    @Nullable
    private PlayerTeam getGolemTeam() {
        return this.menu.blockEntity.findCrossbowGolem()
                .map(golem -> golem.getTeam() instanceof PlayerTeam playerTeam ? playerTeam : null)
                .orElse(null);
    }

    /** Draws the team icon in both header slots, tinted to the golem's team colour (untinted grey if no team). */
    private void renderTeamIcons(GuiGraphics guiGraphics, int originX, int originY) {
        PlayerTeam team = getGolemTeam();
        Integer teamColor = team == null ? null : team.getColor().getColor();
        float r = 1, g = 1, b = 1;
        if (teamColor != null) {
            r = (teamColor >> 16 & 255) / 255.0F;
            g = (teamColor >> 8 & 255) / 255.0F;
            b = (teamColor & 255) / 255.0F;
        }
        // Grey icon art is fairly dark, so brighten the tint a little to keep team colours readable.
        guiGraphics.setColor(Math.min(1, r * 1.4F), Math.min(1, g * 1.4F), Math.min(1, b * 1.4F), 1.0F);
        guiGraphics.blit(TEAM_ICON, originX + ICON_LEFT_X, originY + ICON_Y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        guiGraphics.blit(TEAM_ICON, originX + ICON_RIGHT_X, originY + ICON_Y, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
        guiGraphics.setColor(1, 1, 1, 1);
    }

    private boolean isHoveringTeamIcon(int mouseX, int mouseY) {
        int originX = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2 - 31 + ICON_Y;
        if (mouseY < y || mouseY >= y + ICON_SIZE) {
            return false;
        }
        return (mouseX >= originX + ICON_LEFT_X && mouseX < originX + ICON_LEFT_X + ICON_SIZE)
                || (mouseX >= originX + ICON_RIGHT_X && mouseX < originX + ICON_RIGHT_X + ICON_SIZE);
    }

    @Override
    protected void init() {
        super.init();

        // Position button relative to the GUI
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
    }
}
