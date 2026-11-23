package net.luckystudios.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.luckystudios.BlockySiege;
import net.luckystudios.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Trim masks that are built at runtime, so any trim pattern or material added by any mod works on the burst cannon.
 * <p>
 * A mask is looked up as {@code textures/trims/<weapon>/<weapon>_<pattern>_trim.png}. When no one has drawn it and the weapon
 * is the burst cannon, it is cut out of the pattern's armor texture ({@code textures/trims/models/armor/<pattern>.png}, or {@code trims/entity/humanoid} on newer versions) and
 * saved next to the other masks (never over an existing file) so it can be edited. The mask uses the vanilla trim palette key
 * colors; it is recolored per material from {@code textures/trims/color_palettes/<material>.png} into a dynamic texture.
 */
public final class WeaponTrimMasks {
    private static final String BURST_CANNON = "multi_cannon";
    private static final String SPEWER = "spewer_cannon";
    /** Weapons whose masks are cut out of the armor trim textures when no one has drawn them. */
    private static final java.util.List<String> GENERATED_WEAPONS = java.util.List.of(BURST_CANNON, SPEWER);
    private static final int MASK_SIZE = 128;
    /** Source crop on the armor texture: top left corner and size. */
    private static final int CROP_X = 18, CROP_Y = 21, CROP_W = 12, CROP_H = 9;

    private static final Map<String, ResourceLocation> TEXTURES = new HashMap<>();

    private WeaponTrimMasks() {
    }

    /** The recolored dynamic texture for this weapon and trim, or null when no mask exists for the pattern. */
    @Nullable
    public static ResourceLocation texture(String weapon, ArmorTrim trim) {
        String materialName = trim.material().value().assetName();
        String key = weapon + "/" + maskName(trim.pattern().value()) + "_" + materialName;
        if (TEXTURES.containsKey(key)) return TEXTURES.get(key);

        ResourceLocation result = null;
        NativeImage mask = loadMask(weapon, trim.pattern().value(), true);
        if (mask != null) {
            try {
                Map<Integer, Integer> palette = palette(materialName);
                if (palette != null) {
                    for (int y = 0; y < mask.getHeight(); y++) {
                        for (int x = 0; x < mask.getWidth(); x++) {
                            Integer recolored = palette.get(mask.getPixelRGBA(x, y));
                            if (recolored != null) mask.setPixelRGBA(x, y, recolored);
                        }
                    }
                    result = BlockySiege.id("dynamic_trim/" + weapon + "/" + key.substring(weapon.length() + 1));
                    Minecraft.getInstance().getTextureManager().register(result, new DynamicTexture(mask));
                    mask = null; // owned by the texture now
                }
            } finally {
                if (mask != null) mask.close();
            }
        }
        TEXTURES.put(key, result);
        return result;
    }

    /** Builds and saves the missing burst cannon masks for every trim pattern registered, including other mods' ones. */
    public static void generateAll(RegistryAccess registries) {
        for (Holder<TrimPattern> pattern : registries.registryOrThrow(Registries.TRIM_PATTERN).holders().toList()) {
            for (String weapon : GENERATED_WEAPONS) {
                try (NativeImage ignored = loadMask(weapon, pattern.value(), true)) {
                    // loading saves the mask when it had to be generated
                } catch (Exception e) {
                    BlockySiege.LOGGER.warn("Could not generate {} trim mask for {}", weapon, pattern.value().assetId(), e);
                }
            }
        }
    }

    private static String maskName(TrimPattern pattern) {
        ResourceLocation id = pattern.assetId();
        String path = id.getPath().replace('/', '_');
        return id.getNamespace().equals("minecraft") ? path : id.getNamespace() + "_" + path;
    }

    private static String fileName(String weapon, TrimPattern pattern) {
        return weapon + "_" + maskName(pattern) + "_trim.png";
    }

    @Nullable
    private static NativeImage loadMask(String weapon, TrimPattern pattern, boolean saveGenerated) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> authored = manager.getResource(BlockySiege.id("textures/trims/" + weapon + "/" + fileName(weapon, pattern)));
        if (authored.isPresent()) {
            return read(authored.get());
        }
        if (!GENERATED_WEAPONS.contains(weapon)) return null;

