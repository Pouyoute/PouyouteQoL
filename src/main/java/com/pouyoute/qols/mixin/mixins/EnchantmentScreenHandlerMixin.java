package com.pouyoute.qols.mixin.mixins;

import com.pouyoute.qols.Interface.QolsEnchantSync;
import com.pouyoute.qols.Pouyouteqols;
import com.pouyoute.qols.mixin.accessors.EnchantmentScreenHandlerAccessor;
import io.netty.buffer.Unpooled;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(EnchantmentScreenHandler.class)
public class EnchantmentScreenHandlerMixin implements QolsEnchantSync{

    @Shadow @Final public int[] enchantmentPower;
    @Unique
    private List<EnchantmentLevelEntry> enchantments = null;
    @Unique
    private static boolean qols_disableMixin = false;
    @Unique
    private final List<EnchantmentLevelEntry>[] allEnchantments = new List[3];
    @Unique
    ServerPlayerEntity qolsPlayer;

    @Inject(method = "generateEnchantments", at = @At("HEAD"))
    private void injectBeforeGenerateEnchantments(ItemStack stack, int slot, int level, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        if (qols_disableMixin) return;
        EnchantmentScreenHandler handler = (EnchantmentScreenHandler) (Object) this;
        ScreenHandlerContext context = ((EnchantmentScreenHandlerAccessor) handler).getContext();


        context.run((world, pos) -> {
            int bookshelfCount = Math.min(calculateEnchantmentPower(world, pos), 15);
            long seed = handler.getSeed() + slot;
            Random random = new Random(seed);

            this.enchantmentPower[0] = 1 + (19 * bookshelfCount / 15) - random.nextInt(2);
            this.enchantmentPower[1] =  4 + (36 * bookshelfCount / 15) - random.nextInt(2);
            this.enchantmentPower[2] =  7 + (53 * bookshelfCount / 15) - random.nextInt(3);

            ItemStack itemStack = handler.getSlot(0).getStack();
            qols_disableMixin = true;
            try {
                List<EnchantmentLevelEntry>[] allEnchantments = new List[3];
                for (int i = 0; i < 3; i++) {
                    allEnchantments[i] = ((EnchantmentScreenHandlerAccessor) handler).invokeGenerateEnchantments(itemStack, i, enchantmentPower[i]);
                }

                List<EnchantmentLevelEntry> selectedEnchantments = allEnchantments[slot];
                this.enchantments = selectedEnchantments;
                ((QolsEnchantSync) handler).setQolsServerEnchantments(slot, selectedEnchantments);

                // Envoi ciblé au joueur concerné si possible
                PlayerEntity player = ((QolsEnchantSync) handler).getQolsPlayer();
                Pouyouteqols.LOGGER.info(qolsPlayer.getName().getString());

                if (player instanceof ServerPlayerEntity serverPlayer) {
                    sendPacket(serverPlayer, slot, selectedEnchantments);
                }
            } finally {
                qols_disableMixin = false;
            }
        });
    }

    @Unique
    private final List<EnchantmentLevelEntry>[] qolsEnchantments = new List[3];
    @Unique
    private ItemStack lastItemStack = ItemStack.EMPTY;
    @Inject(method = "onContentChanged", at = @At("HEAD"))
    private void qols_onContentChanged(Inventory inventory, CallbackInfo ci) {
        EnchantmentScreenHandler handler = (EnchantmentScreenHandler) (Object) this;
        ItemStack current = handler.getSlot(0).getStack();

        if (ItemStack.areEqual(current, lastItemStack)) return;
        lastItemStack = current.copy();

        // Génère et stocke les enchantements pour chaque slot
        for (int slot = 0; slot < 3; slot++) {
            qolsEnchantments[slot] = generateEnchantments(handler, current, slot);
        }
    }

    @Inject(method = "onButtonClick", at = @At("HEAD"))
    private void injectOnButtonClick(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
        List<EnchantmentLevelEntry> list = getQolsServerEnchantments(id);

        if (list == null) {
            return;
        }
        EnchantmentScreenHandler handler = (EnchantmentScreenHandler) (Object) this;
        ItemStack itemStack = handler.getSlot(0).getStack();
        ItemStack lapisStack = handler.getSlot(1).getStack();
        int cost = handler.enchantmentPower[id];

        if (lapisStack.getCount() < 1 || player.experienceLevel < cost) return;

        // Applique chaque enchantement de la liste à l'item
        for (EnchantmentLevelEntry entry : list) {
            itemStack.addEnchantment(entry.enchantment, entry.level);
        }

        lapisStack.decrement(1);
        player.addExperienceLevels(-cost);
        handler.getSlot(0).setStack(itemStack);


        cir.setReturnValue(true);
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

    @Unique
    private List<EnchantmentLevelEntry> generateEnchantments(EnchantmentScreenHandler handler, ItemStack stack, int slot) {
        // Utilise la logique vanilla ou personnalisée ici
        int power = handler.enchantmentPower[slot];
        return ((EnchantmentScreenHandlerAccessor) handler).invokeGenerateEnchantments(stack, slot, power);
    }

    @Unique
    private void sendPacket(ServerPlayerEntity player, int slot, List<EnchantmentLevelEntry> enchantments) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeInt(slot);
        buf.writeInt(enchantments.size());
        for (EnchantmentLevelEntry entry : enchantments) {
            buf.writeVarInt(Registries.ENCHANTMENT.getRawId(entry.enchantment));
            buf.writeVarInt(entry.level);
        }
        player.networkHandler.sendPacket(
                new net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket(
                        Pouyouteqols.UPDATE_ENCHANTMENTS_PACKET_ID, buf
                )
        );
    }

    @Unique
    private List<EnchantmentLevelEntry>[] qolsServerEnchantments = new List[3];

    @Override
    public void setQolsServerEnchantments(int slot, List<EnchantmentLevelEntry> list) {
        this.qolsServerEnchantments[slot] = list;
    }

    @Override
    public List<EnchantmentLevelEntry> getQolsServerEnchantments(int slot) {
        return this.qolsServerEnchantments[slot];
    }

    @Override
    public void setQolsPlayer(ServerPlayerEntity player) {
        this.qolsPlayer = player;
    }

    @Override
    public ServerPlayerEntity getQolsPlayer() {
        return this.qolsPlayer;
    }
}







