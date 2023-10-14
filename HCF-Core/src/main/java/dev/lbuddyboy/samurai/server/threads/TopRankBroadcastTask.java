package dev.lbuddyboy.samurai.server.threads;

import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class TopRankBroadcastTask extends BukkitRunnable {

    @Override
    public void run() {
        List<Player> onlineTopRanks = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("top.rank")) {
                if (!player.hasPermission("foxtrot.staff")) {
                    onlineTopRanks.add(player);
                }
            }
        }

        if (!onlineTopRanks.isEmpty()) {
            StringBuilder builder = new StringBuilder(CC.translate("&5&lOnline LEGEND Ranks&f: "));

            for (Player player : onlineTopRanks) {
                builder.append(ChatColor.WHITE).append(player.getName()).append(ChatColor.GRAY).append(", ");
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("");
                player.sendMessage(builder.substring(0, builder.length() - 2));
                player.sendMessage("");
                player.sendMessage(CC.translate("&7Purchase &5Legend Rank&7 @ https://store.minesurge.org in our /coinshop"));
            }
        }
    }

}
