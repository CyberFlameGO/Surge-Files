package dev.lbuddyboy.lqueue.util;

import dev.lbuddyboy.lqueue.lQueue;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class BungeeUtils {

    public static void sendPlayerToServer(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(lQueue.getInstance(), "BungeeCord", b.toByteArray());
            b.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
