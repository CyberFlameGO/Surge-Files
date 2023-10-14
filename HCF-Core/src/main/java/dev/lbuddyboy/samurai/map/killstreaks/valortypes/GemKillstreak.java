package dev.lbuddyboy.samurai.map.killstreaks.valortypes;

import com.google.common.collect.ImmutableMap;
import dev.lbuddyboy.samurai.map.killstreaks.Killstreak;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;

import java.util.Map;

public class GemKillstreak extends Killstreak {

	private static final Map<Integer, Integer> KILL_GEM_MAP = ImmutableMap.<Integer, Integer>builder()
			.put(5, 3)
			.put(10, 5)
			.put(25, 20)
			.put(75, 60)
			.put(100, 80)
			.put(200, 150)
			.build();

	private static final int[] KILLS = KILL_GEM_MAP.keySet().stream()
			.mapToInt(Integer::intValue)
			.toArray();

	@Override
	public String getName() {
		return "Shard";
	}

	@Override
	public int[] getKills() {
		return KILLS;
	}

	@Override
	public void apply(Player player, int kills) {
		int shards = KILL_GEM_MAP.get(kills);
		Samurai.getInstance().getShardMap().addShards(player.getUniqueId(), shards);
		player.sendMessage(CC.WHITE + "You have received " + CC.GOLD + "+" + (shards) +
				CC.WHITE + " for a " + CC.GOLD + kills + CC.WHITE + " killstreak!");
	}

	@Override
	public void apply(Player player) {


	}
}
