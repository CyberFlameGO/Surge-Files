package dev.lbuddyboy.pcore.api.event;

import dev.lbuddyboy.pcore.pets.IPet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @Setter
@RequiredArgsConstructor
public class PetActivateEvent extends Event implements Cancellable {

    @Getter public static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final IPet pet;
    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}