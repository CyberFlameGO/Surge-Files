package dev.lbuddyboy.samurai.scoreboard.assemble;

import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class AssembleBoardEntry {

	private AssembleBoard board;
	@Setter private String text, identifier;
	private Team team;
	private int position;

	/**
	 * Assemble Board Entry
	 *
	 * @param board that entry belongs to.
	 * @param text of entry.
	 * @param position of entry.
	 */
	public AssembleBoardEntry(AssembleBoard board, String text, int position) {
		this.board = board;
		this.text = text;
		this.position = position;
		this.identifier = this.board.getUniqueIdentifier(position);

		this.setup();
	}

	/**
	 * Setup Board Entry.
	 */
	public void setup() {
		Scoreboard scoreboard = this.board.getScoreboard();

		if (scoreboard == null) {
			return;
		}

		String teamName = this.identifier;

		// This shouldn't happen, but just in case.
		if (teamName.length() > 128) {
			teamName = teamName.substring(0, 128);
		}

		Team team = scoreboard.getTeam(teamName);

		// Register the team if it does not exist.
		if (team == null) {
			team = scoreboard.registerNewTeam(teamName);
		}

		// Add the entry to the team.
		if (team.getEntries() == null || team.getEntries().isEmpty() || !team.getEntries().contains(this.identifier)) {
			team.addEntry(this.identifier);
		}

		// Add the entry if it does not exist.
		if (!this.board.getEntries().contains(this)) {
			this.board.getEntries().add(this);
		}

		this.team = team;
	}

	/**
	 * Send Board Entry Update.
	 *
	 * @param position of entry.
	 */
	public void send(int position) {
		if (this.text.length() > 128) {
			String prefix = this.text.substring(0, 128);
			String suffix;

			if (prefix.charAt(127) == ChatColor.COLOR_CHAR) {
				prefix = prefix.substring(0, 127);
				suffix = this.text.substring(127, this.text.length());
			} else if (prefix.charAt(126) == ChatColor.COLOR_CHAR) {
				prefix = prefix.substring(0, 126);
				suffix = this.text.substring(126, this.text.length());
			} else {
				if (ChatColor.getLastColors(prefix).equalsIgnoreCase(ChatColor.getLastColors(this.identifier))) {
					suffix = this.text.substring(128, this.text.length());
				} else {
					suffix = ChatColor.getLastColors(prefix) + this.text.substring(128, this.text.length());
				}
			}

			if (suffix.length() > 128) {
				suffix = suffix.substring(0, 128);
			}

			this.team.setPrefix(prefix);
			this.team.setSuffix(suffix);
		} else {
			this.team.setPrefix(this.text);
			this.team.setSuffix("");
		}

		Score score = this.board.getObjective().getScore(this.identifier);
		score.setScore(position);
	}

	/**
	 * Remove Board Entry from Board.
	 */
	public void remove() {
		this.board.getIdentifiers().remove(this.identifier);
		this.board.getScoreboard().resetScores(this.identifier);
	}

}
