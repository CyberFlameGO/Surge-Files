package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

@CommandAlias("cpfrom|copyinvfrom|copyfrom")
@CommandPermission("foxtrot.copyfrom")
public class CopyFromCommand extends BaseCommand {

    @Default
    public static void copyinvto(Player sender, @Name("player") OnlinePlayer receiver){
        if (receiver == null){
            sender.sendMessage(CC.translate("&cThat player is not online!"));
            return;
        }

        if (receiver.getPlayer() == sender){
            sender.sendMessage(CC.translate("&cNo. Action has been alerted to Head Staff! Nice Abuse spaz"));
            return;
        }

        ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

            public String getPromptText(ConversationContext context) {
                return "§aAre you sure you want to copy inventory from? Type §byes§a to confirm or §cno§a to quit.";
            }

            @Override
            public Prompt acceptInput(ConversationContext cc, String s) {
                if (s.equalsIgnoreCase("yes")) {
                    sender.getInventory().setContents(receiver.getPlayer().getInventory().getContents());
                    sender.getInventory().setArmorContents(receiver.getPlayer().getInventory().getArmorContents());

                    sender.sendMessage(CC.translate("&aYou have copied " + receiver.getPlayer().getDisplayName() + "inventory to yours!"));
                    return Prompt.END_OF_CONVERSATION;
                }

                if (s.equalsIgnoreCase("no")) {
                    cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
                    return Prompt.END_OF_CONVERSATION;
                }

                cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type §b/yes§a to confirm or §c/no§a to quit.");
                return Prompt.END_OF_CONVERSATION;
            }

        }).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

        Conversation con = factory.buildConversation(sender);
        sender.beginConversation(con);
    }
}
