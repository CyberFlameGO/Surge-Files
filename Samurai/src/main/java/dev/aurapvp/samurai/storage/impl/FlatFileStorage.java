package dev.aurapvp.samurai.storage.impl;

import com.google.gson.reflect.TypeToken;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.essential.locator.ItemLocation;
import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.storage.IStorage;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.gson.GSONUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

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
        this.playerDataFile = new File(Samurai.getInstance().getDataFolder(), "player-data");
        if (!playerDataFile.exists()) this.playerDataFile.mkdir();

        Bukkit.getConsoleSender().sendMessage(CC.translate("&fStorage Type: &5FLAT FILE"));
        return this;
    }

    @Override
    public Map<UUID, Faction> loadFactions(Map<UUID, Faction> map, String key, boolean async) {
        return null;
    }

    @Override
    public void insertFaction(UUID uuid, String key, Object data, boolean async) {

    }

    @Override
    public void wipeFaction(UUID uuid, boolean async) {

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
    public SamuraiPlayer loadPlayer(UUID id, boolean async) {
        return null;
    }

    @Override
    public void updatePlayer(UUID id, SamuraiPlayer player, boolean async) {

    }

    @Override
    public void deletePlayer(UUID id, boolean async) {

    }

    @Override
    public void loadData(Map<UUID, ?> map, String key, Type type, boolean async) {
        for (String s : this.playerDataFile.list()) {
            UUID uuid = UUID.fromString(s.replaceAll(".yml", ""));
//            YMLBase yml = new YMLBase(Samurai.getInstance(), uuid.toString());
//            FileConfiguration config = yml.getConfiguration();
//
//            map.put(uuid, GSONUtils.getGSON().fromJson(config.getString(key), type));
        }
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
//        YMLBase yml = new YMLBase(Samurai.getInstance(), uuid.toString());
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
