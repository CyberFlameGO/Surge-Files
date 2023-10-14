package dev.lbuddyboy.samurai.server;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableHandler;
import dev.lbuddyboy.samurai.util.loottable.menu.LootTablePreviewMenu;
import dev.lbuddyboy.samurai.util.object.Config;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

@Getter
@CommandAlias("fishing")
public class FishingHandler extends BaseCommand implements Listener {

    private Config config;
    private LootTable lootTable;

    public FishingHandler() {
        reload();
        this.lootTable = new LootTable(this.config);
        LootTableHandler.getLootTables().add(this.lootTable);
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
        Samurai.getInstance().getPaperCommandManager().registerCommand(this);
    }

    @Default
    public void def(Player sender) {
        new LootTablePreviewMenu(this.lootTable, (Menu) null).openMenu(sender.getPlayer());
    }

    public void reload() {
        this.config = new Config(Samurai.getInstance(), "fishing.yml");
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getHook().getState() == FishHook.HookState.HOOKED_ENTITY && player.getLocation().distance(event.getHook().getLocation()) > 10) {
            event.setCancelled(true);
            return;
        }
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;

        Samurai.getInstance().getServerHandler().getFishingHandler().getLootTable().open(player);
        event.getHook().remove();
        event.setCancelled(true);
    }

}
