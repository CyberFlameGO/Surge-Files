package dev.aurapvp.samurai.api.event.faction;

import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.claim.Claim;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
@Getter @Setter
public class FactionClaimEvent extends Event implements Cancellable {

    @Getter
    public static HandlerList handlerList = new HandlerList();

    private final Player sender;
    private final Faction faction;
    private final Claim claim;
    private final boolean forced;
    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
