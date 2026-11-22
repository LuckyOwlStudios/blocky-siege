package net.luckystudios.datagen.types;

import com.google.gson.JsonObject;
import net.luckystudios.BlockySiege;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.concurrent.CompletableFuture;

/** Generates assets/blockysiege/dynamiclights/entity/*.json (read by dynamic-light mods). */
public class ModDynamicLightProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;

    public ModDynamicLightProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "dynamiclights/entity");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.allOf(
                entity(cache, "ember", 12),
                entity(cache, "fire_bomb", 15)
        );
    }

    private CompletableFuture<?> entity(CachedOutput cache, String name, int luminance) {
        JsonObject match = new JsonObject();
        match.addProperty("type", BlockySiege.id(name).toString());
        JsonObject json = new JsonObject();
        json.add("match", match);
        json.addProperty("luminance", luminance);
        ResourceLocation id = BlockySiege.id(name);
        return DataProvider.saveStable(cache, json, pathProvider.json(id));
    }

    @Override
    public String getName() {
        return "Dynamic lights: " + BlockySiege.MOD_ID;
    }
}
