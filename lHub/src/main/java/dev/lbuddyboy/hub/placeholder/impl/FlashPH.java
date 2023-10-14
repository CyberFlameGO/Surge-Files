package dev.lbuddyboy.hub.placeholder.impl;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.user.User;
import dev.lbuddyboy.flash.user.model.PunishmentType;
import dev.lbuddyboy.hub.placeholder.PlaceholderImpl;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

public class FlashPH implements PlaceholderImpl {

    @Override
    public String applyPlaceholders(String string, Player player) {
        User user = Flash.getInstance().getUserHandler().tryUser(player.getUniqueId(), false);
        if (user == null) return string;

        for (PunishmentType type : PunishmentType.values()) {
            if (type == PunishmentType.MUTE || type == PunishmentType.WARN || type == PunishmentType.KICK) continue;

            if (user.hasActivePunishment(type)) {
                string = string
                        .replaceAll("%punishment_type%", user.getActivePunishment(type).getType().getDisplay())
                        .replaceAll("%punishment_time%", user.getActivePunishment(type).getExpireStringShort())
                ;
            }
        }

        return string
                .replaceAll("%punishment_type%", "N/A")
                .replaceAll("%punishment_time%","N/A")
                .replaceAll("%player_coins%", NumberFormat.getInstance(Locale.ENGLISH).format(user.getCoins()))
                .replaceAll("%player_credits%", NumberFormat.getInstance(Locale.ENGLISH).format(user.getJailCredits()))
                ;
    }

    @Override
    public String applyPlaceholders(String string) {
        return string;
    }
}
