package dev.lbuddyboy.pcore;

import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.LocationUtils;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public enum SettingsConfiguration {

    SPAWN_LOCATION("spawn-location", "world;0.5;100.0;0.5;0.0;0.0;"),
    FIRST_JOIN_MESSAGE("first-join.message", "&eWelcome &6%player_display% &eto Prisons! &7(#%joins%)"),
    FIRST_JOIN_MONEY("first-join.money", 5000),
    FIRST_JOIN_COINS("first-join.coins", 0);

    private final String path;
    private final Object value;

    public String getString(Object... objects) {
        if (pCore.getInstance().getConfig().contains(this.path))
            return CC.translate(pCore.getInstance().getConfig().getString(this.path), objects);

        loadDefault();

        return CC.translate(String.valueOf(this.value), objects);
    }

    public boolean getBoolean() {
        if (pCore.getInstance().getConfig().contains(this.path))
            return pCore.getInstance().getConfig().getBoolean(this.path);

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
        if (pCore.getInstance().getConfig().contains(this.path))
            return pCore.getInstance().getConfig().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public List<String> getStringList(Object... objects) {
        if (pCore.getInstance().getConfig().contains(this.path))
            return CC.translate(pCore.getInstance().getConfig().getStringList(this.path), objects);

        loadDefault();

        return CC.translate((List<String>) this.value, objects);
    }

    public void update(Object value) {
        pCore.getInstance().getConfig().set(this.path, value);
        pCore.getInstance().saveConfig();
    }

    public void loadDefault() {
        if (pCore.getInstance().getConfig().contains(this.path)) return;

        pCore.getInstance().getConfig().set(this.path, this.value);
        pCore.getInstance().saveConfig();
    }

    public static Object[] PLAYER_PLACEHOLDERS(Player player) {
        return new Object[]{
                "%player-name%", player.getName(),
                "%player-display%", player.getDisplayName()
        };
    }

}
