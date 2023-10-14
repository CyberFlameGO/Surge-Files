package dev.lbuddyboy.samurai.map.killstreaks;

import com.google.common.collect.Lists;
import dev.lbuddyboy.samurai.map.killstreaks.velttypes.*;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.killstreaks.valortypes.GemKillstreak;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

public class KillstreakHandler implements Listener {

    @Getter private List<Killstreak> killstreaks = Lists.newArrayList();
    @Getter private List<PersistentKillstreak> persistentKillstreaks = Lists.newArrayList();

    public KillstreakHandler() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(this, Samurai.getInstance());

        killstreaks.add(new Debuffs());
        killstreaks.add(new Gapple());
        killstreaks.add(new GoldenApples());
        killstreaks.add(new PotionRefillToken());
        killstreaks.add(new GemKillstreak());

        persistentKillstreaks.add(new FireRes());
        persistentKillstreaks.add(new Invis());
        persistentKillstreaks.add(new PermSpeed2());
        persistentKillstreaks.add(new Speed2());
        persistentKillstreaks.add(new Strength());

        killstreaks.sort((first, second) -> {
            int firstNumber = first.getKills()[0];
            int secondNumber = second.getKills()[0];

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
        
        persistentKillstreaks.sort((first, second) -> {
            int firstNumber = first.getKillsRequired();
            int secondNumber = second.getKillsRequired();

            if (firstNumber < secondNumber) {
                return -1;
            }
            return 1;

        });
    }

    public Killstreak check(int kills) {
        for (Killstreak killstreak : killstreaks) {
            for (int kill : killstreak.getKills()) {
                if (kills == kill) {
                    return killstreak;
                }
            }
        }

        return null;
    }
    
    public List<PersistentKillstreak> getPersistentKillstreaks(Player player, int count) {
        return persistentKillstreaks.stream().filter(s -> s.check(count)).collect(Collectors.toList());
    }

}
