package net.luckystudios.blocks.custom.shooting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Applying and removing vanilla armor trims on siege weapons. Hold a smithing template in one hand and a trim
 * material (ingot, gem, ...) in the other, then right-click the weapon. Right-clicking a trimmed weapon with shears strips the trim.
 */
public final class WeaponTrims {
    private WeaponTrims() {
    }

    /** @return the interaction result when the click was a trim action, or null when it was an ordinary click. */
    @Nullable
    public static ItemInteractionResult tryUse(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof AbstractShootingAimableBlockEntity weapon)) return null;

        if (stack.is(Items.SHEARS) && weapon.getTrim() != null) {
            if (level.isClientSide) return ItemInteractionResult.SUCCESS;
            weapon.setTrim(null);
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.BLOCKS, 1.0F, 1.0F);
            return ItemInteractionResult.SUCCESS;
        }

        ItemStack other = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        if (stack.isEmpty() || other.isEmpty()) return null;

        RegistryAccess access = level.registryAccess();
        ItemStack templateStack = stack;
        ItemStack materialStack = other;
        Optional<Holder.Reference<TrimPattern>> pattern = TrimPatterns.getFromTemplate(access, templateStack);
        if (pattern.isEmpty()) {
            templateStack = other;
            materialStack = stack;
            pattern = TrimPatterns.getFromTemplate(access, templateStack);
        }
        if (pattern.isEmpty()) return null;
        Optional<Holder.Reference<TrimMaterial>> material = TrimMaterials.getFromIngredient(access, materialStack);
        if (material.isEmpty()) return null;

        ArmorTrim trim = new ArmorTrim(material.get(), pattern.get());
        ArmorTrim current = weapon.getTrim();
        if (current != null && current.hasPatternAndMaterial(trim.pattern(), trim.material())) {
            return ItemInteractionResult.CONSUME; // already wearing this exact trim, so keep the items
        }
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        weapon.setTrim(trim);
        if (!player.getAbilities().instabuild) {
            templateStack.shrink(1);
            materialStack.shrink(1);
        }
        level.playSound(null, pos, SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        return ItemInteractionResult.SUCCESS;
    }
}
