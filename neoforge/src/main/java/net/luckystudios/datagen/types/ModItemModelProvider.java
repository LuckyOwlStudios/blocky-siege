package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.luckystudios.init.ModBlocks;
import net.luckystudios.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BlockySiege.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(ModItems.WOODEN_SHRAPNEL.get());
        this.basicItem(ModItems.FORT_BANNER_PATTERN.get());
        this.withExistingParent(ModItems.CROSSBOW_GOLEM_SPAWN_EGG.getId().getPath(), mcLoc("item/template_spawn_egg"));

        // Blocks whose in-world model is hand-authored but whose item is a flat 2D icon
        this.basicItem(ModBlocks.CANNON.get().asItem());
        this.basicItem(ModBlocks.MORTAR.get().asItem());
        this.basicItem(ModBlocks.MULTI_CANNON.get().asItem());
        this.basicItem(ModBlocks.SPEWER_CANNON.get().asItem());
        this.basicItem(ModBlocks.VOLLEY_RACK.get().asItem());
        this.basicItem(ModBlocks.CROSSBOW_GOLEM_BLOCK.get().asItem());
    }
}
