package dev.lbuddyboy.bot.listener;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class WelcomeEvent extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        Member member = event.getMember();
        Guild guild = event.getGuild();
        Role role = guild.getRoleById(BotConfiguration.JOIN_ROLE_ID.getLong());

        if (role != null) {
            guild.addRoleToMember(UserSnowflake.fromId(member.getId()), role).queue();
        }

        Bot.getInstance().getInviteHandler().handleJoin(event.getUser());

        TextChannel channel = BotConfiguration.WELCOME_CHANNEL.getChannelById(TextChannel.class);

        if (channel == null) {
            System.out.println("[!] Welcome channel could not be found.");
            return;
        }

        channel.sendMessageEmbeds(MessageConfiguration.WELCOME_CONFIGURATION.getEmbed().format(member).build()).queue();

    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        Bot.getInstance().getInviteHandler().handleLeave(event.getUser());
    }

}
