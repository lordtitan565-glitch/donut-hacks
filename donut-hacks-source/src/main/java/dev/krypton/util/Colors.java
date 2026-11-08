package dev.krypton.util;

public final class Colors {
    public static final String[] NAMES = {"Red", "Orange", "Yellow", "Green", "Cyan", "Blue", "Purple", "Pink", "White"};
    private static final int[] ARGB = {
            0xFFFF4444, 0xFFFF9922, 0xFFFFE033, 0xFF44DD55, 0xFF33DDFF,
            0xFF4477FF, 0xFFAA55FF, 0xFFFF66CC, 0xFFFFFFFF
    };

    private Colors() {}

    public static int get(double index) {
        int i = (int) Math.round(index);
        return ARGB[Math.max(0, Math.min(ARGB.length - 1, i))];
    }
}
