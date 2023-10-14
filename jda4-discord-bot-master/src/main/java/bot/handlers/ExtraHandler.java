package bot.handlers;

import bot.Bot;
import bot.Config;
import bot.command.CommandCategory;
import bot.command.CommandContext;
import bot.command.ICommand;
import bot.commands.admin.*;
import bot.commands.admin.extra.ExtraSetup;
import bot.commands.admin.greeting.Farewell;
import bot.commands.admin.greeting.Welcome;
import bot.commands.admin.mod_config.MaxWarningsCommand;
import bot.commands.admin.mod_config.ModLogChannel;
import bot.commands.admin.reaction_role.AddReactionRoleCommand;
import bot.commands.admin.reaction_role.RemoveReactionRoleCommand;
import bot.commands.automod.*;
import bot.commands.economy.BalanceCommand;
import bot.commands.economy.DailyCommand;
import bot.commands.economy.GambleCommand;
import bot.commands.economy.TransferCommand;
import bot.commands.fun.*;
import bot.commands.image.filters.*;
import bot.commands.image.generators.*;
import bot.commands.image.text_generators.Achievement;
import bot.commands.image.text_generators.BeLikeBill;
import bot.commands.image.text_generators.Presentation;
import bot.commands.information.*;
import bot.commands.invites.*;
import bot.commands.moderation.*;
import bot.commands.owner.EvalCommand;
import bot.commands.owner.ShutDownCommand;
import bot.commands.owner.UsageCommand;
import bot.commands.social.ReputationCommand;
import bot.commands.utility.*;
import bot.database.DataSource;
import bot.database.objects.ExtraSettings;
import bot.database.objects.GuildSettings;
import bot.database.objects.Ticket;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.interactions.components.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ExtraHandler extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExtraHandler.class);
    private static Map<Long, Long> suggestionsYes = new HashMap<>();
    private static Map<Long, Long> suggestionsNo = new HashMap<>();

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot() || event.isWebhookMessage()) {
            return;
        }

        Guild guild = event.getGuild();
        ExtraSettings settings = DataSource.INS.getExtraConfig(guild.getId());
        Member member = event.getMember();

        if (settings == null) return;
        if (settings.suggestionChannel != null) {
            if (event.getChannel().getId().equals(settings.suggestionChannel)) {
                TextChannel channel = guild.getTextChannelById(settings.suggestionChannel);

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle(member.getEffectiveName() + "'s suggests that:");
                builder.setDescription(
                        event.getMessage().getContentRaw() +
                                " \n" +
                                "\nVotes Yes: 0" +
                                "\nVotes No: 0" +
                                "\n \n " +
                                " Click ✔ if you agree!\n" +
                                " Click ✖ if you disagree! \n "
                );
                builder.setColor(Color.ORANGE);
                builder.setAuthor("MineSurge | Suggestions");
                builder.setFooter("MineSurge | Suggestions");

                channel.sendMessageEmbeds(builder.build())
                        .setActionRow(Button.success("suggestion-yes", "✔"), Button.success("suggestion-no", "✖"))
                        .queue();

                event.getMessage().delete().queue();
            }
        }
        if (settings.rankRenewalChannel != null) {
            if (event.getChannel().getId().equals(settings.rankRenewalChannel)) {
                TextChannel channel = guild.getTextChannelById(settings.rankRenewalChannel);

                EmbedBuilder builder = new EmbedBuilder();

                builder.setTitle(member.getEffectiveName() + "'s Rank Renewal");
                builder.setDescription(
                        member.getEffectiveName() + " has requested a rank:\n" +
                                event.getMessage().getContentRaw() +
                                " \n" +
                                "\nGranted: No" +
                                "\n \n" +
                                " Click ✔ to mark it as granted! \n "
                );
                builder.setColor(Color.ORANGE);
                builder.setAuthor("MineSurge | Rank Renewals");
                builder.setFooter("MineSurge | Rank Renewals");

                channel.sendMessageEmbeds(builder.build())
                        .setActionRow(Button.success("renewal-yes", "✔"))
                        .queue();

                event.getMessage().delete().queue();
            }
        }

    }


    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        if (event.getButton().getId().equalsIgnoreCase("suggestion-yes")) {
            if (hasSuggested(event.getMessage(), event.getMember())) {
                event.reply("You have already suggested!").setEphemeral(true).queue();
                return;
            }
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);

            List<String> lines = new ArrayList<>(Arrays.asList(messageEmbed.getDescription().split("\n")));
            int yesLine = lines.size() - 5;

            int yesCount = Integer.parseInt(lines.get(yesLine).replaceAll("Votes Yes: ", "").replaceAll(" ", ""));
            lines.set(yesLine, "Votes Yes: " + (++yesCount));

            EmbedBuilder builder = new EmbedBuilder(messageEmbed);
            StringBuilder message = new StringBuilder();

            for (String line : lines) {
                message.append(line).append("\n");
            }

            builder.setDescription(message.toString());

            suggestionsYes.put(event.getMember().getIdLong(), event.getMessageIdLong());
            event.getMessage().editMessageEmbeds(builder.build()).queue();
            event.reply("You have voted yes!").setEphemeral(true).queue();
        } else if (event.getButton().getId().equalsIgnoreCase("suggestion-no")) {
            if (hasSuggested(event.getMessage(), event.getMember())) {
                event.reply("You have already suggested!").setEphemeral(true).queue();
                return;
            }
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);

            List<String> lines = new ArrayList<>(Arrays.asList(messageEmbed.getDescription().split("\n")));
            int noLine = lines.size() - 4;

            int yesCount = Integer.parseInt(lines.get(noLine).replaceAll("Votes No: ", "").replaceAll(" ", ""));
            lines.set(noLine, "Votes No: " + (++yesCount));

            EmbedBuilder builder = new EmbedBuilder(messageEmbed);
            StringBuilder message = new StringBuilder();

            for (String line : lines) {
                message.append(line).append("\n");
            }

            builder.setDescription(message.toString());

            suggestionsNo.put(event.getMember().getIdLong(), event.getMessageIdLong());
            event.getMessage().editMessageEmbeds(builder.build()).queue();
            event.reply("You have voted no!").setEphemeral(true).queue();
        } else if (event.getButton().getId().equalsIgnoreCase("renewal-yes")) {
            if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_ROLES)) {
                event.reply("Permission denied!").setEphemeral(true).queue();
                return;
            }
            MessageEmbed messageEmbed = event.getMessage().getEmbeds().get(0);

            List<String> lines = new ArrayList<>(Arrays.asList(messageEmbed.getDescription().split("\n")));
            int noLine = lines.size() - 3;

            boolean granted = lines.get(noLine).replaceAll("Granted: ", "").equalsIgnoreCase("Yes");

            if (granted) {
                event.reply("This player has already been granted their rank!").setEphemeral(true).queue();
                return;
            }

            lines.set(noLine, "Granted: Yes");

            EmbedBuilder builder = new EmbedBuilder(messageEmbed);
            StringBuilder message = new StringBuilder();

            for (String line : lines) {
                message.append(line).append("\n");
            }

            builder.setDescription(message.toString());

            event.getMessage().editMessageEmbeds(builder.build()).queue();
            event.reply("You have marked this member as granted!").setEphemeral(true).queue();
        }

    }

    public boolean hasSuggested(Message message, Member member) {
        return suggestionsYes.containsKey(member.getIdLong()) && suggestionsYes.get(member.getIdLong()) == message.getIdLong() ||
                suggestionsNo.containsKey(member.getIdLong()) && suggestionsNo.get(member.getIdLong()) == message.getIdLong();
    }


}
