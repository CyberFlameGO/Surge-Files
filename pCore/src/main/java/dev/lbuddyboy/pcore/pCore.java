package dev.lbuddyboy.pcore;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.pcore.auction.AuctionHandler;
import dev.lbuddyboy.pcore.battlepass.BattlePassHandler;
import dev.lbuddyboy.pcore.battlepass.command.BattlePassCommand;
import dev.lbuddyboy.pcore.coinflip.CoinFlipHandler;
import dev.lbuddyboy.pcore.command.SelectionWandCommand;
import dev.lbuddyboy.pcore.command.CoreCommand;
import dev.lbuddyboy.pcore.command.SpawnCommand;
import dev.lbuddyboy.pcore.command.param.PriceAmountContext;
import dev.lbuddyboy.pcore.economy.EconomyHandler;
import dev.lbuddyboy.pcore.enchants.EnchantHandler;
import dev.lbuddyboy.pcore.essential.enderchest.EnderchestHandler;
import dev.lbuddyboy.pcore.essential.kit.KitHandler;
import dev.lbuddyboy.pcore.essential.locator.LocatorHandler;
import dev.lbuddyboy.pcore.essential.offline.OfflineHandler;
import dev.lbuddyboy.pcore.essential.plot.PlotHandler;
import dev.lbuddyboy.pcore.essential.rollback.RollbackHandler;
import dev.lbuddyboy.pcore.essential.trade.TradeHandler;
import dev.lbuddyboy.pcore.essential.vaults.PlayerVaultHandler;
import dev.lbuddyboy.pcore.essential.warp.WarpHandler;
import dev.lbuddyboy.pcore.events.EventHandler;
import dev.lbuddyboy.pcore.listener.JoinListener;
import dev.lbuddyboy.pcore.listener.SelectionListener;
import dev.lbuddyboy.pcore.lootbags.LootBagHandler;
import dev.lbuddyboy.pcore.mines.PrivateMineHandler;
import dev.lbuddyboy.pcore.pets.PetHandler;
import dev.lbuddyboy.pcore.robots.RobotHandler;
import dev.lbuddyboy.pcore.scoreboard.ScoreboardHandler;
import dev.lbuddyboy.pcore.shop.ShopHandler;
import dev.lbuddyboy.pcore.shop.command.ShopCommand;
import dev.lbuddyboy.pcore.storage.StorageHandler;
import dev.lbuddyboy.pcore.thread.PlayerDataSaveThread;
import dev.lbuddyboy.pcore.timer.TimerHandler;
import dev.lbuddyboy.pcore.user.MineUserHandler;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.PriceAmount;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.loottable.LootTableHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class pCore extends JavaPlugin {

    @Getter
    private static pCore instance;
    @Getter
    private static boolean ENABLED = false;

    private List<IModule> modules;

    private MineUserHandler mineUserHandler;
    private AuctionHandler auctionHandler;
    private BattlePassHandler battlePassHandler;
    private CoinFlipHandler coinFlipHandler;
    private EconomyHandler economyHandler;
    private EnchantHandler enchantHandler;
    private EventHandler eventHandler;
    private LootTableHandler lootTableHandler;

    private LootBagHandler lootBagHandler;
    private RobotHandler robotHandler;
    private ScoreboardHandler scoreboardHandler;
    private StorageHandler storageHandler;
    private ShopHandler shopHandler;
    private TimerHandler timerHandler;
    private TradeHandler tradeHandler;
    private PetHandler petHandler;
    private PrivateMineHandler privateMineHandler;
    private PlayerVaultHandler playerVaultHandler;
    private PaperCommandManager commandManager;
    private WarpHandler warpHandler;

    private KitHandler kitHandler;
    private EnderchestHandler enderchestHandler;
    private RollbackHandler rollbackHandler;
    private LocatorHandler locatorHandler;
    private OfflineHandler offlineHandler;
    private PlotHandler plotHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.modules = new ArrayList<>();

        this.loadCommands();
        this.loadThreads();
        this.loadListeners();
        this.loadHandlers();

        Tasks.runLater(() -> ENABLED = true, 20);
    }

    @Override
    public void onDisable() {
        this.modules.forEach(m -> m.unload(this));
    }

    private void loadHandlers() {
        this.modules.addAll(Arrays.asList(
                this.storageHandler = new StorageHandler(),
                this.lootTableHandler = new LootTableHandler(),
                this.economyHandler = new EconomyHandler(),
                this.mineUserHandler = new MineUserHandler(),
                this.auctionHandler = new AuctionHandler(),
                this.battlePassHandler = new BattlePassHandler(),
                this.coinFlipHandler = new CoinFlipHandler(),
                this.enchantHandler = new EnchantHandler(),
                this.eventHandler = new EventHandler(),
                this.enderchestHandler = new EnderchestHandler(),
                this.rollbackHandler = new RollbackHandler(),
                this.locatorHandler = new LocatorHandler(),
                this.offlineHandler = new OfflineHandler(),
                this.plotHandler = new PlotHandler(),
                this.kitHandler = new KitHandler(),
                this.lootBagHandler = new LootBagHandler(),
                this.robotHandler = new RobotHandler(),
                this.scoreboardHandler = new ScoreboardHandler(),
                this.shopHandler = new ShopHandler(),
                this.tradeHandler = new TradeHandler(),
                this.playerVaultHandler = new PlayerVaultHandler(),
                this.petHandler = new PetHandler(),
                this.privateMineHandler = new PrivateMineHandler(),
                this.timerHandler = new TimerHandler(),
                this.warpHandler = new WarpHandler()
        ));

        this.modules.forEach(m -> m.load(this));
    }

    private void loadCommands() {
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.enableUnstableAPI("help");
        this.commandManager.getCommandContexts().registerContext(PriceAmount.class, new PriceAmountContext());
        this.commandManager.registerCommand(new BattlePassCommand());
        this.commandManager.registerCommand(new SelectionWandCommand());
        this.commandManager.registerCommand(new CoreCommand());
        this.commandManager.registerCommand(new ShopCommand());
        this.commandManager.registerCommand(new SpawnCommand());
    }

    private void loadThreads() {
        new PlayerDataSaveThread().start();
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new SelectionListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }

}
