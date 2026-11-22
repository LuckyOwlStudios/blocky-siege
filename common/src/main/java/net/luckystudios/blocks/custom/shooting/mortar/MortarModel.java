package net.luckystudios.blocks.custom.shooting.mortar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.Entity;

/** Bryan's Blockbench mortar (models/MortarModel.bbmodel). The barrel points along -Y, tilted by the pitch part. */
public class MortarModel<T extends Entity> extends EntityModel<T> {

    private final ModelPart root;
    private final ModelPart yaw;
    private final ModelPart pitch;
    private final ModelPart barrel;
    private final ModelPart bone;
    private final ModelPart tip;

    public MortarModel(ModelPart root) {
        this.root = root;
        this.yaw = root.getChild("yaw");
        this.pitch = this.yaw.getChild("pitch");
        this.barrel = this.pitch.getChild("barrel");
        this.bone = this.barrel.getChild("bone");
        this.tip = this.barrel.getChild("tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition yaw = partdefinition.addOrReplaceChild("yaw", CubeListBuilder.create().texOffs(0, 38).addBox(-8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 72).mirror().addBox(-8.0F, -16.0F, -8.0F, 2.0F, 14.0F, 16.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 72).addBox(6.0F, -16.0F, -8.0F, 2.0F, 14.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition pitch = yaw.addOrReplaceChild("pitch", CubeListBuilder.create().texOffs(0, 56).addBox(-9.0F, -4.0F, -4.0F, 18.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -9.0F, 0.0F, 0.5236F, 0.0F, 0.0F));

        PartDefinition barrel = pitch.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -11.0F, -6.0F, 12.0F, 17.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        barrel.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 6.0F, -1.5708F, 0.0F, 0.0F));

        barrel.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(48, 0).addBox(-7.0F, -7.0F, -2.0F, 14.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T t, float v, float v1, float v2, float v3, float v4) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    /**
     * @param yaw   turret yaw in degrees, as the block entity stores it
     * @param pitch barrel elevation above the horizon in degrees. The barrel points straight up at xRot 0, so the
     *              model's own 30 degrees of tilt is an elevation of 60.
     */
    public void setRotations(float yaw, float pitch) {
        this.yaw.yRot = (float) Math.toRadians(yaw);
        this.pitch.xRot = (float) Math.toRadians(90.0F - pitch);
    }
}
