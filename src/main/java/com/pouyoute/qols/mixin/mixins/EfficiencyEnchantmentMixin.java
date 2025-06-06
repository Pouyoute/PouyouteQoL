package com.pouyoute.qols.mixin.mixins;

import com.pouyoute.qols.Pouyouteqols;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.enchantment.EfficiencyEnchantment.class)
public abstract class EfficiencyEnchantmentMixin {

    @Shadow public abstract int getMinPower(int level);

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void injectGetMaxLevel(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(10);
    }

    @Inject(method = "getMinPower", at = @At("HEAD"), cancellable = true)
    private void injectGetMinPower(int level, CallbackInfoReturnable<Integer> cir) {
        int minPower = 1 + 13 * (level - 1);
        cir.setReturnValue(minPower);
    }

    @Inject(method = "getMaxPower", at = @At("HEAD"), cancellable = true)
    private void injectGetMaxPower(int level, CallbackInfoReturnable<Integer> cir) {
        int maxPower = getMinPower(level) + 52;
        cir.setReturnValue(maxPower);
    }

}
