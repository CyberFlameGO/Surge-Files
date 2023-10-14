package dev.lbuddyboy.samurai.server.deathban;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Deathban {

    @Getter private static Map<String, Integer> deathban = new LinkedHashMap<>();
    @Getter private static int defaultMinutes = 240;

    static {
        deathban.put("COPPER", 20);
        deathban.put("TIN", 15);
        deathban.put("QUARTZ", 10);
        deathban.put("STEEL", 5);
        deathban.put("YOUTUBER", 5);
        deathban.put("STREAMER", 5);
        deathban.put("FAMOUS", 5);
        deathban.put("PARTNER", 5);
    }

    public static void load(BasicDBObject object) {
        deathban.clear();

        for (String key : object.keySet()) {
            if (key.equals("DEFAULT"))  {
                defaultMinutes = object.getInt(key);
            } else {
                deathban.put(key, object.getInt(key));
            }
        }
    }

    public static int getDeathbanSeconds(Player player) {
        int minutes = defaultMinutes;

        for (Map.Entry<String, Integer> entry : deathban.entrySet()) {
			if (player.hasPermission("deathbans." + entry.getKey().toLowerCase()) && entry.getValue() < minutes) {
				minutes = entry.getValue();
			}
        }

        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }

}
