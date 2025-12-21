package dev.arzcbnh.tekoha.mixin;

import dev.arzcbnh.tekoha.data.PlayerData;
import java.util.Map;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "handleEquipmentChanges", at = @At("HEAD"))
    private void tekoha$removeHiddenEquipment(Map<EquipmentSlot, ItemStack> map, CallbackInfo ci) {
        if ((LivingEntity) (Object) this instanceof ServerPlayer player) {
            final var data = PlayerData.of(player);
            EquipmentSlot.VALUES.stream()
                    .filter(data::isEquipmentHidden)
                    .forEach(slot -> map.put(slot, ItemStack.EMPTY));
        }
    }
}
