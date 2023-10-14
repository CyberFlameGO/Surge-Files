package dev.lbuddyboy.crates.api;

import dev.lbuddyboy.crates.API;
import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.persist.maps.statistics.BaseStatisticMap;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FoxtrotAPI extends API {

    private final Map<Crate, BaseStatisticMap> statisticMaps = new HashMap<>();

    @Override
    public void onEnable() {
        for (Crate crate : lCrates.getInstance().getCrates().values()) {
            statisticMaps.put(crate, new BaseStatisticMap("keys." + crate.getName()));
        }
        statisticMaps.values().forEach(PersistMap::loadFromRedis);
    }

    @Override
    public void registerCrate(Crate crate) {
        BaseStatisticMap map;
        (map = new BaseStatisticMap("keys." + crate.getName())).loadFromRedis();

        statisticMaps.put(crate, map);
    }

    @Override
    public boolean attemptUse(Player player, Crate crate) {
/*        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation()) || SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(player.getUniqueId())) return true;

        player.sendMessage(CC.translate("&cYou can't use this outside of spawn."));*/
        return true;
    }

    public int getKeys(UUID query, Crate crate) {
        BaseStatisticMap map = statisticMaps.get(crate);

        return map.getStatistic(query);
    }

    public void removeKeys(UUID query, Crate crate, int amount) {
        setKeys(query, crate, getKeys(query, crate) - amount);
    }

    public void addKeys(UUID query, Crate crate, int amount) {
        setKeys(query, crate, getKeys(query, crate) + amount);
    }

    public void setKeys(UUID query, Crate crate, int amount) {
        BaseStatisticMap map = statisticMaps.get(crate);

        map.setStatistic(query, amount);
    }

}
