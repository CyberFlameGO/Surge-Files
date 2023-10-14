package dev.lbuddyboy.bunkers.scoreboard;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.command.HQCommand;
import dev.lbuddyboy.bunkers.game.GameState;
import dev.lbuddyboy.bunkers.game.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.scoreboard.assemble.AssembleAdapter;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.ScoreFunction;
import dev.lbuddyboy.bunkers.util.SymbolUtil;
import dev.lbuddyboy.bunkers.util.TimeUtils;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 10:31 PM
 * SteelBunkers / com.steelpvp.bunkers.scoreboard
 */
public class BunkersScoreboard implements AssembleAdapter {

	@Setter public static long KOTH_START_MILLIS = 300_000L;

	@Override
	public String getTitle(Player player) {
		return "&x&0&4&4&5&f&b&lʙᴜɴᴋᴇʀꜱ";
	}

	@Override
	public List<String> getLines(Player player) {

		List<String> lines = new ArrayList<>();

		lines.add(" ");

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.WAITING || Bunkers.getInstance().getGameHandler().getState() == GameState.COUNTING) {
			getLobbyLines(player, lines);
		}

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.ACTIVE) {
			getGameLines(player, lines);
		}

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.ENDING) {
			if (Bunkers.getInstance().getGameHandler().getWinner() != null) {
				lines.add(" &x&0&4&4&5&f&b&lᴡɪɴɴᴇʀ&f: " + Bunkers.getInstance().getGameHandler().getWinner().getDisplay());
			} else {
				lines.add(" &x&0&4&4&5&f&b&lᴡɪɴɴᴇʀ&f: &cNone");
			}
		}

		lines.add(" ");
		lines.add(" &x&a&3&b&4&b&0&lꜱᴛᴇᴇʟᴘᴠᴘ.ᴄᴏᴍ  ");
		lines.add(" ");

		lines.forEach(CC::translate);

		return lines;
	}

	private void getLobbyLines(Player player, List<String> lines) {

		lines.add(" &x&0&4&4&5&f&b&lᴛᴇᴀᴍꜱ");
		for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
			if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;
			lines.add(" &f" + SymbolUtil.UNICODE_ARROW_RIGHT + " " + team.getDisplay() + "&7: " + team.getColor() + team.getOnlineMembers().size());
		}

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.COUNTING) {
			lines.add(" ");
			lines.add(" &fStarting in &x&0&4&4&5&f&b" + Bunkers.getInstance().getGameHandler().getTask().getRemaining() + "...");
		}

	}

	private void getGameLines(Player player, List<String> lines) {

		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

		if (Bunkers.getInstance().getGameHandler().canKoTHActivate()) {
			long diff = (Bunkers.getInstance().getGameHandler().getStartedAt() + KOTH_START_MILLIS) - System.currentTimeMillis();
			String form = TimeUtils.formatLongIntoMMSS(diff / 1000);
			lines.add(" &x&1&8&f&b&5&b&lᴋᴏᴛʜ ɪɴ&f: &c" + form);
		} else {
			String form = TimeUtils.formatLongIntoMMSS(Bunkers.getInstance().getGameHandler().getKoTHHandler().getRemaining());
			lines.add(" &x&1&8&f&b&5&b&l&f" + Bunkers.getInstance().getGameHandler().getGameSettings().getKothName() + ": &c" + form);
		}

		if (Bunkers.getInstance().getGameHandler().isGracePeriod()) {
			long diff = (Bunkers.getInstance().getGameHandler().getStartedAt() + 60_000L) - System.currentTimeMillis();
			String form = TimeUtils.formatLongIntoMMSS(diff / 1000);
			lines.add(" &x&1&8&f&b&5&b&lɢʀᴀᴄᴇ&f: &c" + form);
		}

		lines.add(" &x&f&b&9&3&1&0&lɢᴀᴍᴇ ᴛɪᴍᴇ&f: &c" + TimeUtils.formatLongIntoMMSS((System.currentTimeMillis() - Bunkers.getInstance().getGameHandler().getStartedAt()) / 1000));

		String enderpearlScore = getEnderpearlScore(player);
		if (enderpearlScore != null) {
			lines.add(" &x&0&4&4&5&f&b&lᴘᴇᴀʀʟ&f: &c" + enderpearlScore);
		}

		if (HQCommand.time.onCooldown(player)) {
			lines.add(" &x&0&4&4&5&f&b&lʜᴏᴍᴇ&f: &c" + HQCommand.time.getRemaining(player));
		}

		String line = " &x&0&4&4&5&f&b&lᴄʟᴀɪᴍ&f: &cWarzone";
		for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
			if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;
			if (team.getCuboid() != null) {
				if (team.getCuboid().contains(player)) {
					line = " &x&0&4&4&5&f&b&lᴄʟᴀɪᴍ&f: " + team.getDisplay();
				}
			}
		}

		lines.add(line);
		lines.add(" &x&0&4&4&5&f&b&lʙᴀʟᴀɴᴄᴇ&f: &a$" + user.getBalance());

		if (!player.hasMetadata("spectator")) {
			lines.add(" &x&0&4&4&5&f&b&lᴛᴇᴀᴍꜱ");
			for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
				if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;
				lines.add(" &f" + SymbolUtil.UNICODE_ARROW_RIGHT + " " + team.getDisplay() + "&7: " + team.getColor() + team.getDTRFormatted());
			}
			return;
		}

		lines.add(" &x&0&4&4&5&f&b&lᴛᴇᴀᴍꜱ");
		for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
			if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;
			lines.add(" &f" + SymbolUtil.UNICODE_ARROW_RIGHT + " " + team.getDisplay() + "&7: " + team.getColor() + team.getDTRFormatted());
		}

	}

	public String getEnderpearlScore(Player player) {
		if (EnderpearlCooldownHandler.getEnderpearlCooldown().containsKey(player.getName()) && EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) >= System.currentTimeMillis()) {
			float diff = EnderpearlCooldownHandler.getEnderpearlCooldown().get(player.getName()) - System.currentTimeMillis();

			if (diff >= 0) {
				return (ScoreFunction.TIME_FANCY.apply(diff / 1000F));
			}
		}

		return (null);
	}

}
