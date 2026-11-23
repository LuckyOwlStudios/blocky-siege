package net.luckystudios.mixins;

import net.luckystudios.BlockySiege;
import net.luckystudios.client.WeaponTrimMasks;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
    /** Once the world's registries are known, make sure every trim pattern has its burst cannon mask saved. */
    @Inject(method = "handleLogin", at = @At("TAIL"), require = 0)
    private void blockysiege$generateTrimMasks(ClientboundLoginPacket packet, CallbackInfo ci) {
        try {
            WeaponTrimMasks.generateAll(((ClientPacketListener) (Object) this).registryAccess());
        } catch (Exception e) {
            BlockySiege.LOGGER.warn("Could not generate trim masks", e);
        }
    }
}
