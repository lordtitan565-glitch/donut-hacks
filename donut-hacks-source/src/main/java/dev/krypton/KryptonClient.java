package dev.krypton;

import dev.krypton.config.Config;
import dev.krypton.gui.ClickGuiScreen;
import dev.krypton.modules.Freecam;
import dev.krypton.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KryptonClient implements ClientModInitializer {
    private boolean wasDown = false;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        Config.load();
        ClientTickEvents.START_CLIENT_TICK.register(Freecam::suppress);
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
        HudRenderCallback.EVENT.register((ctx, tickCounter) -> ModuleManager.hud(ctx, MinecraftClient.getInstance()));
    }

    private void tick(MinecraftClient mc) {
        // Right Shift opens/closes the menu (polled directly, no keybind registration needed)
        boolean down = GLFW.glfwGetKey(mc.getWindow().getHandle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        if (down && !wasDown) {
            if (mc.currentScreen == null) {
                mc.setScreen(new ClickGuiScreen());
            } else if (mc.currentScreen instanceof ClickGuiScreen) {
                mc.setScreen(null);
            }
        }
        wasDown = down;

        ModuleManager.tick(mc);
    }
}
