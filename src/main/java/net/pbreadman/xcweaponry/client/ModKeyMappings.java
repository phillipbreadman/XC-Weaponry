package net.pbreadman.xcweaponry.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final KeyMapping OPEN_ART_WHEEL = new KeyMapping(
            "key.xcweaponry.openartwheel",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            "key.categories.xcweaponry");

    private ModKeyMappings() {
    }
}