package dev.lbuddyboy.pcore.auction;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.auction.command.AuctionCommand;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.storage.IStorage;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Getter
public class AuctionHandler implements IModule {

    private final Map<UUID, List<AuctionItem>> auctions;
    private final Config config;
    private final PagedGUISettings pagedGUISettings;

    public AuctionHandler() {
        this.auctions = new ConcurrentHashMap<>();
        this.config = new Config(pCore.getInstance(), "auction-house");
        this.pagedGUISettings = new PagedGUISettings(config, "menu-settings");
    }

    @Override
    public void load(pCore plugin) {
        this.loadCommands();

        reload();
    }

    @Override
    public void unload(pCore plugin) {
        this.auctions.forEach((key, value) -> save(key, false));
    }

    @Override
    public void save() {
        this.auctions.forEach((key, value) -> save(key, true));
    }

    @Override
    public void reload() {
        this.auctions.clear();

        this.loadAuctions();
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().registerCommand(new AuctionCommand());
    }

    private void loadAuctions() {
        IStorage storage = pCore.getInstance().getStorageHandler().getStorage();

        storage.loadData(
                auctions,
                "auctions",
                GSONUtils.AUCTION_ITEMS,
                true
        );

        Bukkit.getConsoleSender().sendMessage("[Auction Handler] Loaded " + this.auctions.size() + " auction items!");
    }

    public void save(UUID uuid, boolean async) {
        IStorage storage = pCore.getInstance().getStorageHandler().getStorage();

        storage.insertData(uuid,
                "auctions",
                this.auctions.getOrDefault(uuid, new ArrayList<>()),
                GSONUtils.AUCTION_ITEMS,
                async
        );
    }

    public List<AuctionItem> getAuctionItems(UUID uuid) {
        return this.auctions.getOrDefault(uuid, new ArrayList<>());
    }

    public List<AuctionItem> getActiveAuctionItems(UUID uuid) {
        return this.auctions.getOrDefault(uuid, new ArrayList<>());
    }

    public List<AuctionItem> getBinAuctionItems(UUID uuid) {
        return this.auctions.getOrDefault(uuid, new ArrayList<>()).stream().filter(AuctionItem::isExpired).collect(Collectors.toList());
    }

    public List<AuctionItem> getAuctionItems(String search) {
        return new ArrayList<AuctionItem>() {{
            auctions.forEach((k, v) -> {
                v.stream().filter(i -> i.matches(search)).forEach(this::add);
            });
        }};
    }

    public AuctionItem createAuction(Player sender, ItemStack item, double price, EconomyType economy) {
        List<AuctionItem> items = getAuctionItems(sender.getUniqueId());
        AuctionItem auctionItem = new AuctionItem(UUID.randomUUID(), sender.getUniqueId(), item, System.currentTimeMillis(), TimeUnit.DAYS.toMillis(1L), price, economy.name(), false);

        items.add(auctionItem);
        this.auctions.put(sender.getUniqueId(), items);

        return auctionItem;
    }

    public void deleteAuction(UUID sender, AuctionItem item) {
        List<AuctionItem> items = getAuctionItems(sender);

        items.remove(item);

        this.auctions.put(sender, items);
    }

}
