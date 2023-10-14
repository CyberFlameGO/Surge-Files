package dev.lbuddyboy.pcore.essential.locator;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.locator.listener.ItemLocatorListener;
import dev.lbuddyboy.pcore.util.IModule;
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
    public void load(pCore plugin) {
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void unload(pCore plugin) {
        for (Map.Entry<UUID, ItemCache> entry : this.itemCache.entrySet()) {
            pCore.getInstance().getStorageHandler().getStorage().updateLocation(entry.getKey(), entry.getValue().getLocation(), false);
        }
        this.itemCache.clear();
    }

    @Override
    public void save() {
        for (Map.Entry<UUID, ItemCache> entry : this.itemCache.entrySet()) {
            if (entry.getValue().getLastMentioned() + 120_000L < System.currentTimeMillis()) {
                pCore.getInstance().getStorageHandler().getStorage().updateLocation(entry.getKey(), entry.getValue().getLocation(), true);
                this.itemCache.remove(entry.getKey());
            }
        }
    }

    private void loadCommands() {

    }

    private void loadListeners() {
        pCore.getInstance().getServer().getPluginManager().registerEvents(new ItemLocatorListener(), pCore.getInstance());
    }

    public ItemCache fetchCache(UUID uuid) {
        if (itemCache.containsKey(uuid)) {
            ItemCache cache = itemCache.get(uuid);

            cache.setLastMentioned(System.currentTimeMillis());

            return this.itemCache.put(uuid, cache);
        }

        ItemCache cache = new ItemCache(uuid, pCore.getInstance().getStorageHandler().getStorage().loadLocation(uuid, true));

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

        cache.setLocation(location);
        cache.setLastMentioned(System.currentTimeMillis());

        this.itemCache.put(uuid, cache);
    }

}
