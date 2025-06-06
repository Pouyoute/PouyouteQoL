package com.pouyoute.qols.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.enchantment.UnbreakingEnchantment.class)
public abstract class UnbreakingEnchantmentMixin {

    @Shadow public abstract int getMinPower(int level);

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void injectGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(10);
    }

    @Inject(method = "getMinPower", at = @At("HEAD"), cancellable = true)
    private void injectGetMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        int minPower = 10 + (level - 1) * 10;
        cir.setReturnValue(minPower);
    }

    @Inject(method = "getMaxPower", at = @At("HEAD"), cancellable = true)
    private void injectGetMaxPower(int level, CallbackInfoReturnable<Integer> cir) {
        int maxPower = getMinPower(level) + 25;
        cir.setReturnValue(maxPower);
    }
}
