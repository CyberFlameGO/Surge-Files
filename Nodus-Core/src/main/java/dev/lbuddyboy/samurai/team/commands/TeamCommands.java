package dev.lbuddyboy.samurai.team.commands;

import co.aikar.commands.*;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.flash.util.PagedItem;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.chat.enums.ChatMode;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.profile.AbilityProfile;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.custom.vaults.listener.VaultListener;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.pvpclasses.PvPClassHandler;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.TeamFilter;
import dev.lbuddyboy.samurai.team.boosters.TeamBoosterType;
import dev.lbuddyboy.samurai.team.brew.menu.BrewMenu;
import dev.lbuddyboy.samurai.team.claims.*;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.team.dtr.DTRHandler;
import dev.lbuddyboy.samurai.team.event.FullTeamBypassEvent;
import dev.lbuddyboy.samurai.team.ftop.FTopHandler;
import dev.lbuddyboy.samurai.team.logs.TeamLog;
import dev.lbuddyboy.samurai.team.logs.menu.TeamLogsMenu;
import dev.lbuddyboy.samurai.team.menu.FactionSettingsMenu;
import dev.lbuddyboy.samurai.team.menu.FilterMenu;
import dev.lbuddyboy.samurai.team.roster.menu.RosterMenu;
import dev.lbuddyboy.samurai.team.track.TeamActionTracker;
import dev.lbuddyboy.samurai.team.track.TeamActionType;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.util.jpaste.exceptions.PasteException;
import dev.lbuddyboy.samurai.util.jpaste.pastebin.Pastebin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bson.types.ObjectId;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CommandAlias("team|t|f|faction|fac")

public class TeamCommands extends BaseCommand implements Listener {
    @Getter
    private static Map<UUID, String> teamMutes = new HashMap<>();
    @Getter
    public static Map<UUID, String> teamShadowMutes = new HashMap<>();

    public static final Pattern ALPHA_NUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    private static final Set<String> disallowedTeamNames = ImmutableSet.of("list", "Glowstone", "self");

    private static final double MAX_DISTANCE = 5;

    private static final Set<Integer> warn = new HashSet<>();

    @Subcommand("a|accept|join|j")
    @Description("Accepts a team invitation")
    public static void teamAccept(Player sender, @Name("team") Team team) {
        if (team.getInvitations().contains(sender.getUniqueId()) || team.getRoster().contains(sender.getUniqueId())) {
            if (Samurai.getInstance().getTeamHandler().getTeam(sender.getUniqueId()) != null) {
                sender.sendMessage(ChatColor.RED + "You are already on a team!");
                return;
            }

            if (team.getMembers().size() >= Samurai.getInstance().getMapHandler().getTeamSize()) {
                FullTeamBypassEvent attemptEvent = new FullTeamBypassEvent(sender, team);
                Samurai.getInstance().getServer().getPluginManager().callEvent(attemptEvent);

                if (!attemptEvent.isAllowBypass()) {
                    sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team is full!");
                    return;
                }
            }

            if (DTRHandler.isOnCooldown(team) && !Samurai.getInstance().getServerHandler().isPreEOTW() && !Samurai.getInstance().getMapHandler().isKitMap() && !Samurai.getInstance().getMapHandler().isKitMap()) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: Team not regenerating DTR!");
                return;
            }

            if (SpawnTagHandler.isTagged(sender)) {
                sender.sendMessage(ChatColor.RED + team.getName() + " cannot be joined: You are combat tagged!");
                return;
            }

            if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        team.getFocusedTeam().getName() + "'s HQ",
                        team.getFocusedTeam().getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }

            if (team.getTeamRally() != null) {
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        "Team Rally",
                        team.getTeamRally(),
                        Color.AQUA.hashCode(),
                        true
                ));
            }

            if (team.getHQ() != null) {
                LunarClientAPI.getInstance().sendWaypoint(sender, new LCWaypoint(
                        "HQ",
                        team.getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }

            team.createLog(sender.getUniqueId(), "JOIN", "/t accept " + team.getName());
            team.getInvitations().remove(sender.getUniqueId());
            team.addMember(sender.getUniqueId());
            if (team.getRoster().contains(sender.getUniqueId())) {
                if (team.getRoster().getCaptains().contains(sender.getUniqueId())) {
                    team.addCaptain(sender.getUniqueId());
                } else if (team.getRoster().getColeaders().contains(sender.getUniqueId())) {
                    team.addCoLeader(sender.getUniqueId());
                }
            }
            Samurai.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), team);

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has joined the team!");

            FrozenNametagHandler.reloadPlayer(sender);
            FrozenNametagHandler.reloadOthersFor(sender);

