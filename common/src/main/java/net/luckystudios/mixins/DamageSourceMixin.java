package net.luckystudios.mixins;

import net.luckystudios.init.ModDamageTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {

    /** How many random cannonball death messages exist in the lang files. */
    private static final int BLOCKYSIEGE$CANNONBALL_VARIANTS = 8;

    /** Picks one of the cannonball death message variants at random, using the "player" form when someone fired it. */
    @Inject(method = "getLocalizedDeathMessage", at = @At("HEAD"), cancellable = true)
    private void blockysiege$cannonballDeathMessage(LivingEntity victim, CallbackInfoReturnable<Component> cir) {
        DamageSource self = (DamageSource) (Object) this;
        if (!self.is(ModDamageTypes.CANNONBALL)) return;
        String key = "death.attack.cannonball." + (victim.getRandom().nextInt(BLOCKYSIEGE$CANNONBALL_VARIANTS) + 1);
        Entity attacker = self.getEntity();
        if (attacker == null || attacker == victim) attacker = victim.getKillCredit();
        if (attacker != null && attacker != victim) {
            cir.setReturnValue(Component.translatable(key + ".player", victim.getDisplayName(), attacker.getDisplayName()));
        } else {
            cir.setReturnValue(Component.translatable(key, victim.getDisplayName()));
        }
    }
}
