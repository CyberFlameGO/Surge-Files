package dev.lbuddyboy.pcore.robots.upgrade.impl;

import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.robots.upgrade.RobotUpgrade;

public class EfficiencyUpgrade extends RobotUpgrade {

    @Override
    public String getId() {
        return "efficiency";
    }

    @Override
    public void multiply(pRobot robot) {
        robot.setLastProduceMillis(System.currentTimeMillis() + (robot.getType().getProduceTime() - (robot.getUpgradeLevel(this) * 1000L)));
    }

}
