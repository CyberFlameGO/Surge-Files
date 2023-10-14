package dev.lbuddyboy.bot.invite.listener;

import dev.lbuddyboy.bot.Bot;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InviteEvent extends ListenerAdapter {

    @Override
    public void onGuildInviteCreate(@NotNull GuildInviteCreateEvent event) {
        Bot.getInstance().getInviteHandler().updateGuildInvites();
    }

    @Override
    public void onGuildInviteDelete(@NotNull GuildInviteDeleteEvent event) {
        Bot.getInstance().getInviteHandler().updateGuildInvites();

    }

}
