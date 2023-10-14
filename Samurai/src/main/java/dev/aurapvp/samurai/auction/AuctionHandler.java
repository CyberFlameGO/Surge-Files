package dev.aurapvp.samurai.auction;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.auction.command.AuctionCommand;
import dev.aurapvp.samurai.economy.EconomyType;
import dev.aurapvp.samurai.storage.IStorage;
import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.IModule;
import dev.aurapvp.samurai.util.gson.GSONUtils;
import dev.aurapvp.samurai.util.menu.PagedGUISettings;
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
        this.config = new Config(Samurai.getInstance(), "auction-house");
        this.pagedGUISettings = new PagedGUISettings(config, "menu-settings");
    }

    @Override
    public String getId() {
        return "auction-house";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadCommands();

        reload();
    }

    @Override
    public void unload(Samurai plugin) {
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
        Samurai.getInstance().getCommandManager().registerCommand(new AuctionCommand());
    }

    private void loadAuctions() {
        IStorage storage = Samurai.getInstance().getStorageHandler().getStorage();

        storage.loadData(
                auctions,
                "auctions",
                GSONUtils.AUCTION_ITEMS,
                true
        );

        Bukkit.getConsoleSender().sendMessage("[Auction Handler] Loaded " + this.auctions.size() + " auction items!");
    }

    public void save(UUID uuid, boolean async) {
        IStorage storage = Samurai.getInstance().getStorageHandler().getStorage();

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
