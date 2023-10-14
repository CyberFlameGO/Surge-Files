package dev.lbuddyboy.pcore.scoreboard.thread;

import dev.lbuddyboy.pcore.events.Event;
import dev.lbuddyboy.pcore.events.koth.KoTH;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.scoreboard.SamuraiScoreboard;

import java.util.List;
import java.util.stream.Collectors;

public class ActiveEventThread extends Thread {

    private int i = 0;

    @Override
    public void run() {
        while (true) {
            try {
                tick();
                sleep(10000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        List<Event> actives = pCore.getInstance().getEventHandler().getEvents().values().stream().filter(Event::isActive).filter(e -> e instanceof KoTH).collect(Collectors.toList());
        if (actives.isEmpty()) {
            pCore.getInstance().getScoreboardHandler().setActiveKoTH(null);
            return;
        }
        pCore.getInstance().getScoreboardHandler().setActiveKoTH((KoTH) actives.get(i++));
        if (i >= actives.size()) i = 0;
    }

}
