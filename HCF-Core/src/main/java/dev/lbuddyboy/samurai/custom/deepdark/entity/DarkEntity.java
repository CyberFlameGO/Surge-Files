package dev.lbuddyboy.samurai.custom.deepdark.entity;

import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.level.World;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bson.types.ObjectId;
import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Warden;

import java.util.*;

@RequiredArgsConstructor
@Data
public class DarkEntity {

    private final UUID owner;
    private final Warden warden;
    private final Location spawnLocation;
    private long spawnedAt = System.currentTimeMillis();
    private DarkStage stage = DarkStage.SPAWNING;
    private BossBar bossBar;
    private Map<UUID, Double> damage = new HashMap<>();
    private Map<UUID, List<LootTableItem>> rewards = new HashMap<>();

    public Map<UUID, Double> getTopDamagers() {
        LinkedList<Map.Entry<UUID, Double>> list = new LinkedList<>(this.damage.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        LinkedHashMap<UUID, Double> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<UUID, Double> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return sortedHashMap;
    }

    public List<UUID> getDamagers() {
        return new ArrayList<>(getTopDamagers().keySet());
    }

}
