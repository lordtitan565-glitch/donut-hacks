package dev.krypton.gui;

import dev.krypton.config.Config;
import dev.krypton.module.Module;
import dev.krypton.module.ModuleManager;
import dev.krypton.module.NumSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ClickGuiScreen extends Screen {
    public ClickGuiScreen() {
        super(Text.literal("Donut Hacks"));
    }

    private static Text label(Module m) {
        return Text.literal(m.name + (m.enabled ? ": ON" : ": OFF"))
                .formatted(m.enabled ? Formatting.GREEN : Formatting.GRAY);
    }

    @Override
    protected void init() {
        Module.Category[] cats = Module.Category.values();
        int colW = Math.min(130, (width - 20) / cats.length - 6);
        int total = cats.length * (colW + 6) - 6;
        int x0 = (width - total) / 2;

        for (int i = 0; i < cats.length; i++) {
            int x = x0 + i * (colW + 6);
            int y = 30;
            for (Module m : ModuleManager.all()) {
                if (m.category != cats[i]) continue;
                ButtonWidget b = ButtonWidget.builder(label(m), btn -> {
                    m.toggle();
                    btn.setMessage(label(m));
                }).dimensions(x, y, colW, 20).build();
                addDrawableChild(b);
                y += 22;
                for (NumSetting s : m.settings) {
                    ButtonWidget sb = ButtonWidget.builder(Text.literal(s.label()), btn -> {
                        s.cycle();
                        btn.setMessage(Text.literal(s.label()));
                    }).dimensions(x + 8, y, colW - 8, 16).build();
                    addDrawableChild(sb);
                    y += 18;
                }
                y += 4;
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Module.Category[] cats = Module.Category.values();
        int colW = Math.min(130, (width - 20) / cats.length - 6);
        int total = cats.length * (colW + 6) - 6;
        int x0 = (width - total) / 2;
        for (int i = 0; i < cats.length; i++) {
            context.drawCenteredTextWithShadow(textRenderer, Text.literal(cats[i].name()),
                    x0 + i * (colW + 6) + colW / 2, 16, 0xFFFFFFFF);
        }
    }

    @Override
    public boolean shouldPause() { return false; }

    @Override
    public void close() {
        Config.save();
        super.close();
    }
}
