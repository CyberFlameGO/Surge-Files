package dev.aurapvp.samurai.api.event.faction;

import dev.aurapvp.samurai.faction.Faction;
import lombok.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@RequiredArgsConstructor
@Getter @Setter
public class FactionJoinEvent extends Event implements Cancellable {

    @Getter
    public static HandlerList handlerList = new HandlerList();

    private final Player sender;
    private final Faction faction;
    private final boolean forced;
    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
