package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModRussianLanguageProvider extends LanguageProvider {

    public ModRussianLanguageProvider(PackOutput output) {
        super(output, BlockySiege.MOD_ID, "ru_ru");
    }

    @Override
    protected void addTranslations() {
        add("advancement.blockysiege.summon_crossbow_golem.title", "Железный страж");
        add("advancement.blockysiege.summon_crossbow_golem.description", "Постройте арбалетного голема");
        add("itemGroup.blockysiege", "Осадные орудия");
        add("item.blockysiege.crossbow_golem_spawn_egg", "Яйцо призыва арбалетного голема");
        add("item.blockysiege.bullet", "Снаряд");
        add("item.blockysiege.fort_banner_pattern", "Узор для флага");
        add("item.blockysiege.fort_banner_pattern.desc", "Форт");
        add("item.blockysiege.boom_banner_pattern", "Узор для флага");
        add("item.blockysiege.boom_banner_pattern.desc", "Взрыв");
        add("block.blockysiege.cannon", "Пушка");
        add("block.blockysiege.cannon.description", "Стреляет любым блоком");
        add("block.blockysiege.multi_cannon", "Скорострельная пушка");
        add("block.blockysiege.multi_cannon.description", "Стреляет любым предметом");
        add("block.blockysiege.spewer_cannon", "Извергатель");
        add("block.blockysiege.spewer_cannon.description", "Стреляет любой жидкостью.");
        add("block.blockysiege.volley_rack", "Залповая стойка");
        add("block.blockysiege.volley_rack.description", "Выпускает залп снарядов");
        add("block.blockysiege.cannonball", "Пушечное ядро");
        add("block.blockysiege.explosive_keg", "Взрывной бочонок");
        add("block.blockysiege.fire_bomb", "Зажигательная бомба");
        add("block.blockysiege.frost_bomb", "Ледяная бомба");
        add("block.blockysiege.wind_bomb", "Ветряная бомба");
        add("block.blockysiege.explosive_barrel", "Взрывная бочка");
        add("block.blockysiege.crossbow_golem_block", "Арбалетный голем");
        add("container.blockysiege.cannon", "Пушка");
        add("container.blockysiege.aim", "Нажмите, чтобы прицелиться");
        add("container.blockysiege.cannon.set_power", "Задать мощность");
        add("container.blockysiege.multi_cannon", "Скорострельная пушка");
        add("container.blockysiege.multi_cannon.set_count", "Задать длину очереди");
        add("container.blockysiege.spewer_cannon", "Извергатель");
        add("container.blockysiege.volley_rack", "Залповая стойка");
        add("container.blockysiege.crossbow_golem", "Арбалетный голем");
        add("container.blockysiege.empty", "Пусто");
        add("container.blockysiege.full", "Полно");
        add("container.blockysiege.info", "Информация");
        add("entity.blockysiege.cannonball", "Пушечное ядро");
        add("entity.blockysiege.explosive_keg", "Взрывной бочонок");
        add("entity.blockysiege.fire_bomb", "Зажигательная бомба");
        add("entity.blockysiege.frost_bomb", "Ледяная бомба");
        add("entity.blockysiege.wind_bomb", "Ветряная бомба");
        add("entity.blockysiege.wooden_shrapnel", "Шрапнель");
        add("entity.blockysiege.crossbow_golem", "Арбалетный голем");
        add("entity.minecraft.villager.blockysiege.siege_engineer", "Осадный инженер");
        add("sounds.blockysiege.cannon_fire", "ВЫСТРЕЛ ИЗ ПУШКИ!");
        add("block.blockysiege.shoot", "ОГОНЬ!");
        add("sounds.blockysiege.hit_metal", "Удар по металлу");
        add("sounds.blockysiege.bottle_pop_small", "Хлопок маленькой бутылки");
        add("sounds.blockysiege.bottle_pop_large", "Хлопок большой бутылки");
        add("sounds.blockysiege.impact_fiery", "Огненный удар!");
        add("sounds.blockysiege.impact_icy", "Ледяной удар!");
        add("tooltip.blockysiege.placeable", "Можно разместить");
        add("death.attack.cannon", "Игрока %1$s разнесло из пушки!");
        add("key.blockysiege.fire_cannon", "Выстрелить из пушки");
        add("gamerule.doSiegeWeaponCrews", "Расчёты осадных орудий");
        add("gamerule.doSiegeWeaponCrews.description", "Разбойники, поборники и пиглины обслуживают ближайшие пушки, скорострельные пушки и извергатели");
        add("gui.blockysiege.crossbow_golem.health", "Здоровье: %s");
    }
}
