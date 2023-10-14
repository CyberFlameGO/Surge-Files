/*
package dev.lbuddyboy.lqueue;

import dev.lbuddyboy.queue.model.Queue;
import dev.lbuddyboy.queue.model.QueueUser;
import dev.lbuddyboy.queue.packet.QueueAddUserPacket;
import dev.lbuddyboy.spoof.lSpoof;
import dev.lbuddyboy.spoof.model.Server;
import dev.lbuddyboy.spoof.model.SpoofSettings;
import dev.lbuddyboy.spoof.model.SpoofedPlayer;
import lombok.Data;
import org.bukkit.Bukkit;

import java.util.concurrent.ThreadLocalRandom;


@Data
public class SpoofPopulator {

    private boolean populating = false;

    public void populateQueue(Queue queue) {
        if (!queue.isPaused()) return;
        if (populating) return;

        populating = true;

        Server server = lSpoof.getInstance().getLocalServer();
        int spoofAmount = server.getSpoofedPlayers().size() / Bukkit.getOnlinePlayers().size();
        SpoofSettings settings = lSpoof.getInstance().getSettings();

        if (settings.getGradualJoin().isEnabled()) {
            for (int i = 0; i <= queue.getQueueBots().size() + spoofAmount; i++) {
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(lSpoof.getInstance(), () -> {
                    try {
                        SpoofedPlayer player = server.getSpoofedPlayers().get(finalI);

                        boolean found = false;
                        for (QueueUser u : queue.getQueueUsers()) {
                            if (u.getUuid().toString().equals(player.getUuid().toString())) {
                                found = true;
                                break;
                            }
                        }
                        if (found) return;

                        QueueUser user = new QueueUser(player.getUuid());

                        user.setBot(true);
                        user.setPriority(ThreadLocalRandom.current().nextInt(0, 100));
                        user.setLeft(Long.MAX_VALUE);

                        queue.getQueueUsers().add(user);
                        QueuePlugin.getInstance().getPidginHandler().sendPacket(new QueueAddUserPacket(queue.getName(), user));
                    } catch (Exception ignored) {

                    }
                    if (finalI >= spoofAmount) setPopulating(false);

                }, 20L * (ThreadLocalRandom.current().nextInt(20)));
            }
        } else {
            for (int i = 0; i < queue.getQueueBots().size() + spoofAmount; i++) {
                try {
                    SpoofedPlayer player = server.getSpoofedPlayers().get(i);

                    boolean found = false;
                    for (QueueUser u : queue.getQueueUsers()) {
                        if (u.getUuid().toString().equals(player.getUuid().toString())) {
                            found = true;
                            break;
                        }
                    }
                    if (found) continue;

                    QueueUser user = new QueueUser(player.getUuid());

                    user.setBot(true);
                    user.setPriority(ThreadLocalRandom.current().nextInt(0, 100));
                    user.setLeft(Long.MAX_VALUE);

                    queue.getQueueUsers().add(user);
                    QueuePlugin.getInstance().getPidginHandler().sendPacket(new QueueAddUserPacket(queue.getName(), user));
                } catch (Exception ignored) {

                }
            }
            setPopulating(false);
        }

        queue.recalculate();
    }

    public void moveUser(QueueUser user, Queue queue) {
        Server prev = null, server = lSpoof.getInstance().getServer(queue.getName());
        SpoofedPlayer player = null;

        for (Server s : lSpoof.getInstance().getServers()) {
            for (SpoofedPlayer p : s.getSpoofedPlayers()) {
                if (!p.getUuid().toString().equals(user.getUuid().toString())) continue;

                player = p;
                prev = s;
                break;
            }
        }

        if (player == null) {
            return;
        }

        prev.getSpoofedPlayers().remove(player);
        player.setServer(queue.getName());
        server.getSpoofedPlayers().add(player);

        lSpoof.getInstance().save(prev);
        lSpoof.getInstance().save(server);
    }

}
*/
