package dev.krypton.util;

import net.minecraft.client.gui.DrawContext;

public final class Draw {
    private Draw() {}

    public static void rect(DrawContext c, int x1, int y1, int x2, int y2, int col) {
        c.fill(x1, y1, x2, y1 + 1, col);
        c.fill(x1, y2 - 1, x2, y2, col);
        c.fill(x1, y1, x1 + 1, y2, col);
        c.fill(x2 - 1, y1, x2, y2, col);
    }

    public static void line(DrawContext c, double x1, double y1, double x2, double y2, int col) {
        if (Math.abs(x2) > 5000 || Math.abs(y2) > 5000) return;
        double dx = x2 - x1, dy = y2 - y1;
        int n = (int) Math.min(1500, Math.max(Math.abs(dx), Math.abs(dy)));
        if (n < 1) return;
        for (int i = 0; i <= n; i++) {
            int x = (int) (x1 + dx * i / n);
            int y = (int) (y1 + dy * i / n);
            c.fill(x, y, x + 1, y + 1, col);
        }
    }
}
