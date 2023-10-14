package dev.lbuddyboy.pcore.robots.upgrade.impl;

import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.robots.upgrade.RobotUpgrade;

public class FortuneUpgrade extends RobotUpgrade {

    @Override
    public String getId() {
        return "fortune";
    }

    @Override
    public void multiply(pRobot robot) {
        robot.setItemsProduced(robot.getItemsProduced() + (robot.getType().getTokensPerProduce() * robot.getUpgradeLevel(this)));
    }

}
