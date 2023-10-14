package dev.aurapvp.samurai.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.aurapvp.samurai.auction.AuctionItem;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.economy.BankAccount;
import dev.aurapvp.samurai.economy.transaction.Transaction;
import dev.aurapvp.samurai.essential.enderchest.EnderChest;
import dev.aurapvp.samurai.essential.enderchest.EnderChestData;
import dev.aurapvp.samurai.essential.kit.KitCooldown;
import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.claim.Claim;
import dev.aurapvp.samurai.faction.member.FactionInvitation;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class GSONUtils {

    @Getter public static Gson GSON;

    public static final Type BANK_ACCOUNT = new TypeToken<BankAccount>() {}.getType();
    public static final Type BATTLE_PASS = new TypeToken<BattlePass>() {}.getType();
    public static final Type INTEGER = new TypeToken<Integer>() {}.getType();
    public static final Type TRANSACTIONS = new TypeToken<List<Transaction>>() {}.getType();
    public static final Type AUCTION_ITEM = new TypeToken<AuctionItem>() {}.getType();
    public static final Type CLAIMS = new TypeToken<List<Claim>>() {}.getType();
    public static final Type PLAYER_DEATHS = new TypeToken<List<PlayerDeath>>() {}.getType();
    public static final Type ENDER_CHEST = new TypeToken<EnderChestData>() {}.getType();
    public static final Type FACTION_MEMBERS = new TypeToken<List<FactionPermission.FactionMember>>() {}.getType();
    public static final Type FACTION_INVITES = new TypeToken<List<FactionInvitation>>() {}.getType();
    public static final Type FACTION_MEMBER = new TypeToken<FactionPermission.FactionMember>() {}.getType();
    public static final Type FACTION = new TypeToken<Faction>() {}.getType();
    public static final Type AUCTION_ITEMS = new TypeToken<List<AuctionItem>>() {}.getType();
    public static final Type KIT_COOLDOWNS = new TypeToken<List<KitCooldown>>() {}.getType();
    public static final Type OFFLINE_DATA = new TypeToken<OfflineData>() {}.getType();
    public static final Type TRANSACTION = new TypeToken<Transaction>() {}.getType();
    public static final Type ITEM_STACKS = new TypeToken<ItemStack[]>() {}.getType();
    public static final Type STRING = new TypeToken<List<String>>() {}.getType();
    public static final Type UUID = new TypeToken<List<UUID>>() {}.getType();

    static {
        GSON = new GsonBuilder()
                .registerTypeHierarchyAdapter(Faction.class, new FactionAdapter())
                .registerTypeHierarchyAdapter(Transaction.class, new TransactionAdapter())
                .registerTypeHierarchyAdapter(AuctionItem.class, new AuctionItemAdapter())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackArrayAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .create();
    }

}
