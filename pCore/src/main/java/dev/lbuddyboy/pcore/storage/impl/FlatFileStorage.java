package dev.lbuddyboy.pcore.storage.impl;

import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.storage.IStorage;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FlatFileStorage implements IStorage {

    private File playerDataFile;

    @Override
    public String getId() {
        return "FLAT_FILE";
    }

    @Override
    public FlatFileStorage initialize() {
        this.playerDataFile = new File(pCore.getInstance().getDataFolder(), "player-data");
        if (!playerDataFile.exists()) this.playerDataFile.mkdir();

        Bukkit.getConsoleSender().sendMessage(CC.translate("&fStorage Type: &5FLAT FILE"));
        return this;
    }

    @Override
    public ItemLocation loadLocation(UUID id, boolean async) {
        return null;
    }

    @Override
    public void updateLocation(UUID id, ItemLocation location, boolean async) {

    }

    @Override
    public void wipeLocation(UUID id, boolean async) {

    }

    @Override
    public MineUser loadMineUser(UUID id, boolean async) {
        return null;
    }

    @Override
    public void saveMineUser(UUID id, MineUser user, boolean async) {

    }

    @Override
    public void wipeMineUser(UUID id, boolean async) {

    }

    @Override
    public List<pRobot> loadRobots(boolean async) {
        return null;
    }

    @Override
    public void insertRobots(List<pRobot> robots, boolean async) {

    }

    @Override
    public void removeRobots(List<pRobot> robots, boolean async) {

    }

    @Override
    public void loadData(Map<UUID, ?> map, String key, Type type, boolean async) {
        for (String s : this.playerDataFile.list()) {
            UUID uuid = UUID.fromString(s.replaceAll(".yml", ""));
//            YMLBase yml = new YMLBase(pcore.getInstance(), uuid.toString());
//            FileConfiguration config = yml.getConfiguration();
//
//            map.put(uuid, GSONUtils.getGSON().fromJson(config.getString(key), type));
        }
    }

    @Override
    public Map<UUID, ?> loadData(String key, Type type, boolean async) {
        return null;
    }

    @Override
    public Object loadData(UUID uuid, String key, Type type, boolean async) {
        return null;
    }

    @Override
    public void insertData(UUID uuid, String key, Object data, Type type, boolean async) {
        insertData(uuid, new String[]{key}, new Object[]{data}, new Type[]{type}, async);
    }

    @Override
    public void insertData(UUID uuid, String[] key, Object[] data, Type[] types, boolean async) {
//        YMLBase yml = new YMLBase(pcore.getInstance(), uuid.toString());
//        FileConfiguration config = yml.getConfiguration();
//        int i = 0;
//
//        for (String s : key) {
//            config.set(s, GSONUtils.getGSON().toJson(data, TypeToken.get(data[i++].getClass()).getType()));
//        }
//
//        yml.save();
    }

    @Override
    public void wipeData(UUID uuid, boolean async) {
        try {
            Files.deleteIfExists(new File(this.playerDataFile, uuid.toString() + ".yml").toPath());
        } catch (IOException e) {
            Bukkit.getLogger().info("Problem wiping the data of " + uuid);
        }
    }

}
