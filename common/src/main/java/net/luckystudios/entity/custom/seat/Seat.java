package net.luckystudios.entity.custom.seat;

import net.luckystudios.blocks.custom.shooting.AbstractShootingAimableBlockEntity;
import net.luckystudios.init.ModEntityTypes;
import net.luckystudios.util.SubLevels;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

public class Seat extends Entity {

    public Seat(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    public Seat(Level level, BlockPos pos) {
        super(ModEntityTypes.SEAT.get(), level);
        // Doubles, not floats: sub-level plots sit ~20 million blocks out, where a float can't hold a block position
        double xx = pos.getX() + 0.5;
        double yy = pos.getY();
        double zz = pos.getZ() + 0.5;
        this.setPos(xx, yy, zz);
        this.xo = xx;
        this.yo = yy;
        this.zo = zz;
    }

    @Override
    public void tick() {
        super.tick();
        Level level = this.level();
        if (this.getControllingPassenger() == null) return;
        if (!(this.getControllingPassenger() instanceof LivingEntity livingEntity)) return;
        this.setRot(livingEntity.getYRot(), livingEntity.getXRot() * 0.5F);
        if (!(level.getBlockEntity(blockPosition()) instanceof AbstractShootingAimableBlockEntity aimableBlockEntity)) {
            this.discard();
            return;
        }
        // Mob crews aim the weapon themselves (see SiegeCrewAI); only players steer it with their view
        if (livingEntity instanceof Mob) return;
        float playerYaw = livingEntity.getYRot();
        float playerPitch = livingEntity.getXRot();
        if (SubLevels.isOnSubLevel(this)) {
            // The weapon aims in the sub-level's frame but the player looks in world space: convert the view
            Vec3 look = SubLevels.directionToFrameOf(this, livingEntity.getViewVector(1.0F));
            playerYaw = (float) Math.toDegrees(Math.atan2(-look.x, look.z));
            playerPitch = (float) -Math.toDegrees(Math.asin(Mth.clamp(look.y, -1.0, 1.0)));
        }
        float normalizedYaw = (playerYaw % 360 + 360) % 360;
        float flippedPitch = -playerPitch;
        aimableBlockEntity.setYaw(normalizedYaw);
        aimableBlockEntity.setPitch(flippedPitch + aimableBlockEntity.pitchOffset());
        aimableBlockEntity.setChanged();
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        // How far behind the entity you want the passenger to sit
        double backOffset = 1.0;

        // Convert yaw to radians (yaw is in degrees)
        double yawRad = Math.toRadians(this.getYRot());

        // Calculate the offset vector (behind the entity)
        double offsetX = -Math.sin(yawRad) * -backOffset;
        double offsetZ = Math.cos(yawRad) * -backOffset;

        // Return the new attachment point (offset relative to entity's center)
        return new Vec3(offsetX, 0.5, offsetZ);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    // we don't need to read or write any data to the NBT.
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {}
    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {}

    // NeoForge hook (no @Override because vanilla and Fabric lack it): keeps the rider from using the sitting pose.
    public boolean shouldRiderSit() {
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity livingEntity
                ? livingEntity
                : super.getControllingPassenger();
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        // Lie the player down while they control the weapon
        if (passenger instanceof Player player) {
            player.setPose(Pose.SLEEPING);
        }
        // Face the way the turret is already aiming so mounting doesn't snap it to the player's view
        if (this.level().getBlockEntity(this.blockPosition()) instanceof AbstractShootingAimableBlockEntity aimable) {
            float yaw = aimable.yaw;
            // Inverse of the mapping in tick(): turret pitch = -playerPitch + pitchOffset
            float pitch = Mth.clamp(aimable.pitchOffset() - aimable.pitch, -90.0F, 90.0F);
            if (SubLevels.isOnSubLevel(this)) {
                // Turret angles are in the sub-level's frame; the player's view is in world space
                Vec3 world = SubLevels.directionToWorld(this, aimDirection(yaw, pitch));
                yaw = (float) Math.toDegrees(Math.atan2(-world.x, world.z));
                pitch = (float) -Math.toDegrees(Math.asin(Mth.clamp(world.y, -1.0, 1.0)));
            }
            passenger.setYRot(yaw);
            passenger.setXRot(pitch);
            passenger.setYHeadRot(yaw);
            passenger.yRotO = yaw;
            passenger.xRotO = pitch;
            if (passenger instanceof LivingEntity living) {
                living.yHeadRotO = yaw;
                living.yBodyRot = yaw;
                living.yBodyRotO = yaw;
            }
            this.setRot(yaw, pitch * 0.5F);
        }
    }

    /** View direction for a player yaw/pitch (degrees). */
    private static Vec3 aimDirection(float yaw, float pitch) {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        return new Vec3(-Math.sin(yawRad) * Math.cos(pitchRad), -Math.sin(pitchRad), Math.cos(yawRad) * Math.cos(pitchRad));
    }

    @Override
    protected void removePassenger(Entity passenger) {
        super.removePassenger(passenger);
        if (passenger instanceof Player player) {
            player.setPose(Pose.STANDING);
        }
        this.discard();
    }
}
