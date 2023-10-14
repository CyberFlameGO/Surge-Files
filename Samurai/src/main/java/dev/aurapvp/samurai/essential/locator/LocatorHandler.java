package dev.aurapvp.samurai.essential.locator;

import de.tr7zw.nbtapi.NBTItem;
import dev.aurapvp.samurai.essential.locator.listener.ItemLocatorListener;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class LocatorHandler implements IModule {

    private final Map<UUID, ItemCache> itemCache;

    public LocatorHandler() {
        this.itemCache = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return "item-locator";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void unload(Samurai plugin) {
        for (Map.Entry<UUID, ItemCache> entry : this.itemCache.entrySet()) {
            Samurai.getInstance().getStorageHandler().getStorage().updateLocation(entry.getKey(), entry.getValue().getLocation(), false);
        }
        this.itemCache.clear();
    }

    @Override
    public void save() {
        for (Map.Entry<UUID, ItemCache> entry : this.itemCache.entrySet()) {
            if (entry.getValue().getLastMentioned() + 120_000L < System.currentTimeMillis()) {
                Samurai.getInstance().getStorageHandler().getStorage().updateLocation(entry.getKey(), entry.getValue().getLocation(), true);
                this.itemCache.remove(entry.getKey());
            }
        }
    }

    private void loadCommands() {

    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new ItemLocatorListener(), Samurai.getInstance());
    }

    public ItemCache fetchCache(UUID uuid) {
        if (itemCache.containsKey(uuid)) {
            ItemCache cache = itemCache.get(uuid);

            cache.setLastMentioned(System.currentTimeMillis());

            return this.itemCache.put(uuid, cache);
        }

        ItemCache cache = new ItemCache(uuid, Samurai.getInstance().getStorageHandler().getStorage().loadLocation(uuid, true));

        cache.setLastMentioned(System.currentTimeMillis());

        return cache;
    }

    public ItemCache fetchCache(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) return null;
        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("id")) return null;

        return fetchCache(item.getUUID("id"));
    }

    public void updateCache(UUID uuid, ItemLocation location) {
        ItemCache cache = fetchCache(uuid);
        if (cache == null) return;

        cache.setLocation(location);
        cache.setLastMentioned(System.currentTimeMillis());

        this.itemCache.put(uuid, cache);
    }

}
