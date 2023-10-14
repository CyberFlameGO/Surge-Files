package dev.aurapvp.samurai;

import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.LocationUtils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum SettingsConfiguration {

    SPAWN_LOCATION("spawn-location", "world;0.5;100.0;0.5;0.0;0.0;"),
    FIRST_JOIN_MONEY("first-join.money", 1000),
    FIRST_JOIN_COINS("first-join.coins", 0),
    FACTION_SETTINGS_REGEN_MINUTES("faction-settings.regen-minutes", 30),
    FACTION_SETTINGS_FACTION_SIZE("faction-settings.faction-size", 1000);

    private final String path;
    private final Object value;

    public String getString(Object... objects) {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return CC.translate(Samurai.getInstance().getFactionHandler().getFactionsLang().getString(this.path), objects);

        loadDefault();

        return CC.translate(String.valueOf(this.value), objects);
    }

    public boolean getBoolean() {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return Samurai.getInstance().getFactionHandler().getFactionsLang().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public ChatColor getChatColor() {
        return ChatColor.valueOf(getString());
    }

    public Location getLocation() {
        return LocationUtils.deserializeString(getString());
    }

    public int getInt() {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return Samurai.getInstance().getFactionHandler().getFactionsLang().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public List<String> getStringList(Object... objects) {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return CC.translate(Samurai.getInstance().getFactionHandler().getFactionsLang().getStringList(this.path), objects);

        loadDefault();

        return CC.translate((List<String>) this.value, objects);
    }

    public void update(Object value) {
        Samurai.getInstance().getFactionHandler().getFactionsLang().set(this.path, value);
        Samurai.getInstance().saveConfig();
    }

    public void loadDefault() {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path)) return;

        Samurai.getInstance().getFactionHandler().getFactionsLang().set(this.path, this.value);
        Samurai.getInstance().saveConfig();
    }

    public static Object[] FACTION_PLACEHOLDERS(Faction faction) {
        return new Object[]{
                "%faction-name%", faction.getName(),
                "%faction-display%", faction.getName(),
                "%faction-size%", faction.getMembers().size()
        };
    }

    public static Object[] PLAYER_PLACEHOLDERS(Player player) {
        return new Object[]{
                "%player-name%", player.getName(),
                "%player-display%", player.getDisplayName()
        };
    }

}
