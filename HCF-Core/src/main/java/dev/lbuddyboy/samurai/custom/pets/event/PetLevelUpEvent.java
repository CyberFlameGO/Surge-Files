package dev.lbuddyboy.samurai.custom.pets.event;

import dev.lbuddyboy.samurai.custom.pets.IPet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter @Setter
@RequiredArgsConstructor
public class PetLevelUpEvent extends Event implements Cancellable {

    @Getter public static HandlerList handlerList = new HandlerList();

    private final Player player;
    private final IPet pet;
    private final int oldLevel;
    private final int newLevel;
    private final boolean candied;
    private boolean cancelled = false;

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}