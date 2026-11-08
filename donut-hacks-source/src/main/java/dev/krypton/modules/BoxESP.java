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
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

/** Screen-space boxes around players, items, hostile mobs and storage blocks, visible through walls. */
public class BoxESP extends Module {
    private final NumSetting range = num("Range", 64, 16, 256, 16);
    private final NumSetting players = num("Players (1=on)", 1, 0, 1, 1);
    private final NumSetting items = num("Items (1=on)", 0, 0, 1, 1);
    private final NumSetting mobs = num("Hostile mobs (1=on)", 0, 0, 1, 1);
    private final NumSetting storage = num("Storage (1=on)", 1, 0, 1, 1);
    private final NumSetting playerColor = choice("Player color", 0, dev.krypton.util.Colors.NAMES);
    private final NumSetting storageColor = choice("Storage color", 4, dev.krypton.util.Colors.NAMES);

    public BoxESP() { super("Box ESP", Category.VISUAL); }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        double r2 = range.value * range.value;
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player && !Freecam.active()) continue;
            int color;
            if (players.value > 0 && e instanceof PlayerEntity) color = Colors.get(playerColor.value);
            else if (items.value > 0 && e instanceof ItemEntity) color = 0xFFFFDD33;
            else if (mobs.value > 0 && e instanceof HostileEntity) color = 0xFFFF8800;
            else continue;
            double dx = e.getX() - Projector.cam.x, dy = e.getY() - Projector.cam.y, dz = e.getZ() - Projector.cam.z;
            if (dx * dx + dy * dy + dz * dz > r2) continue;
            int[] b = Projector.bounds(e.getBoundingBox());
            if (b != null) Draw.rect(ctx, b[0], b[1], b[2], b[3], color);
        }
        if (storage.value > 0) {
            for (BlockPos pos : Targets.storage(mc)) {
                double dx = pos.getX() + 0.5 - Projector.cam.x, dy = pos.getY() + 0.5 - Projector.cam.y, dz = pos.getZ() + 0.5 - Projector.cam.z;
                if (dx * dx + dy * dy + dz * dz > r2) continue;
                int[] b = Projector.bounds(new Box(pos));
                if (b != null) Draw.rect(ctx, b[0], b[1], b[2], b[3], Colors.get(storageColor.value));
            }
        }
    }
}