/*            if (team.getArchers() >= 1 && PvPClassHandler.hasKitOn(sender, new ArcherClass())) {
                PvPClassHandler.getPvPClass(sender).remove(sender);
            }*/

        } else {
            sender.sendMessage(ChatColor.RED + "This team has not invited you!");
        }
    }

    @CommandAlias("ally")
    @Description("Allies a team with another team")
    @CommandCompletion("@team")
    public static void teamAlly(Player sender, @Name("team") Team team) {
        Team senderTeam = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getUniqueId()) || senderTeam.isCaptain(sender.getUniqueId()) || senderTeam.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (senderTeam.equals(team)) {
            sender.sendMessage(ChatColor.YELLOW + "You cannot ally your own team!");
            return;
        }

        if (senderTeam.getAllies().size() >= Samurai.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "Your team already has the max number of allies, which is " + Samurai.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }

        if (team.getAllies().size() >= Samurai.getInstance().getMapHandler().getAllyLimit()) {
            sender.sendMessage(ChatColor.YELLOW + "The team you're trying to ally already has the max number of allies, which is " + Samurai.getInstance().getMapHandler().getAllyLimit() + ".");
            return;
        }

        if (senderTeam.isAlly(team)) {
            sender.sendMessage(ChatColor.YELLOW + "You're already allied to " + team.getName(sender) + ChatColor.YELLOW + ".");
            return;
        }

        if (senderTeam.getRequestedAllies().contains(team.getUniqueId())) {
            senderTeam.getRequestedAllies().remove(team.getUniqueId());

            team.createLog(sender.getUniqueId(), "NEW ALLIANCE", "/t ally " + team.getName());
            team.getAllies().add(senderTeam.getUniqueId());
            senderTeam.getAllies().add(team.getUniqueId());

            team.flagForSave();
            senderTeam.flagForSave();

            for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has accepted your request to ally. You now have " + Team.ALLY_COLOR + team.getAllies().size() + "/" + Samurai.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                } else if (senderTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has allied " + team.getName(sender) + ChatColor.YELLOW + ". You now have " + Team.ALLY_COLOR + senderTeam.getAllies().size() + "/" + Samurai.getInstance().getMapHandler().getAllyLimit() + " allies" + ChatColor.YELLOW + ".");
                }

                if (team.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                    FrozenNametagHandler.reloadPlayer(sender);
                    FrozenNametagHandler.reloadOthersFor(sender);
                }
            }
        } else {
            if (team.getRequestedAllies().contains(senderTeam.getUniqueId())) {
                sender.sendMessage(ChatColor.YELLOW + "You have already requested to ally " + team.getName(sender) + ChatColor.YELLOW + ".");
                return;
            }

            team.createLog(sender.getUniqueId(), "ALLY REQUEST", "/t ally " + team.getName());
            team.getRequestedAllies().add(senderTeam.getUniqueId());
            team.flagForSave();

            for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(senderTeam.getName(player.getPlayer()) + ChatColor.YELLOW + " has requested to be your ally. Type " + Team.ALLY_COLOR + "/team ally " + senderTeam.getName() + ChatColor.YELLOW + " to accept.");
                } else if (senderTeam.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + "Your team has requested to ally " + team.getName(player) + ChatColor.YELLOW + ".");
                }
            }
        }
    }

    @Subcommand("announcement|announcement")
    @Description("Announce a message to all teams.")
    public static void teamAnnouncement(Player sender, @Name("announcement") String newAnnouncement) {//TODO Check if string will work
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (newAnnouncement.equalsIgnoreCase("clear")) {
            team.setAnnouncement(null);
            sender.sendMessage(ChatColor.YELLOW + "Team announcement cleared.");
            return;
        }

        team.setAnnouncement(newAnnouncement);
        team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " changed the team announcement to " + ChatColor.LIGHT_PURPLE + newAnnouncement);
    }

    @Subcommand("captain add|mod add")
    @Description("Add a player to the mod team.")
    public static void captainAdd(Player sender, @Name("target") OfflinePlayer promote) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Only team co-leaders can execute this command.");
            return;
        }

        if (!team.isMember(promote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if (team.isOwner(promote.getUniqueId()) || team.isCaptain(promote.getUniqueId()) || team.isCoLeader(promote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player is already a captain (or above) of your team.");
            return;
        }

        team.createLog(sender.getUniqueId(), "PROMOTE (Captain)", "/t captain add " + promote.getName());
        team.removeCoLeader(promote.getUniqueId());
        team.addCaptain(promote.getUniqueId());
        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(promote.getUniqueId()) + " has been promoted to Captain!");
    }

    @Subcommand("giveteambooster")
    @Description("give a team booster")
    @CommandPermission("op")
    @CommandCompletion("@team @boosters")
    public static void giveteambooster(CommandSender sender, @Name("team") Team team, @Name("booster") TeamBoosterType booster, @Name("duration") String timeString) {
        long duration = JavaUtils.parse(timeString);

        if (duration <= 0) {
            sender.sendMessage(CC.translate("&cInvalid time!"));
            return;
        }

        team.rewardBooster(booster, System.currentTimeMillis() + duration);
        sender.sendMessage(CC.translate("&aBooster rewarded!"));
    }

    @Subcommand("roster")
    @Description("edit your roster")
    public static void roster(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Only team leaders can execute this command.");
            return;
        }

        new RosterMenu(team).openMenu(sender);
    }

    @Subcommand("claimbundle|claimteambundle")
    @Description("claim your team bundle")
    public static void claimBundle(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

        if (team == null) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Only team leaders can execute this command.");
            return;
        }

        if (!sender.hasPermission("foxtrot.admin")) {
            if (team.getOnlineMembers().size() < Samurai.getInstance().getMapHandler().getTeamSize()) {
                sender.sendMessage(CC.translate("&cYou need a full " + Samurai.getInstance().getMapHandler().getTeamSize() + " man to claim this."));
                return;
            }

            List<Player> players = team.getOnlineMembers().stream().filter(player -> !Samurai.getInstance().getTeamBundleMap().isActive(player.getUniqueId())).toList();

            if (players.size() < Samurai.getInstance().getMapHandler().getTeamSize()) {
                sender.sendMessage(CC.translate("&c" + players.size() + " have already claimed team bundles."));
                for (Player player : players) {
                    sender.sendMessage(CC.translate("&c- " + player.getName()));
                }
                return;
            }

            if (LandBoard.getInstance().getTeam(sender.getLocation()) == null || LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                sender.sendMessage(CC.translate("&cYou need to be in your team's claim to do this."));
                return;
            }

            players.forEach(player -> Samurai.getInstance().getTeamBundleMap().setActive(player.getUniqueId(), true));
        }

        VaultListener.paste("factionbundle", sender.getLocation());
        team.sendMessage(CC.translate("&6&lTEAM BUNDLE &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fYour team bundle has been claimed!"));
    }

    @Subcommand("top|leaderboards")
    @Description("view the team leaderboards.")
    public static void ftop(Player sender) {
        FTopHandler top = Samurai.getInstance().getTopHandler();
        ArrayList<Team> teams = new ArrayList<>(Samurai.getInstance().getTeamHandler().getTeams());
        teams.removeIf(team -> team.getOwner() == null);
        teams.sort(Collections.reverseOrder(Comparator.comparingInt(top::getTotalPoints)));

        sender.sendMessage(CC.translate("&8&m--------------------------------"));
        sender.sendMessage(CC.translate("&h» &g&lTop Factions &7(Points) &h«"));
        sender.sendMessage(" ");
        for (int i = 0; i < teams.size(); i++) {
            if (i > 4) break;
            Team team = teams.get(i);
            FancyMessage fancyMessage = new FancyMessage(CC.translate("&h" + (i + 1) + ") &7" + team.getName() + "&f: " + top.getTotalPoints(team)));
            fancyMessage.tooltip(
                    CC.translate(Arrays.asList(
                            "&g&l" + team.getName(sender),
                            " ",
                            "&gLeader: &7" + FrozenUUIDCache.name(team.getOwner()),
                            "&gKills: &7" + team.getKills(),
                            "&gDeaths: &7" + team.getDeaths(),
                            " ",
                            "&gCitadel Captures: &7" + team.getCitadelsCapped(),
                            "&gConquest Captures: &7" + team.getConquestCapped(),
                            "&gKOTH Captures: &7" + team.getKothCaptures(),
                            "&gTotal Points: &7" + Samurai.getInstance().getTopHandler().getTotalPoints(team),
                            " ",
                            "&aClick to view " + team.getName() + "'s team info"
                    ))
            );

            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                fancyMessage.command("/f who " + team.getName());
                fancyMessage.send(sender);
            });
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage(CC.translate("&8&m--------------------------------"));
            }
        }.runTask(Samurai.getInstance());
    }

    @Subcommand("top raidable|leaderboards raidable")
    @Description("view the team top raidable leaderboards.")
    public static void ftopRaidable(Player sender) {
        FTopHandler top = Samurai.getInstance().getTopHandler();
        ArrayList<Team> teams = new ArrayList<>(Samurai.getInstance().getTeamHandler().getTeams());
        teams.removeIf(team -> team.getOwner() == null);
        teams.sort(Collections.reverseOrder(Comparator.comparingInt(Team::getRaidableTeams)));

        sender.sendMessage(CC.translate("&8&m--------------------------------"));
        sender.sendMessage(CC.translate("&h» &g&lTop Factions &7(Raidable) &h«"));
        sender.sendMessage(" ");
        for (int i = 0; i < teams.size(); i++) {
            if (i > 4) break;
            Team team = teams.get(i);
            FancyMessage fancyMessage = new FancyMessage(CC.translate("&h" + (i + 1) + ") &7" + team.getName() + "&f: " + team.getRaidableTeams()));
            fancyMessage.tooltip(
                    CC.translate(Arrays.asList(
                            "&g&l" + team.getName(sender),
                            " ",
                            "&aClick to view " + team.getName() + "'s team info"
                    ))
            );

            Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                fancyMessage.command("/f who " + team.getName());
                fancyMessage.send(sender);
            });
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage(CC.translate("&8&m--------------------------------"));
            }
        }.runTask(Samurai.getInstance());
    }

    @Subcommand("captain remove|captain demote| mod remove|mod demote")
    @Description("Demote a player to a member.")
    public static void captainRemove(Player sender, @Name("target") OfflinePlayer demote) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if (team == null) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "Only team co-leaders can execute this command.");
            return;
        }

        if (!team.isMember(demote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if (!team.isCaptain(demote.getUniqueId())) {
            sender.sendMessage(net.md_5.bungee.api.ChatColor.RED + "This player is not a captain of your team.");
            return;
        }

        team.createLog(sender.getUniqueId(), "DEMOTE (Member)", "/t captain remove " + demote.getName());
        team.removeCoLeader(demote.getUniqueId());
        team.removeCaptain(demote.getUniqueId());
        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(demote.getUniqueId()) + " has been demoted to a member!");
    }

    @Subcommand("addtofac")
    @CommandCompletion("@team")
    @CommandPermission("op")
    public static void addToFac(CommandSender sender, @Name("player") String name, @Name("team") Team team) {
        UUID uuid = Flash.getInstance().getCacheHandler().getUserCache().getUUID(name);

        if (uuid == null) {
            sender.sendMessage(CC.translate("&cThat name is not cached."));
            return;
        }

        if (team.getMembers().contains(uuid)) {
            sender.sendMessage(CC.translate("&cThat player is already in that team!"));
            return;
        }

        if (team.getOwner() == null) {
            team.setOwner(uuid);
        } else {
            team.addMember(uuid);
        }
        Samurai.getInstance().getTeamHandler().setTeam(uuid, team);

        sender.sendMessage(CC.translate("&aAdded " + name + " to the " + team.getName() + " team."));
    }

    @Subcommand("chat|c")
    @Description("Toggle team chat.")
    public static void teamChat(Player sender, @Name("mode") @Optional String chatMode) {
        ChatMode parsedChatMode = null;

        if (chatMode == null) {
            sender.sendMessage(CC.translate("&cPlease a chat mode to switch to."));
            return;
        }

        if (chatMode.equalsIgnoreCase("t") || chatMode.equalsIgnoreCase("team") || chatMode.equalsIgnoreCase("f") || chatMode.equalsIgnoreCase("fac") || chatMode.equalsIgnoreCase("faction") || chatMode.equalsIgnoreCase("fc")) {
            parsedChatMode = ChatMode.TEAM;
        } else if (chatMode.equalsIgnoreCase("g") || chatMode.equalsIgnoreCase("p") || chatMode.equalsIgnoreCase("global") || chatMode.equalsIgnoreCase("public") || chatMode.equalsIgnoreCase("gc")) {
            parsedChatMode = ChatMode.PUBLIC;
        } else if (chatMode.equalsIgnoreCase("a") || chatMode.equalsIgnoreCase("allies") || chatMode.equalsIgnoreCase("ally") || chatMode.equalsIgnoreCase("alliance") || chatMode.equalsIgnoreCase("ac")) {
            parsedChatMode = ChatMode.ALLIANCE;
        } else if (chatMode.equalsIgnoreCase("captain") || chatMode.equalsIgnoreCase("officer") || chatMode.equalsIgnoreCase("o") || chatMode.equalsIgnoreCase("c") || chatMode.equalsIgnoreCase("oc")) {
            parsedChatMode = ChatMode.OFFICER;
        } else {
            parsedChatMode = ChatMode.TEAM;
        }

        setChat(sender, parsedChatMode);
    }


    private static void setChat(Player player, ChatMode chatMode) {
        if (chatMode != null) {
            Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player);

            if (chatMode != ChatMode.PUBLIC) {
                if (playerTeam == null) {
                    player.sendMessage(ChatColor.RED + "You must be on a team to use this chat mode.");
                    return;
                } else if (chatMode == ChatMode.OFFICER && !playerTeam.isCaptain(player.getUniqueId()) && !playerTeam.isCoLeader(player.getUniqueId()) && !playerTeam.isOwner(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You must be an officer or above in your team to use this chat mode.");
                    return;
                }
            }

            switch (chatMode) {
                case PUBLIC:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in public chat.");
                    break;
                case ALLIANCE:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in alliance chat.");
                    break;
                case TEAM:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in team chat.");
                    break;
                case OFFICER:
                    player.sendMessage(ChatColor.DARK_AQUA + "You are now in officer chat.");
                    break;
            }

            Samurai.getInstance().getChatModeMap().setChatMode(player.getUniqueId(), chatMode);
        } else {
            switch (Samurai.getInstance().getChatModeMap().getChatMode(player.getUniqueId())) {
                case PUBLIC -> {
                    Team team = Samurai.getInstance().getTeamHandler().getTeam(player);
                    boolean teamHasAllies = team != null && team.getAllies().size() > 0;
                    setChat(player, teamHasAllies ? ChatMode.ALLIANCE : ChatMode.TEAM);
                }
                case ALLIANCE -> setChat(player, ChatMode.TEAM);
                case TEAM -> {
                    Team team2 = Samurai.getInstance().getTeamHandler().getTeam(player);
                    boolean isOfficer = team2 != null && (team2.isCaptain(player.getUniqueId()) || team2.isCoLeader(player.getUniqueId()) || team2.isOwner(player.getUniqueId()));
                    setChat(player, isOfficer ? ChatMode.OFFICER : ChatMode.PUBLIC);
                }
                case OFFICER -> setChat(player, ChatMode.PUBLIC);
            }
        }
    }

    @Subcommand("create")
    @Description("Create a team")
    public static void teamCreate(Player sender, @Name("name") String team) {
        if (Samurai.getInstance().getTeamHandler().getTeam(sender) != null) {
            sender.sendMessage(ChatColor.GRAY + "You're already in a team!");
            return;
        }

        if (team.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
            return;
        }

        if (team.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
            return;
        }

        if (Samurai.getInstance().getTeamHandler().getTeam(team) != null) {
            sender.sendMessage(ChatColor.GRAY + "That team already exists!");
            return;
        }

        if (disallowedTeamNames.contains(team.toLowerCase())) {
            sender.sendMessage(CC.translate("&cThat team name is not allowed."));
            return;
        }

        if (ALPHA_NUMERIC.matcher(team).find()) {
            sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
            return;
        }

        if (EOTWCommand.realFFAStarted()) {
            sender.sendMessage(ChatColor.RED + "You can't create teams during FFA.");
            return;
        }
        //No longer needed cause we don't disband factions
        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You can't create teams during EOTW.");
            return;
        }
        // sender.sendMessage(ChatColor.DARK_AQUA + "Team Created!");
        sender.sendMessage(ChatColor.GRAY + "To learn more about teams, do /team");

        Team createdTeam = new Team(team);

        createdTeam.setUniqueId(new ObjectId());
        createdTeam.setOwner(sender.getUniqueId());
        createdTeam.setName(team);
        createdTeam.setDTR(1);

        Samurai.getInstance().getTeamHandler().setupTeam(createdTeam);
        createdTeam.createLog(sender.getUniqueId(), "CREATED", "/t create " + team);

        sender.sendMessage(ChatColor.YELLOW + "Team " + ChatColor.BLUE + createdTeam.getName() + ChatColor.YELLOW + " has been " + ChatColor.GREEN + "created" + ChatColor.YELLOW + " by " + sender.getDisplayName());
    }

    @Subcommand("demote")
    @Description("Demote a player from team.")
    public static void teamDemote(Player sender, @Name("target") OfflinePlayer player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " is not on your team.");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is the leader. To change leaders, the team leader must use /t leader <name>");
        } else if (team.isCoLeader(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                team.removeCoLeader(player.getUniqueId());
                team.addCaptain(player.getUniqueId());
                team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " has been demoted to Captain!");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can demote Co-Leaders.");
            }
        } else if (team.isCaptain(player.getUniqueId())) {
            team.removeCaptain(player.getUniqueId());
            team.createLog(sender.getUniqueId(), "DEMOTED", "/t demote " + player.getName());
            team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " has been demoted to a Member!");
        } else {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " is currently a member. To kick them, use /t kick");
        }
    }

    @Subcommand("deposit|d")
    @Description("Deposit money to your team.")
    public static void teamDeposit(Player sender, @Name("amount") String ogAmount) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);
        float amount = (ogAmount.equals("all")) ? (float) FrozenEconomyHandler.getBalance(sender.getUniqueId())
                : Float.parseFloat(ogAmount);


        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (amount <= 0) {
            sender.sendMessage(ChatColor.RED + "You can't deposit $0.0 (or less)!");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage(ChatColor.RED + "Nope.");
            return;
        }

        if (FrozenEconomyHandler.getBalance(sender.getUniqueId()) < amount) {
            sender.sendMessage(ChatColor.RED + "You don't have enough money to do this!");
            return;
        }

        FrozenEconomyHandler.withdraw(sender.getUniqueId(), amount);

        sender.sendMessage(ChatColor.YELLOW + "You have added " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " to the team balance!");

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_DEPOSIT_MONEY, ImmutableMap.of(
                "playerId", sender.getUniqueId(),
                "playerName", sender.getName(),
                "amount", amount,
                "oldBalance", team.getBalance(),
                "newBalance", team.getBalance() + amount
        ));

        team.createLog(sender.getUniqueId(), "DEPOSITED", "/t deposit " + amount);
        team.setBalance(team.getBalance() + amount);
        team.sendMessage(ChatColor.YELLOW + sender.getName() + " deposited " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " into the team balance.");

        Samurai.getInstance().getWrappedBalanceMap().setBalance(sender.getUniqueId(), FrozenEconomyHandler.getBalance(sender.getUniqueId()));
    }

    @Subcommand("disband")
    @Description("Disband your team.")
    public static void teamDisband(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of the team to disband it!");
            return;
        }

        if (team.isRaidable()) {
            player.sendMessage(ChatColor.RED + "You cannot disband your team while raidable.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            team.getFocusedTeam().getName() + "'s HQ",
                            team.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }

        if (team.getTeamRally() != null) {
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                            "Team Rally",
                            team.getTeamRally(),
                            Color.AQUA.hashCode(),
                            true
                    )));
        }

        if (team.getHQ() != null) {
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(player.getUniqueId())).forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            )));
        }

        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + player.getName() + " has disbanded the team.");

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_DISBAND_TEAM, ImmutableMap.of(
                "playerId", player.getUniqueId(),
                "playerName", player.getName()
        ));

        team.disband();
    }

    @Subcommand("focus")
    @Description("Focus on a team.")
    @CommandCompletion("@team")
    public static void focus(Player player, @Name("team") @Optional Team team) {
        Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (playerTeam == null) {
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team == null || team == playerTeam) {

            AbilityProfile profile = AbilityProfile.byUUID(player.getUniqueId());

            if (profile.getLastHitName() == null) {
                player.sendMessage(CC.translate("&cNo one has hit you last."));
                return;
            }

            Team target = Samurai.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(profile.getLastHitName()));

            if (target == null) {
                player.sendMessage(CC.translate("&7That person is not on a team!"));
                return;
            }

            if (playerTeam.getUniqueId() == target.getUniqueId()) {
                player.sendMessage(CC.translate("&cYou can't focus your own team!"));
                return;
            }

            //Removing the current waypoint.
            if (playerTeam.getFocusedTeam() != null && playerTeam.getFocusedTeam().getHQ() != null) {
                Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                        .forEach(all -> {
                            LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                                    playerTeam.getFocusedTeam().getName() + "'s HQ",
                                    playerTeam.getFocusedTeam().getHQ(),
                                    Color.BLUE.hashCode(),
                                    true
                            ));

                            for (Player onlineMember : playerTeam.getOnlineMembers()) {
                                FrozenNametagHandler.reloadPlayer(all, onlineMember);
                            }
                        });
            }

            playerTeam.setFocusedTeam(target);
            playerTeam.sendMessage(CC.translate("&d" + target.getName() + " &efaction has been focused by &d" + player.getName() + "&e."));

            // Adding the new focused team's waypoint.

            if (playerTeam.getFocusedTeam() != null && playerTeam.getFocusedTeam().getHQ() != null) {
                Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                        .forEach(all -> {
                            LunarClientAPI.getInstance().sendWaypoint(all, new LCWaypoint(
                                    playerTeam.getFocusedTeam().getName() + "'s HQ",
                                    playerTeam.getFocusedTeam().getHQ(),
                                    Color.BLUE.hashCode(),
                                    true
                            ));

                            for (Player onlineMember : playerTeam.getOnlineMembers()) {
                                FrozenNametagHandler.reloadPlayer(all, onlineMember);
                            }
                        });
            }

            return;
        }

        //Removing the current waypoint.
        if (playerTeam.getFocusedTeam() != null && playerTeam.getFocusedTeam().getHQ() != null) {
            Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                    .forEach(all -> {
                        LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                                playerTeam.getFocusedTeam().getName() + "'s HQ",
                                playerTeam.getFocusedTeam().getHQ(),
                                Color.BLUE.hashCode(),
                                true
                        ));

                        for (Player onlineMember : playerTeam.getOnlineMembers()) {
                            FrozenNametagHandler.reloadPlayer(all, onlineMember);
                        }
                    });
        }

        playerTeam.setFocusedTeam(team);
        playerTeam.sendMessage(CC.translate("&d" + team.getName() + " &efaction has been focused by &d" + player.getName() + "&e."));

        // Adding the new focused team's waypoint.

        if (playerTeam.getFocusedTeam() != null && playerTeam.getFocusedTeam().getHQ() != null) {
            Bukkit.getOnlinePlayers().stream().filter(all -> playerTeam.isMember(all.getUniqueId()))
                    .forEach(all -> {
                        LunarClientAPI.getInstance().sendWaypoint(all, new LCWaypoint(
                                playerTeam.getFocusedTeam().getName() + "'s HQ",
                                playerTeam.getFocusedTeam().getHQ(),
                                Color.BLUE.hashCode(),
                                true
                        ));

                        for (Player onlineMember : playerTeam.getOnlineMembers()) {
                            FrozenNametagHandler.reloadPlayer(all, onlineMember);
                        }
                    });
        }
    }

    @Subcommand("unfocus")
    @Description("Unfocus a team.")
    public static void unfocus(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getFocusedTeam() == null) {
            player.sendMessage(CC.translate("&cYou aren't currently focusing anyone!"));
            return;
        }

        if (team.getFocusedTeam().getHQ() != null) {
            Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                    .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(all, new LCWaypoint(
                            team.getFocusedTeam().getName() + "'s HQ",
                            team.getFocusedTeam().getHQ(),
                            Color.BLUE.hashCode(),
                            true
                    )));
        }


        team.sendMessage(CC.translate("&d" + team.getFocusedTeam().getName() + " &efaction has been unfocused by &d" + player.getName() + "&e."));
        team.setFocusedTeam(null);
    }

    @Subcommand("forceinvite")
    @Description("Force a player to join your team.")
    public static void teamForceInvite(Player sender, @Name("target") OfflinePlayer player) {
        if (!Samurai.getInstance().getServerHandler().isForceInvitesEnabled()) {
            sender.sendMessage(ChatColor.RED + "Force-invites are not enabled on this server.");
            return;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (Samurai.getInstance().getMapHandler().isKitMap() || Samurai.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "You don't need to use this during kit maps.");
            return;
        }

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Samurai.getInstance().getMapHandler().getTeamSize()) {
            sender.sendMessage(ChatColor.RED + "The max team size is " + Samurai.getInstance().getMapHandler().getTeamSize() + "!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " is already on your team.");
            return;
        }

        if (team.getInvitations().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        if (!team.getHistoricalMembers().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has never been a member of your faction. Please use /f invite.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        if (team.getForceInvites() == 0) {
            sender.sendMessage(ChatColor.RED + "You do not have any force-invites left!");
            return;
        }

        team.setForceInvites(team.getForceInvites() - 1);
        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_SENT, ImmutableMap.of(
                "playerId", player,
                "invitedById", sender.getUniqueId(),
                "invitedByName", sender.getName(),
                "betrayOverride", "false",
                "usedForceInvite", "true"
        ));

        // we use a runnable so this message gets displayed at the end
        new BukkitRunnable() {
            @Override
            public void run() {
                sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "You have used a force-invite.");

                if (team.getForceInvites() != 0) {
                    sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + team.getForceInvites() + ChatColor.YELLOW + " of those left.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You have " + ChatColor.RED + "none" + ChatColor.YELLOW + " of those left.");
                }
            }
        }.runTask(Samurai.getInstance());

        team.getInvitations().add(player.getUniqueId());
        team.flagForSave();

        Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(player.getUniqueId());

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");

            FancyMessage clickToJoin = new FancyMessage("Type '").color(ChatColor.DARK_AQUA).then("/team join " + team.getName()).color(ChatColor.YELLOW);
            clickToJoin.then("' or ").color(ChatColor.DARK_AQUA);
            clickToJoin.then("click here").color(ChatColor.AQUA).command("/team join " + team.getName()).tooltip("§aJoin " + team.getName());
            clickToJoin.then(" to join.").color(ChatColor.DARK_AQUA);

            clickToJoin.send(bukkitPlayer);
        }

        team.createLog(sender.getUniqueId(), "FORCE INVITE", "/t forceinvite " + player.getName());
        team.sendMessage(ChatColor.YELLOW + UUIDUtils.name(player.getUniqueId()) + " has been invited to the team!");
    }

    @Subcommand("forcekick")
    @Description("Forcefully kicks a member from your faction.")
    public static void teamForceKick(Player sender, @Name("player") OfflinePlayer player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " isn't on your team!");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if (team.isCoLeader(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (team.isCaptain(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(player.getUniqueId());


        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null && bukkitPlayer != null) {
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null && bukkitPlayer != null) {
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null && bukkitPlayer != null) {
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "true"
        ));

        if (team.removeMember(player.getUniqueId())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Samurai.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        if (SpawnTagHandler.isTagged(bukkitPlayer)) {
            team.setDTR(team.getDTR() - 1);
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " was force kicked by " + sender.getName() + " and your team lost 1 DTR!");
            long dtrCooldown;
            if (team.isRaidable()) {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_NOW_RAIDABLE, ImmutableMap.of());
                dtrCooldown = System.currentTimeMillis() + Samurai.getInstance().getMapHandler().getRegenTimeRaidable();
            } else {
                dtrCooldown = System.currentTimeMillis() + Samurai.getInstance().getMapHandler().getRegenTimeDeath();
            }

            team.createLog(sender.getUniqueId(), "FORCE KICK", "/t forcekick " + player.getName());
            team.setDTRCooldown(dtrCooldown);
            DTRHandler.markOnDTRCooldown(team);
        } else {
            team.sendMessage(ChatColor.RED + UUIDUtils.name(player.getUniqueId()) + " was force kicked by " + sender.getName() + "!");
        }

        if (bukkitPlayer != null) {
            FrozenNametagHandler.reloadPlayer(bukkitPlayer);
            FrozenNametagHandler.reloadOthersFor(bukkitPlayer);
        }
    }

    @Subcommand("forceleave")
    public static void forceLeave(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null) {
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        team.createLog(sender.getUniqueId(), "FORCE LEFT", "/t forceleave");

        if (team.removeMember(sender.getUniqueId())) {
            team.createLog(sender.getUniqueId(), "DISBANDED DUE TO FORCE LEAVE", "/t forceleave");

            team.disband();
            Samurai.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            Samurai.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();

            if (SpawnTagHandler.isTagged(sender)) {
                team.setDTR(team.getDTR() - 1);
                team.sendMessage(ChatColor.RED + sender.getName() + " forcibly left the team. Your team has lost 1 DTR.");

                sender.sendMessage(ChatColor.RED + "You have forcibly left your team. Your team lost 1 DTR.");
            } else {
                team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");

                sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
            }
        }

        FrozenNametagHandler.reloadPlayer(sender);
        FrozenNametagHandler.reloadOthersFor(sender);
    }

    @Subcommand("hq|home")
    @Description("Teleport to your team's HQ")
    public static void teamHQ(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.DARK_AQUA + "You are not on a team!");
            return;
        }

        if (team.getHQ() == null) {
            sender.sendMessage(ChatColor.RED + "HQ not set.");
            return;
        }

        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters during the End of the World!");
            return;
        }

        if (sender.hasMetadata("frozen")) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to your team headquarters while you're frozen!");
            return;
        }

        if (Samurai.getInstance().getInDuelPredicate().test(sender)) {
            sender.sendMessage(ChatColor.RED + "You cannot teleport to HQ during a duel!");
            return;
        }

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Use /pvp enable to toggle your PvP Timer off!");
            return;
        }

        Samurai.getInstance().getServerHandler().beginHQWarp(sender, team, SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(sender.getUniqueId()) ? 0 : 10, false);
    }

    @Subcommand("who|info|show|i")
    @Description("Show info about your team")
    @CommandCompletion("@team")
    public static void teamInfo(final Player sender, @Name("team|player") @Optional Team team) {

        if (team == null) {
            if (Samurai.getInstance().getTeamHandler().getTeam(sender) == null) {
                sender.sendMessage(CC.translate("&7You are not on a team!"));
                return;
            }
            team = Samurai.getInstance().getTeamHandler().getTeam(sender);
        }
        Team finalTeam = team;
        new BukkitRunnable() {
            public void run() {
                Team exactPlayerTeam = Samurai.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(finalTeam.getName()));

                if (exactPlayerTeam != null && exactPlayerTeam != finalTeam) {
                    exactPlayerTeam.sendTeamInfo(sender);
                }

                finalTeam.sendTeamInfo(sender);
            }
        }.runTask(Samurai.getInstance());
    }

    @Subcommand("invite|inv")
    @Description("Invite a player to your team")
    @CommandCompletion("@uuid")
    public static void teamInvite(Player sender, @Flags("other") UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            sender.sendMessage(CC.translate("&cNo one is online under that name."));
            return;
        }
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.getMembers().size() >= Samurai.getInstance().getMapHandler().getTeamSize()) {
            FullTeamBypassEvent bypassEvent = new FullTeamBypassEvent(sender, team);
            Samurai.getInstance().getServer().getPluginManager().callEvent(bypassEvent);

            if (!bypassEvent.isAllowBypass()) {
                sender.sendMessage(ChatColor.RED + "The max team size is " + Samurai.getInstance().getMapHandler().getTeamSize() + (bypassEvent.getExtraSlots() == 0 ? "" : " (+" + bypassEvent.getExtraSlots() + ")") + "!");
                return;
            }
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + player.getName() + " is already on your team.");
            return;
        }

        if (team.getInvitations().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "That player has already been invited.");
            return;
        }

        /*if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not invite players while your team is raidable!");
            return;
        }*/

        if (Samurai.getInstance().getServerHandler().isForceInvitesEnabled() && !Samurai.getInstance().getServerHandler().isPreEOTW()) {
            /* if we just check team.getSize() players can make a team with 10 players,
            send out 20 invites, and then have them all accepted (instead of 1 invite,
            1 join, 1 invite, etc) To solve this we treat their size as their actual
            size + number of open invites. */
            int possibleTeamSize = team.getSize() + team.getInvitations().size();

            if (!Samurai.getInstance().getMapHandler().isKitMap() && !Samurai.getInstance().getMapHandler().isKitMap() && team.getHistoricalMembers().contains(player.getUniqueId()) && possibleTeamSize > Samurai.getInstance().getMapHandler().getMinForceInviteMembers()) {
                sender.sendMessage(ChatColor.RED + "This player has previously joined your faction. You must use a force-invite to re-invite " + player.getName() + ". Type "
                        + ChatColor.YELLOW + "'/f forceinvite " + player.getName() + "'" + ChatColor.RED + " to use a force-invite."
                );

                return;
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_SENT, ImmutableMap.of(
                "playerId", player,
                "invitedById", sender.getUniqueId(),
                "invitedByName", sender.getName(),
                "betrayOverride", "something other then yeah",
                "usedForceInvite", "false"
        ));

        team.createLog(sender.getUniqueId(), "INVITE", "/t invite " + player.getName());
        team.getInvitations().add(player.getUniqueId());
        team.flagForSave();

        Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(player.getUniqueId());

        if (bukkitPlayer != null) {
            bukkitPlayer.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " invited you to join '" + ChatColor.YELLOW + team.getName() + ChatColor.DARK_AQUA + "'.");

            ComponentBuilder clickToJoin = new ComponentBuilder("Type '").color(ChatColor.DARK_AQUA.asBungee())
                    .append("/team join " + team.getName()).color(ChatColor.YELLOW.asBungee());
            clickToJoin.append("' or ").color(ChatColor.DARK_AQUA.asBungee());
            clickToJoin.append("click here").color(ChatColor.AQUA.asBungee())
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f join " + team.getName()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§aJoin " + team.getName()).create()));
            clickToJoin.append(" to join.").color(ChatColor.DARK_AQUA.asBungee());

            bukkitPlayer.spigot().sendMessage(clickToJoin.create());
        }

        team.sendMessage(ChatColor.YELLOW + player.getName() + " has been invited to the team!");
    }


    @Subcommand("invites")
    @Description("View your team's pending invites.")
    public static void teamInvites(Player sender) {
        StringBuilder yourInvites = new StringBuilder();

        for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
            if (team.getInvitations().contains(sender.getUniqueId())) {
                yourInvites.append(ChatColor.GRAY).append(team.getName()).append(ChatColor.YELLOW).append(", ");
            }
        }

        if (yourInvites.length() > 2) {
            yourInvites.setLength(yourInvites.length() - 2);
        } else {
            yourInvites.append(ChatColor.GRAY).append("No pending invites.");
        }

        sender.sendMessage(ChatColor.YELLOW + "Your Invites: " + yourInvites);

        Team current = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (current != null) {
            StringBuilder invitedToYourTeam = new StringBuilder();

            for (UUID invited : current.getInvitations()) {
                invitedToYourTeam.append(ChatColor.GRAY).append(UUIDUtils.name(invited)).append(ChatColor.YELLOW).append(", ");
            }

            if (invitedToYourTeam.length() > 2) {
                invitedToYourTeam.setLength(invitedToYourTeam.length() - 2);
            } else {
                invitedToYourTeam.append(ChatColor.GRAY).append("No pending invites.");
            }

            sender.sendMessage(ChatColor.YELLOW + "Invited to your Team: " + invitedToYourTeam.toString());
        }
    }

    @Subcommand("json")
    @CommandPermission("op")
    @Description("View the JSON for a team.")
    @CommandCompletion("@team")
    public static void teamJSON(CommandSender sender, @Optional Team team) {
        sender.sendMessage(team.toJSON().toString());
    }

    @Subcommand("kick")
    @Description("Kick a player from your team.")
    public static void teamKick(Player sender, @Name("player") OfflinePlayer player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " isn't on your team!");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "You cannot kick the team leader!");
            return;
        }

        if (team.isCoLeader(player.getUniqueId()) && (!team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.RED + "Only the owner can kick other co-leaders!");
            return;
        }

        if (team.isCaptain(player.getUniqueId()) && !team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only an owner or co-leader can kick other captains!");
            return;
        }

        Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(player.getUniqueId());

        if (SpawnTagHandler.isTagged(bukkitPlayer)) {
            sender.sendMessage(ChatColor.RED + bukkitPlayer.getName() + " is currently combat-tagged! You can forcibly kick " + bukkitPlayer.getName() + " by using '"
                    + ChatColor.YELLOW + "/f forcekick " + bukkitPlayer.getName() + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        if (team.getFocusedTeam() != null) {
            if (team.getFocusedTeam().getHQ() != null && bukkitPlayer != null) {
                LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                        team.getFocusedTeam().getName() + "'s HQ",
                        team.getFocusedTeam().getHQ(),
                        Color.BLUE.hashCode(),
                        true
                ));
            }
        }

        if (team.getTeamRally() != null && bukkitPlayer != null) {
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null && bukkitPlayer != null) {
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        team.sendMessage(ChatColor.DARK_AQUA + UUIDUtils.name(player.getUniqueId()) + " was kicked by " + sender.getName() + "!");

        TeamActionTracker.logActionAsync(team, TeamActionType.MEMBER_KICKED, ImmutableMap.of(
                "playerId", player,
                "kickedById", sender.getUniqueId(),
                "kickedByName", sender.getName(),
                "usedForceKick", "false"
        ));

        team.createLog(sender.getUniqueId(), "KICK", "/t kick " + player.getName());
        if (team.removeMember(player.getUniqueId())) {
            team.disband();
        } else {
            team.flagForSave();
        }

        Samurai.getInstance().getTeamHandler().setTeam(player.getUniqueId(), null);

        if (player.isOnline() && team.getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(bukkitPlayer, new LCWaypoint("HQ", team.getHQ(), 0, true));
        }
    }

    @Subcommand("newleader|leader")
    @Description("Set a new team leader")
    public static void teamLeader(Player sender, @Flags("other") @Name("target") Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only the team leader can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is not on your team.");
            return;
        }

        team.createLog(sender.getUniqueId(), "LEADERSHIP CHANGE", "/t leader " + player.getName());
        team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been given ownership of " + team.getName() + ".");
        team.setOwner(player.getUniqueId());
        team.addCaptain(sender.getUniqueId());
    }

    @Subcommand("leave")
    @Description("Leave your current team")
    public static void teamLeave(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) && team.getSize() > 1) {
            sender.sendMessage(ChatColor.RED + "Please choose a new leader before leaving your team!");
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == team) {
            sender.sendMessage(ChatColor.RED + "You cannot leave your team while on team territory.");
            return;
        }

        if (SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(ChatColor.RED + "You are combat-tagged! You can only leave your faction by using '" + ChatColor.YELLOW + "/f forceleave" + ChatColor.RED + "' which will cost your team 1 DTR.");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    team.getFocusedTeam().getName() + "'s HQ",
                    team.getFocusedTeam().getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        if (team.getTeamRally() != null) {
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.AQUA.hashCode(),
                    true
            ));
        }

        if (team.getHQ() != null) {
            LunarClientAPI.getInstance().removeWaypoint(sender, new LCWaypoint(
                    "HQ",
                    team.getHQ(),
                    Color.BLUE.hashCode(),
                    true
            ));
        }

        team.createLog(sender.getUniqueId(), "LEFT", "/t leave");

        if (team.removeMember(sender.getUniqueId())) {
            team.createLog(sender.getUniqueId(), "DISBANDED DUE TO LEAVE", "none");

            team.disband();
            Samurai.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left and disbanded team!");
        } else {
            Samurai.getInstance().getTeamHandler().setTeam(sender.getUniqueId(), null);
            team.flagForSave();
            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has left the team.");

            sender.sendMessage(ChatColor.DARK_AQUA + "Successfully left the team!");
        }

        FrozenNametagHandler.reloadPlayer(sender);
        FrozenNametagHandler.reloadOthersFor(sender);
    }

    @Subcommand("filter")
    @Description("toggle different list filter modes")
    public static void filter(final Player sender) {
        new FilterMenu().openMenu(sender);
    }

    @Subcommand("list")
    @Description("List all teams")
    public static void teamList(Player sender, @Name("page") @Optional Integer page) {
        int finalPage2;
        finalPage2 = Objects.requireNonNullElse(page, 1);

        Integer finalPage = finalPage2;
        new BukkitRunnable() {

            public void run() {
                if (finalPage < 1) {
                    sender.sendMessage(ChatColor.RED + "You cannot view a page less than 1");
                    return;
                }

                Map<Team, Integer> teamPlayerCount = new HashMap<>();

                // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
                for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (player.hasMetadata("invisible")) {
                        continue;
                    }

                    Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player);

                    if (playerTeam != null) {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                            teamPlayerCount.put(playerTeam, 1);
                        }
                    }
                }

                for (SpoofHandler.SpoofPlayer player : Flash.getInstance().getSpoofHandler().getSpoofPlayers().values()) {

                    Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(player.getUuid());

                    if (playerTeam != null) {
                        if (teamPlayerCount.containsKey(playerTeam)) {
                            teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                        } else {
                            teamPlayerCount.put(playerTeam, 1);
                        }
                    }
                }

                int maxPages = (teamPlayerCount.size() / 10) + 1;
                int currentPage = Math.min(finalPage, maxPages);

                LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(sender, teamPlayerCount);

                int start = (currentPage - 1) * 10;
                int index = 0;

                sender.sendMessage(Team.GRAY_LINE);
                sender.sendMessage(CC.GOLD + CC.BOLD + "Team List " + CC.GRAY + "(Page " + currentPage + "/" + maxPages + ")");

                for (Map.Entry<Team, Integer> teamEntry : sortedTeamPlayerCount.entrySet()) {
                    index++;

                    if (index < start) {
                        continue;
                    }

                    if (index > start + 10) {
                        break;
                    }

                    ComponentBuilder teamMessage = new ComponentBuilder("");

                    teamMessage.append(CC.UNICODE_ARROWS_RIGHT + " ")
                            .color(ChatColor.GOLD.asBungee())
                            .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + "Click to view team info").create()));
                    teamMessage.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f who " + teamEntry.getKey().getName()));
                    teamMessage.append(CC.translate(
                            teamEntry.getKey().getName(sender)
                                    + " &7(" + teamEntry.getValue() + "/" + teamEntry.getKey().getSize() + ")"
                                    + " &7- "
                                    + teamEntry.getKey().getDTRColor() + teamEntry.getKey().getDTR() + "/" + teamEntry.getKey().getMaxDTR()));

                    sender.spigot().sendMessage(teamMessage.create());
                }

                sender.sendMessage(CC.translate(" "));
                sender.sendMessage(CC.translate("&7You can do /f list <page> - You're currently on page #" + finalPage2 + "."));
                sender.sendMessage(Team.GRAY_LINE);
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

    public static LinkedHashMap<Team, Integer> sortByValues(Player player, Map<Team, Integer> map) {
        LinkedList<Map.Entry<Team, Integer>> list = new LinkedList<>(map.entrySet());
        TeamFilter teamFilter = Samurai.getInstance().getFilterModeMap().getFilter(player.getUniqueId());

        teamFilter.callback.callback(list);

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Integer> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

    @Subcommand("map")
    @Description("View the map")
    public static void teamMap(Player sender) {
        (new VisualClaim(sender, VisualClaimType.MAP, false)).draw(false);
    }

    @Subcommand("mute")
    @Description("Mute a player")
    @CommandPermission("foxtrot.mute")
    public static void teamMute(Player sender, @Name("team") final Team team, @Name("time") int time, @Name("reason") String reason) {
        int timeSeconds = time * 60;

        for (UUID player : team.getMembers()) {
            teamMutes.put(player, team.getName());

            Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(player);

            if (bukkitPlayer != null) {
                bukkitPlayer.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team has been muted for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
            }
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "false",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        team.createLog(sender.getUniqueId(), "MUTE", "/t mute " + team.getName());

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "false"
                ));

                Iterator<Map.Entry<UUID, String>> mutesIterator = teamMutes.entrySet().iterator();

                while (mutesIterator.hasNext()) {
                    Map.Entry<UUID, String> mute = mutesIterator.next();

                    if (mute.getValue().equalsIgnoreCase(team.getName())) {
                        mutesIterator.remove();
                    }
                }
            }

        }.runTaskLater(Samurai.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + " for " + reason + ".");
    }

    @Subcommand("nullleader")
    @Description("Nullify the leader of a team")
    @CommandPermission("foxtrot.nullleader")
    public static void teamNullLeader(Player sender) {
        int nullLeaders = 0;

        for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() == null) {
                nullLeaders++;
                sender.sendMessage(ChatColor.RED + "- " + team.getName());
            }
        }

        if (nullLeaders == 0) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "No null teams found.");
        } else {
            sender.sendMessage(ChatColor.DARK_PURPLE.toString() + nullLeaders + " null teams found.");
        }
    }

    @Subcommand("opclaim")
    @Description("Opclaim a team")
    @CommandPermission("foxtrot.opclaim")
    public static void teamOpClaim(final Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        sender.getInventory().remove(SELECTION_WAND);

        new BukkitRunnable() {

            public void run() {
                sender.getInventory().addItem(SELECTION_WAND.clone());
            }

        }.runTaskLater(Samurai.getInstance(), 1L);

        new VisualClaim(sender, VisualClaimType.CREATE, true).draw(false);

        if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.MAP, true).draw(true);
        }
    }

    @Subcommand("recacheclaims")
    @Description("Recache Claims")
    @CommandPermission("foxtrot.opclaim")
    public static void teamOpClaim(final CommandSender sender) {
        LandBoard.getInstance().loadFromTeams();
    }

    @Subcommand("promote")
    @Description("Promote a player in a team.")
    public static void teamPromote(Player sender, @Name("target") OfflinePlayer player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders (and above) can do this.");
            return;
        }

        if (!team.isMember(player.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + player.getName() + " is not on your team.");
            return;
        }

        if (team.isOwner(player.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + player.getName() + " is already a leader.");
        } else if (team.isCoLeader(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                sender.sendMessage(ChatColor.RED + player.getName() + " is already a co-leader! To make them a leader, use /t leader");
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new leaders.");
            }
        } else if (team.isCaptain(player.getUniqueId())) {
            if (team.isOwner(sender.getUniqueId())) {
                team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been promoted to Co-Leader!");
                team.addCoLeader(player.getUniqueId());
                team.createLog(sender.getUniqueId(), "PROMOTE (Co-Leader)", "/t promote " + player.getName());
                team.removeCaptain(player.getUniqueId());
            } else {
                sender.sendMessage(ChatColor.RED + "Only the team leader can promote new Co-Leaders.");
            }
        } else {
            team.sendMessage(ChatColor.DARK_AQUA + player.getName() + " has been promoted to Captain!");
            team.addCaptain(player.getUniqueId());
            team.createLog(sender.getUniqueId(), "PROMOTE (Captain)", "/t promote " + player.getName());
        }
    }

    @Subcommand("rally")
    @Description("Rally for your team!")
    public static void rally(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getTeamRally() != null) {
            team.getOnlineMembers().forEach(member -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                    "Team Rally",
                    team.getTeamRally(),
                    Color.ORANGE.hashCode(),
                    true
            )));
        }

        team.setTeamRally(player.getLocation());
        team.sendMessage(CC.translate("&3" + player.getName() + " has updated the team's rally point!"));

        team.getOnlineMembers().forEach(member -> LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint(
                "Team Rally",
                team.getTeamRally(),
                Color.ORANGE.hashCode(),
                true
        )));
    }

    @Subcommand("unrally")
    @Description("Unsets the rally point.")
    public static void unrally(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        if (team == null) {
            player.sendMessage(CC.translate("&7You are not on a team!"));
            return;
        }

        if (team.getTeamRally() == null) {
            player.sendMessage(CC.translate("&cYou don't have an active rally!"));
            return;
        }

        team.setTeamRally(null);
        team.sendMessage(CC.translate("&3" + player.getName() + " has removed the team's rally point!"));

        Bukkit.getOnlinePlayers().stream().filter(all -> team.isMember(all.getUniqueId()))
                .forEach(all -> LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(
                        "Team Rally",
                        team.getTeamRally(),
                        Color.AQUA.hashCode(),
                        true
                )));
    }

    @Subcommand("brew")
    @Description("open the brew menu for your team.")
    public static void brew(Player sender) {

        if (Feature.TEAM_BREW.isDisabled()) {
            sender.sendMessage(CC.translate("&cThis feature is currently disabled."));
            return;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(CC.translate("&cYou can't be in combat to do this."));
            return;
        }

        if (LandBoard.getInstance().getTeam(sender.getLocation()) == null || !LandBoard.getInstance().getTeam(sender.getLocation()).isMember(sender.getUniqueId()) && !DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.sendMessage(CC.translate("&cYou need to be in your teams claim to do this."));
            return;
        }

        new BrewMenu().openMenu(sender);

    }

    @Subcommand("rename")
    @Description("Renames your team.")
    public static void teamRename(Player sender, @Name("newName") String newName) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (Samurai.getInstance().getCitadelHandler().getCappers().contains(team.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Citadel cappers cannot change their name. Please contact an admin to rename your team.");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team owners and co-leaders can use this command!");
            return;
        }

        if (newName.length() > 16) {
            sender.sendMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
            return;
        }

        if (newName.length() < 3) {
            sender.sendMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
            return;
        }

        if (!ALPHA_NUMERIC.matcher(newName).find()) {
            if (Samurai.getInstance().getTeamHandler().getTeam(newName) == null) {
                team.createLog(sender.getUniqueId(), "RENAME", "/t rename " + newName);

                team.rename(newName);
                sender.sendMessage(ChatColor.GREEN + "Team renamed to " + newName);
            } else {
                sender.sendMessage(ChatColor.RED + "A team with that name already exists!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Team names must be alphanumeric!");
        }
    }

    @Subcommand("sethq|sethome|setheadquarters")
    @Description("Sets the team's headquarters.")
    public static void teamSetHQ(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your team's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Setting HQ outside of your team's territory would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }

            /*
             * Removed at Jon's request.
             * https://github.com/FrozenOrb/HCTeams/issues/59
             */

