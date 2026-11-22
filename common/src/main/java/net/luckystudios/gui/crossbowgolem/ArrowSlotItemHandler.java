package net.luckystudios.gui.crossbowgolem;

import net.luckystudios.blocks.custom.turret.crossbowgolem.CrossbowGolemBlockEntity;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;

public class ArrowSlotItemHandler extends Slot {
    public ArrowSlotItemHandler(Container container, int index, int xPosition, int yPosition) {
        super(container, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.container instanceof CrossbowGolemBlockEntity be ? be.isValidAmmo(stack) : this.container.canPlaceItem(this.getContainerSlot(), stack);
    }
}
