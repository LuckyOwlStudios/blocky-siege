package net.luckystudios.entity.custom.turrets.crossbowgolem;

import net.luckystudios.util.MenuHelper;
import net.luckystudios.util.SubLevels;
import net.luckystudios.blocks.custom.turret.crossbowgolem.CrossbowGolemBlockEntity;
import net.luckystudios.init.ModBlockEntityTypes;
import net.luckystudios.init.ModBlocks;
import net.luckystudios.init.ModEntityTypes;
import net.luckystudios.entity.custom.turrets.AbstractTurret;
import net.luckystudios.init.ModSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class CrossbowGolemEntity extends AbstractTurret implements CrossbowAttackMob {

    // Synced to clients so the model can hide the strings without a crossbow and pull them back while charging.
    private static final EntityDataAccessor<Boolean> HAS_CROSSBOW = SynchedEntityData.defineId(CrossbowGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> CHARGE_PROGRESS = SynchedEntityData.defineId(CrossbowGolemEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> AMMO_FILL = SynchedEntityData.defineId(CrossbowGolemEntity.class, EntityDataSerializers.FLOAT);

    public CrossbowGolemEntity(EntityType<CrossbowGolemEntity> entityType, Level level) {
        super(entityType, level);
        // The crossbow it holds is the one stored in the block, which drops it; never drop a copy.
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.lookControl = new SubLevelLookControl(this);
    }

    public CrossbowGolemEntity(Level level, BlockPos pos) {
        super(ModEntityTypes.CROSSBOW_GOLEM.get(), level, pos);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        this.lookControl = new SubLevelLookControl(this);
    }

    /** The player who built this crossbow_golem, if any. Saved with the entity. */
    @Nullable
    private UUID placerUUID;

    public void setPlacer(@Nullable LivingEntity placer) {
        this.placerUUID = placer == null ? null : placer.getUUID();
    }

    @Nullable
    public UUID getPlacerUUID() {
        return this.placerUUID;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (this.placerUUID != null) {
            compound.putUUID("Placer", this.placerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.placerUUID = compound.hasUUID("Placer") ? compound.getUUID("Placer") : null;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HAS_CROSSBOW, false);
        builder.define(CHARGE_PROGRESS, 0.0F);
        builder.define(AMMO_FILL, 0.0F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0F)
                .add(Attributes.ATTACK_SPEED, 1)
                .add(Attributes.FOLLOW_RANGE, ATTACK_RANGE)
                .add(Attributes.ARMOR, 4.0F);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new StationaryCrossbowAttackGoal<>(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, (float) ATTACK_RANGE, 1.0F));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Mob.class, (float) ATTACK_RANGE));

        // FIXED: Remove randomInterval for immediate targeting, set mustSee to false for longer range
        // A crossbow_golem on a scoreboard team (it joins its placer's) leaves teammates alone; without a team it shoots any player
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 1, false, false, (entity) -> !this.isAlliedTo(entity)));
        this.targetSelector.addGoal(2, new TowerTargetGoal(this, (entity) -> entity instanceof Enemy && !this.isAlliedTo(entity)));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            if (this.fireRingTicks > 0) {
                this.fireRingTicks--;
            }
            return;
        }
        ItemStack crossbow = ItemStack.EMPTY;
        Optional<CrossbowGolemBlockEntity> blockEntity = getCrossbowGolemBlockEntity();
        if (blockEntity.isPresent()) {
            ItemStack stored = blockEntity.get().inventory.getItem(CrossbowGolemBlockEntity.CROSSBOW_SLOT);
            if (stored.isEmpty() && stored != ItemStack.EMPTY) {
                // The crossbow broke while firing.
                blockEntity.get().inventory.setItem(CrossbowGolemBlockEntity.CROSSBOW_SLOT, ItemStack.EMPTY);
            } else if (stored.getItem() instanceof CrossbowItem) {
                crossbow = stored;
            }
        }
        // Hold the very same stack that lives in the block's inventory, so charging, damage,
        // enchantments and the loaded projectiles all stay on the real item.
        if (this.getMainHandItem() != crossbow) {
            this.setItemInHand(InteractionHand.MAIN_HAND, crossbow);
        }

        boolean hasCrossbow = !crossbow.isEmpty();
        this.getEntityData().set(HAS_CROSSBOW, hasCrossbow);
        float progress = 0.0F;
        if (hasCrossbow) {
            if (CrossbowItem.isCharged(crossbow)) {
                progress = 1.0F;
            } else if (this.isUsingItem()) {
                progress = Mth.clamp(this.getTicksUsingItem() / (float) Math.max(1, CrossbowItem.getChargeDuration(crossbow, this)), 0.0F, 1.0F);
            }
        }
        this.getEntityData().set(CHARGE_PROGRESS, progress);
        this.getEntityData().set(AMMO_FILL, blockEntity.map(CrossbowGolemEntity::computeAmmoFill).orElse(0.0F));
    }

    /** Fraction (0 to 1) of the ammo slots' capacity that is filled, by item count. */
    private static float computeAmmoFill(CrossbowGolemBlockEntity be) {
        int count = 0;
        int capacity = 0;
        for (int i = 0; i < CrossbowGolemBlockEntity.CROSSBOW_SLOT; i++) {
            ItemStack stack = be.inventory.getItem(i);
            count += stack.getCount();
            capacity += stack.isEmpty() ? 64 : stack.getMaxStackSize();
        }
        return capacity == 0 ? 0.0F : Mth.clamp(count / (float) capacity, 0.0F, 1.0F);
    }

    /** How full the ammo inventory is, 0 to 1, as synced to the client (used by the model). */
    public float getAmmoFill() {
        return this.getEntityData().get(AMMO_FILL);
    }

    public Optional<CrossbowGolemBlockEntity> getCrossbowGolemBlockEntity() {
        Level level = this.level();
        return level.getBlockEntity(this.blockPosition().below(), ModBlockEntityTypes.CROSSBOW_GOLEM_BLOCK_ENTITY.get());
    }

    @Override
    public int getMaxHeadXRot() {
        return 85;
    }

    @Override
    public int getMaxHeadYRot() {
        return 180;
    }

    @Override
    public BlockState attachedBlockState() {
        return ModBlocks.CROSSBOW_GOLEM_BLOCK.get().defaultBlockState();
    }

    @Override
    public ParticleOptions breakParticle() {
        return new BlockParticleOption(ParticleTypes.BLOCK, attachedBlockState());
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource damageSource) {
        return ModSoundEvents.METAL_HIT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR;
    }

    /** Whether a crossbow is installed, as synced to the client (used by the model). */
    public boolean isCrossbowInstalled() {
        return this.getEntityData().get(HAS_CROSSBOW);
    }

    /** 0 when relaxed, 1 when fully drawn or loaded. */
    public float getChargeProgress() {
        return this.getEntityData().get(CHARGE_PROGRESS);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        BlockPos currentPos = this.blockPosition();
        BlockPos blockPosBelow = currentPos.below();
        Level level = this.level();
        if (level.getBlockEntity(blockPosBelow) instanceof CrossbowGolemBlockEntity crossbow_golemBlockEntity) {
            MenuHelper.openMenu(player, new SimpleMenuProvider(crossbow_golemBlockEntity, crossbow_golemBlockEntity.getDisplayName()), blockPosBelow);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public void die(DamageSource damageSource) {
        BlockPos currentPos = this.blockPosition();
        BlockPos blockPosBelow = currentPos.below();
        Level level = this.level();
        if (level.getBlockEntity(blockPosBelow) instanceof CrossbowGolemBlockEntity) {
            level.destroyBlock(blockPosBelow, false);
        }
        super.die(damageSource);
        this.discard();
    }

    public boolean hasCrossbow() {
        return getCrossbowGolemBlockEntity()
                .map(be -> be.inventory.getItem(CrossbowGolemBlockEntity.CROSSBOW_SLOT).getItem() instanceof CrossbowItem)
                .orElse(false);
    }

    /**
     * Ammo slots that hold something the given weapon can fire: whatever the crossbow itself supports
     * (arrows, tipped/spectral arrows, fireworks, modded ammo), plus any arrow item even if a mod forgot
     * to tag it, since crossbows build those through ArrowItem#createArrow.
     */
    private List<ItemStack> findProjectiles(ItemStack weapon) {
        List<ItemStack> candidates = new ArrayList<>();
        if (!(weapon.getItem() instanceof ProjectileWeaponItem weaponItem)) {
            return candidates;
        }
        // A crossbow's "all supported" predicate is arrows only; fireworks live in its "held" predicate,
        // which players only use for ammo in their hands. A golem's ammo slots count as both.
        Predicate<ItemStack> supported = weaponItem.getAllSupportedProjectiles().or(weaponItem.getSupportedHeldProjectiles());
        getCrossbowGolemBlockEntity().ifPresent(be -> {
            for (int i = 0; i < CrossbowGolemBlockEntity.CROSSBOW_SLOT; i++) {
                ItemStack stack = be.inventory.getItem(i);
                if (!stack.isEmpty() && (supported.test(stack) || stack.getItem() instanceof ArrowItem)) {
                    candidates.add(stack);
                }
            }
        });
        return candidates;
    }

    public boolean hasProjectile(ItemStack weapon) {
        return !findProjectiles(weapon).isEmpty();
    }

    /**
     * The crossbow draws its ammunition through this method when it loads, so picking a random
     * stack here makes the crossbow_golem fire a random projectile from its inventory.
     */
    @Override
    public ItemStack getProjectile(ItemStack weapon) {
        List<ItemStack> candidates = findProjectiles(weapon);
        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return candidates.get(this.getRandom().nextInt(candidates.size()));
    }

    @Override
    public void setChargingCrossbow(boolean charging) {
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, FIRED_EVENT);
        }
    }

    /** Entity event sent to clients when the crossbow golem fires, to play the muzzle ring animation. */
    public static final byte FIRED_EVENT = 70;
    /** How long the muzzle ring animation lasts, in ticks. */
    public static final int FIRE_RING_TICKS = 6;
    /** Client only: ticks left of the muzzle ring animation, 0 when idle. */
    public int fireRingTicks;

    @Override
    public void handleEntityEvent(byte id) {
        if (id == FIRED_EVENT) {
            this.fireRingTicks = FIRE_RING_TICKS;
        } else {
            super.handleEntityEvent(id);
        }
    }

    public static final double ATTACK_RANGE = 48.0;
    private static final float ARROW_POWER = 3.15F;
    private static final float ROCKET_POWER = 1.6F;
    private static final float INACCURACY = 0.25F;

    private static boolean firesRocket(ItemStack weapon) {
        ChargedProjectiles loaded = weapon.get(DataComponents.CHARGED_PROJECTILES);
        return loaded != null && loaded.contains(Items.FIREWORK_ROCKET);
    }

    /** Aim solution for the loaded (or, while charging, the expected) projectile, leading the target. */
    public CrossbowGolemAiming.Solution solveAim(LivingEntity target) {
        if (firesRocket(this.getMainHandItem())) {
            return CrossbowGolemAiming.solve(this, target, ROCKET_POWER, CrossbowGolemAiming.ROCKET_GRAVITY, CrossbowGolemAiming.ROCKET_DRAG);
        }
        return CrossbowGolemAiming.solve(this, target, ARROW_POWER, CrossbowGolemAiming.ARROW_GRAVITY, CrossbowGolemAiming.ARROW_DRAG);
    }

    /**
     * Fires the loaded crossbow through its own item logic, so multishot, piercing, durability and
     * any modded crossbow or enchantment behaviour all apply. The crossbow shoots along the
     * crossbow_golem's view, which is pointed at the solved (leading, arcing) aim first.
     */
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        ItemStack weapon = this.getMainHandItem();
        if (!(weapon.getItem() instanceof CrossbowItem crossbow) || !CrossbowItem.isCharged(weapon)) {
            return;
        }
        CrossbowGolemAiming.Solution aim = solveAim(target);
        this.setYHeadRot(aim.yRot());
        this.yHeadRotO = aim.yRot();
        this.setXRot(aim.xRot());
        this.xRotO = aim.xRot();
        float power = firesRocket(weapon) ? ROCKET_POWER : ARROW_POWER;
        crossbow.performShooting(this.level(), this, InteractionHand.MAIN_HAND, weapon, power, INACCURACY, null);
        getCrossbowGolemBlockEntity().ifPresent(be -> be.inventory.setChanged());
        this.onCrossbowAttackPerformed();
    }

    /**
     * Vanilla only searches 4 blocks up and down for mobs, which leaves a crossbow golem on a
     * tower blind to anything below it. This searches the full attack range in every direction.
     */
    private static class TowerTargetGoal extends NearestAttackableTargetGoal<Mob> {
        TowerTargetGoal(Mob mob, Predicate<LivingEntity> selector) {
            super(mob, Mob.class, 1, false, false, selector);
        }

        @Override
        protected AABB getTargetSearchArea(double targetDistance) {
            // On a Sable sub-level the golem sits in the sub-level's plot, so search around where it really is.
            Vec3 world = SubLevels.toWorld(this.mob.level(), this.mob.position());
            return this.mob.getBoundingBox().move(world.subtract(this.mob.position())).inflate(targetDistance);
        }
    }

    /**
     * Line of sight from where the golem really is. On a Sable sub-level its own coordinates are in the
     * sub-level's plot, so vanilla's ray would be cast from the wrong place.
     */
    @Override
    public boolean hasLineOfSight(Entity target) {
        if (!SubLevels.isOnSubLevel(this)) {
            return super.hasLineOfSight(target);
        }
        if (target.level() != this.level()) {
            return false;
        }
        Vec3 from = SubLevels.toWorld(this.level(), this.getEyePosition());
        Vec3 to = SubLevels.toWorld(this.level(), target.getEyePosition());
        if (to.distanceToSqr(from) > 128.0 * 128.0) {
            return false;
        }
        return this.level().clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this)).getType() == HitResult.Type.MISS;
    }

    /**
     * Look targets are given in world space, but on a Sable sub-level the golem's yaw and pitch are relative
     * to the sub-level, so convert the point into its frame first. Off a sub-level this is plain vanilla.
     */
    private static class SubLevelLookControl extends LookControl {
        SubLevelLookControl(Mob mob) {
            super(mob);
        }

        @Override
        public void setLookAt(double x, double y, double z, float deltaYaw, float deltaPitch) {
            Vec3 local = SubLevels.toFrameOf(this.mob, new Vec3(x, y, z));
            super.setLookAt(local.x, local.y, local.z, deltaYaw, deltaPitch);
        }
    }
}
