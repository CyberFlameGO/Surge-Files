package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.team.menu.*;
import dev.lbuddyboy.samurai.util.object.Callback;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.commands.staff.SetTeamBalanceCommand;
import dev.lbuddyboy.samurai.team.commands.TeamCommands;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

@CommandAlias("manageteam")
public class TeamManageCommand extends BaseCommand {

    @Subcommand("leader")
    @CommandPermission("foxtrot.manage.leader")
    @CommandCompletion("@team")
    public static void teamLeader(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.leader")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        new SelectNewLeaderMenu(team).openMenu(sender);
    }

    @Subcommand("promote")
    @CommandPermission("foxtrot.manage.promote")
    @CommandCompletion("@team")
    public static void promoteTeam(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.promote")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        new PromoteMembersMenu(team).openMenu(sender);
    }

    @Subcommand("demote")
    @CommandPermission("foxtrot.manage.demote")
    @CommandCompletion("@team")
    public static void demoteTeam(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.demote")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        new DemoteMembersMenu(team).openMenu(sender);
    }

    @Subcommand("kick")
    @CommandPermission("foxtrot.manage.kick")
    @CommandCompletion("@team")
    public static void kickTeam(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.kick")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        new KickPlayersMenu(team).openMenu(sender);
    }

    @Subcommand("balance")
    @CommandPermission("foxtrot.manage.balance")
    @CommandCompletion("@team")
    public static void balanceTeam(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.balance")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        conversationDouble(sender, "§bEnter a new balance for " + team.getName() + ".", (d) -> {
            SetTeamBalanceCommand.setTeamBalance(sender, team, d.floatValue());
            sender.sendRawMessage(ChatColor.GRAY + team.getName() + " now has a balance of " + team.getBalance());
        });
    }

    @Subcommand("dtr")
    @CommandPermission("foxtrot.manage.dtr")
    @CommandCompletion("@team")
    public static void dtrTeam(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.dtr")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        if (sender.hasPermission("foxtrot.manage.setdtr")) {
            conversationDouble(sender, "§eEnter a new DTR for " + team.getName() + ".", (d) -> {
                team.setDTR(d.intValue());
                sender.sendRawMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " has a new DTR of " + ChatColor.LIGHT_PURPLE + d.floatValue() + ChatColor.YELLOW + ".");
            });
        } else {
            new DTRMenu(team).openMenu(sender);
        }
    }

    @Subcommand("rename")
    @CommandPermission("foxtrot.manage.rename")
    @CommandCompletion("@team")
    public static void renameTeam(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.rename")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        conversationString(sender, "§aEnter a new name for " + team.getName() + ".", (name) -> {
            String oldName = team.getName();
            team.rename(name);
            sender.sendRawMessage(ChatColor.GRAY + oldName + " now has a name of " + team.getName());
        });
    }

    @Subcommand("mute")
    @CommandPermission("foxtrot.manage.mute")
    @CommandCompletion("@team")
    public static void muteTeam(Player sender, @Name("team")Team team) {
        if (!sender.hasPermission("foxtrot.manage.mute")) {
            sender.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        new MuteMenu(team).openMenu(sender);

    }

    @Subcommand("manage")
    @CommandPermission("foxtrot.manage.manage")
    @CommandCompletion("@team")
    public static void manageTeam(Player sender, @Name("team")Team team) {
        new TeamManageMenu(team).openMenu(sender);
    }

    private static void conversationDouble(Player p, String prompt, Callback<Double> callback) {
        ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                try {
                    callback.callback(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + s + " is not a number.");
                }

                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }

    private static void conversationString(Player p, String prompt, Callback<String> callback) {
        ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            @Override
            public String getPromptText(ConversationContext context) {
                return prompt;
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String newName) {

                if (newName.length() > 16) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Maximum team name size is 16 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (newName.length() < 3) {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Minimum team name size is 3 characters!");
                    return Prompt.END_OF_CONVERSATION;
                }

                if (!TeamCommands.ALPHA_NUMERIC.matcher(newName).find()) {
                    if (Samurai.getInstance().getTeamHandler().getTeam(newName) == null) {
                        callback.callback(newName);
                        return Prompt.END_OF_CONVERSATION;

                    } else {
                        cc.getForWhom().sendRawMessage(ChatColor.RED + "A team with that name already exists!");
                    }
                } else {
                    cc.getForWhom().sendRawMessage(ChatColor.RED + "Team names must be alphanumeric!");
                }


                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("quit").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(p);
        p.beginConversation(con);

    }
}
