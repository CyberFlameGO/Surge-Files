package dev.lbuddyboy.pcore.essential.locator;

import dev.lbuddyboy.pcore.pCore;
import lombok.Data;

import java.util.UUID;

@Data
public class ItemCache {

    private UUID id;
    private ItemLocation location;
    private long lastMentioned;

    public ItemCache(UUID uuid, ItemLocation location) {
        this.location = location;

        pCore.getInstance().getLocatorHandler().getItemCache().put(uuid, this);
    }

}
