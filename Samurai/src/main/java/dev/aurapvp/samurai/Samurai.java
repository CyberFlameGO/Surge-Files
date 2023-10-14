package dev.aurapvp.samurai;

import co.aikar.commands.PaperCommandManager;
import dev.aurapvp.samurai.api.impl.dynmap.DynMapImpl;
import dev.aurapvp.samurai.auction.AuctionHandler;
import dev.aurapvp.samurai.battlepass.BattlePassHandler;
import dev.aurapvp.samurai.battlepass.command.BattlePassCommand;
import dev.aurapvp.samurai.command.SamuraiCommand;
import dev.aurapvp.samurai.economy.EconomyHandler;
import dev.aurapvp.samurai.enchants.ArmorSetHandler;
import dev.aurapvp.samurai.enchants.EnchantHandler;
import dev.aurapvp.samurai.essential.enderchest.EnderchestHandler;
import dev.aurapvp.samurai.essential.kit.KitHandler;
import dev.aurapvp.samurai.essential.locator.LocatorHandler;
import dev.aurapvp.samurai.essential.offline.OfflineHandler;
import dev.aurapvp.samurai.essential.rollback.RollbackHandler;
import dev.aurapvp.samurai.events.EventHandler;
import dev.aurapvp.samurai.faction.FactionHandler;
import dev.aurapvp.samurai.listener.GeneralListener;
import dev.aurapvp.samurai.listener.JoinListener;
import dev.aurapvp.samurai.listener.SelectionListener;
import dev.aurapvp.samurai.lootbags.LootBagHandler;
import dev.aurapvp.samurai.nametag.NameTagHandler;
import dev.aurapvp.samurai.player.PlayerHandler;
import dev.aurapvp.samurai.scoreboard.ScoreboardHandler;
import dev.aurapvp.samurai.storage.StorageHandler;
import dev.aurapvp.samurai.thread.PlayerDataSaveThread;
import dev.aurapvp.samurai.timer.TimerHandler;
import dev.aurapvp.samurai.util.IModule;
import dev.aurapvp.samurai.util.Tasks;
import dev.aurapvp.samurai.util.loottable.LootTableHandler;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public final class Samurai extends JavaPlugin {

    @Getter
    private static Samurai instance;
    @Getter
    private static boolean ENABLED = false;

    private List<IModule> modules;

    private DynMapImpl dynMapImpl;

    private AuctionHandler auctionHandler;
    private BattlePassHandler battlePassHandler;
    private EconomyHandler economyHandler;
    private ArmorSetHandler armorSetHandler;
    private EnchantHandler enchantHandler;
    private EventHandler eventHandler;
    private FactionHandler factionHandler;
    private LootBagHandler lootBagHandler;
    private LootTableHandler lootTableHandler;
    private ScoreboardHandler scoreboardHandler;
    private StorageHandler storageHandler;
    private TimerHandler timerHandler;
    private NameTagHandler nameTagHandler;
    private PlayerHandler playerHandler;
    private PaperCommandManager commandManager;

    private EnderchestHandler enderchestHandler;
    private KitHandler kitHandler;
    private LocatorHandler locatorHandler;
    private OfflineHandler offlineHandler;
    private RollbackHandler rollbackHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.modules = new ArrayList<>();

        this.loadHandlers();
        this.loadCommands();
        this.loadThreads();
        this.loadListeners();

        Tasks.runLater(() -> ENABLED = true, 20);
    }

    @Override
    public void onDisable() {
        this.modules.forEach(m -> m.unload(this));
    }

    private void loadHandlers() {
        this.commandManager = new PaperCommandManager(this);

        this.modules.addAll(Arrays.asList(
                this.storageHandler = new StorageHandler(),
                this.eventHandler = new EventHandler(),
                this.economyHandler = new EconomyHandler(),
                this.playerHandler = new PlayerHandler(),
                this.lootTableHandler = new LootTableHandler(),
                this.auctionHandler = new AuctionHandler(),
                this.battlePassHandler = new BattlePassHandler(),
                this.enchantHandler = new EnchantHandler(),
                this.armorSetHandler = new ArmorSetHandler(),
                this.kitHandler = new KitHandler(),
                this.factionHandler = new FactionHandler(),
                this.lootBagHandler = new LootBagHandler(),
                this.scoreboardHandler = new ScoreboardHandler(),
                this.enderchestHandler = new EnderchestHandler(),
                this.kitHandler = new KitHandler(),
                this.locatorHandler = new LocatorHandler(),
                this.offlineHandler = new OfflineHandler(),
                this.rollbackHandler = new RollbackHandler(),
                this.nameTagHandler = new NameTagHandler(),
                this.timerHandler = new TimerHandler(),
                this.dynMapImpl = new DynMapImpl(this)
        ));

        this.modules.forEach(m -> {
            try {
                if (getConfig().contains("modules." + m.getId()) && getConfig().getBoolean("modules." + m.getId())) {
                    m.load(this);
                } else {
                    if (getConfig().contains("modules." + m.getId()) && !getConfig().getBoolean("modules." + m.getId()))
                        return;

                    m.load(this);
                    getConfig().set("modules." + m.getId(), true);
                    saveConfig();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void loadCommands() {
        this.commandManager.registerCommand(new BattlePassCommand());
        this.commandManager.registerCommand(new SamuraiCommand());
    }

    private void loadThreads() {
        new PlayerDataSaveThread().start();
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new SelectionListener(), this);
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }

}
