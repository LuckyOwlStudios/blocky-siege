package net.luckystudios.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.luckystudios.blocks.custom.shooting.AbstractShootingAimableBlockEntity;
import net.luckystudios.blocks.custom.shooting.AbstractShootingBlock;
import net.luckystudios.blocks.custom.shooting.volley.VolleyRackBlock;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.SmithingScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * When a trimmable siege weapon sits in the smithing table, previews the weapon (with the trimmed result once
 * the recipe resolves) in place of the armor stand.
 */
@Mixin(SmithingScreen.class)
public abstract class SmithingScreenMixin {
    @Shadow
    private ArmorStand armorStandPreview;

    @Unique
    private static final Map<Block, AbstractShootingAimableBlockEntity> blockysiege$previews = new HashMap<>();

    /** The smithing menu's slots are: 0 template, 1 base, 2 addition, 3 result. */
    @Unique
    private ItemStack blockysiege$previewStack() {
        AbstractContainerMenu menu = ((AbstractContainerScreen<?>) (Object) this).getMenu();
        ItemStack result = menu.getSlot(3).getItem();
        ItemStack stack = !result.isEmpty() ? result : menu.getSlot(1).getItem();
        if (stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock() instanceof AbstractShootingBlock
                && !(blockItem.getBlock() instanceof VolleyRackBlock)) {
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Inject(method = "renderBg", at = @At("HEAD"), require = 0)
    private void blockysiege$hideArmorStand(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.armorStandPreview != null) {
            this.armorStandPreview.setInvisible(!blockysiege$previewStack().isEmpty());
        }
    }

    @Inject(method = "renderBg", at = @At("TAIL"), require = 0)
    private void blockysiege$renderWeaponPreview(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        ItemStack stack = blockysiege$previewStack();
        Minecraft minecraft = Minecraft.getInstance();
        if (stack.isEmpty() || minecraft.level == null) return;

        Block block = ((BlockItem) stack.getItem()).getBlock();
        AbstractShootingAimableBlockEntity preview = blockysiege$previews.get(block);
        if (preview == null) {
            BlockEntity created = ((AbstractShootingBlock) block).newBlockEntity(BlockPos.ZERO, block.defaultBlockState());
            if (!(created instanceof AbstractShootingAimableBlockEntity weapon)) return;
            preview = weapon;
            blockysiege$previews.put(block, preview);
        }
        preview.setLevel(minecraft.level);
        preview.setTrimForPreview(stack.get(DataComponents.TRIM));

        Screen screen = (Screen) (Object) this;
        int centerX = (screen.width - 176) / 2 + 141;
        int centerY = (screen.height - 166) / 2 + 50;

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(centerX, centerY, 100.0F);
        pose.scale(32.0F, -32.0F, 32.0F);
        pose.mulPose(Axis.XP.rotationDegrees(-20.0F));
        pose.mulPose(Axis.YP.rotationDegrees((Util.getMillis() / 30L) % 360L));
        pose.translate(-0.5F, -0.5F, -0.5F);
        minecraft.getBlockEntityRenderDispatcher().renderItem(preview, pose, guiGraphics.bufferSource(), LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        guiGraphics.flush();
        pose.popPose();
    }
}
