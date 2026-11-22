package net.luckystudios.entity.custom.turrets.crossbowgolem;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luckystudios.BlockySiege;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class CrossbowGolemModel<T extends CrossbowGolemEntity> extends HierarchicalModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(BlockySiege.id("crossbow_golem"), "main");
    private final ModelPart root;
    private final ModelPart crossbow_golem;
    private final ModelPart turret;
    private final ModelPart ammo;
    private final ModelPart nose;
    private final ModelPart banners;
    private final ModelPart string_left;
    private final ModelPart string_right;
    private final ModelPart fire_ring;

    public CrossbowGolemModel(ModelPart root) {
        this.root = root;
        this.crossbow_golem = root.getChild("crossbow_golem");
        this.turret = this.crossbow_golem.getChild("turret");
        this.ammo = this.turret.getChild("ammo");
        this.nose = this.turret.getChild("nose");
        this.banners = this.turret.getChild("banners");
        this.string_left = this.turret.getChild("string_left");
        this.string_right = this.turret.getChild("string_right");
        this.fire_ring = this.turret.getChild("fire_ring");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition crossbow_golem = partdefinition.addOrReplaceChild("crossbow_golem", CubeListBuilder.create().texOffs(60, 0).addBox(-9.0F, -6.0F, -3.0F, 18.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition turret = crossbow_golem.addOrReplaceChild("turret", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -13.0F, -16.0F, 14.0F, 14.0F, 32.0F, new CubeDeformation(0.0F))
                .texOffs(0, 46).addBox(-15.0F, -9.0F, -14.0F, 30.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition ammo = turret.addOrReplaceChild("ammo", CubeListBuilder.create().texOffs(52, 46).addBox(-3.0F, -12.0F, -4.0F, 6.0F, 10.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, 0.0F));

        PartDefinition nose = turret.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -2.0F, 8.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -16.0F));

        // Flat face in front of the nose (the "flash" part in the Blockbench model); the muzzle ring animation plays out on it.
        turret.addOrReplaceChild("fire_ring", CubeListBuilder.create().texOffs(1, 21).addBox(-5.0F, -5.0F, -1.0F, 10.0F, 10.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, -18.0F));

        PartDefinition banners = turret.addOrReplaceChild("banners", CubeListBuilder.create().texOffs(56, 56).addBox(-14.0F, -6.0F, -14.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(56, 56).mirror().addBox(8.0F, -6.0F, -14.0F, 6.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition string_left = turret.addOrReplaceChild("string_left", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(12.0F, -6.0F, -10.0F));

        PartDefinition cube_r1 = string_left.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(4, 0).mirror().addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        PartDefinition string_right = turret.addOrReplaceChild("string_right", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offset(-12.0F, -6.0F, -10.0F));

        PartDefinition cube_r2 = string_right.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(4, 0).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 20.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int i1, int i2) {
        this.fire_ring.visible = false;
        crossbow_golem.render(poseStack, vertexConsumer, i, i1, i2);
    }

    /** Draws only the muzzle ring face, following the turret's current rotation. */
    public void renderFireRing(PoseStack poseStack, VertexConsumer vertexConsumer, int light, int overlay, int color) {
        poseStack.pushPose();
        this.crossbow_golem.translateAndRotate(poseStack);
        this.turret.translateAndRotate(poseStack);
        this.fire_ring.visible = true;
        this.fire_ring.render(poseStack, vertexConsumer, light, overlay, color);
        this.fire_ring.visible = false;
        poseStack.popPose();
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T t, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        setStringPose(t);
        setAmmoPose(t);
        setTurretRotations(t.isHanging(), netHeadYaw, headPitch);
    }

    // Each string is pivoted at its limb tip (x = +-12, z = -10) and runs back towards the stock (+Z; the front of
    // the turret is -Z). Drawing the string swings it back through the Y rotation below, measured from the +Z axis
    // towards the centre line (values taken from the Blockbench model): 30 degrees relaxed/fired, 17.5 fully drawn.
    private static final float RELAXED_ANGLE = (float) Math.toRadians(30.0);
    private static final float DRAWN_ANGLE = (float) Math.toRadians(17.5);

    private void setStringPose(CrossbowGolemEntity entity) {
        boolean visible = entity.isCrossbowInstalled();
        this.string_left.visible = visible;
        this.string_right.visible = visible;
        if (!visible) {
            return;
        }
        float progress = Mth.clamp(entity.getChargeProgress(), 0.0F, 1.0F);
        float angle = Mth.lerp(progress, RELAXED_ANGLE, DRAWN_ANGLE);
        this.string_left.yRot = -angle;
        this.string_right.yRot = angle;
    }

    // The model's default pose is a full ammo box; sinking it this many pixels into the turret is empty.
    private static final float AMMO_EMPTY_DROP = 9.0F;

    private void setAmmoPose(CrossbowGolemEntity entity) {
        float fill = Mth.clamp(entity.getAmmoFill(), 0.0F, 1.0F);
        this.ammo.y += AMMO_EMPTY_DROP * (1.0F - fill);
    }

    private void setTurretRotations(boolean hanging, float netHeadYaw, float headPitch) {

        // Upside down, the turret can look further down than up. The renderer turns the whole model over, which
        // mirrors left/right and up/down, so the angles are negated to keep the turret aimed where it is looking.
        if (hanging) {
            headPitch = -Math.clamp(headPitch, -30, 60);
            netHeadYaw = -netHeadYaw;
        } else {
            headPitch = Math.clamp(headPitch, -60, 30);
        }

        // Convert to radians
        float yawRad = (float) Math.toRadians(netHeadYaw);
        float pitchRad = (float) Math.toRadians(headPitch);

        // Apply yaw rotation to the turret (horizontal rotation)
        this.crossbow_golem.yRot = yawRad;

        // Apply pitch rotation to the turret (vertical elevation)
        this.turret.xRot = pitchRad;
    }
}
