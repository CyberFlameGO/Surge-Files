package dev.lbuddyboy.pcore.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.pcore.auction.AuctionItem;
import dev.lbuddyboy.pcore.battlepass.BattlePass;
import dev.lbuddyboy.pcore.user.model.CoinFlipInfo;
import dev.lbuddyboy.pcore.economy.BankAccount;
import dev.lbuddyboy.pcore.economy.transaction.Transaction;
import dev.lbuddyboy.pcore.essential.kit.KitCooldown;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.user.model.RankInfo;
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
    public static final Type CF_INFO = new TypeToken<CoinFlipInfo>() {}.getType();
    public static final Type TRANSACTIONS = new TypeToken<List<Transaction>>() {}.getType();
    public static final Type AUCTION_ITEM = new TypeToken<AuctionItem>() {}.getType();
    public static final Type AUCTION_ITEMS = new TypeToken<List<AuctionItem>>() {}.getType();
    public static final Type KIT_COOLDOWNS = new TypeToken<List<KitCooldown>>() {}.getType();
    public static final Type TRANSACTION = new TypeToken<Transaction>() {}.getType();
    public static final Type ITEM_STACKS = new TypeToken<ItemStack[]>() {}.getType();
    public static final Type PRIVATE_MINE = new TypeToken<PrivateMine>() {}.getType();
    public static final Type RANK_INFO = new TypeToken<RankInfo>() {}.getType();
    public static final Type COIN_FLIP_INFO = new TypeToken<CoinFlipInfo>() {}.getType();
    public static final Type STRING = new TypeToken<List<String>>() {}.getType();
    public static final Type UUID = new TypeToken<List<UUID>>() {}.getType();

    static {
        GSON = new GsonBuilder()
                .registerTypeHierarchyAdapter(Transaction.class, new TransactionAdapter())
                .registerTypeHierarchyAdapter(AuctionItem.class, new AuctionItemAdapter())
                .registerTypeHierarchyAdapter(ItemStack.class, new ItemStackAdapter())
                .setPrettyPrinting()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .create();
    }

}
