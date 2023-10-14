package dev.lbuddyboy.samurai.team.event;

import dev.lbuddyboy.samurai.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@AllArgsConstructor
@Getter
public class TeamRegenerateEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Team team;
    private final double oldDTR;
    private final double newDTR;
    private final boolean wasRaidable;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
