package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.luckystudios.init.ModParticleTypes;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.ParticleDescriptionProvider;

/** Generates assets/blockysiege/particles/*.json. */
public class ModParticleDescriptionProvider extends ParticleDescriptionProvider {
    public ModParticleDescriptionProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, helper);
    }

    @Override
    protected void addDescriptions() {
        sprite(ModParticleTypes.BOTTLE_CAP.get(), BlockySiege.id("bottle_cap"));
        spriteSet(ModParticleTypes.FLAME_TRAIL.get(), BlockySiege.id("flame_trail"), 7, false);
        // Vanilla generic_7 .. generic_0, played from the largest frame down.
        spriteSet(ModParticleTypes.WATER_TRAIL.get(), ResourceLocation.withDefaultNamespace("generic"), 8, true);
    }
}
