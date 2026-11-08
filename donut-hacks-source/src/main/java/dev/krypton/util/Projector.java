package dev.krypton.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/** Projects world positions onto the screen (scaled GUI coordinates) from the current camera. */
public final class Projector {
    public static Vec3d cam = Vec3d.ZERO;
    public static int sw, sh;
    private static double fx, fy, fz, rx, rz, ux, uy, uz, tanHalf, aspect;

    private Projector() {}

    public static boolean begin(MinecraftClient mc) {
        Entity ce = mc.getCameraEntity();
        if (ce == null) return false;
        cam = ce.getEyePos();
        double yaw = Math.toRadians(ce.getYaw()), pitch = Math.toRadians(ce.getPitch());
        fx = -Math.sin(yaw) * Math.cos(pitch);
        fy = -Math.sin(pitch);
        fz = Math.cos(yaw) * Math.cos(pitch);
        rx = -Math.cos(yaw);
        rz = -Math.sin(yaw);
        ux = -rz * fy;
        uy = rz * fx - rx * fz;
        uz = rx * fy;
        tanHalf = Math.tan(Math.toRadians(mc.options.getFov().getValue()) / 2.0);
        sw = mc.getWindow().getScaledWidth();
        sh = mc.getWindow().getScaledHeight();
        aspect = (double) sw / (double) sh;
        return true;
    }

    /** Returns {screenX, screenY, depth}, or null when the point is behind the camera. */
    public static double[] project(double x, double y, double z) {
        double dx = x - cam.x, dy = y - cam.y, dz = z - cam.z;
        double d = dx * fx + dy * fy + dz * fz;
        if (d < 0.05) return null;
        double r = dx * rx + dz * rz;
        double u = dx * ux + dy * uy + dz * uz;
        double nx = r / (d * tanHalf * aspect);
        double ny = u / (d * tanHalf);
        return new double[]{sw / 2.0 + nx * sw / 2.0, sh / 2.0 - ny * sh / 2.0, d};
    }

    /** Screen-space rectangle {minX, minY, maxX, maxY} around a box, or null if not fully in front of the camera / off screen. */
    public static int[] bounds(Box b) {
        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, maxY = -Double.MAX_VALUE;
        for (int i = 0; i < 8; i++) {
            double x = (i & 1) == 0 ? b.minX : b.maxX;
            double y = (i & 2) == 0 ? b.minY : b.maxY;
            double z = (i & 4) == 0 ? b.minZ : b.maxZ;
            double[] p = project(x, y, z);
            if (p == null) return null;
            minX = Math.min(minX, p[0]); maxX = Math.max(maxX, p[0]);
            minY = Math.min(minY, p[1]); maxY = Math.max(maxY, p[1]);
        }
        if (maxX < 0 || minX > sw || maxY < 0 || minY > sh) return null;
        return new int[]{(int) minX, (int) minY, (int) maxX, (int) maxY};
    }
}
