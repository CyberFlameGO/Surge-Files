package dev.lbuddyboy.samurai.events.region.loothill.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.region.cavern.Cavern;
import dev.lbuddyboy.samurai.events.region.loothill.LootHill;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;

@CommandAlias("loothill|lhill|loot")
public class LootHillCommand extends BaseCommand {

    @Default
    public static void def(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam("LootHill");

        team.sendTeamInfo(sender);
    }

    @Subcommand("scan")
    @CommandPermission("op")
    public static void abilityhill(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam("LootHill");

        // Make sure we have a team
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must first create the team (LootHill) and claim it!");
            return;
        }

        // Make sure said team has a claim
        if (team.getClaims().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You must claim land for 'LootHill' before scanning it!");
            return;
        }

        if (!Samurai.getInstance().getLootHillHandler().hasAbilityHill()) {
            Samurai.getInstance().getLootHillHandler().setLootHill(new LootHill());
        }

        Samurai.getInstance().getLootHillHandler().getLootHill().scan();
        Samurai.getInstance().getLootHillHandler().save();

        sender.sendMessage(CC.translate("&x&f&b&9&e&0&9[Loot Hill]") + " Scanned all diamond blocks and saved it to the collection!");
    }

    @Subcommand("reset")
    @CommandPermission("op")
    public static void abilityhillReset(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam("LootHill");

        if (team == null || team.getClaims().isEmpty()) {
            sender.sendMessage(RED + "Create the team 'LootHill', then make a claim for it, finally scan it! (/loothill scan)");
            return;
        }

        Samurai.getInstance().getLootHillHandler().getLootHill().reset();

        Bukkit.broadcastMessage(CC.translate("&x&f&b&9&e&0&9[Loot Hill]") + GREEN + " All ability blocks has been reset!");
    }
}