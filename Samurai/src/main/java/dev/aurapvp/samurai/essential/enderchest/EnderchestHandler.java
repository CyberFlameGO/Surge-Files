package dev.aurapvp.samurai.essential.enderchest;

import co.aikar.commands.ACFBukkitUtil;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MinecraftMessageKeys;
import co.aikar.commands.annotation.Optional;
import dev.aurapvp.samurai.essential.command.EnderchestCommand;
import dev.aurapvp.samurai.essential.enderchest.listener.EnderChestListener;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.IModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EnderchestHandler implements IModule {

    @Override
    public void load(Samurai plugin) {
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public String getId() {
        return "enderchest";
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().registerCommand(new EnderchestCommand());
    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new EnderChestListener(), Samurai.getInstance());
    }

    public ItemStack[] fetchCache(UUID uuid) {
        return Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true).getEnderChest();
    }

    public void updateCache(UUID uuid, ItemStack[] data) {
        SamuraiPlayer player = Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true);

        player.setEnderChest(data);
        player.updated();
    }

    public void openEnderChest(Player sender, UUID target) {
        ItemStack[] chest = fetchCache(target);

        if (chest == null) {
            sender.sendMessage(CC.translate("&cThere was an issue loading ender chest data. Contact an admin if this continues."));
            return;
        }

        Inventory inventory = Bukkit.createInventory(null, InventoryType.ENDER_CHEST, Bukkit.getOfflinePlayer(target).getName() + "'s EnderChest");
        inventory.setContents(chest);

        sender.openInventory(inventory);
    }

}
