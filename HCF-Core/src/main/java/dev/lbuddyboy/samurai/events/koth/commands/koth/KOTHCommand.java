package dev.lbuddyboy.samurai.events.koth.commands.koth;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.commands.menu.schedule.buttons.KoTHScheduleButton;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventScheduledTime;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.events.dtc.DTC;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.ChatColor.*;
import static org.bukkit.ChatColor.GRAY;

@CommandAlias("koth")
public class KOTHCommand extends BaseCommand {

    // Make this pretty.

    @Default
    public void def(Player sender) {
        koth(sender);
    }

    @Subcommand("next|info|next|info")
    public static void koth(Player sender) {
        for (Event koth : Samurai.getInstance().getEventHandler().getEvents()) {
            if (!koth.isHidden() && koth.isActive()) {
                FancyMessage fm = new FancyMessage("[Events] ")
                        .color(GOLD)
                        .then(koth.getName())
                            .color(YELLOW) // koth name should be yellow
                            .style(UNDERLINE);
                            if (koth instanceof KOTH) {
                                fm.tooltip(YELLOW.toString() + ((KOTH) koth).getCapLocation().getBlockX() + ", " + ((KOTH) koth).getCapLocation().getBlockZ());
                            }
                            fm.color(YELLOW) // should color Event coords gray
                        .then(" can be contested now.")
                            .color(GOLD);
                        fm.send(sender);
                return;
            }
        }

        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : Samurai.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            if (entry.getKey().toDate().after(now)) {
                sender.sendMessage(GOLD + "[King of the Hill] " + YELLOW + entry.getValue() + GOLD + " can be captured at " + BLUE + KoTHScheduleButton.KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + GOLD + ".");
                sender.sendMessage(GOLD + "[King of the Hill] " + YELLOW + "It is currently " + BLUE + KoTHScheduleButton.KOTH_DATE_FORMAT.format(now) + GOLD + ".");
                sender.sendMessage(YELLOW + "Type '/koth schedule' to see more upcoming Events.");
                return;
            }
        }

