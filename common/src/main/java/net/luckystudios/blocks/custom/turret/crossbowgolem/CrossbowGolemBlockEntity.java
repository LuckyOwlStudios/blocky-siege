package net.luckystudios.blocks.custom.turret.crossbowgolem;

import net.luckystudios.gui.crossbowgolem.CrossbowGolemMenu;
import net.luckystudios.init.ModBlockEntityTypes;
import net.luckystudios.entity.custom.turrets.crossbowgolem.CrossbowGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.Direction;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class CrossbowGolemBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    public static final int INVENTORY_SIZE = 10;
    public static final int CROSSBOW_SLOT = 9;

    // Slots 0-8 hold ammo, slot 9 holds the crossbow.
    public final SimpleContainer inventory = new SimpleContainer(INVENTORY_SIZE) {
        @Override
        public void setChanged() {
            super.setChanged();
            CrossbowGolemBlockEntity.this.setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };
    public CrossbowGolemBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.CROSSBOW_GOLEM_BLOCK_ENTITY.get(), pos, blockState);
    }

    /**
     * Set by generated structures (the village crossbow towers), where block placement does not run
     * {@code onPlace}. The golem is spawned once the chunk is loaded, then this is cleared.
     */
    public boolean spawnGolemOnLoad;

    /**
     * Ticks on the server only to spawn the golem for a block that came from a generated structure. Doing it
     * from a ticker (rather than at chunk load) waits until the chunk is live, so the spawn always happens.
     */
    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state, CrossbowGolemBlockEntity be) {
        if (!be.spawnGolemOnLoad) return;
        be.spawnGolemOnLoad = false;
        be.setChanged();
        BlockPos abovePos = pos.above();
        if (level.getBlockState(abovePos).canBeReplaced()
                && level.getEntitiesOfClass(net.luckystudios.entity.custom.turrets.crossbowgolem.CrossbowGolemEntity.class,
                new net.minecraft.world.phys.AABB(abovePos)).isEmpty()) {
            var golem = new net.luckystudios.entity.custom.turrets.crossbowgolem.CrossbowGolemEntity(level, pos);
            golem.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            golem.setPersistenceRequired();
            level.addFreshEntity(golem);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.blockysiege.crossbow_golem");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new CrossbowGolemMenu(i, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        // Same layout NeoForge's ItemStackHandler used, so existing worlds keep their items.
        CompoundTag inventoryTag = new CompoundTag();
        ContainerHelper.saveAllItems(inventoryTag, inventory.getItems(), registries);
        inventoryTag.putInt("Size", INVENTORY_SIZE);
        tag.put("inventory", inventoryTag);
        if (this.spawnGolemOnLoad) {
            tag.putBoolean("SpawnGolem", true);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.spawnGolemOnLoad = tag.getBoolean("SpawnGolem");
        NonNullList<ItemStack> items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag.getCompound("inventory"), items, registries);
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            inventory.getItems().set(i, items.get(i));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        return this.saveWithFullMetadata(lookupProvider);
    }

    /** The crossbow_golem turret entity standing on this block, if any. */
    public Optional<CrossbowGolemEntity> findCrossbowGolem() {
        if (this.level == null) {
            return Optional.empty();
        }
        List<CrossbowGolemEntity> crossbow_golems = this.level.getEntitiesOfClass(CrossbowGolemEntity.class, new AABB(CrossbowGolemBlock.golemPos(this.getBlockState(), this.getBlockPos())).inflate(0.1));
        return crossbow_golems.stream().filter(CrossbowGolemEntity::isAlive).findFirst();
    }

    /** Whether the item is ammo the golem can use (arrows, fireworks, or whatever the installed crossbow fires). */
    public boolean isValidAmmo(ItemStack stack) {
        if (stack.is(ItemTags.ARROWS) || stack.getItem() instanceof ArrowItem || stack.getItem() instanceof FireworkRocketItem) {
            return true;
        }
        ItemStack crossbow = inventory.getItem(CROSSBOW_SLOT);
        return crossbow.getItem() instanceof ProjectileWeaponItem weapon
                && (weapon.getAllSupportedProjectiles().test(stack) || weapon.getSupportedHeldProjectiles().test(stack));
    }

    // Container view of the inventory for hoppers, droppers and pipes. Only the ammo slots are
    // reachable from outside, only ammo can go in, and nothing can be pulled out.
    private static final int[] AMMO_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};

    @Override
    public int[] getSlotsForFace(Direction side) {
        return AMMO_SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        return slot != CROSSBOW_SLOT && isValidAmmo(stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot != CROSSBOW_SLOT && isValidAmmo(stack);
    }

    @Override
    public int getContainerSize() {
        return inventory.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return inventory.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return inventory.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        inventory.clearContent();
    }
}
