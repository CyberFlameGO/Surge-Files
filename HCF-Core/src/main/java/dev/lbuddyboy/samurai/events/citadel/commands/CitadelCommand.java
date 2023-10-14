package dev.lbuddyboy.samurai.events.citadel.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.google.common.base.Joiner;
import dev.lbuddyboy.samurai.events.citadel.CitadelHandler;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.team.Team;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

@CommandAlias("citadel")
public class CitadelCommand extends BaseCommand {

    // Make this pretty.
    @Default
    public static void citadel(Player sender) {
        Set<ObjectId> cappers = Samurai.getInstance().getCitadelHandler().getCappers();
        Set<String> capperNames = new HashSet<>();

        for (ObjectId capper : cappers) {
            Team capperTeam = Samurai.getInstance().getTeamHandler().getTeam(capper);

            if (capperTeam != null) {
                capperNames.add(capperTeam.getName());
            }
        }

        if (!capperNames.isEmpty()) {
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was captured by " + ChatColor.GREEN + Joiner.on(", ").join(capperNames) + ChatColor.YELLOW + ".");
        } else {
            Event citadel = Samurai.getInstance().getEventHandler().getEvent("Citadel");

            if (citadel != null && citadel.isActive()) {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel can be captured now.");
            } else {
                sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Citadel was not captured last week.");
            }
        }

        Date lootable = Samurai.getInstance().getCitadelHandler().getLootable();
        sender.sendMessage(ChatColor.GOLD + "Citadel: " + ChatColor.WHITE + "Lootable " + (lootable.before(new Date()) ? "now" : "at " + (new SimpleDateFormat()).format(lootable) + (capperNames.isEmpty() ? "." : ", and lootable now by " + Joiner.on(", ").join(capperNames) + ".")));
    }

    @Subcommand("loadloottable")
    @CommandPermission("foxtrot.citadel")
    public static void citadelLoadLoottable(Player sender) {
        sender.getInventory().clear();
        int itemIndex = 0;

        for (ItemStack itemStack : Samurai.getInstance().getCitadelHandler().getCitadelLoot()) {
            sender.getInventory().setItem(itemIndex, itemStack);
            itemIndex++;
        }

        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Loaded Citadel loot into your inventory.");
    }

    @Subcommand("rescan|rescanchests")
    @CommandPermission("foxtrot.citadel")
    public static void citadelRescanChests(CommandSender sender) {
        Samurai.getInstance().getCitadelHandler().scanLoot();
        Samurai.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Rescanned all Citadel chests.");
    }

    @Subcommand("refill|respawnchests")
    @CommandPermission("foxtrot.citadel")
    public static void citadelRespawnChests(CommandSender sender) {
        Samurai.getInstance().getCitadelHandler().respawnCitadelChests();
        Bukkit.broadcastMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Respawned all Citadel chests.");
    }

    @Subcommand("save")
    @CommandPermission("foxtrot.citadel")
    public static void citadelSave(Player sender) {
        Samurai.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel info to file.");
    }

    @Subcommand("saveloottable|saveloot")
    @CommandPermission("foxtrot.citadel")
    public static void citadelSaveLoottable(Player sender) {
        Samurai.getInstance().getCitadelHandler().getCitadelLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                Samurai.getInstance().getCitadelHandler().getCitadelLoot().add(itemStack);
            }
        }

        Samurai.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel loot from your inventory.");
    }

    @Subcommand("setcapper|setcappingteam")
    @CommandPermission("foxtrot.citadel")
    public static void citadelSetCapper(Player sender, @Name("capper") String cappers) {
        if (cappers.equals("null")) {
            Samurai.getInstance().getCitadelHandler().resetCappers();
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Reset Citadel cappers.");
        } else {
            String[] teamNames = cappers.split(",");
            List<ObjectId> teams = new ArrayList<>();

            for (String teamName : teamNames) {
                Team team = Samurai.getInstance().getTeamHandler().getTeam(teamName);

                if (team != null) {
                    teams.add(team.getUniqueId());
                } else {
                    sender.sendMessage(ChatColor.RED + "Team '" + teamName + "' cannot be found.");
                    return;
                }
            }

            Samurai.getInstance().getCitadelHandler().getCappers().clear();
            Samurai.getInstance().getCitadelHandler().getCappers().addAll(teams);
            sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Updated Citadel cappers.");
        }
    }

}