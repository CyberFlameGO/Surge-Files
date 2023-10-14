package dev.aurapvp.samurai.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
@Getter
public enum EventType {

    KOTH("koth", "KoTH", ChatColor.BLUE);

    final String name;
    final String displayName;
    final ChatColor color;

}
