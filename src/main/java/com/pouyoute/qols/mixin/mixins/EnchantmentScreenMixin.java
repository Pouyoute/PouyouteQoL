package com.pouyoute.qols.mixin.mixins;

import com.pouyoute.qols.Interface.QolsEnchantSync;
import com.pouyoute.qols.Pouyouteqols;
import com.pouyoute.qols.mixin.invokers.HandledScreenInvoker;
import com.pouyoute.qols.mixin.accessors.ScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.EnchantmentScreen;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(net.minecraft.client.gui.screen.ingame.EnchantmentScreen.class)
public class EnchantmentScreenMixin{


    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V"), cancellable = true)
    private void injectRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        EnchantmentScreen self = (EnchantmentScreen)(Object)this;
        EnchantmentScreenHandler handler = self.getScreenHandler();
        if (handler instanceof QolsEnchantSync) {
            QolsEnchantSync sync = (QolsEnchantSync) handler;


            MinecraftClient client = ((ScreenAccessor)self).getClient();

            boolean creative = client.player.getAbilities().creativeMode;
            int lapis = handler.getLapisCount();

            for (int j = 0; j < 3; j++) {
                List<EnchantmentLevelEntry> serverList = sync.getQolsServerEnchantments(j);
                if (serverList == null) return;
                EnchantmentLevelEntry entry = serverList.get(0);
                int power = handler.enchantmentPower[j];
                int m = j + 1;
                if (((HandledScreenInvoker)self).invokeIsPointWithinBounds(60, 14 + 19 * j, 108, 17, mouseX, mouseY) && power > 0 && entry != null) {
                    List<Text> list = new ArrayList<>();
                    list.add(Text.translatable("container.enchant.clue", entry.enchantment.getName(entry.level)).formatted(Formatting.WHITE));
                    if (!creative) {
                        list.add(Text.empty());
                        if (client.player.experienceLevel < power) {
                            list.add(Text.translatable("container.enchant.level.requirement", power).formatted(Formatting.RED));
                        } else {
                            MutableText lapisText = m == 1
                                    ? Text.translatable("container.enchant.lapis.one")
                                    : Text.translatable("container.enchant.lapis.many", m);
                            list.add(lapisText.formatted(lapis >= m ? Formatting.GRAY : Formatting.RED));
                            MutableText levelText = m == 1
                                    ? Text.translatable("container.enchant.level.one")
                                    : Text.translatable("container.enchant.level.many", m);
                            list.add(levelText.formatted(Formatting.GRAY));
                        }
                    }
                    context.drawTooltip(client.textRenderer, list, mouseX, mouseY);
                    break;
                }
            }
            ci.cancel();
        }
    }
}
