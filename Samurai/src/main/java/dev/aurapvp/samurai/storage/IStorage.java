package dev.aurapvp.samurai.storage;

import dev.aurapvp.samurai.essential.locator.ItemLocation;
import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.player.SamuraiPlayer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface IStorage {

    String getId();
    IStorage initialize();

    /*
    Additional Methods
     */

    Map<UUID, Faction> loadFactions(Map<UUID, Faction> map, String key, boolean async);
    void insertFaction(UUID uuid, String key, Object data, boolean async);
    void wipeFaction(UUID uuid, boolean async);

    ItemLocation loadLocation(UUID id, boolean async);
    void updateLocation(UUID id, ItemLocation location, boolean async);
    void wipeLocation(UUID id, boolean async);

    SamuraiPlayer loadPlayer(UUID id, boolean async);
    void updatePlayer(UUID id, SamuraiPlayer player, boolean async);
    void deletePlayer(UUID id, boolean async);

    void loadData(Map<UUID, ?> map, String key, Type type, boolean async);
    Object loadData(UUID uuid, String key, Type type, boolean async);
    void insertData(UUID uuid, String key, Object data, Type type, boolean async);
    void insertData(UUID uuid, String[] key, Object[] data, Type[] type, boolean async);
    void wipeData(UUID uuid, boolean async);

}
