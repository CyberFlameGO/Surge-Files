package dev.lbuddyboy.samurai.custom.teamwar.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.teamwar.menu.MembersMenu;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarState;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarTeam;
import dev.lbuddyboy.samurai.custom.teamwar.thread.WarThread;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Comparator;
import java.util.stream.Collectors;

@CommandAlias("teamwar")
public class TeamWarCommand extends BaseCommand {

    @Subcommand("setspawna")
    @CommandPermission("op")
    public static void setspawn(Player sender) {
        Samurai.getInstance().getTeamWarHandler().setSpawnA(sender.getLocation());
        Samurai.getInstance().getTeamWarHandler().save();
        sender.sendMessage(CC.translate("&aSet the spawn point of the team war a to your location."));
    }

    @Subcommand("setspawnb")
    @CommandPermission("op")
    public static void setspawnB(Player sender) {
        Samurai.getInstance().getTeamWarHandler().setSpawnB(sender.getLocation());
        Samurai.getInstance().getTeamWarHandler().save();
        sender.sendMessage(CC.translate("&aSet the spawn point of the team war b to your location."));
    }

    @Subcommand("setminimumteamsize")
    @CommandPermission("op")
    public static void setminimumteamsize(Player sender, @Name("team-size") Integer teamSize) {
        Samurai.getInstance().getTeamWarHandler().setMinimumTeamSize(teamSize);
        Samurai.getInstance().getTeamWarHandler().save();
        sender.sendMessage(CC.translate("&aUpdated the team size."));
    }

    @Subcommand("setminimumstartteams")
    @CommandPermission("op")
    public static void setminimumstartteams(Player sender, @Name("team-size") Integer teamSize) {
        Samurai.getInstance().getTeamWarHandler().setMinimumStartTeams(teamSize);
        Samurai.getInstance().getTeamWarHandler().save();
        sender.sendMessage(CC.translate("&aUpdated the minimum start team size."));
    }

    @Subcommand("setclasseditoritem")
    @CommandPermission("op")
    public static void setclasseditoritem(Player sender) {
        Samurai.getInstance().getTeamWarHandler().setCLASS_EDITOR_ITEM(sender.getInventory().getItemInMainHand());
        Samurai.getInstance().getTeamWarHandler().save();
        sender.sendMessage(CC.translate("&aUpdated the class editor item."));
    }

    @Subcommand("setleaveitem")
    @CommandPermission("op")
    public static void setleaveitem(Player sender) {
        Samurai.getInstance().getTeamWarHandler().setLEAVE_ITEM(sender.getInventory().getItemInMainHand());
        Samurai.getInstance().getTeamWarHandler().save();
        sender.sendMessage(CC.translate("&aUpdated the leave item."));
    }

    @Subcommand("start")
    @CommandPermission("op")
    public static void start(Player sender, @Name("delay") String delayStr) {
        if (Samurai.getInstance().getTeamWarHandler().getState() != WarState.NOT_STARTED) {
            sender.sendMessage(CC.translate("&cThis game is already starting."));
            return;
        }

        WarThread.INITIATED = -1;
        WarThread.DURATION = JavaUtils.parse(delayStr);
        Samurai.getInstance().getTeamWarHandler().setState(WarState.LOBBY);
        sender.sendMessage(CC.translate("&aThe team war will start shortly."));
    }

    @Subcommand("end")
    @CommandPermission("op")
    public static void end(Player sender) {
        if (Samurai.getInstance().getTeamWarHandler().getState() == WarState.NOT_STARTED) {
            sender.sendMessage(CC.translate("&cThis game is not up."));
            return;
        }

        Samurai.getInstance().getTeamWarHandler().setState(WarState.ENDING);
        Samurai.getInstance().getTeamWarHandler().endGame();
        Samurai.getInstance().getTeamWarHandler().setState(WarState.NOT_STARTED);
        sender.sendMessage(CC.translate("&aThe team war has now ended."));
    }

    @Subcommand("classes")
    public static void classes(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(CC.translate("&cFailed to edit classes: You aren't on a team."));
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to edit classes: You aren't the leader of the team."));
            return;
        }

        if (!Samurai.getInstance().getTeamWarHandler().isAtWar(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to edit classes: You are not at war."));
            return;
        }

        WarTeam warTeam = Samurai.getInstance().getTeamWarHandler().getWarTeam(sender.getUniqueId());

        new MembersMenu(warTeam).openMenu(sender);
    }

    @Subcommand("join")
    public static void join(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(CC.translate("&cFailed to join team war: You aren't on a team."));
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to join team war: You aren't the leader of the team."));
            return;
        }

        if (Samurai.getInstance().getTeamWarHandler().isAtWar(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to join team war: You are already at war."));
            return;
        }

        Samurai.getInstance().getTeamWarHandler().addToQueue(sender, team);
    }

    @Subcommand("summon")
    public static void summon(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(CC.translate("&cFailed to summon your team: You aren't on a team."));
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to summon your team: You aren't the leader of the team."));
            return;
        }

        if (!Samurai.getInstance().getTeamWarHandler().isAtWar(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to join team war: You aren't at war."));
            return;
        }

        for (Player member : team.getOnlineMembers()) {

            if (!Samurai.getInstance().getTeamWarHandler().isAvailable(member)) {
                sender.sendMessage(CC.translate("&cYou cannot summon '" + member.getName() + "' due to them not being available."));
                sender.sendMessage(CC.translate("&c| Ask them to check their chats for more information."));
                continue;
            }

            Samurai.getInstance().getTeamWarHandler().spamMessage(member, "&c[!] Warning [!] If you leave at any point your whole team will be kicked from the event. [!] Warning [!] ");
            Samurai.getInstance().getTeamWarHandler().handleJoin(member);
            member.sendMessage(CC.translate("&aYou are now apart of the team event!"));
            sender.sendMessage(CC.translate("&aSuccessfully summoned: " + member.getName() + " to the team war!"));
        }

        Samurai.getInstance().getTeamWarHandler().setQueuedTeams(Samurai.getInstance().getTeamWarHandler().getQueuedTeams().stream().sorted(Comparator.comparingInt(WarTeam::getMemberSize)).collect(Collectors.toList()));

    }

    @Subcommand("leave")
    public static void leave(Player sender) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(CC.translate("&cFailed to leave team war: You aren't on a team."));
            return;
        }

        if (!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to leave team war: You aren't the leader."));
            return;
        }

        if (!Samurai.getInstance().getTeamWarHandler().isAtWar(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to leave team war: You aren't at war."));
            return;
        }

        Samurai.getInstance().getTeamWarHandler().handleQuit(sender);
    }

    @Subcommand("spectate")
    public static void spectate(Player sender) {

        if (Samurai.getInstance().getTeamWarHandler().isAtWar(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cFailed to join team war: You are already at war."));
            return;
        }

        if (!Samurai.getInstance().getTeamWarHandler().isAvailable(sender)) {
            return;
        }

        sender.setMetadata("spectating_team_war", new FixedMetadataValue(Samurai.getInstance(), true));
        Samurai.getInstance().getTeamWarHandler().handleSpectate(sender);
    }

}
