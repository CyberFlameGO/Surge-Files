package dev.aurapvp.samurai.faction.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.api.event.faction.*;
import dev.aurapvp.samurai.economy.EconomyType;
import dev.aurapvp.samurai.economy.IEconomy;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.faction.claim.Claim;
import dev.aurapvp.samurai.faction.command.contexts.DepositContext;
import dev.aurapvp.samurai.faction.editor.menu.FactionListMenu;
import dev.aurapvp.samurai.faction.member.FactionInvitation;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import dev.aurapvp.samurai.faction.member.FactionRole;
import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.timer.impl.CombatTagTimer;
import dev.aurapvp.samurai.timer.impl.HomeTimer;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cuboid;
import dev.aurapvp.samurai.util.ItemUtils;
import dev.aurapvp.samurai.util.PagedItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("faction|f|fac|team|t")
public class FactionCommand extends BaseCommand {
    @Default
    @HelpCommand
    @Subcommand("help")
    public static void defp(CommandSender sender, @Name("help") @Optional CommandHelp help) {
        int page = help.getPage();
        List<String> header = CC.translate(Arrays.asList(
                CC.CHAT_BAR,
                "&a&lFaction Command Help"
        ));
        List<String> entries = new ArrayList<>();

        for (HelpEntry entry : help.getHelpEntries()) {
            entries.add("&/" + entry.getCommand() + " " + entry.getParameterSyntax() + " &7- " + entry.getDescription());
        }

        PagedItem pagedItem = new PagedItem(entries, header, 10);

        pagedItem.send(help.getIssuer().getIssuer(), page);

        help.getIssuer().sendMessage(" ");
        help.getIssuer().sendMessage("&7You can do /f help <page> - You're currently viewing on page #" + page + ".");
        help.getIssuer().sendMessage(CC.CHAT_BAR);
    }

    @Subcommand("create")
    public void create(Player sender, @Name("name") String name) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByName(name);

