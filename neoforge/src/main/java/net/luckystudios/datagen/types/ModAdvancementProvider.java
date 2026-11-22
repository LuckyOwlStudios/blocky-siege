package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.luckystudios.init.ModEntityTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SummonedEntityTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementProvider extends AdvancementProvider {
    public ModAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper) {
        super(output, registries, helper, List.of(new Generator()));
    }

    private static class Generator implements AdvancementGenerator {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> saver, ExistingFileHelper helper) {
            Advancement.Builder.advancement()
                    .parent(ResourceLocation.withDefaultNamespace("adventure/summon_iron_golem"))
                    .display(Items.CROSSBOW,
                            Component.translatable("advancement.blockysiege.summon_crossbow_golem.title"),
                            Component.translatable("advancement.blockysiege.summon_crossbow_golem.description"),
                            null, AdvancementType.TASK, true, true, false)
                    .addCriterion("summoned_golem", SummonedEntityTrigger.TriggerInstance.summonedEntity(
                            EntityPredicate.Builder.entity().of(ModEntityTypes.CROSSBOW_GOLEM.get())))
                    .save(saver, BlockySiege.id("adventure/summon_crossbow_golem"), helper);
        }
    }
}
