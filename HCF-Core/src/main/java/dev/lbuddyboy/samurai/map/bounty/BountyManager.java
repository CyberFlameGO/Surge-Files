package dev.lbuddyboy.samurai.map.bounty;

import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BountyManager {

    @Getter
    private final Map<UUID, Bounty> bountyMap = new HashMap<>();

    public void save() {
        bountyMap.forEach((key, value) -> Samurai.getInstance().getShardMap().addShardsSync(value.getPlacedBy(), value.getShards()));
        bountyMap.clear();
    }

    public void placeBounty(Player player, Player target, int shards) {
        bountyMap.put(target.getUniqueId(), new Bounty(player.getUniqueId(), shards));
    }

    public Bounty getBounty(Player player) {
        return bountyMap.get(player.getUniqueId());
    }

    public Bounty removeBounty(Player player) {
        return bountyMap.remove(player.getUniqueId());
    }
}
