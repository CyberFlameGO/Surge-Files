package dev.lbuddyboy.pcore.robots.animation;

import dev.lbuddyboy.pcore.pCore;
import lombok.Getter;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

@Getter
public class SwingAnimation {

    private static final double TOP = 7d / 6 * Math.PI;
    private static final double BOTTOM = 7d / 4 * Math.PI;

    private final ArmorStand stand;
    private double rot;
    private Motion motion;
    private final double rotChangesPerTick;
    private int blockBreak = 1;

    public SwingAnimation(ArmorStand stand, double cycleTime) {
        this.stand = stand;

        stand.setArms(true);
        //start the animation with the axe at the top
        rot = TOP;
        stand.setRightArmPose(new EulerAngle(TOP, 0, 0));
        motion = Motion.DOWN;
        rotChangesPerTick = Math.abs(TOP - BOTTOM) / (cycleTime / 2);

    }

    public void run() {
        if (blockBreak > 9) blockBreak = 1;
        if (motion == Motion.DOWN) rot += rotChangesPerTick;
        if (motion == Motion.UP) rot -= rotChangesPerTick;

        if (rot <= TOP) {
            motion = Motion.DOWN;
        }
        if (rot >= BOTTOM) {
            motion = Motion.UP;
            blockBreak = 1;
        }

        blockBreak++;
        stand.setRightArmPose(new EulerAngle(rot, 0, 0));
    }

    private enum Motion {
        DOWN, UP
    }
}