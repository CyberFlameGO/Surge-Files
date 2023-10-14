//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util;

import com.mongodb.BasicDBObject;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class LocationSerializer {
    private LocationSerializer() {
    }

    public static BasicDBObject serialize(Location location) {
        if (location == null) {
            return new BasicDBObject();
        } else {
            BasicDBObject dbObject = new BasicDBObject();
            dbObject.put("world", location.getWorld().getName());
            dbObject.put("x", location.getX());
            dbObject.put("y", location.getY());
            dbObject.put("z", location.getZ());
            dbObject.append("yaw", location.getYaw());
            dbObject.append("pitch", location.getPitch());
            return dbObject;
        }
    }

    public static Location deserialize(BasicDBObject dbObject) {
        if (dbObject != null && !dbObject.isEmpty()) {
            World world = Samurai.getInstance().getServer().getWorld(dbObject.getString("world"));
            double x = dbObject.getDouble("x");
            double y = dbObject.getDouble("y");
            double z = dbObject.getDouble("z");
            int yaw = dbObject.getInt("yaw");
            int pitch = dbObject.getInt("pitch");
            return new Location(world, x, y, z, (float) yaw, (float) pitch);
        } else {
            return null;
        }
    }

    public static Location deserializeString(String str) {

        String[] args = str.split(":");
        return new Location(Bukkit.getWorld(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2]), Double.parseDouble(args[3]), (float) Double.parseDouble(args[4]), (float) Double.parseDouble(args[5]));
    }

    public static String serializeString(Location location) {

        String str = location.getWorld().getName() + ":";
        str += "" + location.getX() + ":";
        str += "" + location.getY() + ":";
        str += "" + location.getZ() + ":";
        str += "" + location.getYaw() + ":";
        str += "" + location.getPitch() + ":";

        return str;
    }

}
