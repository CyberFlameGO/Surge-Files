package dev.lbuddyboy.samurai.api.papi;

import com.google.common.collect.Lists;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.flash.user.User;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.menu.schedule.buttons.KoTHScheduleButton;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventScheduledTime;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.nametag.NametagInfo;
import dev.lbuddyboy.samurai.nametag.impl.FoxtrotNametagProvider;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.util.object.PlayerDirection;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.*;

import static dev.lbuddyboy.samurai.team.commands.TeamCommands.sortByValues;
import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.YELLOW;

@RequiredArgsConstructor
public class SamuraiExtension extends PlaceholderExpansion {

    private final Samurai samurai;

    @Override
    public String getIdentifier() {
        return "samurai";
    }

    @Override
    public String getAuthor() {
        return "LBuddyBoy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String holder) {
        if (holder.equalsIgnoreCase("nametag_prefix")) {
            if (SOTWCommand.isSOTWTimer()) {
                if (SOTWCommand.hasSOTWEnabled(player.getUniqueId())) {
                    return CC.RED + "";
                }
                return CC.GOLD + "";
            }
            return CC.RED + "";
        }
        if (holder.equalsIgnoreCase("nametag_above_prefix")) {
            Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

            if (team != null) {
                return CC.translate("&6[&e" + team.getName() + " &7" + CC.UNICODE_VERTICAL_BAR + " " + team.getDTRColor() + Team.DTR_FORMAT.format(team.getDTR()) + team.getDTRSuffix()) + "&6]";
            }

            return CC.translate("&6[&cNo Team&6]");
        }

        if (holder.equalsIgnoreCase("player_kills")) {
            return String.valueOf(Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player).getKills());
        }