        sender.sendMessage(GOLD + "[King of the Hill] " + RED + "Next Event: " + YELLOW + "Undefined");
    }

    @Subcommand("create")
    @CommandPermission("foxtrot.koth.admin")
    public static void kothCreate(Player sender, @Name("koth") String koth) {
        new KOTH(koth, sender.getLocation());
        sender.sendMessage(ChatColor.GRAY + "Created a KOTH named " + koth + ".");
    }

    @Subcommand("delete")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@events")
    public static void kothDelete(Player sender, @Name("koth") Event koth) {
        Samurai.getInstance().getEventHandler().getEvents().remove(koth);
        Samurai.getInstance().getEventHandler().saveEvents();
        sender.sendMessage(ChatColor.GRAY + "Deleted event " + koth.getName() + ".");
    }

    @Subcommand("activate")
    @CommandPermission("foxtrot.event.activate")
    @CommandCompletion("@events")
    public static void kothActivate(CommandSender sender, @Name("event") Event koth) {
        // Don't start a KOTH if another one is active.
        for (Event otherKoth : Samurai.getInstance().getEventHandler().getEvents()) {
            if (otherKoth.isActive()) {
                sender.sendMessage(ChatColor.RED + otherKoth.getName() + " is currently active.");
                return;
            }
        }

        if( (koth.getName().equalsIgnoreCase("Citadel") || koth.getName().toLowerCase().contains("conquest")) && !sender.isOp()) {
            sender.sendMessage(ChatColor.RED + "Only ops can use the activate command for weekend events.");
            return;
        }

        koth.activate();
        sender.sendMessage(ChatColor.GRAY + "Activated " + koth.getName() + ".");
    }

    @Subcommand("activaterandom")
    @CommandPermission("foxtrot.event.activate")
    @CommandCompletion("@events")
    public static void kothRandom(CommandSender sender) {
        List<Event> events = Samurai.getInstance().getEventHandler().getEvents().stream()
                .filter(event -> !event.getName().equalsIgnoreCase("EOTW") && !event.getName().equalsIgnoreCase("Citadel"))
                .filter(event -> !event.isActive() && !event.isHidden())
                .collect(Collectors.toList());
        Event random = events.get(ThreadLocalRandom.current().nextInt(0, events.size()));

        kothActivate(sender, random);
    }

    @Subcommand("deactivate")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@events")
    public static void kothDectivate(CommandSender sender, @Name("koth") Event koth) {
        koth.deactivate();
        sender.sendMessage(ChatColor.GRAY + "Deactivated " + koth.getName() + " event.");
    }

    @Subcommand("dist|distance")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@events")
    public static void kothDist(Player sender, @Name("koth") Event koth, @Name("distance") int distance) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Can only set distance for KOTHs");
            return;
        }

        ((KOTH) koth).setCapDistance(distance);
        sender.sendMessage(ChatColor.GRAY + "Set max distance for the " + koth.getName() + " KOTH.");
    }

    @Subcommand("help")
    @CommandPermission("foxtrot.koth.admin")
    public static void kothHelp(Player sender) {
        sender.sendMessage(ChatColor.RED + "/koth list - Lists KOTHs");
        sender.sendMessage(ChatColor.RED + "/koth activate <name> - Activates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth deactivate <name> - Deactivates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth loc <name> - Set a KOTH's cap location");
        sender.sendMessage(ChatColor.RED + "/koth time <name> <time> - Sets a KOTH's cap time");
        sender.sendMessage(ChatColor.RED + "/koth dist <name> <distance> - Sets a KOTH's cap distance");
        sender.sendMessage(ChatColor.RED + "/koth tp <name> - TPs to a KOTH's");
        sender.sendMessage(ChatColor.RED + "/koth create <name> - Creates a KOTH");
        sender.sendMessage(ChatColor.RED + "/koth delete <name> - Deletes a KOTH");
    }

    @Subcommand("hidden")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@events")
    public static void kothHidden(Player sender, @Name("koth") Event koth, @Name("hidden") boolean hidden) {
        koth.setHidden(hidden);
        sender.sendMessage(ChatColor.GRAY + "Set visibility for the " + koth.getName() + " event.");
    }

    @Subcommand("list")
    @CommandPermission("foxtrot.koth.admin")
    public static void kothList(Player sender) {
        if (Samurai.getInstance().getEventHandler().getEvents().isEmpty()) {
            sender.sendMessage(RED + "There aren't any events set.");
            return;
        }

        for (Event event : Samurai.getInstance().getEventHandler().getEvents()) {
            if (event.getType() == EventType.KOTH) {
                KOTH koth = (KOTH) event;
                sender.sendMessage((koth.isHidden() ? DARK_GRAY + "[H] " : "") + (koth.isActive() ? GREEN : RED) + koth.getName() + WHITE + " - " + GRAY + TimeUtils.formatIntoMMSS(koth.getRemainingCapTime()) + DARK_GRAY + "/" + GRAY + TimeUtils.formatIntoMMSS(koth.getCapTime()) + " " + WHITE + "- " + GRAY + (koth.getCurrentCapper() == null ? "None" : koth.getCurrentCapper()));
            } else if (event.getType() == EventType.DTC) {
                DTC dtc = (DTC) event;
                sender.sendMessage((dtc.isHidden() ? DARK_GRAY + "[H] " : "") + (dtc.isActive() ? GREEN : RED) + dtc.getName() + WHITE + " - " + GRAY + "P: " + dtc.getCurrentPoints());
            }
        }
    }

    @Subcommand("loc")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@events")
    public static void kothLoc(Player sender, @Name("koth") Event koth) {
        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to set location for a non-KOTH event.");
        } else {
            ((KOTH) koth).setLocation(sender.getLocation());
            sender.sendMessage(ChatColor.GRAY + "Set cap location for the " + koth.getName() + " KOTH.");
        }
    }

    @Subcommand("schedule")
    public static void kothSchedule(Player sender) {
        int sent = 0;
        Date now = new Date();

        for (Map.Entry<EventScheduledTime, String> entry : Samurai.getInstance().getEventHandler().getEventSchedule().entrySet()) {
            Event resolved = Samurai.getInstance().getEventHandler().getEvent(entry.getValue());

            if (resolved == null || resolved.isHidden() || !entry.getKey().toDate().after(now) || resolved.getType() != EventType.KOTH) {
                continue;
            }

            if (sent > 5) {
                break;
            }

            sent++;
            sender.sendMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + KoTHScheduleButton.KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + ChatColor.GOLD + ".");
        }

        if (sent == 0) {
            sender.sendMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.RED + "KOTH Schedule: " + ChatColor.YELLOW + "Undefined");
        } else {
            sender.sendMessage(ChatColor.GOLD + "[King of the Hill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + KoTHScheduleButton.KOTH_DATE_FORMAT.format(new Date()) + ChatColor.GOLD + ".");
        }
    }

    @Subcommand("time")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@events")
    public static void kothTime(Player sender, @Name("koth") Event koth, @Name("time") float time) {
        if (time > 20F) {
            sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "This command was changed! The time parameter is now in minutes, not seconds. For example, to set a KOTH's capture time to 20 minutes 30 seconds, use /koth time 20.5");
        }

        if (koth.getType() != EventType.KOTH) {
            sender.sendMessage(ChatColor.RED + "Unable to modify cap time for a non-KOTH event.");
        } else {
            ((KOTH) koth).setCapTime((int) (time * 60F));
            sender.sendMessage(ChatColor.GRAY + "Set cap time for the " + koth.getName() + " KOTH.");
        }
    }

    @Subcommand("tp|teleport")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@events")
    public static void kothTP(Player sender, @Name("koth") @Optional @Flags("active") Event koth) {
        if (koth.getType() == EventType.KOTH) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(Samurai.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " KOTH.");
        } else if (koth.getType() == EventType.DTC) {
            sender.teleport(((KOTH) koth).getCapLocation().toLocation(Samurai.getInstance().getServer().getWorld(((KOTH) koth).getWorld())));
            sender.sendMessage(ChatColor.GRAY + "Teleported to the " + koth.getName() + " DTC.");
        }

        sender.sendMessage(ChatColor.RED + "You can't TP to an event that doesn't have a location.");
    }

    @Subcommand("givekothticket")
    @CommandPermission("foxtrot.koth.admin")
    @CommandCompletion("@players")
    public static void givekothticket(CommandSender sender, @Name("player") OnlinePlayer target, @Name("amount") int amount) {
        ItemStack stack = kothTicket.clone();
        stack.setAmount(amount);

        InventoryUtils.tryFit(target.getPlayer().getInventory(), stack);
    }

    public static ItemStack kothTicket = new ItemBuilder(Material.NAME_TAG)
            .displayName(CC.translate("&4&lKoTH Ticket &7(Click)"))
            .build();

}