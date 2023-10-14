package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.command.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collections;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class InviteSyncCommand extends Command {

	public InviteSyncCommand() {
		super(
				"invitesync",
				Collections.emptyList(),
				Collections.emptyList(),
				DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)
				, "Syncs the bot invites with the guild invites.",
				""
		);
	}

	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (!(event.getName().equals("invites"))) return;

		Member member = event.getMember();
		Guild guild = event.getGuild();

		if (guild == null) {
			event.getHook().sendMessage("Invalid guild.").setEphemeral(true).queue();
			return;
		}


		Bot.getInstance().getInviteHandler().updateGuildInvites();
		event.reply("All invites joins have been updated!").setEphemeral(true).queue();
	}

}
