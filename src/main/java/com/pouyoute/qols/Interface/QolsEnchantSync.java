package com.pouyoute.qols.Interface;

import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

public interface QolsEnchantSync {
    void setQolsServerEnchantments(int slot, List<EnchantmentLevelEntry> list);

    List<EnchantmentLevelEntry> getQolsServerEnchantments(int slot);

    void setQolsPlayer(ServerPlayerEntity player);

    ServerPlayerEntity getQolsPlayer();
}
