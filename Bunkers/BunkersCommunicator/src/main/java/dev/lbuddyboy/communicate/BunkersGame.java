package dev.lbuddyboy.communicate;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/04/2022 / 4:21 PM
 * BunkersCommunicator / dev.lbuddyboy.communicate.database
 */

@AllArgsConstructor
@Data
public class BunkersGame {

	private String name;
	private int playersLeft;
	private long startedAt;
	private List<Team> teams;
	private boolean ended;
	private boolean kothActivated;
	private int kothTime;
	private GameState gameState;

}
