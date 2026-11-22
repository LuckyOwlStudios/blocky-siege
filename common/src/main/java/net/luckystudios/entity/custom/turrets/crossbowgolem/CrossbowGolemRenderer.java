package net.luckystudios.entity.custom.turrets.crossbowgolem;

import net.luckystudios.BlockySiege;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CrossbowGolemRenderer extends MobRenderer<CrossbowGolemEntity, CrossbowGolemModel<CrossbowGolemEntity>> {

    private static final ResourceLocation CROSSBOW_GOLEM_TEXTURE = BlockySiege.id("textures/entity/crossbow_golem/crossbow_golem.png");

    public CrossbowGolemRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new CrossbowGolemModel<>(pContext.bakeLayer(CrossbowGolemModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new CrossbowGolemCrackinessLayer(this));
        this.addLayer(new CrossbowGolemFireRingLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CrossbowGolemEntity crossbow_golem) {
        return CROSSBOW_GOLEM_TEXTURE;
    }

    /** A hanging golem is turned upside down under its ceiling block, the same way vanilla flips a "Dinnerbone" mob. */
    @Override
    protected void setupRotations(CrossbowGolemEntity golem, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale) {
        super.setupRotations(golem, poseStack, bob, yBodyRot, partialTick, scale);
        if (golem.isHanging()) {
            poseStack.translate(0.0F, golem.getBbHeight(), 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        }
    }
}
