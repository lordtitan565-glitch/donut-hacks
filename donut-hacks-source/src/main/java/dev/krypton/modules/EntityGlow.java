package dev.krypton.modules;

import dev.krypton.module.Module;
import dev.krypton.module.NumSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashSet;
import java.util.Set;

/** Outlines players, dropped items and hostile mobs through walls using the game's glow effect. */
public class EntityGlow extends Module {
    private final NumSetting range = num("Range", 64, 16, 256, 16);
    private final NumSetting players = num("Players (1=on)", 1, 0, 1, 1);
    private final NumSetting items = num("Items (1=on)", 1, 0, 1, 1);
    private final NumSetting mobs = num("Hostile mobs (1=on)", 0, 0, 1, 1);
    private final Set<Integer> glowing = new HashSet<>();

    public EntityGlow() { super("Glow ESP", Category.VISUAL); }

    @Override
    public void onTick(MinecraftClient mc) {
        for (Entity e : mc.world.getEntities()) {
            if (e == mc.player) continue;
            boolean match = (players.value > 0 && e instanceof PlayerEntity)
                    || (items.value > 0 && e instanceof ItemEntity)
                    || (mobs.value > 0 && e instanceof HostileEntity);
            boolean want = match && mc.player.distanceTo(e) <= range.value;
            int id = e.getId();
            if (want && !glowing.contains(id)) {
                e.setGlowing(true);
                glowing.add(id);
            } else if (!want && glowing.contains(id)) {
                e.setGlowing(false);
                glowing.remove(id);
            }
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world != null) {
            for (Entity e : mc.world.getEntities()) {
                if (glowing.contains(e.getId())) e.setGlowing(false);
            }
        }
        glowing.clear();
    }
}
