package dev.lbuddyboy.samurai.map.duel.arena;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.Cuboid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@RequiredArgsConstructor
public class DuelArena {
    private final String name;

    @Setter private Location pointA;
    @Setter private Location pointB;

    @Setter private Cuboid bounds;

    private final Map<Long, ChunkSnapshot> chunkSnapshots = new HashMap<>();

    public boolean isSetup() {
        return pointA != null && pointB != null;
    }

    public void createSnapshot() {
//        synchronized (chunkSnapshots) {
//            bounds.getChunks().forEach(chunk -> chunkSnapshots.put(LongHash.toLong(chunk.getX(), chunk.getZ()), chunk.takeSnapshot()));
//        }
    }

    public void restoreSnapshot() {
//        synchronized (chunkSnapshots) {
//            World world = bounds.getWorld();
//            chunkSnapshots.forEach((key, value) -> world.getChunkAt(LongHash.msw(key), LongHash.lsw(key)).restoreSnapshot(value));
//            chunkSnapshots.clear();
//        }
    }

    public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

        @Override
        public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
            List<String> completions = new ArrayList<>();
            Player player = context.getPlayer();
            String input = context.getInput();

            for (DuelArena arena : Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenas()) {
                if (StringUtils.startsWith(arena.getName(), input)) {
                    completions.add(arena.getName());
                }
            }

            return completions;
        }

    }

    public static class Type implements ContextResolver<DuelArena, BukkitCommandExecutionContext> {

        @Override
        public DuelArena getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
            Player sender = c.getPlayer();
            String source = c.popFirstArg();

            DuelArena arena = Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenaByName(source);

            if (arena == null) {
                throw new InvalidCommandArgument(CC.translate(ChatColor.RED + "Arena named '" + source + "' couldn't be found."));
            }

            return arena;
        }
    }



}
