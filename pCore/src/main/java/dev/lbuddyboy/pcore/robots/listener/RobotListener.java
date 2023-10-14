package dev.lbuddyboy.pcore.robots.listener;

import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTEntity;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import de.tr7zw.nbtapi.data.NBTData;
import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.essential.locator.LocationType;
import dev.lbuddyboy.pcore.essential.plot.PrivatePlot;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.RobotType;
import dev.lbuddyboy.pcore.robots.menu.RobotMenu;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cuboid;
import dev.lbuddyboy.pcore.util.ItemUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RobotListener implements Listener {

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity.getType().equals(EntityType.ARMOR_STAND))) return;

        if (!entity.hasMetadata("robot-id")) return;
        if (!entity.hasMetadata("owner")) return;

        pRobot robot = pCore.getInstance().getRobotHandler().getRobot(entity.getUniqueId());

        if (robot == null) return;

        boolean owner = robot.getOwner().toString().equals(event.getPlayer().getUniqueId().toString());

        if (owner || event.getPlayer().hasPermission("robot.lock.bypass")) {
            if (!owner && event.getPlayer().hasPermission("robot.lock.bypass"))
                event.getPlayer().sendMessage(CC.translate("&cBypassing robot lock..."));
            new RobotMenu(robot, null).openMenu(event.getPlayer());
        } else {
            event.getPlayer().sendMessage(CC.translate("&cYou are not the owner of this robot."));
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityAtInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity.getType().equals(EntityType.ARMOR_STAND))) return;

        if (!entity.hasMetadata("robot-id")) return;
        if (!entity.hasMetadata("owner")) return;

        pRobot robot = pCore.getInstance().getRobotHandler().getRobot(entity.getUniqueId());

        if (robot == null) return;

        boolean owner = robot.getOwner().toString().equals(event.getPlayer().getUniqueId().toString());

        if (owner || event.getPlayer().hasPermission("robot.lock.bypass")) {
            if (!owner && event.getPlayer().hasPermission("robot.lock.bypass"))
                event.getPlayer().sendMessage(CC.translate("&cBypassing robot lock..."));
            new RobotMenu(robot, null).openMenu(event.getPlayer());
        } else {
            event.getPlayer().sendMessage(CC.translate("&cYou are not the owner of this robot."));
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity.getType().equals(EntityType.ARMOR_STAND))) return;
        if (!entity.hasMetadata("robot-id")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (!event.getBlock().hasMetadata("robot-id")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity.getType().equals(EntityType.ARMOR_STAND))) return;
        if (!entity.hasMetadata("robot-id")) return;

        pRobot robot = pCore.getInstance().getRobotHandler().getRobot(entity.getUniqueId());

        if (robot == null) {
            event.setCancelled(true);
            return;
        }

        boolean owner = robot.getOwner().toString().equals(event.getPlayer().getUniqueId().toString());

        if (owner || event.getPlayer().hasPermission("robot.lock.bypass")) {
            if (!owner && event.getPlayer().hasPermission("robot.lock.bypass"))
                event.getPlayer().sendMessage(CC.translate("&cBypassing robot lock..."));
            new RobotMenu(robot, null).openMenu(event.getPlayer());
        } else {
            event.getPlayer().sendMessage(CC.translate("&cYou are not the owner of this robot."));
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        List<pRobot> robots = pCore.getInstance().getRobotHandler().getRobots(player.getUniqueId());

        player.sendMessage(CC.translate("&6&lROBOTS &7" + CC.UNICODE_ARROW_RIGHT + " &fThere has been " + robots.size() + " loaded to your profile!"));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        NBTItem item = new NBTItem(event.getItem());
        if (event.getClickedBlock() == null) return;
        if (!item.hasTag("robot-type")) return;

        Player player = event.getPlayer();
        RobotType type = pCore.getInstance().getRobotHandler().getTypes().get(item.getString("robot-type"));
        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(player.getUniqueId());

        event.setCancelled(true);

        if (!player.getWorld().equals(pCore.getInstance().getPlotHandler().getGrid().getWorld())) {
            player.sendMessage(CC.translate("&cYou can only place robots on your plot!"));
            return;
        }

        if (user == null) return;

        PrivatePlot plot = user.getPlot();

        if (plot == null) {
            player.sendMessage(CC.translate("&cYou can only place robots on your plot!"));
            return;
        }

        if (!plot.getBounds().contains(event.getClickedBlock().getLocation())) {
            player.sendMessage(CC.translate("&cYou can only place robots on your plot!"));
            return;
        }

        Location blockLocation = event.getClickedBlock().getLocation().clone().add(0.5, 1, 0.5), entityLocation = event.getClickedBlock().getLocation();
        List<pRobot> robots = pCore.getInstance().getRobotHandler().getRobots(player.getUniqueId());
        for (pRobot robot : robots) {
            List<Location> locations = new ArrayList<>();
            locations.add(robot.getEntityLocation());
            locations.add(robot.getEntityLocation().clone().add(0, 1, 0));
            locations.add(robot.getEntityLocation().clone().subtract(0, 1, 0));
            locations.add(robot.getBlockLocation());
            locations.add(robot.getBlockLocation().clone().add(0, 1, 0));
            locations.add(robot.getBlockLocation().clone().subtract(0, 1, 0));

            for (Location location : locations) {
                if (blockLocation.getBlockZ() == location.getBlockZ() && blockLocation.getBlockY() == location.getBlockY() && blockLocation.getBlockX() == location.getBlockX()) {
                    player.sendMessage(CC.translate("&cYour robot can't overlap another robot."));
                    return;
                }
                if (entityLocation.getBlockZ() == location.getBlockZ() && entityLocation.getBlockY() == location.getBlockY() && entityLocation.getBlockX() == location.getBlockX()) {
                    player.sendMessage(CC.translate("&cYour robot can't overlap another robot."));
                    return;
                }
            }
        }

        pRobot robot = new pRobot(player.getUniqueId(), type);

        robot.spawnEntity(blockLocation, event.getPlayer().getWorld());
        robot.save();
        pCore.getInstance().getRobotHandler().addRobot(robot);

        player.setItemInHand(ItemUtils.takeItem(player.getItemInHand()));
        pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(player.getLocation(), player.getUniqueId(), LocationType.PLACED_ROBOT));
    }

    @EventHandler
    public void onFall(EntityChangeBlockEvent event) {
        if (!event.getEntity().hasMetadata("robot-id")) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onFall(EntityBlockFormEvent event) {
        if (!event.getEntity().hasMetadata("robot-id")) return;

        event.setCancelled(true);
    }

}
