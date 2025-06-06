package com.pouyoute.qols.client;

import com.pouyoute.qols.Interface.QolsEnchantSync;
import com.pouyoute.qols.Pouyouteqols;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.EnchantmentScreenHandler;

import java.util.ArrayList;
import java.util.List;

public class PouyouteqolsClient implements ClientModInitializer{
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Pouyouteqols.UPDATE_ENCHANTMENTS_PACKET_ID, (client, handler, buf, responseSender) -> {
            handleUpdateEnchantmentsPacket(buf);
        });
    }

    public static void handleUpdateEnchantmentsPacket(PacketByteBuf buf) {
        int slot = buf.readInt();
        int size = buf.readInt();
        List<EnchantmentLevelEntry> enchantments = new ArrayList<>();
        // Côté client, dans handleUpdateEnchantmentsPacket
        System.out.println("-----------------------------------------------");
        for (int i = 0; i < size; i++) {
            Enchantment enchantment = Registries.ENCHANTMENT.get(buf.readVarInt());
            int level = buf.readVarInt();
            enchantments.add(new EnchantmentLevelEntry(enchantment, level));
        }
        // Met à jour l'affichage côté client
        MinecraftClient.getInstance().execute(() -> {
            EnchantmentScreenHandler handler = (EnchantmentScreenHandler) MinecraftClient.getInstance().player.currentScreenHandler;
            ((QolsEnchantSync) handler).setQolsServerEnchantments(slot, enchantments);
        });
    }

}