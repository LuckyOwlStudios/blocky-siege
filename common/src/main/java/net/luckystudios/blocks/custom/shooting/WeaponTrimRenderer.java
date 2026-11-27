package net.luckystudios.blocks.custom.shooting;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luckystudios.BlockySiege;
import net.luckystudios.client.WeaponTrimMasks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

/**
 * Client side of siege weapon armor trims. Each weapon has one mask per trim pattern
 * ({@code textures/trims/<weapon>/<weapon>_<pattern>_trim.png}) drawn in the vanilla trim palette key colors. The armor_trims atlas
 * (see {@code assets/minecraft/atlases/armor_trims.json}) recolors each mask once per trim material, giving the sprite
 * {@code <weapon>_<pattern>_trim_<material>}. Masks that don't exist yet simply draw nothing.
 */
public final class WeaponTrimRenderer {
    private WeaponTrimRenderer() {
    }

    /**
     * Returns a buffer that draws the weapon model's UVs through the trim sprite, or null when the weapon has no trim
     * (or there is no mask for its pattern/material yet).
     */
    @Nullable
    public static VertexConsumer buffer(AbstractShootingAimableBlockEntity weapon, String weaponName, MultiBufferSource buffers) {
        ArmorTrim trim = weapon.getTrim();
        if (trim == null) return null;
        if (weaponName.equals("multi_cannon") || weaponName.equals("spewer_cannon")) {
            // The burst cannon and spewer masks are built at runtime so every mod's trims work (see WeaponTrimMasks)
            ResourceLocation texture = WeaponTrimMasks.texture(weaponName, trim);
            if (texture == null) return null;
            return buffers.getBuffer(RenderType.armorCutoutNoCull(texture));
        }
        ResourceLocation spriteId = BlockySiege.id("trims/" + weaponName + "/" + weaponName + "_" + trim.pattern().value().assetId().getPath() + "_trim_" + trim.material().value().assetName());
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET).getSprite(spriteId);
        if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) return null;
        return sprite.wrap(buffers.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
    }
}
