package dev.lbuddyboy.pcore.robots.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.essential.locator.LocationType;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.menu.RobotListMenu;
import dev.lbuddyboy.pcore.util.ItemUtils;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("robot|probot|robots")
public class RobotCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new RobotListMenu().openMenu(sender);
    }

    @Subcommand("editor")
    @CommandPermission("pcore.command.robot")
    public void gr(Player sender) {

    }

    @Subcommand("stats")
    @CommandPermission("pcore.command.robot")
    public void stats(Player sender) {
        World world = pCore.getInstance().getPlotHandler().getGrid().getWorld();

        int i = 0;
        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof ArmorStand)) continue;

            i++;
        }
        System.out.println(i + " Armor Stands found.");
    }

    @Subcommand("giverobot")
    @CommandPermission("pcore.command.robot")
    public void gr(CommandSender sender, @Name("player") @Single OnlinePlayer player, @Name("robot") @Single String robot, @Name("amount") int amount) {
        NBTItem item = pCore.getInstance().getRobotHandler().getTypes().get(robot).getItem();
        ItemStack stack = item.getItem();

        stack.setAmount(amount);

        boolean fits = ItemUtils.tryFitBool(player.getPlayer(), stack, false);

        if (fits) {
            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, player.getPlayer().getUniqueId(), LocationType.GROUND_ITEM));
        }
    }

}
