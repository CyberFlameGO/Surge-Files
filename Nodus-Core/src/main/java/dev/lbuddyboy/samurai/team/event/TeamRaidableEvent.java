package dev.lbuddyboy.samurai.team.event;

import dev.lbuddyboy.samurai.team.Team;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class TeamRaidableEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();

    private final Player player;
    private final Player killer;
    private final Team team;
    private final double oldDTR;
    private final double newDTR;
    private final UUID playerUUID;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
