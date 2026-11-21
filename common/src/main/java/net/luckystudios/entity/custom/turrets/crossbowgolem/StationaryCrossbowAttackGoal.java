package net.luckystudios.entity.custom.turrets.crossbowgolem;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * Charges the installed crossbow exactly like a player would (start using it, hold for the
 * crossbow's own charge duration, release), so quick charge and any modded charge-time changes
 * apply, then fires once the turret is lined up with the predicted, arcing aim.
 */
public class StationaryCrossbowAttackGoal<T extends CrossbowGolemEntity> extends Goal {
    /** How closely the turret has to be lined up (degrees) before it lets go. */
    private static final float AIM_TOLERANCE = 4.0F;
    private static final float TURN_SPEED = 30.0F;

    private final T crossbow_golem;
    private int attackDelay;

    public StationaryCrossbowAttackGoal(T crossbow_golem) {
        this.crossbow_golem = crossbow_golem;
        this.setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.isValidTarget() || !this.crossbow_golem.hasCrossbow()) {
            return false;
        }
        ItemStack weapon = this.crossbow_golem.getMainHandItem();
        return CrossbowItem.isCharged(weapon) || this.crossbow_golem.hasProjectile(weapon);
    }

    private boolean isValidTarget() {
        LivingEntity target = this.crossbow_golem.getTarget();
        return target != null && target.isAlive()
                && this.crossbow_golem.distanceToSqr(target) <= CrossbowGolemEntity.ATTACK_RANGE * CrossbowGolemEntity.ATTACK_RANGE;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.crossbow_golem.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.crossbow_golem.setAggressive(false);
        if (this.crossbow_golem.isUsingItem()) {
            // Not loaded yet, so nothing is lost; a loaded crossbow stays loaded for the next target.
            this.crossbow_golem.stopUsingItem();
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = this.crossbow_golem.getTarget();
        if (target == null) {
            return;
        }
        boolean canSee = this.crossbow_golem.getSensing().hasLineOfSight(target);

        CrossbowGolemAiming.Solution aim = this.crossbow_golem.solveAim(target);
        Vec3 lookAt = this.crossbow_golem.getEyePosition().add(aim.direction().scale(16.0));
        this.crossbow_golem.getLookControl().setLookAt(lookAt.x, lookAt.y, lookAt.z, TURN_SPEED, TURN_SPEED);

        ItemStack weapon = this.crossbow_golem.getMainHandItem();
        if (CrossbowItem.isCharged(weapon)) {
            if (this.attackDelay > 0) {
                this.attackDelay--;
            } else if (canSee && this.isLinedUp(aim)) {
                this.crossbow_golem.performRangedAttack(target, 1.0F);
            }
        } else if (!this.crossbow_golem.isUsingItem()) {
            if (canSee && this.crossbow_golem.hasProjectile(weapon)) {
                this.crossbow_golem.startUsingItem(InteractionHand.MAIN_HAND);
                this.crossbow_golem.setChargingCrossbow(true);
            }
        } else if (this.crossbow_golem.getTicksUsingItem() >= CrossbowItem.getChargeDuration(weapon, this.crossbow_golem)) {
            // Releasing runs the crossbow's own loading code, which pulls ammo via CrossbowGolemEntity#getProjectile.
            this.crossbow_golem.releaseUsingItem();
            this.crossbow_golem.setChargingCrossbow(false);
            this.crossbow_golem.getCrossbowGolemBlockEntity().ifPresent(be -> be.inventory.setChanged());
            this.attackDelay = 5 + this.crossbow_golem.getRandom().nextInt(10);
        }
    }

    private boolean isLinedUp(CrossbowGolemAiming.Solution aim) {
        float yawError = Math.abs(Mth.wrapDegrees(this.crossbow_golem.getYHeadRot() - aim.yRot()));
        float pitchError = Math.abs(this.crossbow_golem.getXRot() - aim.xRot());
        return yawError <= AIM_TOLERANCE && pitchError <= AIM_TOLERANCE;
    }
}
