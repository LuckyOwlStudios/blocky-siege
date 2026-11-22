package net.luckystudios.blocks.custom.shooting.mortar;

import net.luckystudios.blocks.custom.shooting.cannon.CannonBlockEntity;
import net.luckystudios.init.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * The mortar's block entity. Everything about loading and firing is the cannon's; the barrel only points between
 * 45 and 85 degrees up, so every shot is a high lob, and each shell whistles on its way down (see {@link MortarShells}).
 */
public class MortarBlockEntity extends CannonBlockEntity {

    public static final float MIN_PITCH = 45.0F;
    public static final float MAX_PITCH = 85.0F;
    /** Where a freshly placed mortar points. Also the angle the model was built at. */
    public static final float REST_PITCH = 60.0F;

    public MortarBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.MORTAR_BLOCK_ENTITY.get(), pos, blockState);
        // A newly placed mortar rests at the angle it was modelled at (loading a saved one overrides this)
        this.pitch = REST_PITCH;
    }

    @Override
    protected void onShotFired(Projectile projectile) {
        MortarShells.mark(projectile);
    }

    @Override
    protected Vec3 fuseParticlePos() {
        // The fuse sits on the back of the barrel, just behind the trunnions
        return getRelativeLocationWithOffset(this, new Vec3(0, 0.0625, 0), 0.6f, 0.06f, 0.0f);
    }

    @Override
    public float maxPitch() {
        return MAX_PITCH;
    }

    @Override
    public float minPitch() {
        return MIN_PITCH;
    }

    @Override
    public float pitchOffset() {
        return MIN_PITCH;
    }

    /**
     * A seated player aims at where the shell should land rather than where the barrel points: looking at the
     * horizon gives the flattest, longest lob, and looking down toward the ground brings the shot in closer.
     */
    @Override
    public float pitchFromView(float viewPitch) {
        return MIN_PITCH + viewPitch;
    }

    @Override
    public float viewFromPitch(float pitch) {
        return pitch - MIN_PITCH;
    }

    @Override
    public float barrelOffsetDistance() {
        return 1.25F;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.blockysiege.mortar");
    }
}