        if (faction != null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_ALREADY_EXISTS.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction))));
            return;
        }

        faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction != null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_ALREADY_IN.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction))));
            return;
        }

        faction = Samurai.getInstance().getFactionHandler().createFaction(name, sender.getUniqueId());
        faction.setType(FactionType.SPAWN);

        sender.sendMessage(FactionConfiguration.CREATED_FACTION_SENDER.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction)));
        CC.broadcast(FactionConfiguration.CREATED_FACTION_BROADCAST.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction)));
        FactionCreateEvent event = new FactionCreateEvent(sender, name, false);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Subcommand("createsystem")
    @CommandPermission("op")
    public void createsystem(Player sender, @Name("name") String name) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByName(name);

        if (faction != null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_ALREADY_EXISTS.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction))));
            return;
        }

        faction = Samurai.getInstance().getFactionHandler().createFaction(name, null);

        FactionCreateEvent event = new FactionCreateEvent(sender, name, true);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Subcommand("disband")
    public void disband(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (!member.hasPermission(FactionPermission.DISBAND_FACTION)) {
            sender.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.DISBAND_FACTION.getContext()));
            return;
        }

        faction.disband();

        sender.sendMessage(FactionConfiguration.DISBAND_FACTION_SENDER.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction)));
        CC.broadcast(FactionConfiguration.DISBAND_FACTION_BROADCAST.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction)));

        FactionDisbandEvent event = new FactionDisbandEvent(sender, faction, false);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Subcommand("claim")
    public void claim(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        if (sender.getInventory().contains(Samurai.getInstance().getFactionHandler().getClaimWand())) {
            sender.getInventory().remove(Samurai.getInstance().getFactionHandler().getClaimWand());
        }
        sender.getInventory().addItem(Samurai.getInstance().getFactionHandler().getClaimWand());
    }

    @Subcommand("unclaim")
    public void unclaim(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        Claim claim = faction.getClaim(sender.getLocation());
        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (!member.hasPermission(FactionPermission.UNCLAIM_LAND)) {
            sender.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.UNCLAIM_LAND.getContext()));
            return;
        }

        if (claim == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.LAND_UNCLAIMED_NOT_IN.getString()));
            return;
        }

        Cuboid cuboid = claim.getCuboid();
        double money = (cuboid.getSizeX() + cuboid.getSizeZ()) * FactionConfiguration.LAND_CLAIMED_PRICE_PER_BLOCK.getDouble();

        faction.setBalance(faction.getBalance() + money);
        faction.unclaimLand(claim);
        Samurai.getInstance().getDynMapImpl().getClaimLayer().deleteMarker(claim);
        map(sender);

        faction.sendMessage(FactionConfiguration.LAND_UNCLAIMED_BROADCAST.getString(
                FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(sender, member, faction),
                "%price%", Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(money),
                "%size%", cuboid.getSizeX() + "x" + cuboid.getSizeZ()
        ));
    }

    @Subcommand("invite")
    @CommandCompletion("@players")
    public void invite(Player sender, @Name("player") OfflinePlayer target) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        if (faction.getFactionMember(target.getUniqueId()) != null) {
            faction.sendMessage(FactionConfiguration.MEMBER_INVITATION_ALREADY_IN_FACTION.getString(FactionConfiguration.PLAYER_TARGET_FACTION_PLACEHOLDERS(sender, target, faction)));
            return;
        }

        if (Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(target.getUniqueId()) != null) {
            faction.sendMessage(FactionConfiguration.FACTION_ALREADY_IN_TARGET.getString(FactionConfiguration.PLAYER_TARGET_FACTION_PLACEHOLDERS(sender, target, faction)));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (!member.hasPermission(FactionPermission.INVITE_MEMBERS)) {
            sender.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.INVITE_MEMBERS.getContext()));
            return;
        }

        FactionInvitation invitation = faction.getInvitation(target.getUniqueId());

        if (invitation != null) {
            faction.sendMessage(FactionConfiguration.MEMBER_INVITATION_ALREADY_INVITED.getString(FactionConfiguration.PLAYER_TARGET_FACTION_PLACEHOLDERS(sender, target, faction)));
            return;
        }

        invitation = new FactionInvitation(sender.getUniqueId(), target.getUniqueId());
        faction.getInvitations().add(invitation);
        faction.triggerUpdate();

        faction.sendMessage(FactionConfiguration.MEMBER_INVITATION_FACTION_INVITED.getString(FactionConfiguration.PLAYER_MEMBER_TARGET_FACTION_PLACEHOLDERS(sender, member, target, faction)));
        if (target.isOnline()) {
            FactionConfiguration.MEMBER_INVITATION_TARGET.getFancyMessage(FactionConfiguration.PLAYER_MEMBER_TARGET_FACTION_PLACEHOLDERS(sender, member, target, faction)).send(target.getPlayer());
        }
    }

    @Subcommand("uninvite")
    @CommandCompletion("@invites")
    public void uninvite(Player sender, @Name("player") OfflinePlayer target) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (!member.hasPermission(FactionPermission.INVITE_MEMBERS)) {
            sender.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.INVITE_MEMBERS.getContext()));
            return;
        }

        FactionInvitation invitation = faction.getInvitation(target.getUniqueId());

        if (invitation == null) {
            faction.sendMessage(FactionConfiguration.MEMBER_INVITATION_NOT_INVITED.getString(FactionConfiguration.PLAYER_TARGET_FACTION_PLACEHOLDERS(sender, target, faction)));
            return;
        }

        faction.getInvitations().remove(invitation);
        faction.triggerUpdate();

        faction.sendMessage(FactionConfiguration.MEMBER_INVITATION_FACTION_UNINVITED.getString(FactionConfiguration.PLAYER_MEMBER_TARGET_FACTION_PLACEHOLDERS(sender, member, target, faction)));
    }

    @Subcommand("join|accept")
    @CommandCompletion("@factions")
    public void join(Player sender, @Name("faction") Faction faction) {
        FactionInvitation invitation = faction.getInvitation(sender.getUniqueId());

        if (invitation == null) {
            sender.sendMessage(FactionConfiguration.MEMBER_INVITATION_NOT_INVITED.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction)));
            return;
        }

        FactionPermission.FactionMember member = faction.addMember(sender.getUniqueId());

        faction.getInvitations().removeIf(i -> i.getTarget().toString().equals(sender.getUniqueId().toString()));
        faction.triggerUpdate();
        Samurai.getInstance().getFactionHandler().getFactionPlayerUUIDs().put(sender.getUniqueId(), faction);

        faction.sendMessage(FactionConfiguration.FACTION_MEMBER_ADDED.getString(FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(sender, member, faction)));
        FactionJoinEvent event = new FactionJoinEvent(sender, faction, false);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Subcommand("who|info|i")
    @CommandCompletion("@factions")
    public void who(Player sender, @Name("faction") @Default("self") Faction faction) {
        faction.info(sender);
    }

    @Subcommand("focus")
    @CommandCompletion("@factions")
    public void focus(Player sender, @Name("faction") Faction targetFaction) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        faction.setFocusFaction(targetFaction.getUniqueId());
        faction.sendMessage(CC.translate(FactionConfiguration.FACTION_FOCUSED_BROADCAST.getString(
                FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(sender, faction.getFactionMember(sender.getUniqueId()), targetFaction)
        )));
    }

    @Subcommand("unfocus")
    @CommandCompletion("@factions")
    public void unfocus(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        if (faction.getFocusedFaction() == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_UNFOCUSED_NO_FACTION.getString()));
            return;
        }

        if (faction.getFocusedFaction() != null) {
            faction.sendMessage(CC.translate(FactionConfiguration.FACTION_UNFOCUSED_BROADCAST.getString(
                    FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(sender, faction.getFactionMember(sender.getUniqueId()), faction.getFocusedFaction())
            )));
        }

        faction.setFocusFaction(null);
    }

    /*
    A more specific mapping system
     */

    @Subcommand("claims")
    public void claims(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        if (faction.getClaims() == null || faction.getClaims().isEmpty()) {
            sender.sendMessage(CC.translate(FactionConfiguration.LAND_CLAIMS_NO_CLAIMS.getString()));
            return;
        }

        int i = 1;
        for (Claim claim : faction.getClaims()) {
            sender.sendMessage(CC.translate(FactionConfiguration.LAND_CLAIMS_CLAIMS_FORMAT.getString(
                    "%place%", i++,
                    "%x%", claim.getCuboid().getCenter().getBlockX(),
                    "%y%", claim.getCuboid().getCenter().getBlockY(),
                    "%z%", claim.getCuboid().getCenter().getBlockZ(),
                    "%world%", claim.getCuboid().getWorld().getName()
            )));
        }
    }

    /*
    A more generalized mapping system
     */

    @Subcommand("map")
    public static void map(Player sender) {
        if (Samurai.getInstance().getFactionHandler().getClaimHandler().getBlockChanges().containsKey(sender)) {
            Samurai.getInstance().getFactionHandler().getClaimHandler().unMapClaims(sender);
            return;
        }

        Map<Claim, List<Material>> factions = Samurai.getInstance().getFactionHandler().getClaimHandler().mapClaims(sender);

        if (factions.isEmpty()) {
            sender.sendMessage(CC.translate("&cNo claims found within a 10x10 radius."));
            return;
        }

        sender.sendMessage(" ");
        factions.forEach((k, v) -> {
            Faction faction = Samurai.getInstance().getFactionHandler().getFactionByUniqueId(k.getFaction());
            if (faction == null) return;

            AtomicInteger e = new AtomicInteger(1);

            if (v.size() > 1) {
                sender.sendMessage(CC.translate(faction.getName(sender) + "&7's Claims:"));
                v.forEach(material -> sender.sendMessage(CC.translate("&e#" + (e.getAndIncrement()) + "&7: " + ItemUtils.getName(material))));
                return;
            }

            v.forEach(material -> sender.sendMessage(CC.translate(faction.getName(sender) + "&7's Claim: " + ItemUtils.getName(material))));
        });
        sender.sendMessage(" ");
    }

    @Subcommand("leave")
    public void leave(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (member.getRole() == FactionRole.LEADER) {
            sender.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.LEAVE_FACTION.getContext()));
            return;
        }

        for (PlayerTimer timer : Samurai.getInstance().getTimerHandler().getPlayerTimers()) {
            if (timer instanceof CombatTagTimer combatTagTimer) {
                if (combatTagTimer.getCooldown().onCooldown(sender.getUniqueId())) {
                    sender.sendMessage(CC.translate("&cYou cannot leave your faction home whilst combat tagged!"));
                    return;
                }
            }
        }

        faction.getMembers().removeIf(i -> i.getUuid().toString().equals(sender.getUniqueId().toString()));
        faction.triggerUpdate();
        Samurai.getInstance().getFactionHandler().getFactionPlayerUUIDs().remove(sender.getUniqueId());

        faction.sendMessage(FactionConfiguration.FACTION_MEMBER_REMOVED.getString(FactionConfiguration.PLAYER_FACTION_PLACEHOLDERS(sender, faction)));
        FactionLeaveEvent event = new FactionLeaveEvent(sender, faction, false, false);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Subcommand("deposit|d")
    public void deposit(Player sender, @Name("amount|all") DepositContext.Deposit deposit) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (deposit.getAmount() <= 0) {
            sender.sendMessage(CC.translate("&cYou cannot deposit less than $0."));
            return;
        }

        if (!EconomyType.MONEY.hasAmount(sender.getUniqueId(), deposit.getAmount())) {
            sender.sendMessage(CC.translate("&cYou do not have enough to deposit $" + deposit.getAmount() + "."));
            return;
        }

        EconomyType.MONEY.removeAmount(sender.getUniqueId(), deposit.getAmount(), IEconomy.EconomyChange.builder().forced(false).build());
        faction.setBalance(faction.getBalance() + deposit.getAmount());
        faction.triggerUpdate();

        faction.sendMessage(FactionConfiguration.FACTION_MEMBER_DEPOSITED.getString(
                FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(sender, member, faction),
                "%amount%", Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(deposit.getAmount())
        ));
    }

    @Subcommand("withdraw|w")
    public void withdraw(Player sender, @Name("amount|all") @Flags("faction") DepositContext.Deposit deposit) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (deposit.getAmount() <= 0) {
            sender.sendMessage(CC.translate("&cYou cannot withdraw less than $0."));
            return;
        }

        if (!member.hasPermission(FactionPermission.WITHDRAW_BALANCE)) {
            sender.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.WITHDRAW_BALANCE.getContext()));
            return;
        }

        if (faction.getBalance() < deposit.getAmount()) {
            sender.sendMessage(CC.translate("&cYou do not have enough to deposit $" + deposit.getAmount() + "."));
            return;
        }

        EconomyType.MONEY.addAmount(sender.getUniqueId(), deposit.getAmount(), IEconomy.EconomyChange.builder().forced(true).build());
        faction.setBalance(faction.getBalance() - deposit.getAmount());
        faction.triggerUpdate();

        faction.sendMessage(FactionConfiguration.FACTION_MEMBER_WITHDRAW.getString(
                FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(sender, member, faction),
                "%amount%", Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(deposit.getAmount())
        ));
    }

    @Subcommand("sethome|sethq")
    public void sethq(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(sender.getUniqueId());

        if (!member.hasPermission(FactionPermission.SET_HOME)) {
            sender.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.SET_HOME.getContext()));
            return;
        }

        Faction factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(sender.getLocation());
        if (factionAt != faction) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_MEMBER_NOT_IN_TERRITORY.getString()));
            return;
        }

        FactionSetHomeEvent event = new FactionSetHomeEvent(sender, faction, faction.getHome(), sender.getLocation(), false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        faction.setHome(sender.getLocation());
        faction.triggerUpdate();

        faction.sendMessage(FactionConfiguration.FACTION_MEMBER_SET_HOME.getString(
                FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(sender, member, faction),
                "%x%", sender.getLocation().getBlockX(),
                "%y%", sender.getLocation().getBlockY(),
                "%z%", sender.getLocation().getBlockZ()
        ));
    }

    @Subcommand("home|hq")
    public void home(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (faction == null) {
            sender.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        if (faction.getHome() == null) {
            return;
        }

        for (PlayerTimer timer : Samurai.getInstance().getTimerHandler().getPlayerTimers()) {
            if (!(timer instanceof HomeTimer)) {
                if (timer instanceof CombatTagTimer combatTagTimer) {
                    if (combatTagTimer.getCooldown().onCooldown(sender.getUniqueId())) {
                        sender.sendMessage(CC.translate("&cYou cannot teleport to your faction home whilst combat tagged!"));
                        return;
                    }
                }
                continue;
            }

            if (timer.getCooldown().onCooldown(sender.getUniqueId())) {
                sender.sendMessage(CC.translate("&cYou are already warping to your home!"));
                return;
            }

            timer.activate(sender);
            return;
        }
    }

    /*
    Staff Commands
     */


    @Subcommand("tp")
    @CommandCompletion("@factions")
    @CommandPermission("samurai.faction.teleport")
    public void teleport(Player sender, @Name("faction") Faction targetFaction) {
        if (targetFaction.getHome() == null) {
            if (targetFaction.getClaims().isEmpty()) {
                sender.sendMessage(CC.translate("&cNo claim or faction home found for this faction."));
                return;
            }

            sender.teleport(targetFaction.getClaims().get(0).getCuboid().getUpperSW());
            return;
        }
        sender.teleport(targetFaction.getHome());
    }

    @Subcommand("editor")
    @CommandPermission("op")
    public void editor(Player sender) {
        new FactionListMenu().openMenu(sender);
    }

}
