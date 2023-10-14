package dev.lbuddyboy.samurai.team.ftop;

import dev.lbuddyboy.samurai.team.Team;

public final class FTopHandler {

	// index 0 will be the required number of data they need in that field
	// index 1 is the amount of points it gives
	private static final int[] POINTS_KILLS = { 1, 5 };
	private static final int[] POINTS_DEATHS = { 2, -5 };
	private static final int[] POINTS_WENT_RAIDABLE = { 1, -50 };
	private static final int[] POINTS_MADE_RAIDABLE = { 1, 15 };
	private static final int[] POINTS_KOTH_CAPTURES = { 1, 50 };
	private static final int[] POINTS_VAULT_CAPTURES = { 1, 80 };
	private static final int[] POINTS_CITADEL_CAPTURES = { 1, 200 };
	private static final int[] POINTS_CONQUEST_CAPTURES = { 1, 150 };

	public FTopHandler() {
//		Foxtrot.getInstance().getCommandHandler().register(FTopCommand.class);
	}

	public int getTotalPoints(Team team) {
		int total = 0;

		total += getPoints(team.getKills(), POINTS_KILLS);
		total += getPoints(team.getDeaths(), POINTS_DEATHS);
//		total += getPoints(team.getDiamondsMined(), POINTS_DIAMONDS_MINED);
		total += getPoints(team.getKothCaptures(), POINTS_KOTH_CAPTURES);
		total += getPoints(team.getCitadelsCapped(), POINTS_CITADEL_CAPTURES);
		total += getPoints(team.getVaultCaptures(), POINTS_VAULT_CAPTURES);
		total += getPoints(team.getRaidableTeams(), POINTS_MADE_RAIDABLE);
		total += getPoints(team.getConquestCapped(), POINTS_CONQUEST_CAPTURES);

		total += team.getAddedPoints();

		return Math.max(total, 0);
	}

	private int getPoints(int field, int[] data) {
		int points = 0;

		int required = data[0];

		if (field >= required) {
			int remaining = field - (field % required);
			points += ((remaining / required) * data[1]);
		}

		return points;
	}
}
