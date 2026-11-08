package dev.krypton.modules;

import dev.krypton.module.Module;
import dev.krypton.module.NumSetting;
import dev.krypton.util.Colors;
import dev.krypton.util.Draw;
import dev.krypton.util.Projector;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.lwjgl.glfw.GLFW;

/**
 * Detaches the camera from your player. An invisible client-side camera entity flies around;
 * your real player stays where it is (movement and click keys are blocked while active).
 * Controls: WASD + mouse to fly, Space = up, Left Shift = down.
 */
public class Freecam extends Module {
    private static ArmorStandEntity cam;
    private static ClientWorld camWorld;
    private final NumSetting speed = num("Speed", 1.0, 0.2, 4.0, 0.2);
    private final NumSetting bodyLine = num("Line to body (1=on)", 1, 0, 1, 1);
    private final NumSetting bodyBox = num("Box on body (1=on)", 1, 0, 1, 1);
    private final NumSetting lineColor = choice("Line color", 3, dev.krypton.util.Colors.NAMES);

    public Freecam() { super("Freecam", Category.MOVEMENT); }

    public static boolean active() { return cam != null && !cam.isRemoved(); }

    private static boolean down(long w, int key) {
        return GLFW.glfwGetKey(w, key) == GLFW.GLFW_PRESS;
    }

    private void spawn(MinecraftClient mc) {
        ClientPlayerEntity p = mc.player;
        ArmorStandEntity a = new ArmorStandEntity(EntityType.ARMOR_STAND, mc.world);
        a.setId(-1337);
        a.setInvisible(true);
        a.setNoGravity(true);
        a.setSilent(true);
        a.noClip = true;
        a.setPosition(p.getX(), p.getY(), p.getZ());
        a.setPosition(p.getX(), p.getEyeY() - (a.getEyeY() - a.getY()), p.getZ());
        a.setYaw(p.getYaw());
        a.setPitch(p.getPitch());
        mc.world.addEntity(a);
        cam = a;
        camWorld = mc.world;
        mc.setCameraEntity(a);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (cam == null || cam.isRemoved() || camWorld != mc.world) spawn(mc);
        if (mc.getCameraEntity() != cam) mc.setCameraEntity(cam);

        cam.setYaw(mc.player.getYaw());
        cam.setPitch(mc.player.getPitch());

        if (mc.currentScreen != null) return;
        long w = mc.getWindow().getHandle();
        double yaw = Math.toRadians(cam.getYaw()), pitch = Math.toRadians(cam.getPitch());
        double fx = -Math.sin(yaw) * Math.cos(pitch), fy = -Math.sin(pitch), fz = Math.cos(yaw) * Math.cos(pitch);
        double rx = -Math.cos(yaw), rz = -Math.sin(yaw);
        double mx = 0, my = 0, mz = 0;
        if (down(w, GLFW.GLFW_KEY_W)) { mx += fx; my += fy; mz += fz; }
        if (down(w, GLFW.GLFW_KEY_S)) { mx -= fx; my -= fy; mz -= fz; }
        if (down(w, GLFW.GLFW_KEY_D)) { mx += rx; mz += rz; }
        if (down(w, GLFW.GLFW_KEY_A)) { mx -= rx; mz -= rz; }
        if (down(w, GLFW.GLFW_KEY_SPACE)) my += 1;
        if (down(w, GLFW.GLFW_KEY_LEFT_SHIFT)) my -= 1;
        double len = Math.sqrt(mx * mx + my * my + mz * mz);
        if (len > 0) {
            double s = speed.value / len;
            cam.setPosition(cam.getX() + mx * s, cam.getY() + my * s, cam.getZ() + mz * s);
        }
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        if (!active()) return;
        int color = Colors.get(lineColor.value);
        Vec3d c = mc.player.getBoundingBox().getCenter();
        if (bodyLine.value > 0) {
            double[] p = Projector.project(c.x, c.y, c.z);
            if (p != null) Draw.line(ctx, Projector.sw / 2.0, Projector.sh, p[0], p[1], color);
        }
        if (bodyBox.value > 0) {
            int[] b = Projector.bounds(mc.player.getBoundingBox());
            if (b != null) Draw.rect(ctx, b[0], b[1], b[2], b[3], color);
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.setCameraEntity(mc.player);
        if (cam != null) cam.discard();
        cam = null;
        camWorld = null;
    }

    /** Called at the start of every client tick: keeps your real player still while Freecam is on. */
    public static void suppress(MinecraftClient mc) {
        if (cam == null || cam.isRemoved() || mc.options == null) return;
        mc.options.forwardKey.setPressed(false);
        mc.options.backKey.setPressed(false);
        mc.options.leftKey.setPressed(false);
        mc.options.rightKey.setPressed(false);
        mc.options.jumpKey.setPressed(false);
        mc.options.sneakKey.setPressed(false);
        mc.options.sprintKey.setPressed(false);
        mc.options.attackKey.setPressed(false);
        mc.options.useKey.setPressed(false);
    }
}
