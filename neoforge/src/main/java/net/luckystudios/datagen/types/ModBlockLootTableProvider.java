package net.luckystudios.datagen.types;

import net.luckystudios.init.ModBlocks;
import net.luckystudios.blocks.custom.cannon_ammo.AbstractCannonBallBlock;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.luckystudios.BlockySiege;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.IntStream;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        // Needs to add ModBlocks.CANNON_BARREL.get() to the loot table
        this.add(ModBlocks.CANNON.get(), createTrimmedWeaponDrop(ModBlocks.CANNON.get()));
        this.add(ModBlocks.MULTI_CANNON.get(), createTrimmedWeaponDrop(ModBlocks.MULTI_CANNON.get()));
        this.add(ModBlocks.SPEWER_CANNON.get(), createTrimmedWeaponDrop(ModBlocks.SPEWER_CANNON.get()));
        this.add(ModBlocks.VOLLEY_RACK.get(), createTrimmedWeaponDrop(ModBlocks.VOLLEY_RACK.get()));
        this.add(ModBlocks.EMBER_PILE.get(), noDrop());
        this.add(ModBlocks.FROST_PILE.get(), noDrop());
        // Spectral ammo is a conjured projectile block: it never drops itself (matches the previous missing table).
        this.add(ModBlocks.SPECTRAL_CANNONBALL.get(), noDrop());
        this.add(ModBlocks.CANNONBALL.get(), createCannonAmmoStack(ModBlocks.CANNONBALL.get()));
        this.add(ModBlocks.FIRE_BOMB.get(), createCannonAmmoStack(ModBlocks.FIRE_BOMB.get()));
        this.add(ModBlocks.FROST_BOMB.get(), createCannonAmmoStack(ModBlocks.FROST_BOMB.get()));
        this.add(ModBlocks.WIND_BOMB.get(), createCannonAmmoStack(ModBlocks.WIND_BOMB.get()));
        dropSelf(ModBlocks.EXPLOSIVE_BARREL.get());

        dropSelf(ModBlocks.CROSSBOW_GOLEM_BLOCK.get());
    }

    /** Drops the weapon itself and carries its armor trim over to the item. */
    protected LootTable.Builder createTrimmedWeaponDrop(Block weapon) {
        return LootTable.lootTable().withPool(this.applyExplosionCondition(weapon, LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(weapon)
                        .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                .include(DataComponents.TRIM)))));
    }

    protected LootTable.Builder createCannonAmmoStack(Block ammoStack) {
        return LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(
                                        this.applyExplosionDecay(
                                                ammoStack,
                                                LootItem.lootTableItem(ammoStack)
                                                        .apply(
                                                                IntStream.rangeClosed(1, 4).boxed().toList(),
                                                                intValue -> SetItemCountFunction.setCount(ConstantValue.exactly((float)intValue.intValue()))
                                                                        .when(
                                                                                LootItemBlockStatePropertyCondition.hasBlockStateProperties(ammoStack)
                                                                                        .setProperties(
                                                                                                StatePropertiesPredicate.Builder.properties().hasProperty(AbstractCannonBallBlock.STACK, intValue.intValue())
                                                                                        )
                                                                        )
                                                        )
                                        )
                                )
                );
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream()
                .filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(BlockySiege.MOD_ID))::iterator;
    }
}
