package dev.lbuddyboy.pcore.essential.vaults;

import dev.lbuddyboy.pcore.essential.command.PlayerVaultCommand;
import dev.lbuddyboy.pcore.essential.vaults.listener.PlayerVaultListener;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.IModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class PlayerVaultHandler implements IModule {

    @Override
    public void load(pCore plugin) {
        plugin.getCommandManager().registerCommand(new PlayerVaultCommand());
        plugin.getServer().getPluginManager().registerEvents(new PlayerVaultListener(), plugin);
    }

    @Override
    public void unload(pCore plugin) {

    }

    public void updateVault(UUID target, Integer page, ItemStack[] contents) {
        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(target);
        user.updatePlayerVault(page, contents);
    }

    public void openVault(Player sender, UUID target, Integer page) {
        if (sender.isSleeping() || sender.isDead() || !sender.isOnline()) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory() != null) continue;
            if (player.getOpenInventory().getTopInventory() != null) continue;
            Inventory inventory = player.getOpenInventory().getTopInventory();

            if (inventory.getTitle() == null) continue;
            if (!inventory.getTitle().contains(sender.getName())) continue;

            player.sendMessage(CC.translate("&cSomeone already has that player vault opened, open later!"));
            return;
        }

        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(target);
        ItemStack[] chest = user.getPlayerVaultOrDefault(page);

        if (chest == null) {
            sender.sendMessage(CC.translate("&cThere was an issue loading vault data. Contact an admin if this continues."));
            return;
        }

        String addition = (target == sender.getUniqueId() ? "" : " - " + user.getName());

        Inventory inventory = Bukkit.createInventory(null, 54, "PV #" + page + addition);
        inventory.setContents(chest);
        sender.openInventory(inventory);
        sender.setMetadata(PlayerVaultConstants.VAULT_METADATA, new FixedMetadataValue(pCore.getInstance(), new VaultInfo(target, page)));
    }

}
