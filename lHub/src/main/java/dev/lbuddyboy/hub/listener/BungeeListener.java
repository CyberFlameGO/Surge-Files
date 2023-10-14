package dev.lbuddyboy.hub.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.hub.lHub;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeListener extends Thread implements PluginMessageListener {

    public static int PLAYER_COUNT;

    public static void updateCount(Player player) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("PlayerCount");
        output.writeUTF("ALL");
        player.sendPluginMessage(lHub.getInstance(), "BungeeCord", output.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equalsIgnoreCase("BungeeCord")) return;

        ByteArrayDataInput input = ByteStreams.newDataInput(message);

        String subchannel = input.readUTF();

        if (subchannel.equals("PlayerCount") && input.readUTF().equalsIgnoreCase("ALL")) {
            BungeeListener.PLAYER_COUNT = SpoofHandler.getSpoofedCount(input.readInt());
        }
    }

    public static void sendPlayerToServer(Player player, String serverName) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(lHub.getInstance(), "BungeeCord", out.toByteArray());
    }

    static {
        BungeeListener.PLAYER_COUNT = 0;
    }

    @Override
    public void run() {
        while (lHub.getInstance().isEnabled()) {
            try {
                Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(BungeeListener::updateCount);
            } catch (Exception e) {

            }

            try {
                sleep(5000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
