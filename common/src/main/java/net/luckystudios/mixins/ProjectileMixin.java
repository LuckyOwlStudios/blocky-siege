package net.luckystudios.mixins;

import net.luckystudios.blocks.custom.shooting.mortar.MortarShells;
import net.minecraft.world.entity.projectile.Projectile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Lets any projectile fired from a mortar whistle on its way down, whatever ammo it is. */
@Mixin(Projectile.class)
public abstract class ProjectileMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void blockysiege$mortarShellWhistle(CallbackInfo ci) {
        MortarShells.tick((Projectile) (Object) this);
    }
}
