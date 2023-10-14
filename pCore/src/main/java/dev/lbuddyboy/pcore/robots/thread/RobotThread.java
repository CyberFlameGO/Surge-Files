package dev.lbuddyboy.pcore.robots.thread;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.robots.upgrade.RobotUpgrade;
import dev.lbuddyboy.pcore.user.MineUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class RobotThread extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                tick();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            List<pRobot> robots = pCore.getInstance().getRobotHandler().getRobots(player.getUniqueId());
            for (pRobot robot : robots) {
                robot.updatePosition();

                if (!robot.isReady()) continue;

                robot.setLastProduceMillis(System.currentTimeMillis() + robot.getResetTime());
                robot.setItemsProduced(robot.getItemsProduced() + robot.getType().getTokensPerProduce());

                for (RobotUpgrade upgrade : robot.getUpgrades().keySet()) {
                    if (robot.getUpgradeLevel(upgrade) >= 1) upgrade.multiply(robot);
                }
            }
        }
    }


}
