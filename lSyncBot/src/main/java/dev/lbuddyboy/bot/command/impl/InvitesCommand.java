package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import dev.lbuddyboy.bot.invite.DiscordInvite;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class InvitesCommand extends Command {

	public InvitesCommand() {
		super(
				"invites",
				Collections.emptyList(),
				Collections.singletonList(
						new OptionData(OptionType.MENTIONABLE, "member", "Invites of the mentioned member.", true, false)
				),
				DefaultMemberPermissions.enabledFor(Permission.MESSAGE_SEND)
				, "Displays a users amount of invites.",
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

		SlashCommandInteraction interaction = event.getInteraction();
		OptionMapping mentioned = interaction.getOption("member");

		if (mentioned == null) {
			event.getHook().sendMessage("Mentioned error.").setEphemeral(true).queue();
			return;
		}

		IMentionable mentionable = mentioned.getAsMentionable();

		if (mentionable.getAsMention().isEmpty()) {
			event.getHook().sendMessage("You need to mention a member.").setEphemeral(true).queue();
			return;
		}

		int joins = 0;
		int fakes = 0;
		int left = 0;
		for (DiscordInvite invite : Bot.getInstance().getInviteHandler().getInvites().values()) {
			if (invite.getInviter() == null || invite.getInviter().getIdLong() != mentionable.getIdLong()) continue;

			joins += invite.getJoined();
			fakes += invite.getBots();
			left += invite.getLeaves();
		}

		event.deferReply().queue();
		event.getHook().sendMessageEmbeds(MessageConfiguration.INVITES_CONFIGURATION.getEmbed().format(
				(Member) mentionable,
				"%invites-joined%", joins,
				"%invites-left%", left,
				"%invites-total%", (joins - fakes - left),
				"%invites-fakes%", fakes
		).build()).queue();
	}

}
