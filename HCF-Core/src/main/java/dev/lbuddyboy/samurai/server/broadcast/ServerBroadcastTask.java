/*
package dev.lbuddyboy.samurai.server.broadcast;

import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ServerBroadcastTask extends BukkitRunnable {

    private static final List<ServerBroadcast> BROADCASTS = Arrays.asList(
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Hostable-Events",
                    "",
                    CC.MAIN + "STEEL" + CC.WHITE + " ranks can now host",
                    "events that you can get shards & spring keys from!",
                    "",
                    "/host to start hosting!"
            }),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Shards",
                    "",
                    "Shards can be obtained through killing enemies",
                    "and mining! Use shards to purchase keys, lives",
                    "and one-time use gkits!",
                    "",
                    "/shard help for more information"
            }),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, CC.translate(
                    " ",
                    "&9&lTEXTURE PACK (NEW)",
                    "&f",
                    "&fWe have just released &9&lCUSTOM TEXTURES.",
                    " ",
                    "&9/texturepack to enable it."
            ),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, CC.translate(
                    " ",
                    "&x&f&b&9&e&0&9&lABILITY HILL (NEW)",
                    "&f",
                    "&fWe have just released &x&f&b&9&e&0&9&lABILITY HILL.",
                    " ",
                    "&x&f&b&9&e&0&9/t i AbilityHill for more info"
            ),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Schedule",
                    "",
                    "Curious when a big event is?",
                    "You can figure out when it will occur",
                    "by simply doing",
                    "",
                    "/schedule"
            }),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Preference Settings",
                    "",
                    "Want to toggle a specific setting/display that is",
                    "on the server?",
                    "",
                    "You can do /settings to toggle them on/off"
            }),
            new ServerBroadcast(ServerBroadcast.Type.BOTH, new String[]{
                    "Server Voting",
                    "",
                    "Want to get x3 Vote Keys?",
                    "",
                    "You can do /vote and vote on the links."
            }),
            new ServerBroadcast(ServerBroadcast.Type.HCF_ONLY, new String[]{
                    "Seasonal Pass",
                    " ",
                    "Complete challenges to rank up your",
                    "seasonal pass tiers and claim rewards!",
                    "",
                    "/seasonpass for more information"
            })
    );

    private static final List<ServerBroadcast> ACTIVE_BROADCASTS = new ArrayList<>();

    static {
        for (ServerBroadcast broadcast : BROADCASTS) {
            if (broadcast.isActive())
                ACTIVE_BROADCASTS.add(broadcast);
        }
    }

    private int index = -1;

    @Override
    public void run() {
        index++;

        if (index > ACTIVE_BROADCASTS.size() - 1) {
            index = 0;
        }

        ServerBroadcast broadcast = ACTIVE_BROADCASTS.get(index);
        if (broadcast == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(" ");
            player.sendMessage(broadcast.getMessage());
            player.sendMessage(" ");
        }
    }

}
*/
