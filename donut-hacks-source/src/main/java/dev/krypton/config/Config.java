package dev.krypton.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.krypton.module.Module;
import dev.krypton.module.ModuleManager;
import dev.krypton.module.NumSetting;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Config {
    private Config() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("donut_hacks.json");
    }

    public static void save() {
        try {
            JsonObject root = new JsonObject();
            for (Module m : ModuleManager.all()) {
                JsonObject o = new JsonObject();
                o.addProperty("enabled", m.enabled);
                for (NumSetting s : m.settings) o.addProperty(s.name, s.value);
                root.add(m.name, o);
            }
            Files.writeString(file(), new GsonBuilder().setPrettyPrinting().create().toJson(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load() {
        try {
            Path f = file();
            if (!Files.exists(f)) return;
            JsonObject root = JsonParser.parseString(Files.readString(f)).getAsJsonObject();
            for (Module m : ModuleManager.all()) {
                JsonElement e = root.get(m.name);
                if (e == null || !e.isJsonObject()) continue;
                JsonObject o = e.getAsJsonObject();
                if (o.has("enabled")) m.enabled = o.get("enabled").getAsBoolean();
                for (NumSetting s : m.settings) if (o.has(s.name)) s.value = o.get(s.name).getAsDouble();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
