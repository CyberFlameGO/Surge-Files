package dev.lbuddyboy.lmotd.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import dev.lbuddyboy.lmotd.MOTD;
import dev.lbuddyboy.lmotd.lMOTD;
import dev.lbuddyboy.lmotd.model.MOTDTimer;
import dev.lbuddyboy.lmotd.util.CC;
import dev.lbuddyboy.lmotd.util.Components;
import dev.lbuddyboy.lmotd.util.PingResponse;
import dev.lbuddyboy.lmotd.util.TimeUtils;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.newline;

public class MOTDListener {

    @Subscribe
    public void onPing(ProxyPingEvent event) {
        this.handle(event, lMOTD.getInstance().getActiveTimer() != null, event.getConnection().getProtocolVersion().isLegacy());
    }

    @Subscribe
    public void onConnect(ServerPreConnectEvent event) {
        if (!event.getOriginalServer().getServerInfo().getName().startsWith("Hub-")) return;

        hub(event);
    }

    public static void hub(ServerPreConnectEvent event) {
        if (event.getPlayer().getCurrentServer().isPresent()) return;

        List<RegisteredServer> hubs = lMOTD.getInstance().getServer().getAllServers().stream().filter(s -> s.getServerInfo().getName().startsWith("Hub-")).collect(Collectors.toList());
        RegisteredServer hub = hubs.get(ThreadLocalRandom.current().nextInt(0, hubs.size()));

        event.setResult(ServerPreConnectEvent.ServerResult.allowed(hub));
    }

    private void handle(final ProxyPingEvent event, boolean timer, boolean legacy) {
        final ServerPing.Builder pong = event.getPing().asBuilder();

        int players = getSpoofedCount(lMOTD.getInstance().getServer().getPlayerCount());

        PingResponse<Favicon> response = createMOTD(players, pong.getMaximumPlayers(), timer, legacy);

        pong.onlinePlayers(response.playerCount().onlinePlayers());
        response.icon(pong::favicon);
        response.motd(pong::description);
        response.playerCount().applyCount(pong::onlinePlayers, pong::maximumPlayers);

        if (response.disablePlayerListHover()) {
            pong.clearSamplePlayers();
        }
        if (response.hidePlayerCount()) {
            pong.nullPlayers();
        }

        event.setPing(pong.build());
    }

    public PingResponse<Favicon> createMOTD(final int onlinePlayers, final int maxPlayers, boolean timer, boolean legacy) {
        final PingResponse.Builder<Favicon> response = PingResponse.<Favicon>builder()
                .playerCount(PingResponse.PlayerCount.playerCount(onlinePlayers, maxPlayers))
                .disablePlayerListHover(false)
                .hidePlayerCount(false);

        MOTD randomMOTD = lMOTD.getInstance().getMotds().get(ThreadLocalRandom.current().nextInt(0, lMOTD.getInstance().getMotds().size()));
        Component motd = null;

        if (timer) {
            MOTDTimer motdTimer = lMOTD.getInstance().getActiveTimer();
            randomMOTD = lMOTD.getInstance().getActiveTimerMOTD();

            if (legacy) {
                motd = Components.ofChildren(
                        CC.translate(randomMOTD.getNewLines().get(0)
                                .replaceAll("%timer%", motdTimer.getTitle())
                                .replaceAll("%remaining%", TimeUtils.formatIntoShortenedDetailedString((int) (motdTimer.getRemaining() / 1000)))
                        ),
                        newline(),
                        CC.translate(randomMOTD.getNewLines().get(1)
                                .replaceAll("%timer%", motdTimer.getTitle())
                                .replaceAll("%remaining%", motdTimer.isOver() ? "COMMENCED" : TimeUtils.formatIntoShortenedDetailedString((int) (motdTimer.getRemaining() / 1000)))
                        )
                );
            } else {
                motd = Components.ofChildren(
                        CC.translate(randomMOTD.getLegacyLines().get(0)
                                .replaceAll("%timer%", motdTimer.getTitle())
                                .replaceAll("%remaining%", TimeUtils.formatIntoShortenedDetailedString((int) (motdTimer.getRemaining() / 1000)))
                        ),
                        newline(),
                        CC.translate(randomMOTD.getLegacyLines().get(1)
                                .replaceAll("%timer%", motdTimer.getTitle())
                                .replaceAll("%remaining%", motdTimer.isOver() ? "COMMENCED" : TimeUtils.formatIntoShortenedDetailedString((int) (motdTimer.getRemaining() / 1000)))
                        )
                );
            }
        } else {
            if (legacy) {
                motd = Components.ofChildren(
                        CC.translate(randomMOTD.getNewLines().get(0)),
                        newline(),
                        CC.translate(randomMOTD.getNewLines().get(1))
                );
            } else {
                motd = Components.ofChildren(
                        CC.translate(randomMOTD.getLegacyLines().get(0)),
                        newline(),
                        CC.translate(randomMOTD.getLegacyLines().get(1))
                );
            }
        }

        response.motd(motd);

        return response.build();
    }

    public static int getSpoofedCount(int online) {
        return Math.toIntExact(Math.round(online * lMOTD.getInstance().getConfigYML().getConfig().getDouble("random-value")));
    }

}
