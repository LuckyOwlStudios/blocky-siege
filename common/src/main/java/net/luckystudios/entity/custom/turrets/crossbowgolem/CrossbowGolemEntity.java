package net.luckystudios.entity.custom.turrets.crossbowgolem;

import net.luckystudios.util.MenuHelper;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import java.util.EnumSet;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
    }

    public CrossbowGolemEntity(Level level, BlockPos pos) {
        super(ModEntityTypes.CROSSBOW_GOLEM.get(), level, pos);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
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

    /**
     * Like IronGolem#isPlayerCreated: true when a player built or placed this golem. Golems that were
     * not (natural spawns, spawn eggs, commands) are neutral and only fight back against a player who hurts them.
     */
    private boolean playerCreated;

    public boolean isPlayerCreated() {
        return this.playerCreated;
    }

    public void setPlayerCreated(boolean playerCreated) {
        this.playerCreated = playerCreated;
    }

    /**
     * Spawn eggs (a player's, or a dispenser next to one) make a player-made golem on the nearest player's team,
     * the same as placing the block by hand. Natural spawns and commands stay neutral.
     */
    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, net.minecraft.world.DifficultyInstance difficulty,
                                                                   net.minecraft.world.entity.MobSpawnType spawnType, @Nullable net.minecraft.world.entity.SpawnGroupData spawnGroupData) {
        if (spawnType == net.minecraft.world.entity.MobSpawnType.SPAWN_EGG) {
            this.playerCreated = true;
            Player placer = level.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 8.0, false);
            if (placer != null) {
                this.setPlacer(placer);
                if (placer.getTeam() instanceof net.minecraft.world.scores.PlayerTeam team) {
                    level.getLevel().getScoreboard().addPlayerToTeam(this.getScoreboardName(), team);
                }
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData);
    }

    /** Turrets built by the same player are never hostile to each other, even without a scoreboard team. */
    @Override
    public boolean isAlliedTo(net.minecraft.world.entity.Entity other) {
        if (super.isAlliedTo(other)) {
            return true;
        }
        return other instanceof CrossbowGolemEntity golem && this.placerUUID != null && this.placerUUID.equals(golem.placerUUID);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("PlayerCreated", this.playerCreated);
        if (this.placerUUID != null) {
            compound.putUUID("Placer", this.placerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.placerUUID = compound.hasUUID("Placer") ? compound.getUUID("Placer") : null;
        this.playerCreated = compound.getBoolean("PlayerCreated");
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

        // Player-made golems only shoot players outside their scoreboard team; natural ones leave players alone
        // unless a player hurts them first.
        this.targetSelector.addGoal(1, new RetaliateGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 1, false, false, (entity) -> this.playerCreated && !this.isAlliedTo(entity)));
        // Hostile mobs, plus (for player-made golems) turrets that are not on its team.
        this.targetSelector.addGoal(3, new TowerTargetGoal(this, (entity) -> !this.isAlliedTo(entity)
                && (entity instanceof Enemy || (this.playerCreated && entity instanceof AbstractTurret))));
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
    /** Natural golems fight back against the player who hurt them. */
    private static class RetaliateGoal extends TargetGoal {
        private final CrossbowGolemEntity golem;
        private int lastHurtTimestamp;

        RetaliateGoal(CrossbowGolemEntity golem) {
            super(golem, false);
            this.golem = golem;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            int timestamp = this.golem.getLastHurtByMobTimestamp();
            LivingEntity attacker = this.golem.getLastHurtByMob();
            return !this.golem.playerCreated && timestamp != this.lastHurtTimestamp
                    && attacker instanceof Player && this.canAttack(attacker, TargetingConditions.DEFAULT);
        }

        @Override
        public void start() {
            this.golem.setTarget(this.golem.getLastHurtByMob());
            this.targetMob = this.golem.getTarget();
            this.lastHurtTimestamp = this.golem.getLastHurtByMobTimestamp();
            super.start();
        }
    }

    private static class TowerTargetGoal extends NearestAttackableTargetGoal<Mob> {
        TowerTargetGoal(Mob mob, Predicate<LivingEntity> selector) {
            super(mob, Mob.class, 1, false, false, selector);
        }

        @Override
        protected net.minecraft.world.phys.AABB getTargetSearchArea(double targetDistance) {
            return this.mob.getBoundingBox().inflate(targetDistance);
        }
    }
}
