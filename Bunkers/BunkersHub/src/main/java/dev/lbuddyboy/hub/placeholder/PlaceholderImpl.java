package dev.lbuddyboy.hub.placeholder;

import org.bukkit.entity.Player;

public interface PlaceholderImpl {

    String applyPlaceholders(String string, Player player);
    String applyPlaceholders(String string);

}
