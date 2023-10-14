package dev.drawethree.xprison.utils.misc;

import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.pCore;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class MineUtils {

    public static PrivateMine getPrivateMineAtCache(Location location) {
        return pCore.getInstance().getPrivateMineHandler().fetchPrivateMineAt(location);
    }

    public static PrivateMine getPrivateMineAt(Location location) {
        return getPrivateMineAtCache(location);
    }

}
