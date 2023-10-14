package dev.lbuddyboy.pcore.api.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter @Setter
public class CoinFlipEndEvent extends Event implements Cancellable {

    @Getter public static HandlerList handlerList = new HandlerList();

    private UUID sender, challenger, winner;
    private boolean cancelled = false;

    public CoinFlipEndEvent(UUID sender, UUID challenger, UUID winner) {
        this.sender = sender;
        this.challenger = challenger;
        this.winner = winner;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}