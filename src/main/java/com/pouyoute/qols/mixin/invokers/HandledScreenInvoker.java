package com.pouyoute.qols.mixin.invokers;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

@Mixin(HandledScreen.class)
public interface HandledScreenInvoker {
    @Invoker("isPointWithinBounds")
    boolean invokeIsPointWithinBounds(int x, int y, int width, int height, double pointX, double pointY);
}