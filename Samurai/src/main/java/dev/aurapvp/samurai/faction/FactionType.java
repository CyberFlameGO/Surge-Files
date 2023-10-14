package dev.aurapvp.samurai.faction;

import dev.aurapvp.samurai.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum FactionType {

    SPAWN("Spawn", "Spawn", ChatColor.GREEN, Material.GREEN_WOOL, Arrays.asList(
            "&fFaction&7: &a&lSpawn",
            "&fLocation&7: &a0, 0"
    )),
    KOTH("Koth", "KoTH", ChatColor.YELLOW, Material.YELLOW_WOOL, Arrays.asList(
            "&fFaction&7: &e&l%faction-name% KoTH",
            "&fCap Zone&7: &e%faction-home%"
    )),
    CONQUEST("Conquest", "Conquest", ChatColor.GOLD, Material.ORANGE_WOOL, Arrays.asList(
            "&fFaction&7: &e&l%faction-name% KoTH",
            "&fCap Zone&7: &e%faction-home%"
    )),
    CITADEL("Citadel", "Citadel", ChatColor.DARK_PURPLE, Material.PURPLE_WOOL, Arrays.asList(
            "&fFaction&7: &d&lCitadel",
            "&fCap Zone&7: &9%faction-home%"
    )),
    PLAYER("player", "Player", ChatColor.RESET, Material.WHITE_WOOL, Arrays.asList(

    ));

    private final String name, displayName;
    private final ChatColor color;
    private final Material editorMaterial;
    private final List<String> infoLines;

    public List<String> getInfoLines(Object... replaces) {
        return CC.translate(this.infoLines, replaces);
    }

}
