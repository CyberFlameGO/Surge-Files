package dev.lbuddyboy.pcore.api.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

@Getter @Setter
public class CoinFlipStartEvent extends Event implements Cancellable {

    @Getter public static HandlerList handlerList = new HandlerList();

    private UUID sender, challenger;
    private boolean cancelled = false;

    public CoinFlipStartEvent(UUID sender, UUID challenger) {
        this.sender = sender;
        this.challenger = challenger;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}