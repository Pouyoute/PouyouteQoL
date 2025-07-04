package com.pouyoute.qols.mixin.mixins;

import net.minecraft.enchantment.DamageEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.enchantment.DamageEnchantment.class)
public class DamageEnchantmentMixin {

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void injectGetMaxLevel(CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(10);
    }

    @Inject(method = "getMinPower", at = @At("HEAD"), cancellable = true)
    private void injectGetMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        int[] BASE_POWERS = {3, 8, 8};
        int[] POWERS_PER_LEVEL = {10, 8, 8};
        int typeIndex = ((DamageEnchantment)(Object)this).typeIndex;

        int minPower = BASE_POWERS[typeIndex] + (level - 1) * POWERS_PER_LEVEL[typeIndex];
        cir.setReturnValue(minPower);
    }

    @Inject(method = "getMaxPower", at = @At("HEAD"), cancellable = true)
    private void injectGetMaxPower(int level, CallbackInfoReturnable<Integer> cir) {
        int[] BASE_POWERS = {3, 8, 8};
        int[] POWERS_PER_LEVEL = {10, 8, 8};
        int[] MIN_MAX_POWER_DIFFERENCES = {16, 16, 16};

        int typeIndex = ((DamageEnchantment)(Object)this).typeIndex;

        int maxPower = BASE_POWERS[typeIndex] + (level - 1) * POWERS_PER_LEVEL[typeIndex] + MIN_MAX_POWER_DIFFERENCES[typeIndex];
        cir.setReturnValue(maxPower);
    }
}
