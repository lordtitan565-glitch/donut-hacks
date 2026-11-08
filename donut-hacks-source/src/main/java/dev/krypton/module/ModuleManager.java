package dev.krypton.module;

import dev.krypton.modules.*;
import dev.krypton.util.Projector;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        MODULES.add(new AimAssist());
        MODULES.add(new ToggleSprint());
        MODULES.add(new Freecam());
        MODULES.add(new EntityGlow());
        MODULES.add(new BoxESP());
        MODULES.add(new Tracers());
        MODULES.add(new StorageFinder());
        MODULES.add(new BaseFinder());
    }

    public static List<Module> all() { return MODULES; }

    public static void tick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        for (Module m : MODULES) {
            if (!m.enabled) continue;
            try {
                m.onTick(mc);
            } catch (Throwable t) {
                t.printStackTrace();
                m.enabled = false; // never crash the game because of one module
            }
        }
    }

    public static void hud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null || mc.world == null) return;
        boolean any = false;
        for (Module m : MODULES) if (m.enabled) { any = true; break; }
        if (!any || !Projector.begin(mc)) return;
        for (Module m : MODULES) {
            if (!m.enabled) continue;
            try {
                m.onHud(ctx, mc);
            } catch (Throwable t) {
                t.printStackTrace();
                m.enabled = false;
            }
        }
    }
}
