package dev.lbuddyboy.bunkers.command.context;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ACFTeamType implements ContextResolver<Team, BukkitCommandExecutionContext> {

    @Override
    public Team getContext(BukkitCommandExecutionContext arg) throws InvalidCommandArgument {
        Player sender = arg.getPlayer();
        String source = arg.popFirstArg();

        if (sender != null && arg.isOptional() && (source.equalsIgnoreCase("self") || source.equals(""))) {
            Team team = Bunkers.getInstance().getTeamHandler().getTeam(sender.getUniqueId());

            if (team == null) {
                throw new InvalidCommandArgument(ChatColor.RESET + ChatColor.GRAY.toString() + "You're not on a team!");
            }

            return (team);
        }

        Team byName = Bunkers.getInstance().getTeamHandler().getTeam(source);

        if (byName != null) {
            return (byName);
        }

        Player bukkitPlayer = Bunkers.getInstance().getServer().getPlayer(source);

        if (bukkitPlayer != null) {
            Team byMemberBukkitPlayer = Bunkers.getInstance().getTeamHandler().getTeam(bukkitPlayer.getUniqueId());

            if (byMemberBukkitPlayer != null) {
                return (byMemberBukkitPlayer);
            }
        }

        Team byMemberUUID = Bunkers.getInstance().getTeamHandler().getTeam(Bukkit.getOfflinePlayer(source).getUniqueId());

        if (byMemberUUID != null) {
            return (byMemberUUID);
        }

        throw new InvalidCommandArgument("No team or member with the name " + source + " found.");
    }
}