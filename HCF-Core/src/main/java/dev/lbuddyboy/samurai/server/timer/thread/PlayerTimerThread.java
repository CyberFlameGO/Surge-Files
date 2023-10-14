package dev.lbuddyboy.samurai.server.timer.thread;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.server.timer.PlayerTimer;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.object.ScoreFunction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerTimerThread extends Thread {

    private long lastErrored = System.currentTimeMillis();

    public PlayerTimerThread() {
        super("Player Timer - Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (Samurai.getInstance().isEnabled()) {

            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Samurai.getInstance().getArenaHandler().isDeathbanned(player.getUniqueId())) {
                        long seconds = (Samurai.getInstance().getDeathbanMap().getDeathban(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                        String bar = CC.translate(
                                "&cLives&7: &f" + Samurai.getInstance().getFriendLivesMap().getLives(player.getUniqueId()) + " &f" + CC.UNICODE_VERTICAL_BAR +
                                " &cTime Left&7: &f" + TimeUtils.formatLongIntoDetailedString(seconds) + " &f"
                        );

                        if (SpawnTagHandler.isTagged(player)) {
                            bar += CC.translate(CC.UNICODE_VERTICAL_BAR + " &cSpawn Tag&7: &f" + getSpawnTagScore(player));
                        }

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(bar));
                    } else if (!player.hasMetadata("modmode")) {
                        Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());
                        String team = "&7Wilderness";
                        if (teamAt != null) {
                            team = teamAt.getName(player);
                            if (teamAt.getName().equalsIgnoreCase("deepdark")) {
                                team = CC.DARK_AQUA + "Deep Dark";
                            }
                        } else if (Samurai.getInstance().getServerHandler().isWarzone(player.getLocation())) {
                            team = "&cWarZone";
                        }

                        List<PlayerTimer> timers = Samurai.getInstance().getTimerHandler().getPlayerTimers(player);
                        List<String> mapped = new ArrayList<>(Collections.singleton("&6Claim&7: " + team));
                        mapped.addAll(timers.stream().map(timer -> timer.getDisplayName() + "&7: &f" + timer.getRemainingString(player) + "&7").toList());

                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(CC.translate(StringUtils.join(mapped, " "))));
                    }
                }
            } catch (Exception e) {
                if (lastErrored + 5_000L > System.currentTimeMillis()) continue;
                lastErrored = System.currentTimeMillis();
                e.printStackTrace();
            }

            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getSpawnTagScore(Player player) {
        if (SpawnTagHandler.isTagged(player)) {
            float diff = SpawnTagHandler.getTag(player);

            if (diff >= 0) {
                return (ScoreFunction.TIME_SIMPLE.apply(diff / 1000F));
            }
        }

        return (null);
    }

}
