package net.luckystudios.blocks.custom.shooting;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.luckystudios.BlockySiege;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

/**
 * Client side of siege weapon armor trims. Each weapon has one mask texture ({@code textures/trims/<weapon>.png})
 * drawn in the trim palette key colors. The armor_trims atlas (see {@code assets/minecraft/atlases/armor_trims.json})
 * recolors it once per trim material, so the sprite {@code blockysiege:trims/<weapon>_<material>} is the mask in that material's colors.
 */
public final class WeaponTrimRenderer {
    private WeaponTrimRenderer() {
    }

    /**
     * Returns a buffer that draws the weapon model's UVs through the trim sprite, or null when the weapon has no trim
     * (or its material is a modded one that has no recolored sprite).
     */
    @Nullable
    public static VertexConsumer buffer(AbstractShootingAimableBlockEntity weapon, String weaponName, MultiBufferSource buffers) {
        ArmorTrim trim = weapon.getTrim();
        if (trim == null) return null;
        ResourceLocation spriteId = BlockySiege.id("trims/" + weaponName + "_" + trim.material().value().assetName());
        TextureAtlasSprite sprite = Minecraft.getInstance().getModelManager().getAtlas(Sheets.ARMOR_TRIMS_SHEET).getSprite(spriteId);
        if (sprite.contents().name().equals(MissingTextureAtlasSprite.getLocation())) return null;
        return sprite.wrap(buffers.getBuffer(Sheets.armorTrimsSheet(trim.pattern().value().decal())));
    }
}
