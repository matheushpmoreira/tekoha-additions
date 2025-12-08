package dev.arzcbnh.minecraft.mixin;

import dev.arzcbnh.minecraft.TekohaAdditions;
//import dev.arzcbnh.minecraft.auth.AuthHandler;
import dev.arzcbnh.minecraft.auth.AuthService;
import dev.arzcbnh.minecraft.auth.LoginRequestCallback;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

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
                case "login_request":
                    LoginRequestCallback.EVENT.invoker().offer(player, payload.get().asCompound().get().getStringOr("password", ""));
            }
        }
    }
}
