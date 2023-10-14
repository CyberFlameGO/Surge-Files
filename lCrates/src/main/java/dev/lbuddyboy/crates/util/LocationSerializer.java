//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.crates.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class LocationSerializer {

    public static Location deserializeString(String str) {

        String[] args = str.split(":");
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]));
    }

    public static String serializeString(Location location) {

        String str = location.getWorld().getName() + ":";
        str += "" + location.getX() + ":";
        str += "" + location.getY() + ":";
        str += "" + location.getZ() + ":";

        return str;
    }

}
