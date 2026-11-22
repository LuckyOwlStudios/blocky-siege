package net.luckystudios.entity.custom.cannonball.types.soul_bomb;

import net.luckystudios.entity.custom.cannonball.AbstractCannonBall;
import net.luckystudios.entity.custom.cannonball.AbstractNewProjectile;
import net.luckystudios.init.ModBlocks;
import net.luckystudios.init.ModEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/**
 * A bomb that screams. Fired from a siege weapon it never flies: it stays at the muzzle, charges with the Warden's
 * sonic charge sound, then releases a Warden-style sonic boom beam in the direction it was shot. The beam hurts
 * everything in its path (ignoring armor) and tunnels through blocks, getting weaker the harder the blocks are,
 * until it runs out of strength or hits something it cannot break.
 */
public class SoulBomb extends AbstractCannonBall {

    /** How far the boom travels at most, in blocks. */
    private static final int MAX_RANGE = 20;
    /** The boom's strength. Each block on its centerline spends its blast resistance (at least 0.5) from this. */
    private static final float BOOM_STRENGTH = 60.0F;
    /** Blocks tougher than this (obsidian, ancient debris, ...) stop the boom outright. */
    private static final float MAX_BREAKABLE_RESISTANCE = 100.0F;
    /** Blocks within this distance of the boom's centerline are broken (makes a tunnel about 3 blocks wide). */
    private static final double TUNNEL_RADIUS = 1.3;
    /** Same damage as the Warden's sonic boom. */
    private static final float BOOM_DAMAGE = 10.0F;
    /** Ticks between the charge sound and the boom, same as the Warden. */
    private static final int CHARGE_TICKS = 34;
    /** The weapon firing the bomb is this close to the muzzle, so the boom leaves it (and its crew) alone. */
    private static final double SAFE_DISTANCE = 2.0;

    /** The direction the bomb was shot in; the boom follows it. */
    private Vec3 beamDirection = Vec3.ZERO;
    private int chargeTicks = 0;

    public SoulBomb(EntityType<? extends AbstractNewProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public SoulBomb(Level level, double x, double y, double z) {
        super(ModEntityTypes.SOUL_BOMB.get(), x, y, z, level, new ItemStack(ModBlocks.SOUL_BOMB.get()));
    }

    public SoulBomb(Level level, LivingEntity owner) {
        super(ModEntityTypes.SOUL_BOMB.get(), owner, level, new ItemStack(ModBlocks.SOUL_BOMB.get()));
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return ModBlocks.SOUL_BOMB.get().asItem().getDefaultInstance();
    }

    @Override
    protected float baseDamage() {
        return 8;
    }

    @Override
    protected SoundEvent impactSound() {
        return SoundEvents.SCULK_BLOCK_HIT;
    }

    /** The bomb doesn't fly: it only remembers where it was aimed. */
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3 dir = new Vec3(x, y, z);
        if (dir.lengthSqr() > 1.0E-6) this.beamDirection = dir.normalize();
        this.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void tick() {
        this.setDeltaMovement(Vec3.ZERO);
        if (!(this.level() instanceof ServerLevel level)) return;
        if (this.beamDirection.lengthSqr() < 1.0E-4) {
            this.discard();
            return;
        }
        if (this.chargeTicks == 0) {
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WARDEN_SONIC_CHARGE, this.getSoundSource(), 3.0F, 1.0F);
        }
        if (this.chargeTicks % 4 == 0) {
            level.sendParticles(ParticleTypes.SCULK_SOUL, this.getX(), this.getY(), this.getZ(), 2, 0.2, 0.2, 0.2, 0.02);
        }
        if (++this.chargeTicks >= CHARGE_TICKS) {
            this.sonicBoom();
        }
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    private void sonicBoom() {
        if (!(this.level() instanceof ServerLevel level) || this.isRemoved()) return;

        Vec3 dir = this.beamDirection;
        Vec3 start = this.position();
        Entity owner = this.getOwner();

        level.playSound(null, start.x, start.y, start.z, SoundEvents.WARDEN_SONIC_BOOM, this.getSoundSource(), 3.0F, 1.0F);

        // Blocks: walk the line, spending strength on the blocks along the centerline
        boolean mayGrief = owner instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        Set<BlockPos> visited = new HashSet<>();
        float strength = BOOM_STRENGTH;
        int reach = 0;
        for (int step = 1; step <= MAX_RANGE; step++) {
            Vec3 center = start.add(dir.scale(step));
            BlockPos centerPos = BlockPos.containing(center);
            BlockState centerState = level.getBlockState(centerPos);
            if (!centerState.isAir() && centerState.getFluidState().isEmpty()) {
                float resistance = centerState.getBlock().getExplosionResistance();
                if (centerState.getDestroySpeed(level, centerPos) < 0 || resistance > MAX_BREAKABLE_RESISTANCE || !this.canBreak(centerState)) {
                    break;
                }
                strength -= Math.max(0.5F, resistance);
                if (strength < 0) break;
            }
            reach = step;
            level.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y, center.z, 0, 0, 0, 0, 0);

            if (!mayGrief) continue;
            int r = Mth.ceil(TUNNEL_RADIUS);
            for (BlockPos pos : BlockPos.betweenClosed(centerPos.offset(-r, -r, -r), centerPos.offset(r, r, r))) {
                if (pos.getCenter().distanceToSqr(center) > TUNNEL_RADIUS * TUNNEL_RADIUS || pos.getCenter().distanceToSqr(start) < SAFE_DISTANCE * SAFE_DISTANCE || !visited.add(pos.immutable())) continue;
                BlockState state = level.getBlockState(pos);
                if (state.isAir() || !state.getFluidState().isEmpty()) continue;
                if (state.getDestroySpeed(level, pos) < 0 || state.getBlock().getExplosionResistance() > MAX_BREAKABLE_RESISTANCE || !this.canBreak(state)) {
                    continue;
                }
                if (owner instanceof Player player && !level.mayInteract(player, pos)) continue;
                level.destroyBlock(pos.immutable(), true, this);
            }
        }

        // Entities: everything near the line takes the boom, and armor doesn't help
        Vec3 end = start.add(dir.scale(reach));
        AABB area = new AABB(start, end).inflate(TUNNEL_RADIUS + 1.0);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area, e -> e != owner && e.isAlive())) {
            Vec3 point = target.getBoundingBox().getCenter();
            double along = Mth.clamp(point.subtract(start).dot(dir), 0, reach);
            if (point.distanceToSqr(start) < SAFE_DISTANCE * SAFE_DISTANCE) continue;
            Vec3 closest = start.add(dir.scale(along));
            double reachRadius = TUNNEL_RADIUS + target.getBbWidth() / 2.0;
            if (point.distanceToSqr(closest) > reachRadius * reachRadius) continue;

            if (target.hurt(level.damageSources().sonicBoom(owner != null ? owner : this), BOOM_DAMAGE)) {
                double resist = 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
                target.push(dir.x * 2.5 * resist, 0.5 * resist, dir.z * 2.5 * resist);
            }
        }

        this.discard();
    }
}
