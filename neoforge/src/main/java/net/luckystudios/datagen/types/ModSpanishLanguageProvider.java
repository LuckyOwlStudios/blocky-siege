package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModSpanishLanguageProvider extends LanguageProvider {

    public ModSpanishLanguageProvider(PackOutput output, String locale) {
        super(output, BlockySiege.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
        add("advancement.blockysiege.summon_crossbow_golem.title", "Centinela de hierro");
        add("advancement.blockysiege.summon_crossbow_golem.description", "Construye un gólem ballestero");
        add("itemGroup.blockysiege", "Armas de asedio");
        add("item.blockysiege.crossbow_golem_spawn_egg", "Huevo generador de gólem ballestero");
        add("item.blockysiege.bullet", "Proyectil");
        add("item.blockysiege.fort_banner_pattern", "Diseño de estandarte");
        add("item.blockysiege.fort_banner_pattern.desc", "Fuerte");
        add("item.blockysiege.boom_banner_pattern", "Diseño de estandarte");
        add("item.blockysiege.boom_banner_pattern.desc", "Explosión");
        add("block.blockysiege.cannon", "Cañón");
        add("block.blockysiege.cannon.description", "Dispara cualquier bloque");
        add("block.blockysiege.multi_cannon", "Cañón de ráfaga");
        add("block.blockysiege.multi_cannon.description", "Dispara cualquier objeto");
        add("block.blockysiege.spewer_cannon", "Escupidor");
        add("block.blockysiege.spewer_cannon.description", "Dispara cualquier líquido.");
        add("block.blockysiege.volley_rack", "Rastrillo de salvas");
        add("block.blockysiege.volley_rack.description", "Dispara una salva de proyectiles");
        add("block.blockysiege.cannonball", "Bala de cañón");
        add("block.blockysiege.explosive_keg", "Barril explosivo");
        add("block.blockysiege.fire_bomb", "Bomba incendiaria");
        add("block.blockysiege.frost_bomb", "Bomba de escarcha");
        add("block.blockysiege.wind_bomb", "Bomba de viento");
        add("block.blockysiege.explosive_barrel", "Barril explosivo");
        add("block.blockysiege.crossbow_golem_block", "Gólem ballestero");
        add("container.blockysiege.cannon", "Cañón");
        add("container.blockysiege.aim", "Haz clic para apuntar");
        add("container.blockysiege.cannon.set_power", "Ajustar potencia");
        add("container.blockysiege.multi_cannon", "Cañón de ráfaga");
        add("container.blockysiege.multi_cannon.set_count", "Ajustar tamaño de ráfaga");
        add("container.blockysiege.spewer_cannon", "Escupidor");
        add("container.blockysiege.volley_rack", "Rastrillo de salvas");
        add("container.blockysiege.crossbow_golem", "Gólem ballestero");
        add("container.blockysiege.empty", "Vacío");
        add("container.blockysiege.full", "Lleno");
        add("container.blockysiege.info", "Información");
        add("entity.blockysiege.cannonball", "Bala de cañón");
        add("entity.blockysiege.explosive_keg", "Barril explosivo");
        add("entity.blockysiege.fire_bomb", "Bomba incendiaria");
        add("entity.blockysiege.frost_bomb", "Bomba de escarcha");
        add("entity.blockysiege.wind_bomb", "Bomba de viento");
        add("entity.blockysiege.wooden_shrapnel", "Metralla");
        add("entity.blockysiege.crossbow_golem", "Gólem ballestero");
        add("entity.minecraft.villager.blockysiege.siege_engineer", "Ingeniero de asedio");
        add("sounds.blockysiege.cannon_fire", "¡CAÑONAZO!");
        add("block.blockysiege.shoot", "¡FUEGO!");
        add("sounds.blockysiege.hit_metal", "Golpe de metal");
        add("sounds.blockysiege.bottle_pop_small", "Pequeña botella descorchada");
        add("sounds.blockysiege.bottle_pop_large", "Gran botella descorchada");
        add("sounds.blockysiege.impact_fiery", "¡Impacto ardiente!");
        add("sounds.blockysiege.impact_icy", "¡Impacto gélido!");
        add("tooltip.blockysiege.placeable", "Colocable");
        add("death.attack.cannonball", "¡%1$s fue volado por una bala de cañón!");
        add("death.attack.cannonball.player", "¡%1$s fue volado por una bala de cañón disparada por %2$s!");
        add("key.blockysiege.fire_cannon", "Disparar cañón");
        add("gamerule.doSiegeWeaponCrews", "Dotaciones de armas de asedio");
        add("gamerule.doSiegeWeaponCrews.description", "Saqueadores, vindicadores y piglins manejan los cañones, cañones de ráfaga y escupidores cercanos");
        add("gui.blockysiege.crossbow_golem.health", "Salud: %s");
        add("gui.blockysiege.crossbow_golem.no_team", "Sin equipo");
    }
}
