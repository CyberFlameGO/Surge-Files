package dev.lbuddyboy.samurai.events.koth.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public class EventControlTickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    @Getter private final KOTH KOTH;
    @Getter @Setter private boolean cancelled;

    @Override
    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}