package dev.lbuddyboy.samurai.custom.schedule;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/01/2022 / 7:20 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.schedule
 */

@Getter
public class ScheduleHandler {

    private final Map<String, ScheduledTime> scheduledTimes;
    @Getter public static final List<Long> notifyIntervals = Arrays.asList(
            JavaUtils.parse("24h"),
            JavaUtils.parse("12h"),
            JavaUtils.parse("9h"),
            JavaUtils.parse("6h"),
            JavaUtils.parse("3h"),
            JavaUtils.parse("2h"),
            JavaUtils.parse("1h"),
            JavaUtils.parse("45m"),
            JavaUtils.parse("30m"),
            JavaUtils.parse("25m"),
            JavaUtils.parse("20m"),
            JavaUtils.parse("15m"),
            JavaUtils.parse("10m"),
            JavaUtils.parse("5m"),
            JavaUtils.parse("2m30s"),
            JavaUtils.parse("30s"),
            JavaUtils.parse("5s"),
            JavaUtils.parse("4s"),
            JavaUtils.parse("3s"),
            JavaUtils.parse("2s"),
            JavaUtils.parse("1s")
    );
    private final MongoCollection<Document> collection;

    public ScheduleHandler() {
        this.scheduledTimes = new ConcurrentHashMap<>();
        this.collection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("Schedules");

        for (Document document : this.collection.find()) {
            if (document == null) continue;
            this.scheduledTimes.put(document.getString("name"), new ScheduledTime(document));
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(Samurai.getInstance(), () -> {
            for (Map.Entry<String, ScheduledTime> entry : this.scheduledTimes.entrySet()) {
                ScheduledTime time = entry.getValue();

                if (time.getTimeLeft() <= 0) {
                    Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), time.getCommand());
                    });
                    this.scheduledTimes.remove(entry.getKey());
                    this.collection.deleteOne(Filters.eq("name", entry.getKey()));
                    continue;
                }
                for (Long l : notifyIntervals) {
                    if (time.getReminders().contains(l))
                        continue;

                    if (l > time.getTimeLeft()) {
                        time.getReminders().add(l);

                        String timeForm = TimeUtils.formatLongIntoDetailedString((l / 1000));

                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.sendTitle(
                                    CC.translate("&7" + CC.UNICODE_ARROWS_RIGHT + " &6&lSCHEDULE &7" + CC.UNICODE_ARROWS_LEFT),
                                    CC.translate(CC.YELLOW + time.getName() + "&f will commence in &7" + timeForm)
                            );
                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                        }

                        Bukkit.broadcastMessage(" ");
                        Bukkit.broadcastMessage(CC.translate("&6&l[SCHEDULE] " + time.getName() + "&f will commence in &e" + timeForm));

                        try {
                            DiscordLogger.logSchedule(time.getName(), timeForm);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }

            }

        }, 10, 10);

    }

    public void save() {
        for (ScheduledTime time : this.scheduledTimes.values()) {
            this.collection.replaceOne(Filters.eq("name", time.getName()), time.save(), new ReplaceOptions().upsert(true));
        }
    }

}
