// Made with Blockbench 5.2.2
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class MortarModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "mortarmodel"), "main");
	private final ModelPart yaw;
	private final ModelPart pitch;
	private final ModelPart barrel;
	private final ModelPart bone;
	private final ModelPart tip;

	public MortarModel(ModelPart root) {
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

		PartDefinition bone = barrel.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 6.0F, -1.5708F, 0.0F, 0.0F));

		PartDefinition tip = barrel.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(48, 0).addBox(-7.0F, -7.0F, -2.0F, 14.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -11.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		yaw.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}