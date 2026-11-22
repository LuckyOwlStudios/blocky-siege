package net.luckystudios.datagen.types;

import net.luckystudios.init.ModEntityTypes;
import net.luckystudios.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {

    /**
     * Sable's tag of entities that stay with a moving sub-level instead of being kicked out when it
     * assembles. Purely a data tag, so Sable is not needed at runtime or compile time.
     */
    private static final TagKey<EntityType<?>> SABLE_RETAIN_IN_SUB_LEVEL =
            TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("sable", "retain_in_sub_level"));

    public ModEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        super.addTags(provider);

        tag(ModTags.STRONG_PROJECTILE)
                .add(EntityType.TRIDENT)
                .add(ModEntityTypes.FIREWORK_STAR.get())
        ;

        // Mobs that man cannons, burst cannons and spewers on their own
        tag(ModTags.SIEGE_CREW)
                .add(EntityType.PILLAGER)
                .add(EntityType.VINDICATOR)
                .add(EntityType.PIGLIN)
        ;

        // Sable compat: the crossbow golem rides along on moving sub-levels
        tag(SABLE_RETAIN_IN_SUB_LEVEL)
                .add(ModEntityTypes.CROSSBOW_GOLEM.get())
                // The seat a player or mob crew sits in to man a weapon must stay in the plot with the weapon
                .add(ModEntityTypes.SEAT.get())
        ;
    }
}