//            if (sender.getLocation().getBlockY() > 100) {
//                if (!sender.isOp()) {
//                    sender.sendMessage(ChatColor.RED + "You can't set your HQ above  Y 100.");
//                    return;
//                } else {
//                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Claiming above Y 100 would normally be disallowed, but this check is being bypassed due to your rank.");
//                }
//            }


            team.createLog(sender.getUniqueId(), "SET HQ &7(" + sender.getLocation().getBlockX() + ", " + sender.getLocation().getBlockY() + ", " + sender.getLocation().getBlockZ() + ")", "/t sethq");
            team.setHQ(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");

            Bukkit.getOnlinePlayers().stream().filter(player -> team.isMember(player.getUniqueId())).forEach(player -> LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("HQ", team.getHQ(), java.awt.Color.BLUE.hashCode(), true, true)));


        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

    @Subcommand("settings|allysettings")
    @Description("Edit your teams ally settings.")
    public static void allysettings(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            new FactionSettingsMenu(team).openMenu(sender);
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }


    @Subcommand("shadowmute")
    @Description("Mutes a player from your team.")
    @CommandPermission("foxtrot.team.shadowmute")
    public static void teamShadowMute(Player sender, final Team team, int time) {
        int timeSeconds = time * 60;

        for (UUID player : team.getMembers()) {
            teamShadowMutes.put(player, team.getName());
        }

        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_CREATED, ImmutableMap.of(
                "shadowMute", "true",
                "mutedById", sender.getUniqueId(),
                "mutedByName", sender.getName(),
                "duration", time
        ));

        team.createLog(sender.getUniqueId(), "SHADOW MUTE", "/t shadowmute " + team.getName() + " " + timeSeconds);

        new BukkitRunnable() {

            public void run() {
                TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                        "shadowMute", "true"
                ));

                teamShadowMutes.entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));
            }

        }.runTaskLater(Samurai.getInstance(), timeSeconds * 20L);

        sender.sendMessage(ChatColor.YELLOW + "Shadow muted the team " + team.getName() + ChatColor.GRAY + " for " + TimeUtils.formatIntoMMSS(timeSeconds) + ".");
    }


    static {
        warn.add(300);
        warn.add(270);
        warn.add(240);
        warn.add(210);
        warn.add(180);
        warn.add(150);
        warn.add(120);
        warn.add(90);
        warn.add(60);
        warn.add(30);
        warn.add(10);
        warn.add(5);
        warn.add(4);
        warn.add(3);
        warn.add(2);
        warn.add(1);

        Samurai.getInstance().getServer().getPluginManager().registerEvents(new TeamCommands(), Samurai.getInstance());
    }

    @Getter
    private static Map<String, Long> warping = new ConcurrentHashMap<>();
    private static List<String> damaged = Lists.newArrayList();

    @Subcommand("stuck|unstuck")
    @Description("Teleports you to the nearest spawn point.")
    public static void teamStuck(final Player sender) {
        if (warping.containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already being warped!");
            return;
        }

        if (sender.getWorld().getEnvironment() != World.Environment.NORMAL) {
            sender.sendMessage(ChatColor.RED + "You can only use this command from the overworld.");
            return;
        }

        int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 5 : 60;
        warping.put(sender.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds));

        new BukkitRunnable() {

            private int seconds = sender.isOp() && sender.getGameMode() == GameMode.CREATIVE ? 5 : 60;

            private Location loc = sender.getLocation();

            private int xStart = (int) loc.getX();
            private int yStart = (int) loc.getY();
            private int zStart = (int) loc.getZ();

            private Location nearest;

            @Override
            public void run() {
                if (damaged.contains(sender.getName())) {
                    sender.sendMessage(ChatColor.RED + "You took damage, teleportation cancelled!");
                    damaged.remove(sender.getName());
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                if (!sender.isOnline()) {
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                // Begin asynchronously searching for an available location prior to the actual teleport
                if (seconds == 5) {
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            nearest = nearestSafeLocation(sender.getLocation());
                        }

                    }.runTask(Samurai.getInstance());
                }

                Location loc = sender.getLocation();

                if (seconds <= 0) {
                    if (nearest == null) {
                        kick(sender);
                    } else {
                        Samurai.getInstance().getLogger().info("Moved " + sender.getName() + " " + loc.distance(nearest) + " blocks from " + toStr(loc) + " to " + toStr(nearest));

                        sender.teleport(nearest);
                        sender.sendMessage(ChatColor.YELLOW + "Teleported you to the nearest safe area!");
                    }

                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                // More than 5 blocks away
                if ((loc.getX() >= xStart + MAX_DISTANCE || loc.getX() <= xStart - MAX_DISTANCE) || (loc.getY() >= yStart + MAX_DISTANCE || loc.getY() <= yStart - MAX_DISTANCE) || (loc.getZ() >= zStart + MAX_DISTANCE || loc.getZ() <= zStart - MAX_DISTANCE)) {
                    sender.sendMessage(ChatColor.RED + "You moved more than " + MAX_DISTANCE + " blocks, teleport cancelled!");
                    warping.remove(sender.getName());
                    cancel();
                    return;
                }

                /* Not necessary if we put the stuck timer in sidebar
                if (warn.contains(seconds)) {
                    sender.sendMessage(ChatColor.YELLOW + "You will be teleported in " + ChatColor.RED.toString() + ChatColor.BOLD + TimeUtils.formatIntoMMSS(seconds) + ChatColor.YELLOW + "!");
                }
                */

                seconds--;
            }

        }.runTaskTimer(Samurai.getInstance(), 0L, 20L);
    }

    private static String toStr(Location loc) {
        return "{x=" + loc.getBlockX() + ", y=" + loc.getBlockY() + ", z=" + loc.getBlockZ() + "}";
    }

    public static Location nearestSafeLocation(Location origin) {
        LandBoard landBoard = LandBoard.getInstance();

        if (landBoard.getClaim(origin) == null) {
            return (getActualHighestBlock(origin.getBlock()).getLocation().add(0, 1, 0));
        }

        // Start iterating outward on both positive and negative X & Z.
        for (int xPos = 2, xNeg = -2; xPos < 250; xPos += 2, xNeg -= 2) {
            for (int zPos = 2, zNeg = -2; zPos < 250; zPos += 2, zNeg -= 2) {
                Location atPos = origin.clone().add(xPos, 0, zPos);

                // Try to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atPos) == null && !isAdjacentClaimed(atPos)) {
                    return (getActualHighestBlock(atPos.getBlock()).getLocation().add(0, 1, 0));
                }

                Location atNeg = origin.clone().add(xNeg, 0, zNeg);

                // Try again to find a unclaimed location with no claims adjacent
                if (landBoard.getClaim(atNeg) == null && !isAdjacentClaimed(atNeg)) {
                    return (getActualHighestBlock(atNeg.getBlock()).getLocation().add(0, 1, 0));
                }
            }
        }

        return (null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (warping.containsKey(player.getName())) {
                damaged.add(player.getName());
            }
        }
    }

    private static Block getActualHighestBlock(Block block) {
        block = block.getWorld().getHighestBlockAt(block.getLocation());

        while (block.getType() == Material.AIR && block.getY() > 0) {
            block = block.getRelative(BlockFace.DOWN);
        }

        return (block);
    }

    private static void kick(Player player) {
        player.setMetadata("loggedout", new FixedMetadataValue(Samurai.getInstance(), true));
        player.kickPlayer(ChatColor.RED + "We couldn't find a safe location, so we safely logged you out for now.");
    }

    /**
     * @param base center block
     * @return list of all adjacent locations
     */
    private static List<Location> getAdjacent(Location base) {
        List<Location> adjacent = new ArrayList<>();

        // Add all relevant locations surrounding the base block
        for (BlockFace face : BlockFace.values()) {
            if (face != BlockFace.DOWN && face != BlockFace.UP) {
                adjacent.add(base.getBlock().getRelative(face).getLocation());
            }
        }

        return adjacent;
    }

    /**
     * @param location location to check for
     * @return if any of it's blockfaces are claimed
     */
    private static boolean isAdjacentClaimed(Location location) {
        for (Location adjacent : getAdjacent(location)) {
            if (LandBoard.getInstance().getClaim(adjacent) != null) {
                return true; // we found a claim on an adjacent block!
            }
        }

        return false;
    }

    @Subcommand("tpall")
    @Description("Teleports all players to the nearest safe location")
    @CommandPermission("foxtrot.tpall")
    @CommandCompletion("@team")
    public static void teamTP(Player sender, @Name("team") Team team) {
        ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to teleport all players in " + team.getName() + " (" + team.getOnlineMembers().size() + ") to your location? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    for (Player player : team.getOnlineMembers()) {
                        player.teleport(sender.getLocation());
                    }
                    team.createLog(sender.getUniqueId(), "TELEPORT ALL", "/t tpall " + team.getName());

                    sender.sendMessage(ChatColor.GREEN + "Teleported " + team.getOnlineMembers().size() + " to you.");
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

    @Subcommand("tp")
    @Description("Teleport to a team")
    @CommandPermission("foxtrot.team.tp")
    @CommandCompletion("@team")
    public static void teamTPFaction(Player sender, @Name("team") Team team) {
        if (team.getHQ() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(team.getHQ());
        } else if (team.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s claim.");
            sender.teleport(team.getClaims().get(0).getMaximumPoint().add(0, 100, 0));
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }

    @Subcommand("unally")
    @Description("Unally a team.")
    @CommandCompletion("@team")
    public static void teamUnally(Player sender, @Name("team") Team team) {
        Team senderTeam = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(senderTeam.isOwner(sender.getUniqueId()) || senderTeam.isCoLeader(sender.getUniqueId()) || senderTeam.isCaptain(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        if (!senderTeam.isAlly(team)) {
            sender.sendMessage(ChatColor.RED + "You are not allied to " + team.getName() + "!");
            return;
        }

        senderTeam.createLog(sender.getUniqueId(), "ALLY REMOVE", "/t unally " + team.getName());
        team.createLog(sender.getUniqueId(), "ALLY REMOVE", "/t unally " + senderTeam.getName());

        senderTeam.getAllies().remove(team.getUniqueId());
        team.getAllies().remove(senderTeam.getUniqueId());

        senderTeam.flagForSave();
        team.flagForSave();

        for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
            if (team.isMember(player.getUniqueId())) {
                player.sendMessage(senderTeam.getName(player) + ChatColor.YELLOW + " has dropped their alliance with your team.");
            } else if (senderTeam.isMember(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "Your team has dropped its alliance with " + team.getName(sender) + ChatColor.YELLOW + ".");
            }

            if (team.isMember(player.getUniqueId()) || senderTeam.isMember(player.getUniqueId())) {
                //FrozenNametagHandler.reloadPlayer(sender);
                //FrozenNametagHandler.reloadOthersFor(sender);
            }
        }
    }

    @Subcommand("addpoints")
    @Description("add to the points of a team.")
    @CommandPermission("foxtrot.managepoints")
    @CommandCompletion("@team")
    public static void addpoints(CommandSender sender, @Name("team") Team team, @Name("amount") int amount, @Name("reason") String reason) {

        if (sender instanceof Player) {
            team.createLog(((Player) sender).getUniqueId(), "POINTS ADD", "/t addpoints " + team.getName() + " " + amount + " " + reason);
        }
        team.setAddedPoints(team.getAddedPoints() + amount);

        sender.sendMessage(CC.translate("&a" + amount + " added to " + team.getName() + ". New Points: " + Samurai.getInstance().getTopHandler().getTotalPoints(team)));
    }

    @Subcommand("delpoints")
    @Description("remove points from a team.")
    @CommandPermission("foxtrot.managepoints")
    @CommandCompletion("@team")
    public static void delpoints(Player sender, @Name("team") Team team, @Name("amount") int amount, @Name("reason") String reason) {

        team.createLog(sender.getUniqueId(), "POINTS REMOVE", "/t delpoints " + team.getName() + " " + amount + " " + reason);
        team.setAddedPoints(team.getAddedPoints() - amount);

        sender.sendMessage(CC.translate("&a" + amount + " removed from " + team.getName(sender) + ". New Points: " + Samurai.getInstance().getTopHandler().getTotalPoints(team)));
    }

    @Subcommand("logs|teamlogs")
    @Description("check the logs of a team.")
    @CommandPermission("foxtrot.teamlogs")
    @CommandCompletion("@team")
    public static void teamlogs(Player sender, @Name("team") Team team) {
        new TeamLogsMenu(team).openMenu(sender);
    }

    @Subcommand("history")
    @Description("check the history of a players teams.")
    @CommandPermission("foxtrot.history")
    @CommandCompletion("@players")
    public static void history(Player sender, @Name("player") UUID uuid) {
        List<Team> teams = Samurai.getInstance().getTeamHandler().getTeams().stream().filter(team -> team.getHistoricalMembers().contains(uuid)).toList();

        int i = 0;
        for (Team team : teams) {
            FancyMessage message = new FancyMessage(CC.translate("&e" + i + ") &f" + team.getName()));
            message.tooltip(CC.translate("&7Click to view this teams logs!"));
            message.command("/t logs " + team.getName());
            message.send(sender);
        }

        if (teams.isEmpty()) {
            sender.sendMessage(CC.translate("&cNo past teams found..."));
        }

    }

    @Subcommand("pastelogs")
    @Description("paste logs to a pastebin.")
    @CommandPermission("foxtrot.teamlogs")
    @CommandCompletion("@team")
    public static void pastelogs(Player sender, @Name("team") Team team) {
        String contents = "";
        SimpleDateFormat sdf = new SimpleDateFormat();

        for (TeamLog log : team.getLogs()) {
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));
            String date = sdf.format(new Date(log.getExecutedAt()));

            contents += date + "\n"
                    + " - Action: " + log.getAction() + "\n"
                    + " - Executor: " + log.getAction() + "\n"
                    + " - Command: " + log.getCommand() + "\n \n";
        }

        String developerKey = "A8gv47KG9G-eH5k_vRR75Nb3OdtJ9SSA";
        String date = sdf.format(new Date(System.currentTimeMillis()));
        String title = team.getName() + " Logs - " + date; // insert your own title

        try {
            sender.sendMessage(CC.translate("&6&lTEAM PASTE &7" + CC.UNICODE_ARROWS_RIGHT + " &f" + Pastebin.pastePaste(developerKey, contents, title)));
        } catch (PasteException e) {
            e.printStackTrace();
            sender.sendMessage(CC.translate("&cError pasting the logs, report this to LBuddyBoy"));
        }

    }

    @Subcommand("unclaim")
    @Description("Unclaims.")
    public static void teamUnclaim(Player sender, String all) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);
        if (!all.equals("all")) {
            all = "not all?";
        }
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team co-leaders can do this.");
            return;
        }

        if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
            return;
        }

        if (all.equalsIgnoreCase("all")) {
            int claims = team.getClaims().size();
            int refund = 0;

            for (Claim claim : team.getClaims()) {
                refund += Claim.getPrice(claim, team, false);

                Location minLoc = claim.getMinimumPoint();
                Location maxLoc = claim.getMaximumPoint();

                TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                        "playerId", sender.getUniqueId(),
                        "playerName", sender.getName(),
                        "refund", Claim.getPrice(claim, team, false),
                        "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                        "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
                ));

                team.createLog(sender.getUniqueId(), "UNCLAIM", "/t unclaim");
            }

            team.setBalance(team.getBalance() + refund);
            LandBoard.getInstance().clear(team);
            team.getClaims().clear();

            for (Subclaim subclaim : team.getSubclaims()) {
                LandBoard.getInstance().updateSubclaim(subclaim);
            }

            team.getSubclaims().clear();
            team.setHQ(null);
            team.flagForSave();

            for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed all of your team's claims. (" + ChatColor.LIGHT_PURPLE + claims + " total" + ChatColor.YELLOW + ")");
                }
            }

            return;
        }

        if (LandBoard.getInstance().getClaim(sender.getLocation()) != null && team.ownsLocation(sender.getLocation())) {
            Claim claim = LandBoard.getInstance().getClaim(sender.getLocation());
            int refund = Claim.getPrice(claim, team, false);

            team.setBalance(team.getBalance() + refund);
            team.getClaims().remove(claim);

            for (Subclaim subclaim : new ArrayList<>(team.getSubclaims())) {
                if (claim.contains(subclaim.getLoc1()) || claim.contains(subclaim.getLoc2())) {
                    team.getSubclaims().remove(subclaim);
                    LandBoard.getInstance().updateSubclaim(subclaim);
                }
            }

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed " + ChatColor.LIGHT_PURPLE + claim.getFriendlyName() + ChatColor.YELLOW + ".");
            team.flagForSave();

            LandBoard.getInstance().setTeamAt(claim, null);

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();

            TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "refund", Claim.getPrice(claim, team, false),
                    "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            if (team.getHQ() != null && claim.contains(team.getHQ())) {
                team.setHQ(null);
                sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
            }

            return;
        }

        sender.sendMessage(ChatColor.RED + "You do not own this claim.");
        sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/team unclaim all" + ChatColor.RED + ".");
    }

    @Subcommand("uninvite|revoke")
    @Description("Revoke an invite to your team.")
    public static void teamUninvite(final Player sender, final String allPlayer) {
        final Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (allPlayer.equalsIgnoreCase("all")) {
                team.createLog(sender.getUniqueId(), "INVITATIONS CLEAR", "/t uninvite all");
                team.getInvitations().clear();
                sender.sendMessage(ChatColor.GRAY + "You have cleared all pending invitations.");
            } else {
                new BukkitRunnable() {

                    public void run() {
                        final UUID nameUUID = UUIDUtils.uuid(allPlayer);

                        new BukkitRunnable() {

                            public void run() {
                                if (team.getInvitations().remove(nameUUID)) {
                                    TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_INVITE_REVOKED, ImmutableMap.of(
                                            "playerId", allPlayer,
                                            "uninvitedById", sender.getUniqueId(),
                                            "uninvitedByName", sender.getName()
                                    ));
                                    team.createLog(sender.getUniqueId(), "INVITATIONS REMOVE", "/t uninvite " + allPlayer);

                                    team.getInvitations().remove(nameUUID);
                                    team.flagForSave();
                                    sender.sendMessage(ChatColor.GREEN + "Cancelled pending invitation for " + allPlayer + "!");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "No pending invitation for '" + allPlayer + "'!");
                                }
                            }

                        }.runTask(Samurai.getInstance());
                    }

                }.runTaskAsynchronously(Samurai.getInstance());
            }
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

    @Subcommand("unmute")
    @Description("Unmute a player from your team.")
    @CommandPermission("foxtrot.team.unmute")
    @CommandCompletion("@team")
    public static void teamUnMute(Player sender, @Name("team") Team team) {
        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                "shadowMute", "false"
        ));
        team.createLog(sender.getUniqueId(), "UNMUTE", "/t unmute " + team.getName());

        getTeamMutes().entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));

        sender.sendMessage(ChatColor.GRAY + "Unmuted the team " + team.getName() + ChatColor.GRAY + ".");
    }

    @Subcommand("unshadowmute")
    @Description("Unmute a player from your team.")
    @CommandPermission("foxtrot.team.unmute")
    @CommandCompletion("@team")
    public static void teamUnShadowMute(Player sender, @Name("team") Team team) {
        TeamActionTracker.logActionAsync(team, TeamActionType.TEAM_MUTE_EXPIRED, ImmutableMap.of(
                "shadowMute", "true"
        ));
        team.createLog(sender.getUniqueId(), "UNMUTE", "/t unmute " + team.getName());

        getTeamShadowMutes().entrySet().removeIf(mute -> mute.getValue().equalsIgnoreCase(team.getName()));

        sender.sendMessage(ChatColor.GRAY + "Un-shadowmuted the team " + team.getName() + ChatColor.GRAY + ".");
    }

    @Subcommand("withdraw|w")
    @Description("Withdraw money from your team!")
    public static void teamWithdraw(Player sender, Float amount) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isOwner(sender.getUniqueId())) {
            if (team.getBalance() < amount) {
                sender.sendMessage(ChatColor.RED + "The team doesn't have enough money to do this!");
                return;
            }

            if (Double.isNaN(team.getBalance())) {
                sender.sendMessage(ChatColor.RED + "You cannot withdraw money because your team's balance is broken!");
                return;
            }

            if (amount <= 0) {
                sender.sendMessage(ChatColor.RED + "You can't withdraw $0.0 (or less)!");
                return;
            }

            if (Float.isNaN(amount)) {
                sender.sendMessage(ChatColor.RED + "Nope.");
                return;
            }

            FrozenEconomyHandler.deposit(sender.getUniqueId(), amount);
            sender.sendMessage(ChatColor.YELLOW + "You have withdrawn " + ChatColor.LIGHT_PURPLE + amount + ChatColor.YELLOW + " from the team balance!");

            TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_WITHDRAW_MONEY, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "amount", amount,
                    "oldBalance", team.getBalance(),
                    "newBalance", team.getBalance() - amount
            ));

            team.createLog(sender.getUniqueId(), "WITHDRAW", "/t withdraw " + amount);
            team.setBalance(team.getBalance() - amount);
            team.sendMessage(ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + " withdrew " + ChatColor.LIGHT_PURPLE + "$" + amount + ChatColor.YELLOW + " from the team balance.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }


    public static final ItemStack SELECTION_WAND = new ItemStack(Material.WOODEN_HOE);

    static {
        ItemMeta meta = SELECTION_WAND.getItemMeta();

        meta.setDisplayName("§a§oClaiming Wand");
        meta.setLore(Arrays.asList(

                "",
                "§eRight/Left Click§6 Block",
                "§b- §fSelect claim's corners",
                "",
                "§eRight Click §6Air",
                "§b- §fCancel current claim",
                "",
                "§9Crouch §eLeft Click §6Block/Air",
                "§b- §fPurchase current claim"

        ));

        SELECTION_WAND.setItemMeta(meta);
    }

    @Subcommand("claim")
    @Description("Claim a area for your team!")
    public static void teamClaim(final Player sender) {
        if (Samurai.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "You cannot use this command on a Kit map.");
            return;
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        /*
        if (Foxtrot.getInstance().getServerHandler().isWarzone(sender.getLocation())) {
            sender.sendMessage(ChatColor.RED + "You are currently in the Warzone and can't claim land here. The Warzone ends at " + ServerHandler.WARZONE_RADIUS + ".");
            return;
        }
         */

        if (team.isOwner(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId())) {
            sender.getInventory().remove(SELECTION_WAND);

            if (team.isRaidable()) {
                sender.sendMessage(ChatColor.RED + "You may not claim land while your faction is raidable!");
                return;
            }

            int slot = -1;

            for (int i = 0; i < 9; i++) {
                if (sender.getInventory().getItem(i) == null) {
                    slot = i;
                    break;
                }
            }

            if (slot == -1) {
                sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the claim wand!");
                return;
            }

            int finalSlot = slot;

            new BukkitRunnable() {

                public void run() {
                    sender.getInventory().setItem(finalSlot, SELECTION_WAND.clone());
                }

            }.runTaskLater(Samurai.getInstance(), 1L);

            new VisualClaim(sender, VisualClaimType.CREATE, false).draw(false);

            if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
                new VisualClaim(sender, VisualClaimType.MAP, false).draw(true);
            }

            sender.sendMessage(ChatColor.GREEN + "Gave you a claim wand.");
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().equals(SELECTION_WAND)) {
            VisualClaim visualClaim = VisualClaim.getVisualClaim(event.getPlayer().getName());

            if (visualClaim != null) {
                event.setCancelled(true);
                visualClaim.cancel();
            }

            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(SELECTION_WAND);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        event.getPlayer().getInventory().remove(SELECTION_WAND);
    }

    @Data
    @AllArgsConstructor
    public static class Selection {

        private Location loc1;
        private Location loc2;

        public boolean isComplete() {
            return (loc1 != null && loc2 != null);
        }

    }

    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        int page = help.getPage();
        List<String> header = CC.translate(Arrays.asList(
                CC.CHAT_BAR,
                "&6&lTeam Command Help"
        ));
        List<String> entries = new ArrayList<>();

        for (HelpEntry entry : help.getHelpEntries()) {
            entries.add("&e/" + entry.getCommand() + " " + entry.getParameterSyntax() + " &7- " + entry.getDescription());
        }

        PagedItem pagedItem = new PagedItem(entries, header, 10);

        pagedItem.send(help.getIssuer().getIssuer(), page);

        help.getIssuer().sendMessage(" ");
        help.getIssuer().sendMessage("&7You can do /f help <page> - You're currently viewing on page #" + page + ".");
        help.getIssuer().sendMessage(CC.CHAT_BAR);
    }

}
