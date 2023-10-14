package dev.lbuddyboy.vouchers;

import dev.lbuddyboy.vouchers.object.Voucher;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.vouchers.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
public enum MessageConfiguration {

    USED_VOUCHER("used-voucher", "&fYou have just claimed the %voucher-display%&f voucher!");

    private final String path;
    private final Object value;

    public String getString(Object... objects) {
        if (lVouchers.getInstance().getMessageConfig().contains(this.path))
            return CC.translate(lVouchers.getInstance().getMessageConfig().getString(this.path), objects);

        loadDefault();

        return CC.translate(String.valueOf(this.value), objects);
    }

    public boolean getBoolean() {
        if (lVouchers.getInstance().getMessageConfig().contains(this.path))
            return lVouchers.getInstance().getMessageConfig().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public ChatColor getChatColor() {
        return ChatColor.valueOf(getString());
    }

    public int getInt() {
        if (lVouchers.getInstance().getMessageConfig().contains(this.path))
            return lVouchers.getInstance().getMessageConfig().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public List<String> getStringList(Object... objects) {
        if (lVouchers.getInstance().getMessageConfig().contains(this.path))
            return CC.translate(lVouchers.getInstance().getMessageConfig().getStringList(this.path), objects);

        loadDefault();

        return CC.translate((List<String>) this.value, objects);
    }

    public void update(Object value) {
        lVouchers.getInstance().getMessageConfig().set(this.path, value);
        lVouchers.getInstance().getMessageConfig().save();
    }

    public void loadDefault() {
        if (lVouchers.getInstance().getMessageConfig().contains(this.path)) return;

        lVouchers.getInstance().getMessageConfig().set(this.path, this.value);
        lVouchers.getInstance().getMessageConfig().save();
    }

    public static Object[] VOUCHER_PLAYER_PLACEHOLDERS(Player player, Voucher voucher) {
        return new Object[]{
                "%player-name%", player.getName(),
                "%player-display%", player.getDisplayName(),
                "%voucher-name%", voucher.getName(),
                "%voucher-display%", voucher.getDisplayName()
        };
    }

    public static Object[] PLAYER_PLACEHOLDERS(Player player) {
        return new Object[]{
                "%player-name%", player.getName(),
                "%player-display%", player.getDisplayName()
        };
    }

}
