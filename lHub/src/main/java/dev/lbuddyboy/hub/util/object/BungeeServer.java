package dev.lbuddyboy.hub.util.object;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.util.NumberUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

@RequiredArgsConstructor
@Data
public class BungeeServer {

    public static long RESPONSE_TIME = 10_000L;

    private final String bungeeName, queueName, address;
    private final int port;
    private int onlinePlayers, maxPlayers;
    private long lastResponse;

    public void request() {
        try {
            Socket sock = new Socket(this.address, this.port);
            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            DataInputStream in = new DataInputStream(sock.getInputStream());
            out.write(254);
            StringBuilder str = new StringBuilder();

            int b;
            while((b = in.read()) != -1) {
                if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
                    str.append((char)b);
                }
            }
            String string = str.toString().replace("" + str.toString().charAt(0), "");
            char special = 0;
            for (int i = string.length(); i > 0; i--) {
                if (NumberUtils.isInteger(String.valueOf(string.charAt(i - 1)))) continue;
                special = string.charAt(i - 1);
                break;
            }

            String[] data = string.split(String.valueOf(special));
            int onlinePlayers = Integer.parseInt(data[1]);
            int max = Integer.parseInt(data[2]);

            this.onlinePlayers = onlinePlayers;
            this.maxPlayers = max;
            this.lastResponse = System.currentTimeMillis() + RESPONSE_TIME;
        } catch (Exception ignored) {

        }
    }

    public boolean isOffline() {
        return this.lastResponse - System.currentTimeMillis() < 0;
    }

    public String getStatus() {
        return isOffline() ? lHub.getInstance().getSettingsHandler().getOffline() : lHub.getInstance().getSettingsHandler().getOnline();
    }

    public void connect(Player player) {
        lHub.getInstance().getQueueHandler().getQueueImpl().addToQueue(player, this.queueName);
    }

}
