package dev.lbuddyboy.gkits;

import dev.lbuddyboy.gkits.object.kit.GKit;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.gkits.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public enum MessageConfiguration {

    USED_KIT("used-gkit", "&fYou have just used the %kit-display%&f kit!");

    private final String path;
    private final Object value;

    public String getString(Object... objects) {
        if (lGKits.getInstance().getMessageConfig().contains(this.path))
            return CC.translate(lGKits.getInstance().getMessageConfig().getString(this.path), objects);

        loadDefault();

        return CC.translate(String.valueOf(this.value), objects);
    }

    public boolean getBoolean() {
        if (lGKits.getInstance().getMessageConfig().contains(this.path))
            return lGKits.getInstance().getMessageConfig().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public ChatColor getChatColor() {
        return ChatColor.valueOf(getString());
    }

    public int getInt() {
        if (lGKits.getInstance().getMessageConfig().contains(this.path))
            return lGKits.getInstance().getMessageConfig().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public List<String> getStringList(Object... objects) {
        if (lGKits.getInstance().getMessageConfig().contains(this.path))
            return CC.translate(lGKits.getInstance().getMessageConfig().getStringList(this.path), objects);

        loadDefault();

        return CC.translate((List<String>) this.value, objects);
    }

    public void update(Object value) {
        lGKits.getInstance().getMessageConfig().set(this.path, value);
        lGKits.getInstance().getMessageConfig().save();
    }

    public void loadDefault() {
        if (lGKits.getInstance().getMessageConfig().contains(this.path)) return;

        lGKits.getInstance().getMessageConfig().set(this.path, this.value);
        lGKits.getInstance().getMessageConfig().save();
    }

    public static Object[] KIT_PLACEHOLDERS(GKit kit) {
        return new Object[]{
                "%kit-name%", kit.getName(),
                "%kit-display%", kit.getDisplayName()
        };
    }

    public static Object[] PLAYER_PLACEHOLDERS(Player player) {
        return new Object[]{
                "%player-name%", player.getName(),
                "%player-display%", player.getDisplayName()
        };
    }

}
