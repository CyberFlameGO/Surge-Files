package dev.lbuddyboy.bot.config.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.config.Config;
import dev.lbuddyboy.bot.utils.CC;
import dev.lbuddyboy.bot.utils.ConfiguredEmbed;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.simpleyaml.configuration.ConfigurationSection;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum MessageConfiguration {

    WELCOME_CONFIGURATION("welcome-message", new ConfiguredEmbed(
            true,
            "", // doesn't need a message if it's embed
            new EmbedBuilder()
                    .setAuthor("Flash Bot")
                    .setColor(Color.RED)
                    .setTitle("Welcome, %member-effective%!")
                    .setFooter("**New Join, %member-effective%!**")
                    .setDescription(""
                            + "Welcome, %member-tag% to the Flash Discord Server!\n"
                            + " \n"
                            + " **Useful Links** \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Discord: https://discord.lbuddyboy.dev \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Teamspeak: ts.lbuddyboy.dev \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Website: https://lbuddyboy.dev \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** MC Server: play.lbuddyboy.dev \n"
                            + " \n"
                            + " Check out #announcements to view important news! \n"
                    )
    )),
    INVITES_CONFIGURATION("invites-message", new ConfiguredEmbed(
            true,
            "", // doesn't need a message if it's embed
            new EmbedBuilder()
                    .setAuthor("Flash Bot | Invitation Handler")
                    .setColor(Color.RED)
                    .setTitle("**%member-mentioned%'s Invite Information**")
                    .setFooter("%member-effective%'s Invites")
                    .setDescription(""
                            + " **Invite Summary** \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Joins: %invites-joined% \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Leaves: %invites-left% \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Total: %invites-total% \n"
                    )
    )),
    TICKET_CONFIGURATION("ticket-message", new ConfiguredEmbed(
            true,
            "", // doesn't need a message if it's embed
            new EmbedBuilder()
                    .setAuthor("Flash Bot | Ticket Handler")
                    .setColor(Color.RED)
                    .setTitle("Create a Ticket")
                    .setFooter("Ticket Creation")
                    .setDescription(""
                            + " **Ticket Information** \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Create a ticket and be \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** sure to be specific with \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** your evidence. \n"
                    )
    )),
    TICKET_OPEN_CONFIGURATION("ticket-open-message", new ConfiguredEmbed(
            true,
            "", // doesn't need a message if it's embed
            new EmbedBuilder()
                    .setAuthor("Flash Bot | Ticket Handler")
                    .setColor(Color.RED)
                    .setTitle("**New Ticket**")
                    .setFooter("New Ticket Created")
                    .setDescription(""
                            + " **Ticket Information** \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** 1) Provide your IGN \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** 2) Provide proof (if needed) \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** 3) Be specific! \n"
                    )
    )),
    TICKET_CLOSED_CONFIGURATION("ticket-closed-message", new ConfiguredEmbed(
            true,
            "", // doesn't need a message if it's embed
            new EmbedBuilder()
                    .setAuthor("Flash Bot | Ticket Handler")
                    .setColor(Color.RED)
                    .setTitle("**Ticket CLOSED**")
                    .setFooter("Ticket Closed")
                    .setDescription(""
                            + " **Ticket Closed Information** \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Closed by: %closed-by% \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Open Time: %open-time% \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Ticket Type: %type% \n"
                            + " **" + CC.UNICODE_ARROW_RIGHT + "** Transcript: %transcript% \n"
                    )
    ));

    private final String configName;
    private final ConfiguredEmbed embed;

    public ConfiguredEmbed getEmbed() {
        ConfigurationSection file = Bot.getInstance().getConfig().getConfigurationSection("messages." + this.configName);
        final String[] description = new String[]{""};
        EmbedBuilder builder = new EmbedBuilder();

        if (file.contains("embed.description")) {
            file.getStringList("embed.description").forEach(s -> {
                description[0] += s + "\n";
            });
        }

        if (file.contains("embed.title")) {
            builder.setTitle(file.getString("embed.title"));
        }

        if (!file.contains("embed.author.icon-url") || file.getString("embed.author.icon-url").isEmpty()) {
            builder.setAuthor(file.getString("embed.author.name"));
        } else {
            builder.setAuthor(file.getString("embed.author.name"), file.getString("embed.author.icon-url", ""));
        }

        if (!file.contains("embed.footer.icon-url") || file.getString("embed.footer.icon-url").isEmpty()) {
            builder.setFooter(file.getString("embed.footer.text"));
        } else {
            builder.setFooter(file.getString("embed.footer.text"), file.getString("embed.footer.icon-url", ""));
        }

        if (file.contains("embed.image") && !file.getString("embed.image").isEmpty()) {
            builder.setImage(file.getString("embed.image"));
        }

        if (file.contains("embed.thumbnail") && !file.getString("embed.thumbnail").isEmpty()) {
            builder.setThumbnail(file.getString("embed.thumbnail"));
        }

        return new ConfiguredEmbed(
                file.getBoolean("embed"),
                file.getString("message"),
                builder
                        .setColor(new Color(file.getInt("embed.color.red"), file.getInt("embed.color.green"), file.getInt("embed.color.blue")))
                        .setDescription(description[0])
        );
    }

    public void loadDefault() {
        Config file = Bot.getInstance().getConfig();

        if (!file.contains("messages." + this.configName + ".embed")) file.set("messages." + this.configName + ".embed", this.embed.isEmbed());
        if (!file.contains("messages." + this.configName + ".message")) file.set("messages." + this.configName + ".message", this.embed.getMessage());
        if (this.embed.isEmbed()) {
            MessageEmbed embed = this.embed.getBuilder().build();
            if (!file.contains("messages." + this.configName + ".embed.title")) file.set("messages." + this.configName + ".embed.title", embed.getTitle());
            if (embed.getAuthor() != null) {
                if (!file.contains("messages." + this.configName + ".embed.author.name")) file.set("messages." + this.configName + ".embed.author.name", embed.getAuthor().getName());
                if (!file.contains("messages." + this.configName + ".embed.author.icon-url"))
                    file.set("messages." + this.configName + ".embed.author.icon-url", embed.getAuthor().getIconUrl());
            }
            if (embed.getDescription() != null) {
                if (!file.contains("messages." + this.configName + ".embed.description"))
                    file.set("messages." + this.configName + ".embed.description", Arrays.asList(embed.getDescription().split("\n")));
            }
            if (embed.getFooter() != null) {
                if (!file.contains("messages." + this.configName + ".embed.footer.text")) file.set("messages." + this.configName + ".embed.footer.text", embed.getFooter().getText());
                if (!file.contains("messages." + this.configName + ".embed.footer.icon-url"))
                    file.set("messages." + this.configName + ".embed.footer.icon-url", embed.getFooter().getIconUrl());
            }
            if (embed.getImage() != null) if (!file.contains("messages." + this.configName + ".embed.image")) file.set("messages." + this.configName + ".embed.image", embed.getImage());
            if (embed.getThumbnail() != null)
                if (!file.contains("messages." + this.configName + ".embed.thumbnail")) file.set("messages." + this.configName + ".embed.thumbnail", embed.getThumbnail());
            if (embed.getColor() != null) {
                if (!file.contains("messages." + this.configName + ".embed.color.red")) file.set("messages." + this.configName + ".embed.color.red", embed.getColor().getRed());
                if (!file.contains("messages." + this.configName + ".embed.color.blue")) file.set("messages." + this.configName + ".embed.color.blue", embed.getColor().getBlue());
                if (!file.contains("messages." + this.configName + ".embed.color.green")) file.set("messages." + this.configName + ".embed.color.green", embed.getColor().getGreen());
            }
        }

        Bot.getInstance().getConfig().save();
    }

    public static Object[] MEMBER_PLACEHOLDERS(Member member) {
        List<Object> objects = new ArrayList<>(Arrays.asList(
                "%member-effective%", member.getEffectiveName(),
                "%member-avatar%", member.getAvatarUrl(),
                "%member-nickname%", member.getNickname(),
                "%member-mentioned%", member.getAsMention(),
                "%member-tag%", member.getUser().getAsTag()));

        for (GuildChannel channel : Bot.getInstance().getGuild().getChannels()) {
            objects.add("%channel-" + channel.getIdLong() + "-mentioned%");
            objects.add(channel.getAsMention());
            objects.add("%channel-" + channel.getIdLong() + "-jumpurl%");
            objects.add(channel.getJumpUrl());
        }

        return objects.toArray();
    }

    public static Object[] USER_PLACEHOLDERS(User member) {
        List<Object> objects = new ArrayList<>(Arrays.asList(
                "%member-effective%", member.getName(),
                "%member-avatar%", member.getAvatarUrl(),
                "%member-mentioned%", member.getAsMention(),
                "%member-tag%", member.getAsTag()));

        for (GuildChannel channel : Bot.getInstance().getGuild().getChannels()) {
            objects.add("%channel-" + channel.getIdLong() + "-mentioned%");
            objects.add(channel.getAsMention());
            objects.add("%channel-" + channel.getIdLong() + "-jumpurl%");
            objects.add(channel.getJumpUrl());
        }

        return objects.toArray();
    }

}
