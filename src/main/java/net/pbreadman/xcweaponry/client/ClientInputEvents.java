package net.pbreadman.xcweaponry.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.pbreadman.xcweaponry.XCWeaponry;
import net.pbreadman.xcweaponry.gui.overlays.ArtWheel;
import net.pbreadman.xcweaponry.items.ModItems;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = XCWeaponry.MOD_ID, value = Dist.CLIENT)
public class ClientInputEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen instanceof ArtWheel wheel) {
            if (event.getAction() == GLFW.GLFW_RELEASE && isArtWheelKey(event.getKey(), event.getScanCode())) {
                wheel.commit();
            }
            return;
        }
        if (event.getAction() != GLFW.GLFW_PRESS || minecraft.player == null || minecraft.screen != null) {
            return;
        }
        if (!isArtWheelKey(event.getKey(), event.getScanCode())) {
            return;
        }
        ItemStack held = minecraft.player.getMainHandItem();
        if (held.is(ModItems.MONADO.get())) {
            minecraft.setScreen(new ArtWheel());
        } else {
            minecraft.player.displayClientMessage(
                    Component.translatable("message.xcweaponry.need_monado").withStyle(ChatFormatting.RED), true);
        }
    }

    private static boolean isArtWheelKey(int key, int scanCode) {
        return ModKeyMappings.OPEN_ART_WHEEL.getKey().equals(InputConstants.getKey(key, scanCode));
    }
}