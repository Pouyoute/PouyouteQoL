package com.pouyoute.qols.mixin;

import com.pouyoute.qols.Pouyouteqols;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin {

    @Inject(method = "onContentChanged", at = @At("TAIL"))
    private void injectOnContentChanged(Inventory inventory, CallbackInfo ci) {
        EnchantmentScreenHandler handler = (EnchantmentScreenHandler) (Object) this;
        ScreenHandlerContext context = ((EnchantmentScreenHandlerAccessor) handler).getContext();

        ItemStack stack = inventory.getStack(0);
        if (stack.isEmpty() || stack.hasEnchantments()) {
            return;
        }

        context.run((world, pos) -> {
            int power = calculateEnchantmentPower(world, pos);

            handler.enchantmentPower[0] = Math.min(15, power / 2);
            handler.enchantmentPower[1] = Math.min(25, power);
            handler.enchantmentPower[2] = Math.min(40, power);

            Pouyouteqols.LOGGER.info(String.valueOf(power));
        });


    }

    @Unique
    private int getBookshelfPower(World world, BlockPos pos) {
        return world.getBlockState(pos).isOf(Blocks.BOOKSHELF) ? 1 : 0;
    }

    @Unique
    private int calculateEnchantmentPower(World world, BlockPos pos){
        int power = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if ((dx != 0 || dz != 0) && world.isAir(pos.add(dx, 0, dz)) && world.isAir(pos.add(dx, 1, dz))) {
                    power += getBookshelfPower(world, pos.add(dx * 2, 0, dz * 2));
                    power += getBookshelfPower(world, pos.add(dx * 2, 1, dz * 2));
                    if (dx != 0 && dz != 0) {
                        power += getBookshelfPower(world, pos.add(dx * 2, 0, dz));
                        power += getBookshelfPower(world, pos.add(dx * 2, 1, dz));
                        power += getBookshelfPower(world, pos.add(dx, 0, dz * 2));
                        power += getBookshelfPower(world, pos.add(dx, 1, dz * 2));
                    }
                }
            }
        }

        return power;
    }
}