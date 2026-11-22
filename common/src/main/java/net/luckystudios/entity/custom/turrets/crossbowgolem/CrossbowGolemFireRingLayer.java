package net.luckystudios.entity.custom.turrets.crossbowgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import net.luckystudios.BlockySiege;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

/**
 * Plays a three frame muzzle ring (small, medium, large) on a flat face in front of the barrel
 * for a few ticks after the crossbow golem fires.
 */
public class CrossbowGolemFireRingLayer extends RenderLayer<CrossbowGolemEntity, CrossbowGolemModel<CrossbowGolemEntity>> {

    private static final ResourceLocation[] FRAMES = {
            BlockySiege.id("textures/entity/crossbow_golem/crossbow_golem_fire_ring_small.png"),
            BlockySiege.id("textures/entity/crossbow_golem/crossbow_golem_fire_ring_medium.png"),
            BlockySiege.id("textures/entity/crossbow_golem/crossbow_golem_fire_ring_large.png")
    };

    /** How see-through the ring is, 1 is fully opaque. Lower it to dim the effect. */
    private static final float RING_OPACITY = 0.45F;

    public CrossbowGolemFireRingLayer(RenderLayerParent<CrossbowGolemEntity, CrossbowGolemModel<CrossbowGolemEntity>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, CrossbowGolemEntity golem,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (golem.isInvisible() || golem.fireRingTicks <= 0) return;
        float elapsed = CrossbowGolemEntity.FIRE_RING_TICKS - golem.fireRingTicks + partialTick;
        int frame = Math.min(FRAMES.length - 1, (int) (elapsed / CrossbowGolemEntity.FIRE_RING_TICKS * FRAMES.length));
        getParentModel().renderFireRing(poseStack, buffer.getBuffer(RenderType.entityTranslucent(FRAMES[Math.max(0, frame)])),
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY,
                FastColor.ARGB32.colorFromFloat(RING_OPACITY, 1.0F, 1.0F, 1.0F));
    }
}
