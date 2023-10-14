package dev.lbuddyboy.pcore.essential.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("playervault|pv")
public class PlayerVaultCommand extends BaseCommand {

    @Default
    public void pv(Player sender, @Name("vault") Integer page, @Name("player") @Optional OfflinePlayer target) {
        if (target == null) target = sender;
        if (page == null) {
            // open vault select menu
            return;
        }

        boolean hasPermission = false;
        if (sender.hasPermission("playervaults." + page)) {
            hasPermission = true;
        } else {
            for (int x = page; x <= 20; x++) {
                if (sender.hasPermission("playervaults." + x)) {
                    hasPermission = true;
                }
            }
        }

        if (!hasPermission) {
            sender.sendMessage(CC.translate("&cYou don't have permission for this vault."));
            return;
        }

        pCore.getInstance().getPlayerVaultHandler().openVault(sender, target.getUniqueId(), page);
    }

    @Default
    @CommandPermission("pcore.vaults.admin")
    public void vaults(Player sender, @Name("vault") Integer page, @Name("player") @Optional OfflinePlayer target) {
        if (target == null) target = sender;
        if (page == null) {
            // open vault select menu
            return;
        }

        pCore.getInstance().getPlayerVaultHandler().openVault(sender, target.getUniqueId(), page);
    }

}
