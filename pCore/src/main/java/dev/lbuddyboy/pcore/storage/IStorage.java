package dev.lbuddyboy.pcore.storage;

import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.user.MineUser;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IStorage {

    String getId();
    IStorage initialize();

    /*
    Additional Methods
     */

    ItemLocation loadLocation(UUID id, boolean async);
    void updateLocation(UUID id, ItemLocation location, boolean async);
    void wipeLocation(UUID id, boolean async);

    MineUser loadMineUser(UUID id, boolean async);
    void saveMineUser(UUID id, MineUser user, boolean async);
    void wipeMineUser(UUID id, boolean async);

    List<pRobot> loadRobots(boolean async);
    void insertRobots(List<pRobot> robots, boolean async);
    void removeRobots(List<pRobot> robots, boolean async);

    void loadData(Map<UUID, ?> map, String key, Type type, boolean async);
    Map<UUID, ?> loadData(String key, Type type, boolean async);
    Object loadData(UUID uuid, String key, Type type, boolean async);
    void insertData(UUID uuid, String key, Object data, Type type, boolean async);
    void insertData(UUID uuid, String[] key, Object[] data, Type[] type, boolean async);
    void wipeData(UUID uuid, boolean async);

}
