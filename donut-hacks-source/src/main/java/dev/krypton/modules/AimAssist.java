package dev.krypton.modules;

import dev.krypton.module.Module;
import dev.krypton.module.NumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/** Gently turns your view toward the nearest target while you hold attack. */
public class AimAssist extends Module {
    private final NumSetting range = num("Range", 4, 2, 6, 0.5);
    private final NumSetting fov = num("FOV", 60, 20, 120, 10);
    private final NumSetting speed = num("Speed", 0.15, 0.05, 0.5, 0.05);

    public AimAssist() { super("Aim Assist", Category.COMBAT); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.currentScreen != null || !mc.options.attackKey.isPressed()) return;
        ClientPlayerEntity p = mc.player;

        LivingEntity best = null;
        double bestDist = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (e == p || !(e instanceof LivingEntity le) || !le.isAlive()) continue;
            if (!(e instanceof PlayerEntity) && !(e instanceof HostileEntity)) continue;
            double d = p.distanceTo(e);
            if (d > range.value || d >= bestDist || !p.canSee(e)) continue;
            best = le;
            bestDist = d;
        }
        if (best == null) return;

        Vec3d eye = p.getEyePos();
        Vec3d t = best.getBoundingBox().getCenter();
        double dx = t.x - eye.x, dy = t.y - eye.y, dz = t.z - eye.z;
        float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float targetPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.sqrt(dx * dx + dz * dz)));

        float yawDelta = MathHelper.wrapDegrees(targetYaw - p.getYaw());
        float pitchDelta = targetPitch - p.getPitch();
        if (Math.abs(yawDelta) > fov.value) return;

        p.setYaw(p.getYaw() + (float) (yawDelta * speed.value));
        p.setPitch(MathHelper.clamp(p.getPitch() + (float) (pitchDelta * speed.value), -90f, 90f));
    }
}
