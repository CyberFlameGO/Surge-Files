package dev.lbuddyboy.pcore.robots;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.command.RobotCommand;
import dev.lbuddyboy.pcore.robots.listener.RobotListener;
import dev.lbuddyboy.pcore.robots.thread.RobotThread;
import dev.lbuddyboy.pcore.robots.upgrade.RobotUpgrade;
import dev.lbuddyboy.pcore.robots.upgrade.impl.EfficiencyUpgrade;
import dev.lbuddyboy.pcore.robots.upgrade.impl.FortuneUpgrade;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.menu.GUISettings;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class RobotHandler implements IModule {

    private final Map<UUID, pRobot> robots;
    private final Map<UUID, List<pRobot>> plotRobots;
    private final Map<String, RobotType> types;
    private List<RobotUpgrade> upgrades;
    private Config config;
    private File robotFolder;
    private GUISettings mainMenuSettings;
    private PagedGUISettings upgradeMenuSettings, robotListMenuSettings;

    public RobotHandler() {
        this.robots = new ConcurrentHashMap<>();
        this.plotRobots = new ConcurrentHashMap<>();
        this.types = new HashMap<>();
    }

    @Override
    public void load(pCore plugin) {
        this.loadListeners();
        this.loadCommands();
        reload();

        for (pRobot robot : plugin.getStorageHandler().getStorage().loadRobots(true)) {
            addRobot(robot);
        }
    }

    @Override
    public void unload(pCore plugin) {
        pCore.getInstance().getStorageHandler().getStorage().insertRobots(this.robots.values().stream().filter(pRobot::isUpdated).collect(Collectors.toList()), false);
    }

    @Override
    public void reload() {
        this.loadConfig();
        this.loadUpgrades();
        this.loadGUISettings();
        this.loadDirectories();
        this.loadDefaults();
        this.loadTypes();
        this.loadThreads();
    }

    @Override
    public void save() {
        pCore.getInstance().getStorageHandler().getStorage().insertRobots(this.robots.values().stream().filter(pRobot::isUpdated).collect(Collectors.toList()), true);
    }

    private void loadConfig() {
        this.config = new Config(pCore.getInstance(), "robots");
    }

    private void loadUpgrades() {
        this.upgrades = new ArrayList<>(Arrays.asList(
                new EfficiencyUpgrade(),
                new FortuneUpgrade()
        ));
    }

    private void loadGUISettings() {
        this.mainMenuSettings = new GUISettings(this.config.getConfigurationSection("robot-menu"));
        this.upgradeMenuSettings = new PagedGUISettings(this.config, "upgrade-menu");
        this.robotListMenuSettings = new PagedGUISettings(this.config, "list-menu");
    }

    private void loadDirectories() {
        this.robotFolder = new File(pCore.getInstance().getDataFolder(), "robot-types");

        if (!this.robotFolder.exists()) this.robotFolder.mkdir();
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new RobotListener(), pCore.getInstance());
    }
    private void loadCommands() {
        pCore.getInstance().getCommandManager().registerCommand(new RobotCommand());
    }
    private void loadTypes() {
        for (String key : this.robotFolder.list()) {
            String name = key.replaceAll(".yml", "");
            this.types.put(name, new RobotType(new Config(pCore.getInstance(), name, this.robotFolder)));
        }
        Bukkit.getConsoleSender().sendMessage("[Robot Handler] Loaded " + this.types.size() + " robot types.");

    }

    private void loadDefaults() {
        if (!this.types.isEmpty()) return;

        this.types.put("lapis", new RobotType(new Config(pCore.getInstance(), "lapis", this.robotFolder)));
    }

    private void loadThreads() {
        new RobotThread().start();
    }

    public RobotUpgrade getUpgrade(String id) {
        for (RobotUpgrade upgrade : this.upgrades) {
            if (Objects.equals(upgrade.getId(), id)) return upgrade;
        }

        return null;
    }

    public List<pRobot> getRobots(UUID query) {
        return new ArrayList<>(this.plotRobots.getOrDefault(query, new ArrayList<>()));
    }

    public pRobot getRobot(UUID id) {
        return this.robots.getOrDefault(id, null);
    }

    public void addRobot(pRobot robot) {
        List<pRobot> robots = new ArrayList<>(this.plotRobots.getOrDefault(robot.getOwner(), new ArrayList<>()));

        robots.add(robot);
        this.robots.put(robot.getId(), robot);
        this.plotRobots.put(robot.getOwner(), robots);
    }

    public void removeRobot(pRobot robot) {
        this.robots.remove(robot.getId());

        List<pRobot> robots = getRobots(robot.getOwner());

        for (pRobot other : getRobots(robot.getOwner())) {
            if (other == robot) {
                robots.remove(other);
                break;
            }
        }

        this.plotRobots.put(robot.getOwner(), robots);
        pCore.getInstance().getStorageHandler().getStorage().removeRobots(Collections.singletonList(robot), true);
    }

}
