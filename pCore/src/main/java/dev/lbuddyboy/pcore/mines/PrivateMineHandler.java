package dev.lbuddyboy.pcore.mines;

import com.boydti.fawe.Fawe;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.SchematicFormat;
import dev.lbuddyboy.pcore.mines.command.PrivateMineCommand;
import dev.lbuddyboy.pcore.mines.listener.PrivateMineListener;
import dev.lbuddyboy.pcore.mines.thread.PrivateMineThread;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.LocationUtils;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class PrivateMineHandler implements IModule {

    private final Map<String, MineRank> ranks;
    private Config config;
    private int levelIncrement;
    private File defaultSchematicFile;
    private PagedGUISettings rankGUISettings;
    private MineGrid grid;

    public PrivateMineHandler() {
        this.ranks = new ConcurrentHashMap<>();
    }

    @Override
    public void load(pCore plugin) {
        this.loadListeners();
        this.loadCommands();

        reload();
    }

    @Override
    public void unload(pCore plugin) {

    }

    @Override
    public void reload() {
        this.loadConfig();
        this.grid = new MineGrid(this.config);
        this.loadRanks();
        this.loadTasks();
    }

    private void loadConfig() {
        this.config = new Config(pCore.getInstance(), "private-mines");
        this.rankGUISettings = new PagedGUISettings(this.config, "private-mines-menu");
        this.levelIncrement = this.config.getInt("increase-rank-every");
        this.defaultSchematicFile = new File(
                new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics"),
                this.config.getString("private-mine-settings.schematic-name") + ".schematic");
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PrivateMineListener(), pCore.getInstance());
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().registerCommand(new PrivateMineCommand());
    }

    private void loadRanks() {
        for (String key : this.config.getConfigurationSection("ranks").getKeys(false)) {
            ConfigurationSection section = this.config.getConfigurationSection("ranks");
            HashMap<MaterialData, Double> materials = new HashMap<MaterialData, Double>(){{
                for (String s : section.getStringList(key + ".blocks")) {
                    put(new MaterialData(Material.getMaterial(s.split(";")[0]), (byte) Integer.parseInt(s.split(";")[1])),
                            Double.parseDouble(s.split(";")[2]));
                }
            }};
            String displayName = section.getString(key + ".display-name");
            int mineSize  = section.getInt(key + ".mine-size", 6);
            int blockData  = section.getInt(key + ".block-data", 0);
            MaterialData data = new MaterialData(Material.getMaterial(section.getString(key + ".display-material").split(";")[0]), (byte) Integer.parseInt(section.getString(key + ".display-material").split(";")[1]));

            this.ranks.put(key, new MineRank(key, data, materials, displayName, blockData, mineSize));
        }
    }

    private void loadTasks() {
        Tasks.runTimer(() -> {
            for (MineUser user : pCore.getInstance().getMineUserHandler().getUsers().values()) {
                if (user.getPrivateMine() == null) continue;
                if (!user.getPrivateMine().isResettable()) continue;

                user.getPrivateMine().reset();
            }
        }, 20, 20);
        new PrivateMineThread().start();
    }

    public PrivateMine fetchCache(UUID uuid) {
        return pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid).getPrivateMine();
    }

    public void updateCache(UUID uuid, PrivateMine mine) {
        MineUser owner = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);
        if (owner == null) return;

        owner.setPrivateMine(mine);
        owner.save();
    }

    public PrivateMine fetchPrivateMineAt(Location location) {
        for (MineUser user : pCore.getInstance().getMineUserHandler().getUsers().values()) {
            if (user.getPrivateMine() == null) continue;
            if (!user.getPrivateMine().getBounds().contains(location))  {
                System.out.println("not in bounds");
                System.out.println("bound 1: " + LocationUtils.toString(user.getPrivateMine().getBounds().getLowerNE()));
                System.out.println("bound 2: " + LocationUtils.toString(user.getPrivateMine().getBounds().getUpperSW()));
                continue;
            }

            return user.getPrivateMine();
        }

        return null;
    }

    public PrivateMine createPrivateMine(UUID sender) {
        PrivateMine mine = new PrivateMine(sender);

        grid.createMine(mine);

        return mine;
    }

    public void deletePrivateMine(UUID sender, PrivateMine mine) {
        this.grid.delete(mine);

        MineUser owner = pCore.getInstance().getMineUserHandler().tryMineUserAsync(mine.getOwner());
        if (owner == null) return;

        owner.setPrivateMine(null);
        owner.save();
    }

}
