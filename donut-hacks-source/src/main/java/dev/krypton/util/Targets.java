package dev.krypton.util;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.List;

public final class Targets {
    private static final List<BlockPos> STORAGE = new ArrayList<>();
    private static long last = -1000;

    private Targets() {}

    public static boolean isStorage(BlockEntity be) {
        return be instanceof ChestBlockEntity || be instanceof BarrelBlockEntity || be instanceof ShulkerBoxBlockEntity;
    }

    /** Chests, barrels and shulker boxes in loaded chunks. Rescanned about once a second. */
    public static List<BlockPos> storage(MinecraftClient mc) {
        long t = mc.world.getTime();
        if (t - last >= 20 || t < last) {
            last = t;
            STORAGE.clear();
            int pcx = mc.player.getBlockX() >> 4, pcz = mc.player.getBlockZ() >> 4;
            int r = Math.min(16, mc.options.getViewDistance().getValue());
            for (int cx = pcx - r; cx <= pcx + r; cx++) {
                for (int cz = pcz - r; cz <= pcz + r; cz++) {
                    WorldChunk ch = mc.world.getChunk(cx, cz);
                    if (ch == null || ch.isEmpty()) continue;
                    for (BlockEntity be : ch.getBlockEntities().values()) {
                        if (isStorage(be)) STORAGE.add(be.getPos());
                    }
                }
            }
        }
        return STORAGE;
    }
}
