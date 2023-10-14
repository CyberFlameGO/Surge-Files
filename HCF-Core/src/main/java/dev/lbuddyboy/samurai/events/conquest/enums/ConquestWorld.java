package dev.lbuddyboy.samurai.events.conquest.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

@AllArgsConstructor
public enum ConquestWorld {

    OVERWORLD(ChatColor.GREEN, "Overworld"),
    NETHER(ChatColor.RED, "Nether");

    @Getter private ChatColor color;
    @Getter private String name;

}