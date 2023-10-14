package dev.lbuddyboy.communicate.database.redis.packets;

import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.BunkersGame;
import dev.lbuddyboy.communicate.database.redis.JedisPacket;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/04/2022 / 4:25 PM
 * BunkersCommunicator / dev.lbuddyboy.communicate.database.redis.packets
 */

@AllArgsConstructor
public class BunkersUpdatePacket implements JedisPacket {

	private List<BunkersGame> games;

	@Override
	public void onReceive() {
		BunkersCom.getInstance().setBunkersGames(this.games);
	}

	@Override
	public String getID() {
		return "Bunkers Game Update";
	}
}
