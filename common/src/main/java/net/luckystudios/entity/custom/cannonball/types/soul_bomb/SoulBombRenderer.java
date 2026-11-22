package net.luckystudios.entity.custom.cannonball.types.soul_bomb;

import com.mojang.blaze3d.vertex.PoseStack;
import net.luckystudios.BlockySiege;
import net.luckystudios.entity.custom.cannonball.AbstractCannonBallRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SoulBombRenderer extends AbstractCannonBallRenderer<SoulBomb> {

    public static final ResourceLocation CANNON_BALL_LOCATION = ResourceLocation.fromNamespaceAndPath(BlockySiege.MOD_ID, "textures/entity/projectile/soul_bomb.png");

    public SoulBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    /** The bomb never flies, it only charges at the muzzle, so there is nothing to draw. */
    @Override
    public void render(@NotNull SoulBomb entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SoulBomb entity) {
        return CANNON_BALL_LOCATION;
    }
}
