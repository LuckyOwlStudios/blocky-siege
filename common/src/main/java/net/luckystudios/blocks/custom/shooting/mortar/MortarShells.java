package net.luckystudios.blocks.custom.shooting.mortar;

import net.luckystudios.init.ModSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;

/**
 * Mortar shells are ordinary projectiles with a tag on them. When one tops out and starts to fall, it plays the
 * shell whistle, which follows it down and cuts off when it lands. Ticked from {@code ProjectileMixin}.
 */
public final class MortarShells {

    /** Scoreboard tag on projectiles fired from a mortar that haven't whistled yet. Saved with the entity. */
    public static final String TAG = "blockysiege.mortar_shell";
    /** Volume above 1 only widens who hears it: 6 reaches about 96 blocks. */
    private static final float WHISTLE_VOLUME = 6.0F;

    private MortarShells() {
    }

    public static void mark(Projectile projectile) {
        projectile.addTag(TAG);
    }

    /** Called at the start of every projectile tick, on both sides. */
    public static void tick(Projectile projectile) {
        if (projectile.level().isClientSide) return;
        if (projectile.getDeltaMovement().y > 0.0 || !projectile.getTags().contains(TAG)) return;
        // Past the top of the arc: whistle once, on the way down
        projectile.removeTag(TAG);
        float pitch = 0.95F + projectile.getRandom().nextFloat() * 0.1F;
        projectile.level().playSound(null, projectile, ModSoundEvents.MORTAR_WHISTLE.get(), SoundSource.BLOCKS, WHISTLE_VOLUME, pitch);
    }
}
