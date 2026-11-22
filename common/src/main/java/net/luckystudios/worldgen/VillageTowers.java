package net.luckystudios.worldgen;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import net.luckystudios.BlockySiege;
import net.luckystudios.BlockySiegeConfig;
import net.luckystudios.mixins.StructureTemplatePoolAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adds the crossbow golem towers to the vanilla village house pools.
 * <p>
 * Purely additive and low-impact: one entry with a low weight per biome pool (about one house in thirty is a
 * tower), nothing is replaced, a pool that does not exist (a modded or changed village) is skipped, and the
 * whole thing can be switched off with the {@code villageCrossbowTowers} config option.
 */
public final class VillageTowers {
    private static final Logger LOGGER = LogUtils.getLogger();
    // TEMPORARY test value (normally 1): makes towers very common. Revert before merging.
    private static final int WEIGHT = 50;

    /** Vanilla house pool to the tower structure added to it. */
    private static final Map<String, String> TOWERS = Map.of(
            "minecraft:village/plains/houses", "village/crossbow_tower_plains",
            "minecraft:village/taiga/houses", "village/crossbow_tower_taiga",
            "minecraft:village/snowy/houses", "village/crossbow_tower_snowy",
            "minecraft:village/desert/houses", "village/crossbow_tower_desert",
            "minecraft:village/savanna/houses", "village/crossbow_tower_savanna");

    /** Pools already given their tower, by identity; a new server start loads fresh pool objects. */
    private static final java.util.Set<StructureTemplatePool> INJECTED =
            java.util.Collections.newSetFromMap(new java.util.WeakHashMap<>());

    private VillageTowers() {
    }

    /** Call once per server start, after the data packs have loaded. */
    public static void inject(MinecraftServer server) {
        if (!BlockySiegeConfig.VILLAGE_CROSSBOW_TOWERS.get()) {
            return;
        }
        Registry<StructureTemplatePool> pools = server.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        TOWERS.forEach((poolId, structure) -> {
            StructureTemplatePool pool = pools.get(ResourceLocation.parse(poolId));
            if (pool == null) {
                return;
            }
            ResourceLocation tower = BlockySiege.id(structure);
            StructureTemplatePoolAccessor accessor = (StructureTemplatePoolAccessor) pool;
            if (INJECTED.contains(pool)) {
                return; // already added to this loaded pool
            }
            INJECTED.add(pool);
            StructurePoolElement element = StructurePoolElement.single(tower.toString())
                    .apply(StructureTemplatePool.Projection.RIGID);
            List<Pair<StructurePoolElement, Integer>> raw = new ArrayList<>(accessor.blockysiege$getRawTemplates());
            raw.add(Pair.of(element, WEIGHT));
            accessor.blockysiege$setRawTemplates(raw);
            for (int i = 0; i < WEIGHT; i++) {
                accessor.blockysiege$getTemplates().add(element);
            }
            LOGGER.debug("Added {} to {}", tower, poolId);
        });
    }
}
