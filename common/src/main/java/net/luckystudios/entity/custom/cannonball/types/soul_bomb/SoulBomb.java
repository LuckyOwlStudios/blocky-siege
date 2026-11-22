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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

/**
 * A bomb that screams: on impact it lets out a Warden-style sonic boom that carries on in the direction it was
 * travelling. The boom hurts everything in its path (ignoring armor, like the Warden's) and tunnels through blocks,
 * getting weaker the harder the blocks are, until it runs out of strength or hits something it cannot break.
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

    /** The direction the bomb was last flying in; the boom follows it. */
    private Vec3 travelDirection = Vec3.ZERO;

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

    @Override
    public void tick() {
        Vec3 movement = this.getDeltaMovement();
        if (movement.lengthSqr() > 0.01) {
            this.travelDirection = movement.normalize();
        }
        super.tick();
    }

    @Override
    public void spawnTrailParticles() {
        super.spawnTrailParticles();
        if (this.tickCount > 1 && !this.isInWater()) {
            Vec3 newPos = this.position();
            double dx = newPos.x - xo;
            double dy = newPos.y - yo;
            double dz = newPos.z - zo;
            int s = 4;
            for (int i = 0; i < s; ++i) {
                double j = i / (double) s;
                this.level().addParticle(ParticleTypes.SCULK_SOUL,
                        xo - dx * j,
                        0.25 + yo - dy * j,
                        zo - dz * j,
                        0, 0.02, 0);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        this.sonicBoom();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.sonicBoom();
    }

    private void sonicBoom() {
        if (!(this.level() instanceof ServerLevel level) || this.isRemoved()) return;

        Vec3 dir = this.travelDirection.lengthSqr() > 1.0E-4 ? this.travelDirection : new Vec3(0, -1, 0);
        // Start a little inside whatever we hit, so that block goes too
        Vec3 start = this.position().add(dir.scale(0.4));
        Entity owner = this.getOwner();

        level.playSound(null, start.x, start.y, start.z, SoundEvents.WARDEN_SONIC_BOOM, this.getSoundSource(), 3.0F, 1.0F);

        // Blocks: walk the line, spending strength on the blocks along the centerline
        boolean mayGrief = owner instanceof Player || level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        Set<BlockPos> visited = new HashSet<>();
        float strength = BOOM_STRENGTH;
        int reach = 0;
        for (int step = 0; step <= MAX_RANGE; step++) {
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
                if (pos.getCenter().distanceToSqr(center) > TUNNEL_RADIUS * TUNNEL_RADIUS || !visited.add(pos.immutable())) continue;
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
