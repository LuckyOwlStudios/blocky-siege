package net.luckystudios.entity.custom.cannonball.types.soul_bomb;

import net.luckystudios.BlockySiege;
import net.luckystudios.entity.custom.cannonball.AbstractCannonBallRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class SoulBombRenderer extends AbstractCannonBallRenderer<SoulBomb> {

    public static final ResourceLocation CANNON_BALL_LOCATION = ResourceLocation.fromNamespaceAndPath(BlockySiege.MOD_ID, "textures/entity/projectile/soul_bomb.png");

    public SoulBombRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SoulBomb entity) {
        return CANNON_BALL_LOCATION;
    }
}
