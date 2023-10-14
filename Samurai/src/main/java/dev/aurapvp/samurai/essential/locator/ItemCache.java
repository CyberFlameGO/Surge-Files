package dev.aurapvp.samurai.essential.locator;

import dev.aurapvp.samurai.Samurai;
import lombok.Data;

import java.util.UUID;

@Data
public class ItemCache {

    private UUID id;
    private ItemLocation location;
    private long lastMentioned;

    public ItemCache(UUID uuid, ItemLocation location) {
        this.location = location;

        Samurai.getInstance().getLocatorHandler().getItemCache().put(uuid, this);
    }

}
