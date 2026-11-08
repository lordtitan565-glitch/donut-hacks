package dev.krypton.modules;

import dev.krypton.module.Module;
import dev.krypton.module.NumSetting;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashSet;
import java.util.Set;

/** Lists chests, barrels and shulker boxes in loaded chunks (coordinates in chat). */
public class StorageFinder extends Module {
    private final NumSetting radius = num("Radius", 64, 16, 256, 16);
    private final Set<Long> reported = new HashSet<>();
    private int tick = 0;

    public StorageFinder() { super("Storage Finder", Category.WORLD); }

    @Override
    public void onEnable() { reported.clear(); }

    private static String kind(BlockEntity be) {
        if (be instanceof ShulkerBoxBlockEntity) return "Shulker box";
        if (be instanceof BarrelBlockEntity) return "Barrel";
        if (be instanceof ChestBlockEntity) return "Chest";
        return null;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (++tick % 40 != 0) return;
        int pcx = mc.player.getBlockX() >> 4, pcz = mc.player.getBlockZ() >> 4;
        int r = Math.min(16, mc.options.getViewDistance().getValue());
        int sent = 0;
        for (int cx = pcx - r; cx <= pcx + r && sent < 6; cx++) {
            for (int cz = pcz - r; cz <= pcz + r && sent < 6; cz++) {
                WorldChunk ch = mc.world.getChunk(cx, cz);
                if (ch == null || ch.isEmpty()) continue;
                for (BlockEntity be : ch.getBlockEntities().values()) {
                    String k = kind(be);
                    if (k == null) continue;
                    BlockPos pos = be.getPos();
                    if (!reported.add(pos.asLong())) continue;
                    if (Math.sqrt(pos.getSquaredDistance(mc.player.getBlockPos())) > radius.value) continue;
                    mc.player.sendMessage(Text.literal("[Donut Hacks] " + k + " at "
                            + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()), false);
                    if (++sent >= 6) break;
                }
            }
        }
    }
}
