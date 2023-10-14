package dev.lbuddyboy.pcore.essential.enderchest;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.command.EnderchestCommand;
import dev.lbuddyboy.pcore.essential.enderchest.listener.EnderChestListener;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.IModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EnderchestHandler implements IModule {

    @Override
    public void load(pCore plugin) {
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void unload(pCore plugin) {

    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().registerCommand(new EnderchestCommand());
    }

    private void loadListeners() {
        pCore.getInstance().getServer().getPluginManager().registerEvents(new EnderChestListener(), pCore.getInstance());
    }

    public ItemStack[] fetchCache(UUID uuid) {
        return pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid).getEnderChest();
    }

    public void updateCache(UUID uuid, ItemStack[] data) {
        MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);

        player.setEnderChest(data);
        player.flagUpdate();
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
