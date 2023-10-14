package dev.lbuddyboy.bunkers.game.user;

import dev.lbuddyboy.bunkers.Bunkers;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 10:25 PM
 * SteelBunkers / com.steelpvp.bunkers.game.user
 */

@AllArgsConstructor
@Getter
public class GameUser {

	private UUID uuid;
	private int kills, deaths, oresMined;
	private double balance;
	private long left;
	private boolean loggerDied;

	public boolean isEligibleRejoin() {
		return left + 120_000L > System.currentTimeMillis();
	}

	public boolean hasBalance(double amount) {
		return this.balance > amount;
	}

	public void addBalance(double amount) {
		balance += amount;
		Bunkers.getInstance().getGameHandler().getBalanceMap().put(this.uuid, this.balance);
	}

	public void takeBalance(double amount) {
		balance -= amount;
		Bunkers.getInstance().getGameHandler().getBalanceMap().put(this.uuid, this.balance);
	}

	public void setLeft(long sys) {
		this.left = sys;
		Bunkers.getInstance().getGameHandler().getLastJoinedMap().put(this.uuid, this.left);
	}

	public void setKills(int kills) {
		this.kills = kills;
		Bunkers.getInstance().getGameHandler().getKillsMap().put(this.uuid, this.kills);
	}

	public void setOresMined(int oresMined) {
		this.oresMined = oresMined;
		Bunkers.getInstance().getGameHandler().getOresMinedMap().put(this.uuid, this.oresMined);
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
		Bunkers.getInstance().getGameHandler().getDeathsMap().put(this.uuid, this.deaths);
	}

}
