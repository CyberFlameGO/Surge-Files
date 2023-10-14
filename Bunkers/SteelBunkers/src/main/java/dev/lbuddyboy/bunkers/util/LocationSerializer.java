//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.bunkers.util;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationSerializer {

    public static Location getLocation(JsonObject object) {
        return new Location(Bukkit.getWorld(object.get("world").getAsString()), object.get("x").getAsDouble(), object.get("y").getAsDouble(), object.get("z").getAsDouble());
    }

    public static JsonObject toJsonObject(Location location) {
        JsonObject object = new JsonObject();
        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        return object;
    }

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
