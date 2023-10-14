package dev.lbuddyboy.bot;

import dev.lbuddyboy.bot.command.CommandEvent;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.invite.listener.InviteEvent;
import dev.lbuddyboy.bot.listener.ChatEvent;
import dev.lbuddyboy.bot.listener.WelcomeEvent;
import dev.lbuddyboy.bot.sync.listener.SyncEvent;
import dev.lbuddyboy.bot.ticket.listener.TicketCloseEvent;
import dev.lbuddyboy.bot.ticket.listener.TicketEvent;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class BotHandler implements IHandler {

    @Override
    public void load(Bot instance) {
        instance.setBuilder(JDABuilder.createDefault(instance.getToken()));
        instance.getBuilder().disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        instance.getBuilder().setBulkDeleteSplittingEnabled(false);
        instance.getBuilder().setCompression(Compression.NONE);
        instance.getBuilder().addEventListeners(
                new CommandEvent(),
                new SyncEvent(),
                new WelcomeEvent(),
                new ChatEvent(),
                new TicketEvent(),
                new TicketCloseEvent(),
                new InviteEvent());
        instance.getBuilder().setActivity(Activity.of(Activity.ActivityType.valueOf(BotConfiguration.BOT_ACTIVITY_TYPE.getString()), BotConfiguration.BOT_ACTIVITY_VALUE.getString()));
        instance.getBuilder().enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES);
        instance.setJda(instance.getBuilder().build());
        instance.setGuild(instance.getJda().getGuildById(BotConfiguration.BOT_GUILD.getLong()));
    }

    @Override
    public void unload(Bot instance) {

    }
}