        if (holder.equalsIgnoreCase("player_deaths")) {
            return String.valueOf(Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player).getDeaths());
        }

        if (holder.equalsIgnoreCase("player_killstreak")) {
            return String.valueOf(Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player).getKillstreak());
        }

        if (holder.equalsIgnoreCase("player_lives")) {
            return String.valueOf(Samurai.getInstance().getFriendLivesMap().getLives(player.getUniqueId()));
        }

        if (holder.equalsIgnoreCase("player_location")) {
            return String.format("%d, %d", player.getLocation().getBlockX(), player.getLocation().getBlockZ());
        }

        if (holder.equalsIgnoreCase("player_direction")) {
            String direction = PlayerDirection.getCardinalDirection(player);

            return direction == null ? "" : direction;
        }

        if (holder.equalsIgnoreCase("player_team_at")) {
            Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());
            String team = "&7Wilderness";
            if (teamAt != null) {
                team = teamAt.getName(player);
            } else if (Samurai.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                team = "&cWarZone";
            }
            return team;
        }

        if (holder.equalsIgnoreCase("player_highest_killstreak")) {
            return String.valueOf(Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player).getHighestKillstreak());
        }

        if (holder.equalsIgnoreCase("player_kdr")) {
            return String.valueOf(Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player).getKD());
        }

        if (holder.startsWith("world_")) {
            for (World world : Bukkit.getWorlds()) {
                if (holder.equalsIgnoreCase(world.getName() + "_border")) {
                    return String.valueOf(((int) world.getWorldBorder().getSize() / 2));
                }
            }
        }

        if (holder.equalsIgnoreCase("map_size")) {
            return String.valueOf(Samurai.getInstance().getMapHandler().getTeamSize()) + " Man ";
        }

        if (holder.equalsIgnoreCase("allies")) {
            return String.valueOf(Samurai.getInstance().getMapHandler().getAllyLimit() > 0 ? "&aENABLED" : "&cDISABLED");
        }

        if (holder.startsWith("next_event_")) {
            KOTH activeKOTH = null;
            for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
                if (!(event instanceof KOTH koth)) continue;
                if (koth.isActive() && !koth.isHidden()) {
                    activeKOTH = koth;
                    break;
                }
            }

            if (activeKOTH == null) {
                Date now = new Date();

                Event nextKoTH = null;
                Date nextKothDate = null;

                for (Map.Entry<EventScheduledTime, String> entry : Samurai.getInstance().getEventHandler().getEventSchedule().entrySet()) {
                    if (entry.getKey().toDate().after(now)) {
                        if (nextKothDate == null || nextKothDate.getTime() > entry.getKey().toDate().getTime()) {
                            nextKoTH = Samurai.getInstance().getEventHandler().getEvent(entry.getValue());
                            nextKothDate = entry.getKey().toDate();
                        }
                    }
                }

                if (nextKoTH != null) {
                    if (nextKoTH instanceof KOTH koth) {
                        if (holder.equalsIgnoreCase("next_event_name")) {
                            return nextKoTH.getDisplayName();
                        }
                        if (holder.equalsIgnoreCase("next_event_when")) {
                            return formatIntoDetailedString((int) ((nextKothDate.getTime() - System.currentTimeMillis()) / 1000));
                        }
                        if (holder.equalsIgnoreCase("next_event_location")) {
                            return koth.getCapLocation().getBlockX() + ", " + koth.getCapLocation().getBlockY() + ", " + koth.getCapLocation().getBlockZ();
                        }
                    }
                }
            } else {
                if (holder.equalsIgnoreCase("next_event_name")) {
                    return activeKOTH.getDisplayName();
                }
                if (holder.equalsIgnoreCase("next_event_when")) {
                    return TimeUtils.formatIntoHHMMSS(activeKOTH.getRemainingCapTime());
                }
                if (holder.equalsIgnoreCase("next_event_location")) {
                    return activeKOTH.getCapLocation().getBlockX() + ", " + activeKOTH.getCapLocation().getBlockY() + ", " + activeKOTH.getCapLocation().getBlockZ();
                }
            }
        }

        if (holder.equalsIgnoreCase("next_event_name")) {
            return CC.GRAY + "Undefined";
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);

        if (team != null) {
            if (holder.equalsIgnoreCase("player_team_name")) {
                return team.getName();
            }
            if (holder.equalsIgnoreCase("player_team_dtr")) {
                return team.getDTRColor() + Team.DTR_FORMAT.format(team.getDTR()) + team.getDTRSuffix();
            }
            if (holder.equalsIgnoreCase("player_team_online")) {
                return String.valueOf(team.getOnlineMembers().size());
            }
            if (holder.equalsIgnoreCase("player_team_total")) {
                return String.valueOf(team.getMembers().size());
            }
            if (holder.equalsIgnoreCase("player_team_hq")) {
                Location hqLoc = team.getHQ();

                return hqLoc == null ? CC.RED + "None" : String.format("%d, %d", hqLoc.getBlockX(), hqLoc.getBlockZ());
            }

            if (holder.startsWith("player_team_online_member_")) {
                int i = Integer.parseInt(holder.replaceAll("player_team_online_member_", ""));
                Player owner = null;
                List<Player> all = new ArrayList<>();
                List<Player> coleaders = Lists.newArrayList();
                List<Player> captains = Lists.newArrayList();
                List<Player> members = Lists.newArrayList();
                for (Player member : team.getOnlineMembers()) {
                    if (team.isOwner(member.getUniqueId())) {
                        owner = member;
                    } else if (team.isCoLeader(member.getUniqueId())) {
                        coleaders.add(member);
                    } else if (team.isCaptain(member.getUniqueId())) {
                        captains.add(member);
                    } else {
                        members.add(member);
                    }
                }
                if (owner != null) all.add(owner);
                all.addAll(coleaders);
                all.addAll(captains);
                all.addAll(members);

                if (i > all.size()) return "";

                return memberName(all.get(i - 1).getUniqueId());
            }
        }

        if (holder.equalsIgnoreCase("has_team")) {
            return String.valueOf(team != null);
        }

        if (holder.startsWith("team_list_")) {
            int i = Integer.parseInt(holder.replaceAll("team_list_", ""));
            Map<Team, Integer> teamPlayerCount = new HashMap<>();

            // Sort of weird way of getting player counts, but it does it in the least iterations (1), which is what matters!
            for (Player online : Samurai.getInstance().getServer().getOnlinePlayers()) {
                if (online.hasMetadata("invisible")) {
                    continue;
                }

                Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(online);

                if (playerTeam != null) {
                    if (teamPlayerCount.containsKey(playerTeam)) {
                        teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                    } else {
                        teamPlayerCount.put(playerTeam, 1);
                    }
                }
            }

            for (SpoofHandler.SpoofPlayer online : Flash.getInstance().getSpoofHandler().getSpoofPlayers().values()) {

                Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(online.getUuid());

                if (playerTeam != null) {
                    if (teamPlayerCount.containsKey(playerTeam)) {
                        teamPlayerCount.put(playerTeam, teamPlayerCount.get(playerTeam) + 1);
                    } else {
                        teamPlayerCount.put(playerTeam, 1);
                    }
                }
            }

            LinkedHashMap<Team, Integer> sortedTeamPlayerCount = sortByValues(player, teamPlayerCount);
            List<Team> teams = new ArrayList<>(sortedTeamPlayerCount.keySet());

            if (i > teams.size()) return "";
            Team found = teams.get(i - 1);

            return CC.translate(found.getName() + " &7(" + found.getOnlineFlashMembers().size() + "/" + found.getMembers().size() + ") &e- &7" + found.getDTRColor() + Team.DTR_FORMAT.format(found.getDTR()) + found.getDTRSuffix());

        }

        return null;
    }

    public static String memberName(UUID uuid) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(uuid);
        String astrix = "";
        if (team.isOwner(uuid)) {
            astrix = "***";
        } else if (team.isCoLeader(uuid)) {
            astrix = "**";
        } else if (team.isCaptain(uuid)) {
            astrix = "*";
        }
        return astrix + UUIDUtils.name(uuid);
    }

	public static String formatIntoDetailedString(int secs) {
		if (secs <= 60) {
			return "1 m";
		} else {
			int remainder = secs % 86400;
			int days = secs / 86400;
			int hours = remainder / 3600;
			int minutes = remainder / 60 - hours * 60;
			String fDays = days > 0 ? " " + days + "d": "";
			String fHours = hours > 0 ? " " + hours + "h": "";
			String fMinutes = minutes > 0 ? " " + minutes + "m": "";
			return (fDays + fHours + fMinutes).trim();
		}
	}
}
