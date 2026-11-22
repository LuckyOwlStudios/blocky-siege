package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.luckystudios.init.ModBlocks;
import net.luckystudios.init.ModEntityTypes;
import net.luckystudios.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/** Generates assets/blockysiege/lang/en_us.json. Other locales should be separate LanguageProvider classes. */
public class ModEnglishLanguageProvider extends LanguageProvider {
    public ModEnglishLanguageProvider(PackOutput output) {
        super(output, BlockySiege.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.blockysiege", "Siege Weapons");

        add(ModItems.CROSSBOW_GOLEM_SPAWN_EGG.get(), "Crossbow Golem Spawn Egg");
        add("item.blockysiege.bullet", "Bullet");
        add(ModItems.FORT_BANNER_PATTERN.get(), "Banner Pattern");
        add("item.blockysiege.fort_banner_pattern.desc", "Fort");
        add("item.blockysiege.boom_banner_pattern", "Banner Pattern");
        add("item.blockysiege.boom_banner_pattern.desc", "Boom");

        add(ModBlocks.CANNON.get(), "Cannon");
        add("block.blockysiege.cannon.description", "Shoots any Block");
        add(ModBlocks.MULTI_CANNON.get(), "Burst-Cannon");
        add("block.blockysiege.multi_cannon.description", "Shoots any Item");
        add(ModBlocks.SPEWER_CANNON.get(), "Spewer");
        add("block.blockysiege.spewer_cannon.description", "Shoots any liquid.");
        add(ModBlocks.VOLLEY_RACK.get(), "Volley Rack");
        add("block.blockysiege.volley_rack.description", "Shoots a volley of projectiles");
        add(ModBlocks.CANNONBALL.get(), "Cannonball");
        add("block.blockysiege.explosive_keg", "Explosive Keg");
        add(ModBlocks.FIRE_BOMB.get(), "Fire Bomb");
        add(ModBlocks.FROST_BOMB.get(), "Frost Bomb");
        add(ModBlocks.WIND_BOMB.get(), "Wind Bomb");
        add(ModBlocks.EXPLOSIVE_BARREL.get(), "Explosive Barrel");
        add(ModBlocks.CROSSBOW_GOLEM_BLOCK.get(), "Crossbow Golem");

        add("container.blockysiege.cannon", "Cannon");
        add("container.blockysiege.aim", "Click to Aim");
        add("container.blockysiege.cannon.set_power", "Set Power");
        add("container.blockysiege.multi_cannon", "Burst-Cannon");
        add("container.blockysiege.multi_cannon.set_count", "Set Burst Count");
        add("container.blockysiege.spewer_cannon", "Spewer");
        add("container.blockysiege.volley_rack", "Volley Rack");
        add("container.blockysiege.crossbow_golem", "Crossbow Golem");
        add("container.blockysiege.empty", "Empty");
        add("container.blockysiege.full", "Full");
        add("container.blockysiege.info", "Info");

        add(ModEntityTypes.CANNONBALL.get(), "Cannonball");
        add("entity.blockysiege.explosive_keg", "Explosive Keg");
        add(ModEntityTypes.FIRE_BOMB.get(), "Fire Bomb");
        add(ModEntityTypes.FROST_BOMB.get(), "Frost Bomb");
        add(ModEntityTypes.WIND_BOMB.get(), "Wind Bomb");
        add(ModEntityTypes.WOODEN_SHRAPNEL.get(), "Shrapnel");
        add(ModEntityTypes.CROSSBOW_GOLEM.get(), "Crossbow Golem");

        add("entity.minecraft.villager.blockysiege.siege_engineer", "Siege Engineer");

        add("sounds.blockysiege.cannon_fire", "CANNON FIRED!");
        add("block.blockysiege.shoot", "SHOOT!");
        add("sounds.blockysiege.hit_metal", "Metal Hit");
        add("sounds.blockysiege.bottle_pop_small", "Small Bottle Popped");
        add("sounds.blockysiege.bottle_pop_large", "Large Bottle Popped");
        add("sounds.blockysiege.impact_fiery", "Fiery Impact!");
        add("sounds.blockysiege.impact_icy", "Icy Impact!");

        add("tooltip.blockysiege.placeable", "Placeable");

        add("death.attack.cannonball", "%1$s was blown to bits by a cannonball");
        add("death.attack.cannonball.player", "%1$s was blown to bits by a cannonball fired by %2$s");
        add("key.blockysiege.fire_cannon", "Fire Cannon");

        add("gamerule.doSiegeWeaponCrews", "Siege weapon crews");
        add("gamerule.doSiegeWeaponCrews.description", "Pillagers, Vindicators and Piglins man nearby cannons, burst cannons and spewers");
        add("advancement.blockysiege.summon_crossbow_golem.title", "Iron Sentry");
        add("advancement.blockysiege.summon_crossbow_golem.description", "Build a Crossbow Golem");
        add("gui.blockysiege.crossbow_golem.health", "Health: %s");
        add("gui.blockysiege.crossbow_golem.no_team", "No team");
    }
}
