package dev.lbuddyboy.samurai.custom.deepdark.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.loottable.menu.LootTablePreviewMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("deepdark")
public class DeepDarkCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam("DeepDark");

        team.sendTeamInfo(sender);
    }

    @Subcommand("loottable")
    public static void loottable(Player sender) {
        new LootTablePreviewMenu(Samurai.getInstance().getDeepDarkHandler().getLootTable(), (Menu) null).openMenu(sender);
    }

    @Subcommand("testeffect")
    @CommandPermission("op")
    public static void testeffect(Player sender, @Name("effect") String effect) {
        Samurai.getInstance().getDeepDarkHandler().getDarkAbilities().get(effect).activate(sender.getLocation());
    }

    @Subcommand("giveitem")
    @CommandPermission("op")
    public static void loottable(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        for (int i = 0; i < amount; i++) {
            ItemUtils.tryFit(onlinePlayer.getPlayer(), Samurai.getInstance().getDeepDarkHandler().getSpawnItem(), false);
        }
    }
    @Subcommand("spawnevent")
    @CommandPermission("op")
    public static void spawnevent(CommandSender sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam("DeepDark");

        team.getHQ().getChunk().load();
        Samurai.getInstance().getDeepDarkHandler().spawnEntity(team.getHQ());
    }

}
