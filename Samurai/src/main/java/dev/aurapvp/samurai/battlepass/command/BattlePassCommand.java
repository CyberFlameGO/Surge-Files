package dev.aurapvp.samurai.battlepass.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.battlepass.menu.BattlePassMenu;
import dev.aurapvp.samurai.battlepass.menu.RewardsMenu;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("battlepass|pass|bp|battlep|aurapass")
public class BattlePassCommand extends BaseCommand {

    @Default
    public void bp(Player sender) {
        new BattlePassMenu().openMenu(sender);
    }

    @Subcommand("tiers")
    public void tiers(Player sender) {
        new RewardsMenu().openMenu(sender);
    }

    @Subcommand("wipe")
    @CommandPermission("samurai.command.battlepass.admin")
    @CommandCompletion("@players")
    public void wipe(CommandSender sender, @Name("target") OfflinePlayer player) {
        BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

        pass.setTier(0);
        pass.setExperience(0);
        pass.getProgress().clear();
        pass.getClaimedTiers().clear();

        Samurai.getInstance().getBattlePassHandler().saveBattlePass(player.getUniqueId(), true);
        sender.sendMessage(CC.translate("&aYou have just reset " + player.getName() + "'s battle pass progress!"));
    }

}
