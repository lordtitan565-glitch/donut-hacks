package dev.krypton.modules;

import dev.krypton.module.Module;
import dev.krypton.module.NumSetting;
import dev.krypton.util.Colors;
import dev.krypton.util.Draw;
import dev.krypton.util.Projector;
import dev.krypton.util.Targets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/** Lines from the bottom of your screen to players (and optionally storage blocks). */
public class Tracers extends Module {
    private final NumSetting range = num("Range", 128, 16, 256, 16);
    private final NumSetting players = num("Players (1=on)", 1, 0, 1, 1);
    private final NumSetting storage = num("Storage (1=on)", 0, 0, 1, 1);
    private final NumSetting playerColor = choice("Player color", 0, dev.krypton.util.Colors.NAMES);
    private final NumSetting storageColor = choice("Storage color", 4, dev.krypton.util.Colors.NAMES);

    public Tracers() { super("Tracers", Category.VISUAL); }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        double r2 = range.value * range.value;
        double ox = Projector.sw / 2.0, oy = Projector.sh;
        if (players.value > 0) {
            for (Entity e : mc.world.getEntities()) {
                if (!(e instanceof PlayerEntity) || (e == mc.player && !Freecam.active())) continue;
                Vec3d c = e.getBoundingBox().getCenter();
                double dx = c.x - Projector.cam.x, dy = c.y - Projector.cam.y, dz = c.z - Projector.cam.z;
                if (dx * dx + dy * dy + dz * dz > r2) continue;
                double[] p = Projector.project(c.x, c.y, c.z);
                if (p != null) Draw.line(ctx, ox, oy, p[0], p[1], Colors.get(playerColor.value));
            }
        }
        if (storage.value > 0) {
            for (BlockPos pos : Targets.storage(mc)) {
                double x = pos.getX() + 0.5, y = pos.getY() + 0.5, z = pos.getZ() + 0.5;
                double dx = x - Projector.cam.x, dy = y - Projector.cam.y, dz = z - Projector.cam.z;
                if (dx * dx + dy * dy + dz * dz > r2) continue;
                double[] p = Projector.project(x, y, z);
                if (p != null) Draw.line(ctx, ox, oy, p[0], p[1], Colors.get(storageColor.value));
            }
        }
    }
}
