package dev.lbuddyboy.samurai.events.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class EventDeactivatedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    @Getter private dev.lbuddyboy.samurai.events.Event event;

    public HandlerList getHandlers() {
        return (handlers);
    }

    public static HandlerList getHandlerList() {
        return (handlers);
    }

}