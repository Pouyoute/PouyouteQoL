package com.pouyoute.qols.mixin.mixins;

import com.pouyoute.qols.Interface.QolsEnchantSync;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.block.EnchantingTableBlock.class)
public class EnchantTableBlockMixin {

    @Inject(method = "onUse", at = @At("RETURN"))
    private void injectOnUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir)
    {
        if(!world.isClient && player instanceof ServerPlayerEntity serverPlayer)
        {
            if(serverPlayer.currentScreenHandler instanceof EnchantmentScreenHandler handler)
            {
                ((QolsEnchantSync) handler).setQolsPlayer(serverPlayer);
            }
        }
    }

}
