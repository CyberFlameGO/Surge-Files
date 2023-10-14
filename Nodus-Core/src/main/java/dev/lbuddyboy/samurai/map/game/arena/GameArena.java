package dev.lbuddyboy.samurai.map.game.arena;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.Cuboid;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class GameArena {

    private String name;

    @Setter private Location pointA;
    @Setter private Location pointB;
    @Setter private Location spectatorSpawn;

    @Setter private Cuboid bounds;

    private transient final Map<Location, Material> newOldMatPlaceMap;
    private transient final Map<Location, Material> newOldMatBreakMap;

    private final List<String> compatibleGameTypes = new ArrayList<>();

    public GameArena() {
        this.newOldMatPlaceMap = new HashMap<>();
        this.newOldMatBreakMap = new HashMap<>();
    }

    public GameArena(String name) {
        this();
        this.name = name;
    }

    public boolean isSetup() {
        return pointA != null && pointB != null && spectatorSpawn != null;
    }

    public void createSnapshot() {
        this.newOldMatPlaceMap.clear();
        this.newOldMatBreakMap.clear();
    }

    public void restoreSnapshot() {
        this.newOldMatPlaceMap.forEach((l, mat) -> {
            l.getBlock().setType(mat);
        });
        this.newOldMatBreakMap.forEach((l, mat) -> {
            l.getBlock().setType(mat);
        });
    }

    public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            List<String> completions = new ArrayList<>();
            Player player = context.getPlayer();
            String input = context.getInput();

            for (GameArena arena : Samurai.getInstance().getMapHandler().getGameHandler().getConfig().getArenas()) {
                if (StringUtils.startsWith(arena.getName(), input)) {
                    completions.add(arena.getName());
                }
            }

            return completions;
        }

    }

    public static class Type implements ContextResolver<GameArena, BukkitCommandExecutionContext> {

        @Override
        public GameArena getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
            Player sender = c.getPlayer();
            String source = c.popFirstArg();

            GameArena arena = Samurai.getInstance().getMapHandler().getGameHandler().getConfig().getArenaByName(source);

            if (arena == null) {
                throw new InvalidCommandArgument(CC.translate(ChatColor.RED + "Arena named '" + source + "' couldn't be found."));
            }

            return arena;
        }
    }

}
