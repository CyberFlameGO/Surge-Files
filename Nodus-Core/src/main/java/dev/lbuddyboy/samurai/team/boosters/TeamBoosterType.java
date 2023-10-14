package dev.lbuddyboy.samurai.team.boosters;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.contexts.ContextResolver;
import dev.lbuddyboy.samurai.util.object.Callback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.team.Team;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
public enum TeamBoosterType {
	X2_KILL_POINT("&cx2 Kill Points", (team) -> {
		team.setKills(team.getKills() + 1);
	}),
	TWENTY_MIN_DTR_REGEN("&520m DTR Freeze", (team) -> {
		// had issues with looping, so we do it in the main method only!
	});

	private String displayName;
	private Callback<Team> teamCallback;

	public static class Completion implements CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> {

		@Override
		public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
			List<String> completions = new ArrayList<>();
			Player player = context.getPlayer();
			String input = context.getInput();

			for (TeamBoosterType boosterType : TeamBoosterType.values()) {
				if (StringUtils.startsWithIgnoreCase(boosterType.name(), input)) {
					completions.add(boosterType.name());
				}
			}

			return completions;
		}

	}

	public static class Type implements ContextResolver<TeamBoosterType, BukkitCommandExecutionContext> {

		@Override
		public TeamBoosterType getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
			Player sender = c.getPlayer();
			String source = c.popFirstArg();

			try {
				return TeamBoosterType.valueOf(source.toUpperCase());
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "TeamBooster Type '" + source + "' couldn't be found.");
				return null;
			}
		}
	}

}
