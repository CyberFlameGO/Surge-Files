package dev.lbuddyboy.samurai.chat.listeners;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.user.User;
import dev.lbuddyboy.flash.util.TimeUtils;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.chat.enums.ChatMode;
import dev.lbuddyboy.samurai.commands.menu.EditConfigurationMenu;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.team.commands.TeamCommands;
import dev.lbuddyboy.samurai.team.menu.button.RollbackButton;
import dev.lbuddyboy.samurai.team.roster.menu.RosterMenu;
import dev.lbuddyboy.samurai.team.roster.menu.RosterRoleMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.api.FoxConstants;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    private String getCustomPrefix(UUID uuid) {
        Map<Integer, UUID> placesMap = Samurai.getInstance().getMapHandler().getStatsHandler() != null ? Samurai.getInstance().getMapHandler().getStatsHandler().getTopKills() : null;
        if (placesMap == null) {
            return CC.RESET;
        }

        int place = placesMap.size() == 3 ? uuid.equals(placesMap.get(1)) ? 1 : uuid.equals(placesMap.get(2)) ? 2 : uuid.equals(placesMap.get(3)) ? 3 : 99 : 99;
        if (place == 99) {
            return CC.RESET;
        }

        return ChatColor.translateAlternateColorCodes('&', place == 1 ? "&8[&6#1&8]" : place == 2 ? "&8[&7#2&8]" : "&8[&f#3&8]") + CC.RESET + " ";
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();

        Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());
        String customPrefix = getCustomPrefix(event.getPlayer().getUniqueId());
        ChatMode playerChatMode = Samurai.getInstance().getChatModeMap().getChatMode(event.getPlayer().getUniqueId());
        ChatMode finalChatMode;

        String publicChatFormat = FoxConstants.publicChatFormat(playerTeam, "", Flash.getInstance().getUserHandler().tryUser(player.getUniqueId(), false).isDisguised() ? "" : customPrefix);
        String finalMessage = String.format(publicChatFormat, event.getPlayer().getDisplayName(), (event.getPlayer().isOp() ? CC.translate(event.getMessage()) : ChatColor.stripColor(event.getMessage())));

        if (event.getMessage().startsWith("@")) {
            finalChatMode = ChatMode.TEAM;
            finalMessage = finalMessage.replaceFirst("@", "");
        } else if (event.getMessage().startsWith("!")) {
            finalChatMode = ChatMode.PUBLIC;
            finalMessage = finalMessage.replaceFirst("!", "");
        } else {
            finalChatMode = playerChatMode;
        }

        // If another plugin cancelled this event before it got to us (we are on MONITOR, so it'll happen)
        if (event.isCancelled() && finalChatMode == ChatMode.PUBLIC) { // Only respect cancelled events if this is public chat. Who cares what their team says.
            return;
        }

        // Any route we go down will cancel the event eventually.
        // Let's just do it here.
        event.setCancelled(true);

        // If someone's not in a team, instead of forcing their 'channel' to public,
        // we just tell them they can't.
        if (finalChatMode != ChatMode.PUBLIC && playerTeam == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in non-public chat if you're not in a team!");
            return;
        }

        if (finalChatMode != ChatMode.PUBLIC) {
            if (finalChatMode == ChatMode.OFFICER && !playerTeam.isCaptain(event.getPlayer().getUniqueId()) && !playerTeam.isCoLeader(event.getPlayer().getUniqueId()) && !playerTeam.isOwner(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You can't speak in officer chat if you're not an officer!");
                return;
            }
        }

        // and here starts the big logic switch
        switch (finalChatMode) {
            case PUBLIC:
                if (TeamCommands.getTeamMutes().containsKey(event.getPlayer().getUniqueId())) {
                    event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Your team is muted!");
                    return;
                }

                // Loop those who are to receive the message (which they won't if they have the sender /ignore'd or something),
                // not online players
                for (Player online : event.getRecipients()) {
                    if (playerTeam == null) {
                        // If the player sending the message is shadowmuted (if their team was and they left it)
                        // then we don't allow them to. We probably could move this check "higher up", but oh well.
                        if (TeamCommands.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                            continue;
                        }

                        // If their chat is enabled (which it is by default) or the sender is op, send them the message
                        // The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
                        if (event.getPlayer().isOp() || Samurai.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(online.getUniqueId())) {
                            online.sendMessage(finalMessage);
                        }
                    } else {
                        if (playerTeam.isMember(online.getUniqueId())) {
                            online.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + Samurai.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + ChatColor.DARK_GREEN));

                        } else if (playerTeam.isAlly(online.getUniqueId())) {
                            online.sendMessage(finalMessage.replace(ChatColor.GOLD + "[" + Samurai.getInstance().getServerHandler().getDefaultRelationColor(), ChatColor.GOLD + "[" + Team.ALLY_COLOR));

                        } else {
                            // We only check this here as...
                            // Team members always see their team's messages
                            // Allies always see their allies' messages, 'cause they'll probably be in a TS or something
                            // and they could figure out this code even exists
                            if (TeamCommands.getTeamShadowMutes().containsKey(event.getPlayer().getUniqueId())) {
                                continue;
                            }

                            // If their chat is enabled (which it is by default) or the sender is op, send them the message
                            // The isOp() fragment is so OP messages are sent regardless of if the player's chat is toggled
                            if (event.getPlayer().isOp() || Samurai.getInstance().getToggleGlobalChatMap().isGlobalChatToggled(online.getUniqueId())) {
                                online.sendMessage(finalMessage);
                            }
                        }
                    }
                }

                Samurai.getInstance().getServer().getConsoleSender().sendMessage(finalMessage);
                break;
            case ALLIANCE:
                String allyChatFormat = FoxConstants.allyChatFormat(event.getPlayer(), event.getMessage());
                String allyChatSpyFormat = FoxConstants.allyChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates/allies
                for (Player online : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(online.getUniqueId()) || playerTeam.isAlly(online.getUniqueId())) {
                        online.sendMessage(allyChatFormat);
                    } else if (Samurai.getInstance().getChatSpyMap().getChatSpy(online.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        online.sendMessage(allyChatSpyFormat);
                    }
                }

                Samurai.getInstance().getServer().getLogger().info("[Ally Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
            case TEAM:
                String teamChatFormat = FoxConstants.teamChatFormat(event.getPlayer(), event.getMessage());
                String teamChatSpyFormat = FoxConstants.teamChatSpyFormat(playerTeam, event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates
                for (Player online : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isMember(online.getUniqueId())) {
                        online.sendMessage(teamChatFormat);
                    } else if (Samurai.getInstance().getChatSpyMap().getChatSpy(online.getUniqueId()).contains(playerTeam.getUniqueId())) {
                        online.sendMessage(teamChatSpyFormat);
                    }
                }

                Samurai.getInstance().getServer().getLogger().info("[Team Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
            case OFFICER:
                String officerChatFormat = FoxConstants.officerChatFormat(event.getPlayer(), event.getMessage());

                // Loop online players and not recipients just in case you're weird and
                // /ignore your teammates
                for (Player online : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (playerTeam.isCaptain(online.getUniqueId()) || playerTeam.isCoLeader(online.getUniqueId()) || playerTeam.isOwner(online.getUniqueId())) {
                        online.sendMessage(officerChatFormat);
                    }
                }

                Samurai.getInstance().getServer().getLogger().info("[Officer Chat] [" + playerTeam.getName() + "] " + event.getPlayer().getName() + ": " + event.getMessage());
                break;
        }
    }

}