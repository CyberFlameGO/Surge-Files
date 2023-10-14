package dev.lbuddyboy.pcore.robots;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTType;
import de.tr7zw.nbtapi.plugin.NBTAPI;
import dev.lbuddyboy.pcore.essential.plot.PrivatePlot;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.animation.SwingAnimation;
import dev.lbuddyboy.pcore.robots.menu.RobotMenu;
import dev.lbuddyboy.pcore.robots.menu.RobotUpgradeMenu;
import dev.lbuddyboy.pcore.robots.upgrade.RobotUpgrade;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.LocationUtils;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.Data;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class pRobot {

    private UUID id, owner;
    private RobotType type;
    private long lastProduceMillis, itemsProduced;
    private Location entityLocation, blockLocation;

    private transient Map<RobotUpgrade, Integer> upgrades = new HashMap<>();
    private transient ArmorStand entity;
    private transient Block block;
    private transient SwingAnimation animation;
    private transient boolean updated = false;

    public pRobot(UUID owner, RobotType type) {
        this.owner = owner;
        this.type = type;
    }

    public pRobot(UUID id, UUID owner, RobotType type, long lastProduceMillis, long itemsProduced) {
        this.id = id;
        this.owner = owner;
        this.type = type;
        this.lastProduceMillis = lastProduceMillis;
        this.itemsProduced = itemsProduced;
    }

    public long getNextProduceMillis() {
        return (this.lastProduceMillis + this.type.getProduceTime()) - System.currentTimeMillis();
    }

    public boolean isReady() {
        return getNextProduceMillis() <= 0;
    }

    public void spawnEntity(Location location, World world) {
        this.lastProduceMillis = System.currentTimeMillis();

        ArmorStand stand = world.spawn(location, ArmorStand.class);

        this.id = stand.getUniqueId();
        stand.setHelmet(this.type.getHelmet());
        stand.setChestplate(this.type.getChestplate());
        stand.setLeggings(this.type.getLeggings());
        stand.setBoots(this.type.getBoots());
        stand.setItemInHand(this.type.getHeldItem());
        stand.setSmall(true);
        stand.setGravity(false);
        stand.setBasePlate(false);
        stand.setArms(true);
        stand.setVisible(false);
        stand.setCustomName(CC.translate(type.getDisplayName()));
        stand.setCustomNameVisible(true);

        for (RobotUpgrade upgrade : pCore.getInstance().getRobotHandler().getUpgrades()) {
            this.upgrades.put(upgrade, 0);
        }

        this.entity = stand;
        this.block = this.entity.getLocation().clone().add(0, 0, 1).getBlock();
        this.block.setTypeIdAndData(this.type.getBlockData().getItemType().getId(), this.type.getBlockData().getData(), false);
        this.entityLocation = this.entity.getLocation();
        this.blockLocation = this.block.getLocation();
        this.updateMetadata();
    }

    public void save() {
        updated = true;
    }

    public void delete() {
        pCore.getInstance().getRobotHandler().removeRobot(this);

        if (this.entity == null) {
            PrivatePlot cache = pCore.getInstance().getPlotHandler().fetchCache(this.owner);

            if (cache != null) {
                for (Entity e : cache.getBounds().getWorld().getEntities()) {
                    if (!e.getUniqueId().toString().equals(this.id.toString())) continue;
                    if (!cache.getBounds().contains(e.getLocation())) continue;

                    this.entity = (ArmorStand) e;
                    this.block = this.entity.getLocation().clone().add(0, 0, 1).getBlock();
                    break;
                }
            }
        }

        this.entity.remove();
        this.block.setType(Material.AIR);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!Menu.openedMenus.containsKey(player.getName())) continue;
            if (Menu.openedMenus.get(player.getName()) instanceof RobotMenu) {
                RobotMenu menu = (RobotMenu) Menu.openedMenus.get(player.getName());
                if (menu.getRobot() != this) continue;

                player.closeInventory();
                player.sendMessage(CC.translate("&cThis robot has been picked up by someone, so your menu view was closed."));
                continue;
            }
            if (Menu.openedMenus.get(player.getName()) instanceof RobotUpgradeMenu) {
                RobotUpgradeMenu menu = (RobotUpgradeMenu) Menu.openedMenus.get(player.getName());
                if (menu.getRobot() != this) continue;

                player.closeInventory();
                player.sendMessage(CC.translate("&cThis robot has been picked up by someone, so your menu view was closed."));
            }

        }
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("id", this.id.toString());
        object.addProperty("owner", this.owner.toString());
        object.addProperty("type", this.type.getName());
        object.addProperty("lastProduceMillis", this.lastProduceMillis);
        object.addProperty("itemsProduced", this.itemsProduced);

        for (Map.Entry<RobotUpgrade, Integer> entry : this.upgrades.entrySet()) {
            object.addProperty("upgrade-" + entry.getKey().getId(), entry.getValue());
        }

        if (this.entityLocation != null) object.addProperty("entityLocation", LocationUtils.serializeString(this.entityLocation));
        if (this.blockLocation != null) object.addProperty("blockLocation", LocationUtils.serializeString(this.blockLocation));

        return object;
    }

    public static pRobot deserialize(JsonObject object) {
        pRobot robot = new pRobot(
                UUID.fromString(object.get("id").getAsString()),
                UUID.fromString(object.get("owner").getAsString()),
                pCore.getInstance().getRobotHandler().getTypes().get(object.get("type").getAsString()),
                object.get("lastProduceMillis").getAsLong(),
                object.get("itemsProduced").getAsLong()
        );

        if (object.has("entityLocation")) robot.setEntityLocation(LocationUtils.deserializeString(object.get("entityLocation").getAsString()));
        if (object.has("blockLocation")) robot.setBlockLocation(LocationUtils.deserializeString(object.get("blockLocation").getAsString()));

        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            if (!(entry.getKey().startsWith("upgrade-"))) continue;
            RobotUpgrade upgrade = pCore.getInstance().getRobotHandler().getUpgrade(entry.getKey().replaceAll("upgrade-", ""));

            robot.getUpgrades().put(upgrade, object.get("upgrade-" + upgrade.getId()).getAsInt());
        }

        if (robot.getEntity() == null) {
            World world = pCore.getInstance().getPlotHandler().getGrid().getWorld();
            world.loadChunk(robot.getBlockLocation().getChunk());
            if (!robot.getEntityLocation().getChunk().isLoaded()) world.loadChunk(robot.getEntityLocation().getChunk());

            Optional<Entity> entityOpt = world.getEntitiesByClasses(ArmorStand.class).stream().filter(a -> a.getUniqueId().toString().equals(robot.id.toString())).findFirst();

            if (entityOpt.isPresent()) {
                robot.entity = (ArmorStand) entityOpt.get();
                robot.block = robot.entity.getLocation().clone().add(0, 0, 1).getBlock();

                robot.updateMetadata();
            }
        }

        return robot;
    }

    public void updatePosition() {
        if (this.animation == null && this.entity != null)
            this.animation = new SwingAnimation(this.entity, 15);

        if (this.animation != null) {
            animation.run();
            sendBreakPacket(this.block.getWorld(), block.getLocation(), animation.getBlockBreak());
        }
    }

    public void sendBreakPacket(World world, Location location, int data) {
        BlockPosition bp = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(location.getBlockX() + location.getBlockY() + location.getBlockZ(), bp, data);
        for (Player player : world.getPlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public long getResetTime() {
        for (RobotUpgrade upgrade : this.upgrades.keySet()) {
            if (upgrade.getId().equalsIgnoreCase("efficiency"))
                return this.type.getProduceTime() - (this.getUpgradeLevel(upgrade) * 1000L);
        }
        return this.type.getProduceTime();
    }

    public int getUpgradeLevel(RobotUpgrade upgrade) {
        return this.upgrades.getOrDefault(upgrade, 0);
    }

    public List<String> getFormattedUpgrades() {
        return this.upgrades.entrySet().stream().map(entry -> pCore.getInstance().getRobotHandler().getConfig().getString("upgrade-list-format", "&d&l| &f%upgrade% %upgrade-level%")
                .replaceAll("%upgrade%", entry.getKey().getDisplayName())
                .replaceAll("%upgrade-level%", "" + entry.getValue())).collect(Collectors.toList());
    }

    public double getNextUpgradeCost(RobotUpgrade upgrade) {
        return upgrade.getCostIncrement() * (getUpgradeLevel(upgrade) + 1);
    }

    public ArmorStand getBukkitEntity() {
        if (this.entity == null) {
            World world = pCore.getInstance().getPlotHandler().getGrid().getWorld();
            Optional<Entity> entityOpt = world.getEntitiesByClasses(ArmorStand.class).stream().filter(a -> a.getUniqueId().toString().equals(this.id.toString())).findFirst();

            if (entityOpt.isPresent()) {
                this.entity = (ArmorStand) entityOpt.get();
                this.block = this.entity.getLocation().clone().add(0, 0, 1).getBlock();
                this.updateMetadata();
            }
        }
        return this.entity;
    }

    public void updateMetadata() {
        this.entity.setMetadata("robot-id", new FixedMetadataValue(pCore.getInstance(), this.id.toString()));
        this.entity.setMetadata("owner", new FixedMetadataValue(pCore.getInstance(), this.owner.toString()));
        this.block.setMetadata("robot-id", new FixedMetadataValue(pCore.getInstance(), this.id.toString()));
        this.block.setMetadata("owner", new FixedMetadataValue(pCore.getInstance(), this.owner.toString()));
    }

}
