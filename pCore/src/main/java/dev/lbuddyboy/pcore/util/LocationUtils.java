package dev.lbuddyboy.pcore.util;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocationUtils {

    public static Cuboid deserializeStringCuboid(String str) {
        String[] locations = str.split(",");

        return new Cuboid(deserializeString(locations[0]), deserializeString(locations[1]));
    }

    public static String serializeStringCuboid(Cuboid cuboid) {

        Location loc1 = cuboid.getLowerNE();
        Location loc2 = cuboid.getUpperSW();

        String str = loc1.getWorld().getName() + ";";
        str += "" + loc1.getX() + ";";
        str += "" + loc1.getY() + ";";
        str += "" + loc1.getZ() + ";";
        str += "" + loc1.getYaw() + ";";
        str += "" + loc1.getPitch() + ";";

        str += ",";

        str += "" + loc2.getWorld().getName() + ";";
        str += "" + loc2.getX() + ";";
        str += "" + loc2.getY() + ";";
        str += "" + loc2.getZ() + ";";
        str += "" + loc2.getYaw() + ";";
        str += "" + loc2.getPitch() + ";";

        return str;
    }

    public static Location deserializeString(String str) {
        String[] args = str.split(";");
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), Float.parseFloat(args[4]), Float.parseFloat(args[5]));
    }

    public static String serializeString(Location location) {

        String str = location.getWorld().getName() + ";";
        str += "" + location.getX() + ";";
        str += "" + location.getY() + ";";
        str += "" + location.getZ() + ";";
        str += "" + location.getYaw() + ";";
        str += "" + location.getPitch() + ";";

        return str;
    }

    public static JsonObject toJSON(Location location) {
        JsonObject object = new JsonObject();

        object.addProperty("world", location.getWorld().getName());
        object.addProperty("x", location.getX());
        object.addProperty("y", location.getY());
        object.addProperty("z", location.getZ());
        object.addProperty("yaw", location.getYaw());
        object.addProperty("pitch", location.getPitch());

        return object;
    }

    public static Location fromJSON(JsonObject object) {
        return new Location(
                Bukkit.getWorld(object.get("world").getAsString()),
                object.get("x").getAsDouble(),
                object.get("y").getAsDouble(),
                object.get("z").getAsDouble(),
                object.get("yaw").getAsFloat(),
                object.get("pitch").getAsFloat()
        );
    }

    public static String formatLocation(Location location) {
        if (location == null) return CC.RED + "None";
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

    public static String formatLocationXZ(Location location) {
        if (location == null) return CC.RED + "None";
        return location.getBlockX() + ", " + location.getBlockZ();
    }
    public static void locationToConfigSect(Location location, String key, FileConfiguration config) {

        config.set(key + ".x", location.getX());
        config.set(key + ".y", location.getX());
        config.set(key + ".z", location.getX());
        config.set(key + ".yaw", location.getX());
        config.set(key + ".pitch", location.getPitch());

    }

    public static double[] locationFromConfigSect(String key, Config section) {
        return new double[]{
                section.getDouble(key + ".x"),
                section.getDouble(key + ".y"),
                section.getDouble(key + ".z"),
                (float) section.getDouble(key + ".yaw"),
                (float) section.getDouble(key + ".pitch")
        };
    }

    public static Location getLocation(double[] array, World world) {
        return new Location(
                world,
                array[0],
                array[1],
                array[2],
                (float) array[3],
                (float) array[4]
        );
    }

    public static String toString(Location location) {
        return location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }

}
