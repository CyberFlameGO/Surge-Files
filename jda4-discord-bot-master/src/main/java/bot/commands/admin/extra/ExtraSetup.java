package bot.commands.admin.extra;

import bot.command.CommandCategory;
import bot.command.CommandContext;
import bot.command.ICommand;
import bot.database.DataSource;
import bot.database.objects.ExtraSettings;
import bot.database.objects.Ticket;
import bot.utils.BotUtils;
import bot.utils.TicketUtils;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ExtraSetup extends ICommand {

    public ExtraSetup(EventWaiter waiter) {
        this.name = "extras";
        this.usage =
                "`{p}{i} suggestionchannel` : set the suggestion channel\n" +
                "`{p}{i} rankrenewalchannel` : set the rank renewal channel\n";
        this.help = "setup suggestions & rank renewals in your discord server";
        this.multilineHelp = true;
        this.minArgsCount = 1;
        this.userPermissions = new Permission[]{};
        this.category = CommandCategory.ADMINISTRATION;
    }

    @Override
    public void handle(@NotNull CommandContext ctx) {
        final String toDo = ctx.getArgs().get(0);

        if (ctx.getMember().hasPermission(Permission.MANAGE_SERVER)) {
            switch (toDo.toLowerCase()) {

                case "suggestionchannel":
                    setSuggestionChannel(ctx);
                    break;

                case "rankrenewalchannel":
                    setRankRenewal(ctx);
                    break;

                case "info":
                    sendInfo(ctx);
                    break;

                default:
                    ctx.reply("Invalid input");
                    break;
            }
        }
    }

    private void sendInfo(CommandContext ctx) {
        ExtraSettings config = DataSource.INS.getExtraConfig(ctx.getGuildId());

        if (config == null) {
            DataSource.INS.addExtraConfig(ctx.getGuildId(), "", "");
            config = DataSource.INS.getExtraConfig(ctx.getGuildId());
        }

        ctx.reply("Suggestions: " + config.suggestionChannel);
        ctx.reply("Renewals: " + config.rankRenewalChannel);

    }

    private void setSuggestionChannel(CommandContext ctx) {
        final List<TextChannel> menChannels = ctx.getMessage().getMentionedChannels();

        if (menChannels.isEmpty()) {
            ctx.reply("Please mention a channel name where ticket logs must be sent");
            return;
        }

        TextChannel targetChannel = menChannels.get(0);
        ExtraSettings config = DataSource.INS.getExtraConfig(ctx.getGuildId());

        if (config == null) {
            DataSource.INS.addExtraConfig(ctx.getGuildId(), "", "");
            config = DataSource.INS.getExtraConfig(ctx.getGuildId());
        }

        config.suggestionChannel = targetChannel.getId();

        DataSource.INS.updateExtraConfig(ctx.getGuildId(), config.suggestionChannel, config.rankRenewalChannel);
        ctx.reply("Configuration saved! New suggestions will be sent to " + targetChannel.getAsMention());

    }

    private void setRankRenewal(CommandContext ctx) {
        final List<TextChannel> menChannels = ctx.getMessage().getMentionedChannels();

        if (menChannels.isEmpty()) {
            ctx.reply("Please mention a channel name where ticket logs must be sent");
            return;
        }

        TextChannel targetChannel = menChannels.get(0);
        ExtraSettings config = DataSource.INS.getExtraConfig(ctx.getGuildId());

        if (config == null) {
            DataSource.INS.addExtraConfig(ctx.getGuildId(), "", "");
            config = DataSource.INS.getExtraConfig(ctx.getGuildId());
        }

        config.rankRenewalChannel = targetChannel.getId();

        DataSource.INS.updateExtraConfig(ctx.getGuildId(), config.suggestionChannel, config.rankRenewalChannel);
        ctx.reply("Configuration saved! New rank renewals will be sent to " + targetChannel.getAsMention());

    }

}
