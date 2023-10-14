package dev.lbuddyboy.crates.model;

import dev.lbuddyboy.crates.util.Config;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
public class HologramSettings {

    private Crate crate;
    private double offset = 1.0;
    private boolean enabled;
    private boolean floatingKey;
    private List<String> lines;

    public HologramSettings(Crate crate) {
        this.crate = crate;
        Config config = this.crate.getConfig();

        this.offset = config.getDouble("hologram.offset");
        this.enabled = config.getBoolean("hologram.enabled");
        this.floatingKey = config.getBoolean("hologram.floating-key");
        this.lines = config.getStringList("hologram.lines");
    }

    public HologramSettings loadDefault(Crate crate) {
        this.crate = crate;
        this.enabled = true;
        this.floatingKey = true;
        this.lines = Arrays.asList(crate.getDisplayName(), "&fCrate");

        return this;
    }

    public void save() {
        Config config = this.crate.getConfig();

        config.set("hologram.offset", this.offset);
        config.set("hologram.enabled", this.enabled);
        config.set("hologram.floating-key", this.floatingKey);
        config.set("hologram.lines", this.lines);

        config.save();
    }

}
