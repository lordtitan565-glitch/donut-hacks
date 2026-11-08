package dev.krypton.modules;

import dev.krypton.module.Module;
import dev.krypton.module.NumSetting;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.Set;

/**
 * Flags "suspicious" chunks: many player-made block entities (storage, furnaces, hoppers, beds)
 * in one chunk. Only sees chunks the server has actually sent to your client.
 */
public class BaseFinder extends Module {
    private final NumSetting threshold = num("Threshold", 8, 4, 40, 2);
    private final Set<Long> reported = new HashSet<>();
    private int tick = 0;

    public BaseFinder() { super("Base Finder", Category.WORLD); }

    @Override
    public void onEnable() { reported.clear(); }

    private static int weight(BlockEntity be) {
        if (be instanceof ShulkerBoxBlockEntity) return 3;
        if (be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity
                || be instanceof HopperBlockEntity || be instanceof AbstractFurnaceBlockEntity
                || be instanceof BedBlockEntity) return 1;
        return 0;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (++tick % 60 != 0) return;
        int pcx = mc.player.getBlockX() >> 4, pcz = mc.player.getBlockZ() >> 4;
        int r = Math.min(16, mc.options.getViewDistance().getValue());
        for (int cx = pcx - r; cx <= pcx + r; cx++) {
            for (int cz = pcz - r; cz <= pcz + r; cz++) {
                WorldChunk ch = mc.world.getChunk(cx, cz);
                if (ch == null || ch.isEmpty()) continue;
                int score = 0;
                for (BlockEntity be : ch.getBlockEntities().values()) score += weight(be);
                if (score < threshold.value) continue;
                long key = ((long) cx << 32) ^ (cz & 0xffffffffL);
                if (!reported.add(key)) continue;
                mc.player.sendMessage(Text.literal("[Donut Hacks] Suspicious chunk near "
                        + (cx * 16 + 8) + ", " + (cz * 16 + 8) + " (score " + score + ")"), false);
            }
        }
    }
}
