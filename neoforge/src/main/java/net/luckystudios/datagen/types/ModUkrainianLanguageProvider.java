package net.luckystudios.datagen.types;

import net.luckystudios.BlockySiege;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModUkrainianLanguageProvider extends LanguageProvider {

    public ModUkrainianLanguageProvider(PackOutput output) {
        super(output, BlockySiege.MOD_ID, "uk_ua");
    }

    @Override
    protected void addTranslations() {
        add("advancement.blockysiege.summon_crossbow_golem.title", "Залізний вартовий");
        add("advancement.blockysiege.summon_crossbow_golem.description", "Побудуйте арбалетного голема");
        add("itemGroup.blockysiege", "Облогові знаряддя");
        add("item.blockysiege.crossbow_golem_spawn_egg", "Яйце виклику арбалетного голема");
        add("item.blockysiege.bullet", "Снаряд");
        add("item.blockysiege.fort_banner_pattern", "Візерунок для прапора");
        add("item.blockysiege.fort_banner_pattern.desc", "Форт");
        add("item.blockysiege.boom_banner_pattern", "Візерунок для прапора");
        add("item.blockysiege.boom_banner_pattern.desc", "Вибух");
        add("block.blockysiege.cannon", "Гармата");
        add("block.blockysiege.cannon.description", "Стріляє будь-яким блоком");
        add("block.blockysiege.multi_cannon", "Скорострільна гармата");
        add("block.blockysiege.multi_cannon.description", "Стріляє будь-яким предметом");
        add("block.blockysiege.spewer_cannon", "Викидач");
        add("block.blockysiege.spewer_cannon.description", "Стріляє будь-якою рідиною.");
        add("block.blockysiege.volley_rack", "Залпова стійка");
        add("block.blockysiege.volley_rack.description", "Випускає залп снарядів");
        add("block.blockysiege.cannonball", "Гарматне ядро");
        add("block.blockysiege.explosive_keg", "Вибуховий бочонок");
        add("block.blockysiege.fire_bomb", "Запальна бомба");
        add("block.blockysiege.frost_bomb", "Крижана бомба");
        add("block.blockysiege.soul_bomb", "Бомба душ");
        add("block.blockysiege.wind_bomb", "Вітряна бомба");
        add("block.blockysiege.explosive_barrel", "Вибухова бочка");
        add("block.blockysiege.crossbow_golem_block", "Арбалетний голем");
        add("container.blockysiege.cannon", "Гармата");
        add("container.blockysiege.aim", "Натисніть, щоб прицілитися");
        add("container.blockysiege.cannon.set_power", "Задати потужність");
        add("container.blockysiege.multi_cannon", "Скорострільна гармата");
        add("container.blockysiege.multi_cannon.set_count", "Задати довжину черги");
        add("container.blockysiege.spewer_cannon", "Викидач");
        add("container.blockysiege.volley_rack", "Залпова стійка");
        add("container.blockysiege.crossbow_golem", "Арбалетний голем");
        add("container.blockysiege.empty", "Порожньо");
        add("container.blockysiege.full", "Повно");
        add("container.blockysiege.info", "Інформація");
        add("entity.blockysiege.cannonball", "Гарматне ядро");
        add("entity.blockysiege.explosive_keg", "Вибуховий бочонок");
        add("entity.blockysiege.fire_bomb", "Запальна бомба");
        add("entity.blockysiege.frost_bomb", "Крижана бомба");
        add("entity.blockysiege.soul_bomb", "Бомба душ");
        add("entity.blockysiege.wind_bomb", "Вітряна бомба");
        add("entity.blockysiege.wooden_shrapnel", "Шрапнель");
        add("entity.blockysiege.crossbow_golem", "Арбалетний голем");
        add("entity.minecraft.villager.blockysiege.siege_engineer", "Облоговий інженер");
        add("sounds.blockysiege.cannon_fire", "ПОСТРІЛ ІЗ ГАРМАТИ!");
        add("block.blockysiege.shoot", "ВОГОНЬ!");
        add("sounds.blockysiege.hit_metal", "Удар по металу");
        add("sounds.blockysiege.bottle_pop_small", "Хлопок маленької пляшки");
        add("sounds.blockysiege.bottle_pop_large", "Хлопок великої пляшки");
        add("sounds.blockysiege.impact_fiery", "Вогняний удар!");
        add("sounds.blockysiege.impact_icy", "Крижаний удар!");
        add("tooltip.blockysiege.placeable", "Можна розмістити");
        add("death.attack.cannonball.1", "Гравця %1$s рознесло на шматки ядром");
        add("death.attack.cannonball.1.player", "Гравця %1$s рознесло на шматки ядром, випущеним %2$s");
        add("death.attack.cannonball.2", "Гравця %1$s розплющило ядром");
        add("death.attack.cannonball.2.player", "Гравця %1$s розплющило ядром, випущеним %2$s");
        add("death.attack.cannonball.3", "Гравець %1$s отримав ядро в груди");
        add("death.attack.cannonball.3.player", "Гравець %1$s отримав ядро в груди від %2$s");
        add("death.attack.cannonball.4", "Гравця %1$s відкинуло ядром");
        add("death.attack.cannonball.4.player", "Гравця %1$s відкинуло ядром, випущеним %2$s");
        add("death.attack.cannonball.5", "Гравець %1$s зустрів ядро лоб у лоб");
        add("death.attack.cannonball.5.player", "Гравець %1$s зустрів лоб у лоб ядро, випущене %2$s");
        add("death.attack.cannonball.6", "Гравець %1$s не встояв проти облогової зброї");
        add("death.attack.cannonball.6.player", "Гравець %1$s не встояв проти облогової зброї %2$s");
        add("death.attack.cannonball.7", "Гравця %1$s зруйнувало ядром");
        add("death.attack.cannonball.7.player", "Гравця %1$s зруйнувало ядром, випущеним %2$s");
        add("death.attack.cannonball.8", "Гравець %1$s не помітив ядро, що летіло");
        add("death.attack.cannonball.8.player", "Гравець %1$s не помітив ядро, що летіло від %2$s");
        add("key.blockysiege.fire_cannon", "Вистрілити з гармати");
        add("gamerule.doSiegeWeaponCrews", "Розрахунки облогових знарядь");
        add("gamerule.doSiegeWeaponCrews.description", "Розбійники, поборники та піглини обслуговують найближчі гармати, скорострільні гармати та викидачі");
        add("gui.blockysiege.crossbow_golem.health", "Здоров’я: %s");
        add("gui.blockysiege.crossbow_golem.no_team", "Немає команди");
    }
}
