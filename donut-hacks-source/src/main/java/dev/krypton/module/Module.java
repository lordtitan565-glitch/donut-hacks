package dev.krypton.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public enum Category { COMBAT, MOVEMENT, VISUAL, WORLD }

    public final String name;
    public final Category category;
    public boolean enabled = false;
    public final List<NumSetting> settings = new ArrayList<>();

    protected Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    protected NumSetting num(String n, double v, double min, double max, double step) {
        NumSetting s = new NumSetting(n, v, min, max, step);
        settings.add(s);
        return s;
    }

    protected NumSetting choice(String n, int def, String... labels) {
        NumSetting s = new NumSetting(n, def, 0, labels.length - 1, 1);
        s.labels = labels;
        settings.add(s);
        return s;
    }

    public void toggle() { setEnabled(!enabled); }

    public void setEnabled(boolean v) {
        if (v == enabled) return;
        enabled = v;
        if (v) onEnable(); else onDisable();
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
    public void onHud(DrawContext ctx, MinecraftClient mc) {}
}
