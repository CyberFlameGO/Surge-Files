package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.packet.impl.SyncResetPacket;
import dev.lbuddyboy.bot.sync.cache.SyncInformation;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class SyncResetCommand extends Command {

    public SyncResetCommand() {
        super("syncreset",
                Collections.emptyList(),
                Collections.singletonList(
                        new OptionData(OptionType.USER, "member", "Member that you want to reset", true, false)
                ), DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND), "Unsync your discord account with your minecraft account", "");
    }

    @Override
    public void send(SlashCommandInteractionEvent event) {
        if (event.getName().equals("syncreset")) {

            User user = event.getOption("member").getAsUser();
            Guild guild = event.getGuild();

            if (guild == null) {
                event.reply("Guild not found.").setEphemeral(true).queue();
                return;
            }

            Member member = guild.getMember(user);

            if (member == null) {
                event.reply("Member not found.").setEphemeral(true).queue();
                return;
            }

            if (!Bot.getInstance().getSyncHandler().isSynced(user.getIdLong())) {
                event.reply("Your account is not synced.").setEphemeral(true).queue();
                return;
            }

            SyncInformation information = Bot.getInstance().getSyncHandler().getSyncInformation(user.getIdLong());

            new SyncResetPacket(information, false, member.getRoles().stream().map(Role::getId).collect(Collectors.toList())).send(Bot.getInstance().getRedisHandler().getPidginHandler());
            event.reply("Account is now being reset.").setEphemeral(true).queue();
        }
    }

}
