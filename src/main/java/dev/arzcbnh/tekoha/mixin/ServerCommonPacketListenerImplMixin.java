package dev.arzcbnh.tekoha.mixin;

import dev.arzcbnh.tekoha.TekohaAdditions;
//import dev.arzcbnh.minecraft.auth.AuthHandler;
import dev.arzcbnh.tekoha.auth.AuthRequestCallback;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin {
    @Inject(method = "handleCustomClickAction", at = @At("HEAD"))
    private void handleCustomClickAction(ServerboundCustomClickActionPacket serverboundCustomClickActionPacket, CallbackInfo ci) {
        if ((Object) this instanceof ServerGamePacketListenerImpl impl) {
            final var id = serverboundCustomClickActionPacket.id();
            final var payload = serverboundCustomClickActionPacket.payload();
            final var player = impl.getPlayer();

            if (!id.getNamespace().equals(TekohaAdditions.MOD_ID) || payload.isEmpty()) {
                return;
            }

            switch (id.getPath()) {
                case "auth/login": {
                    AuthRequestCallback.LOGIN.invoker().offer(player, payload.flatMap(Tag::asCompound).flatMap(tag -> tag.getString("password")).orElse(""));
                    break;
                }
                case "auth/signup": {
                    AuthRequestCallback.SIGNUP.invoker().offer(player, payload.flatMap(Tag::asCompound).flatMap(tag -> tag.getString("password")).orElse(""));
                    break;
                }
            }
        }
    }
}
