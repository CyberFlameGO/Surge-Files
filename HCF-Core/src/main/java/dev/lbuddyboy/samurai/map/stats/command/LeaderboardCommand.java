package dev.lbuddyboy.samurai.map.stats.command;

import co.aikar.commands.*;
import co.aikar.commands.annotation.*;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("leaderboard|leaderboards|lb")
public class LeaderboardCommand extends BaseCommand {

    @Default
    @CommandCompletion("@objectives")
    public static void def(CommandSender sender, @Name("objective") @Optional StatsObjective objective) {
        if (objective == null) objective = StatsObjective.KILLS;
        sender.sendMessage(ChatColor.GRAY + "Loading stats...");

        List<Object> messages = new ArrayList<>();
        messages.add("&8&m---------------------------");
        messages.add("&h» &g&lLeaderboards &h«");
        messages.add(" ");
        for (int place = 1; place < 11; place++) {
            StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().get(objective, place);

            if (stats == null)
                continue;

            String name = UUIDUtils.name(stats.getOwner());
            FancyMessage message = new FancyMessage(CC.translate("&h" + place + ") &7" + name));
            message.tooltip(CC.translate(Arrays.asList(
                    "&g&l" + name,
                    " ",
                    "&hKills: &f" + stats.getKills(),
                    "&hDeaths: &f" + stats.getDeaths(),
                    " ",
                    "&aClick to view stats info"
            )));
            message.command("/stats " + name);
            messages.add(message);
        }
        messages.add("&8&m---------------------------");

        messages.forEach(message -> {
            if (message instanceof FancyMessage) {
                ((FancyMessage) message).send(sender);
            } else {
                sender.sendMessage(CC.translate((String) message));
            }
        });
    }

    @Subcommand("add")
    @CommandPermission("op")
    @CommandCompletion("@objectives")
    public static void leaderboardAdd(Player sender, @Name("objective") String objectiveName, @Name("place") int place) {
        Block block = sender.getTargetBlock(null, 10);
        StatsObjective objective;

        try {
            objective = StatsObjective.valueOf(objectiveName);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Invalid objective!");
            return;
        }

        if (block == null || !(block.getState() instanceof Skull || block.getState() instanceof Sign)) {
            sender.sendMessage(ChatColor.RED + "You must be looking at a head or a sign.");
            return;
        }

        if (block.getState() instanceof Skull) {
            Skull skull = (Skull) block.getState();

            if (skull.getSkullType() != SkullType.PLAYER) {
                sender.sendMessage(ChatColor.RED + "That's not a player skull.");
                return;
            }

            Samurai.getInstance().getMapHandler().getStatsHandler().getLeaderboardHeads().put(skull.getLocation(), place);
            Samurai.getInstance().getMapHandler().getStatsHandler().getObjectives().put(skull.getLocation(), objective);
            Samurai.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
            sender.sendMessage(ChatColor.GREEN + "This skull will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's head.");
        } else {
            Sign sign = (Sign) block.getState();

            Samurai.getInstance().getMapHandler().getStatsHandler().getLeaderboardSigns().put(sign.getLocation(), place);
            Samurai.getInstance().getMapHandler().getStatsHandler().getObjectives().put(sign.getLocation(), objective);
            Samurai.getInstance().getMapHandler().getStatsHandler().updatePhysicalLeaderboards();
            sender.sendMessage(ChatColor.GREEN + "This sign will now display the number " + ChatColor.WHITE + place + ChatColor.GREEN + " player's stats.");
        }

        Samurai.getInstance().getMapHandler().getStatsHandler().getObjectives().put(block.getLocation(), objective);
    }

    @Subcommand("clear|reset")
    @CommandPermission("op")
    public static void clearallstats(Player sender) {
        ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to clear leaderboards? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    Samurai.getInstance().getMapHandler().getStatsHandler().clearLeaderboards();
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Leaderboards cleared");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }

    @Getter
    public enum StatsObjective {

        KILLS("Kills", "k"),
        DEATHS("Deaths", "d"),
        KD("KD", "kdr"),
        HIGHEST_KILLSTREAK("Highest Killstreak", "killstreak", "highestkillstreak", "ks", "highestks", "hks");

        private String name;
        private String[] aliases;

        StatsObjective(String name, String... aliases) {
            this.name = name;
            this.aliases = aliases;
        }


        public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

            @Override
            public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
                List<String> completions = new ArrayList<>();
                Player player = context.getPlayer();
                String input = context.getInput();

                for (StatsObjective objective : StatsObjective.values()) {
                    if (StringUtils.startsWithIgnoreCase(objective.name(), input)) {
                        completions.add(objective.name());
                    }
                }

                return completions;
            }

        }

        public static class Type implements ContextResolver<StatsObjective, BukkitCommandExecutionContext> {

            @Override
            public StatsObjective getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
                Player sender = c.getPlayer();
                String source = c.popFirstArg();

                if (sender != null && (source.equalsIgnoreCase("self") || source.equals(""))) {
                    return StatsObjective.KILLS;
                }

                try {
                    return StatsObjective.valueOf(source.toUpperCase());
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Stat type '" + source + "' couldn't be found.");
                    return null;
                }
            }
        }


    }

}
