package dev.aurapvp.samurai.api.impl.dynmap.layer;

import dev.aurapvp.samurai.util.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.NumberConversions;

import java.util.List;

public class LayerConfig {

    private final Config config;
    private final ConfigurationSection section;

    public LayerConfig(Config config, ConfigurationSection section) {
        this.config = config;
        this.section = section;
    }

    public List<String> getStringList(String key) {
        return section.getStringList(key);
    }

    public Double getDouble(String key, double def) {
        return section.getDouble(key, def);
    }

    public Integer getInt(LayerField setting) {
        return section.getInt(setting.path, NumberConversions.toInt(setting.def));
    }

    public Integer getInt(String key, int def) {
        return section.getInt(key, def);
    }

    public String getString(LayerField setting) {
        return section.getString(setting.path, String.valueOf(setting.def));
    }

    public String getString(String key, String def) {
        return section.getString(key, def);
    }

    public Boolean getBoolean(LayerField setting) {
        return section.getBoolean(setting.path, (Boolean) setting.def);
    }

    public Boolean getBoolean(String key, boolean def) {
        return section.getBoolean(key, def);
    }


    /**
     * Represents the enum of <b>general</b> {@link Layer}'s fields
     */
    public enum LayerField {

        ENABLE("enable", true),
        PRIORITY("layer-priority", 1),
        LABEL("label", "Label"),
        FORMAT("format", ""),
        HIDDEN("hide-by-default", false),
        MINZOOM("min-zoom", 0);

        private final String path;
        private final Object def;

        LayerField(String path, Object def) {
            this.path = path;
            this.def = def;
        }
    }
}