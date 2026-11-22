package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.luckystudios.init.ModSoundEvents;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import java.util.function.Supplier;

/** Generates assets/blockysiege/sounds.json. */
public class ModSoundDefinitionsProvider extends SoundDefinitionsProvider {
    public ModSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, BlockySiege.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(ModSoundEvents.CANNON_FIRE, "block.blockysiege.cannon", "cannon_fire_01");
        add(ModSoundEvents.CANNON_FIRE_DISTANT, "block.blockysiege.cannon", "cannon_fire_distant");
        add(ModSoundEvents.MORTAR_WHISTLE, "sounds.blockysiege.mortar_whistle", "shell_whistle");
        add(ModSoundEvents.SHOOT, "block.blockysiege.shoot", "gun_shoot_01", "gun_shoot_02", "gun_shoot_03");
        add(ModSoundEvents.METAL_HIT, "sounds.blockysiege.hit_metal",
                "hit_metal_01", "hit_metal_02", "hit_metal_03", "hit_metal_04", "hit_metal_05", "hit_metal_06");
        add(ModSoundEvents.BOTTLE_POP_SMALL, "sounds.blockysiege.bottle_pop_small", "bottle_pop_small");
        add(ModSoundEvents.BOTTLE_POP_LARGE, "sounds.blockysiege.bottle_pop_large", "bottle_pop_large");
        add(ModSoundEvents.IMPACT_FIERY, "sounds.blockysiege.impact_fiery", "impact_fiery");
        add(ModSoundEvents.IMPACT_ICY, "sounds.blockysiege.impact_icy", "impact_icy");
        add(ModSoundEvents.FIERY_LOOP, "sounds.blockysiege.fiery_loop", "fiery_loop");
    }

    private void add(Supplier<SoundEvent> event, String subtitle, String... files) {
        SoundDefinition definition = SoundDefinition.definition().subtitle(subtitle);
        for (String file : files) {
            definition.with(sound(BlockySiege.id(file)));
        }
        add(event, definition);
    }
}
