package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.config.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.awt.*;
import java.util.Collections;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class ReloadCommand extends Command {

	public ReloadCommand() {
		super("reload", Collections.emptyList(), Collections.emptyList(), DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR), "Reloads the config.yml.", "");
	}

	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (event.getName().equals("reload")) {

			try {
				Bot.getInstance().setConfig(new Config(Bot.getInstance(), "config"));

				EmbedBuilder embedBuilder = new EmbedBuilder();

				embedBuilder.setTitle("Sync Bot");
				embedBuilder.setDescription("The config file reloaded successfully!");
				embedBuilder.setColor(Color.GREEN);

				event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
			} catch (Exception e) {
				EmbedBuilder embedBuilder = new EmbedBuilder();

				embedBuilder.setTitle("Sync Bot");
				embedBuilder.setDescription("The config file had an error reloaded... Check the console for errors.");
				embedBuilder.setColor(Color.RED);

				event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
				e.printStackTrace();
			}
		}
	}
}
