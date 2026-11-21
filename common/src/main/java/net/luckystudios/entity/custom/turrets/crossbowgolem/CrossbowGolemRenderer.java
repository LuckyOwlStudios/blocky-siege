package net.luckystudios.entity.custom.turrets.crossbowgolem;

import net.luckystudios.BlockySiege;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class CrossbowGolemRenderer extends MobRenderer<CrossbowGolemEntity, CrossbowGolemModel<CrossbowGolemEntity>> {

    private static final ResourceLocation CROSSBOW_GOLEM_TEXTURE = BlockySiege.id("textures/entity/crossbow_golem/crossbow_golem.png");

    public CrossbowGolemRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new CrossbowGolemModel<>(pContext.bakeLayer(CrossbowGolemModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new CrossbowGolemCrackinessLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(CrossbowGolemEntity crossbow_golem) {
        return CROSSBOW_GOLEM_TEXTURE;
    }
}