        NativeImage generated = generateMask(weapon, manager, pattern.assetId());
        if (generated != null && saveGenerated) {
            save(weapon, fileName(weapon, pattern), generated);
        }
        return generated;
    }

    @Nullable
    private static NativeImage read(Resource resource) {
        try (InputStream stream = resource.open()) {
            return NativeImage.read(stream);
        } catch (IOException e) {
            BlockySiege.LOGGER.warn("Could not read trim texture", e);
            return null;
        }
    }

    @Nullable
    private static NativeImage generateMask(String weapon, ResourceManager manager, ResourceLocation assetId) {
        // 1.21.1 keeps the armor trim textures in trims/models/armor; newer versions moved them to trims/entity/humanoid
        Optional<Resource> resource = manager.getResource(assetId.withPath("textures/trims/models/armor/" + assetId.getPath() + ".png"));
        if (resource.isEmpty()) {
            resource = manager.getResource(assetId.withPath("textures/trims/entity/humanoid/" + assetId.getPath() + ".png"));
        }
        if (resource.isEmpty()) {
            BlockySiege.LOGGER.warn("No armor trim texture found for pattern {}, so no {} mask can be generated", assetId, weapon);
            return null;
        }
        NativeImage armor = read(resource.get());
        if (armor == null) return null;

        NativeImage mask = new NativeImage(MASK_SIZE, MASK_SIZE, true);
        try {
            if (weapon.equals(SPEWER)) {
                // 8x8 from (20,22) onto (56,82)
                for (int j = 0; j < 8; j++) {
                    for (int i = 0; i < 8; i++) {
                        int sx = 20 + i, sy = 22 + j;
                        if (sx >= armor.getWidth() || sy >= armor.getHeight()) continue;
                        int pixel = armor.getPixelRGBA(sx, sy);
                        if ((pixel >>> 24) != 0) put(mask, 56 + i, 82 + j, pixel);
                    }
                }
            } else {
                for (int j = 0; j < CROP_H; j++) {
                    for (int i = 0; i < CROP_W; i++) {
                        int sx = CROP_X + i, sy = CROP_Y + j;
                        if (sx >= armor.getWidth() || sy >= armor.getHeight()) continue;
                        int pixel = armor.getPixelRGBA(sx, sy);
                        if ((pixel >>> 24) == 0) continue;
                        put(mask, 20 + i, 9 + j, pixel); // top
                        put(mask, 32 + i, 9 + j, pixel); // bottom
                        put(mask, 8 + j, 22 + (CROP_W - 1 - i), pixel); // side, rotated counterclockwise
                        put(mask, 33 + (CROP_H - 1 - j), 22 + i, pixel); // other side, rotated clockwise
                    }
                }
            }
        } finally {
            armor.close();
        }
        return mask;
    }

    private static void put(NativeImage image, int x, int y, int pixel) {
        if (x >= 0 && y >= 0 && x < image.getWidth() && y < image.getHeight()) image.setPixelRGBA(x, y, pixel);
    }

    /** Maps each palette key color to the same slot of the material's palette. */
    @Nullable
    private static Map<Integer, Integer> palette(String materialName) {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> key = manager.getResource(ResourceLocation.withDefaultNamespace("textures/trims/color_palettes/trim_palette.png"));
        if (key.isEmpty()) return null;
        Optional<Resource> material = Optional.empty();
        material = manager.getResource(ResourceLocation.withDefaultNamespace("textures/trims/color_palettes/" + materialName + ".png"));
        if (material.isEmpty()) {
            for (String namespace : manager.getNamespaces()) {
                material = manager.getResource(ResourceLocation.fromNamespaceAndPath(namespace, "textures/trims/color_palettes/" + materialName + ".png"));
                if (material.isPresent()) break;
            }
        }
        if (material.isEmpty()) return null;

        NativeImage keyImage = read(key.get());
        NativeImage materialImage = read(material.get());
        try {
            if (keyImage == null || materialImage == null) return null;
            Map<Integer, Integer> map = new HashMap<>();
            int count = Math.min(keyImage.getWidth(), materialImage.getWidth());
            for (int x = 0; x < count; x++) {
                map.put(keyImage.getPixelRGBA(x, 0), materialImage.getPixelRGBA(x, 0));
            }
            return map;
        } finally {
            if (keyImage != null) keyImage.close();
            if (materialImage != null) materialImage.close();
        }
    }

    private static void save(String weapon, String fileName, NativeImage image) {
        try {
            Path directory = outputDirectory(weapon);
            Path file = directory.resolve(fileName);
            if (Files.exists(file)) return; // never override a texture that is already there
            Files.createDirectories(directory);
            image.writeToFile(file);
            BlockySiege.LOGGER.info("Generated trim mask {}", file);
        } catch (IOException e) {
            BlockySiege.LOGGER.warn("Could not save trim mask {}", fileName, e);
        }
    }

    /** In a dev environment the masks go straight into the mod's resources, otherwise into the game folder. */
    private static Path outputDirectory(String weapon) {
        Path gameDirectory = Minecraft.getInstance().gameDirectory.toPath().toAbsolutePath().normalize();
        if (Services.PLATFORM.isDevelopmentEnvironment() && gameDirectory.getParent() != null && gameDirectory.getParent().getParent() != null) {
            Path project = gameDirectory.getParent().getParent();
            Path resources = project.resolve("common/src/main/resources");
            if (Files.isDirectory(resources)) {
                return resources.resolve("assets/" + BlockySiege.MOD_ID + "/textures/trims/" + weapon);
            }
        }
        return gameDirectory.resolve(BlockySiege.MOD_ID).resolve("trim_masks").resolve(weapon);
    }
}
