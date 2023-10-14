package dev.lbuddyboy.samurai.server.threads;

import com.mongodb.client.model.Filters;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.custom.schedule.ScheduleHandler;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EOTWTask extends BukkitRunnable {

    private final List<Long> notifyIntervals = Arrays.asList(
            JavaUtils.parse("10m"),
            JavaUtils.parse("5m"),
            JavaUtils.parse("4m"),
            JavaUtils.parse("3m"),
            JavaUtils.parse("2m30s"),
            JavaUtils.parse("2m"),
            JavaUtils.parse("1m30s"),
            JavaUtils.parse("1m"),
            JavaUtils.parse("30s"),
            JavaUtils.parse("5s"),
            JavaUtils.parse("4s"),
            JavaUtils.parse("3s"),
            JavaUtils.parse("2s"),
            JavaUtils.parse("1s")
    );
    private final List<Long> reminded = new ArrayList<>();
    private final long BORDER_INTERVAL = JavaUtils.parse("5m");
    private long LAST_UPDATED = -1;

    @Override
    public void run() {
        if (!Samurai.getInstance().getServerHandler().isEOTW()) {
            return;
        }
        if (EOTWCommand.isFfaEnabled()) {
            return;
        }

        World world = Bukkit.getWorld("world");
        double currentBorder = world.getWorldBorder().getSize();
        int BORDER_DECREMENT = 450;
        int predicted = (int) ((currentBorder / 2) - BORDER_DECREMENT);

        if (LAST_UPDATED == -1) {
            LAST_UPDATED = System.currentTimeMillis();
            for (Long interval : notifyIntervals) {
                if (interval > BORDER_INTERVAL) reminded.add(interval);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (world.getWorldBorder().isInside(player.getLocation())) continue;

            player.sendTitle(CC.translate("&4&lWORLD BORDER"), CC.translate("&cYou are outside of the border! Get back in!."));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1));
        }

        if (getTimeLeft() <= 0) {
            world.getWorldBorder().setSize(((currentBorder / 2) - BORDER_DECREMENT) * 2);
            world.getWorldBorder().setWarningDistance(0);
            world.getWorldBorder().setDamageAmount(0);
            world.getWorldBorder().setDamageBuffer(0);

            LAST_UPDATED = System.currentTimeMillis();
            reminded.clear();
            return;
        }

        if (LAST_UPDATED + BORDER_INTERVAL > System.currentTimeMillis()) {
            for (Long l : notifyIntervals) {
                if (reminded.contains(l))
                    continue;

                if (l > getTimeLeft()) {
                    reminded.add(l);

                    String timeForm = TimeUtils.formatLongIntoDetailedString((l / 1000));
                    Bukkit.broadcastMessage(CC.translate("&4&l[EOTW BORDER] &cThe world border will be shrunk to " + predicted + "x" + predicted + " in &e" + timeForm + "."));

                    break;
                }
            }
        }
    }

    public long getTimeLeft() {
        return (BORDER_INTERVAL + LAST_UPDATED) - System.currentTimeMillis();
    }

}
