package dev.lbuddyboy.samurai.persist;

import com.mongodb.DBCollection;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class RedisSaveTask extends BukkitRunnable {

    @Override
    public void run() {
        save(null, false);
//        if (Foxtrot.getInstance().getServer().hasWhitelist()) return;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EST"));
//        String date = simpleDateFormat.format(new Date(System.currentTimeMillis()));
//        TeamDataCommands.exportTeamData(Bukkit.getConsoleSender(), "Backups/!teams/backup-" + date.replaceAll("/", "-").replaceAll(":", "-"));
    }

    public static int save(final CommandSender issuer, final boolean forceAll) {
        long startMs = System.currentTimeMillis();
        int teamsSaved = Samurai.getInstance().runRedisCommand(redis -> {

            DBCollection teamsCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Teams");
            
            int changed = 0;

            for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
                if (team.isNeedsSave() || forceAll) {
                    changed++;

                    redis.set("fox_teams." + team.getName().toLowerCase(), team.saveString(true));
                    teamsCollection.update(team.getJSONIdentifier(), team.toJSON(), true, false);
                }
                
                if (forceAll) {
                    for (UUID member : team.getMembers()) {
                        Samurai.getInstance().getTeamHandler().setTeam(member, team, true);
                    }
                }
            }

            redis.set("RostersLocked", String.valueOf(Samurai.getInstance().getTeamHandler().isRostersLocked()));
            if (issuer != null && forceAll) redis.save();
            return (changed);
        });

        int time = (int) (System.currentTimeMillis() - startMs);

        if (teamsSaved != 0) {
            System.out.println("Saved " + teamsSaved + " teams to Redis in " + time + "ms.");

            if (issuer != null) {
                issuer.sendMessage(ChatColor.DARK_PURPLE + "Saved " + teamsSaved + " teams to Redis in " + time + "ms.");
            }
        }

        Samurai.getInstance().getMapHandler().getStatsHandler().save();



        return (teamsSaved);
    }

}