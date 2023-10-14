package dev.lbuddyboy.bot.config.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.utils.CC;
import lombok.AllArgsConstructor;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.Category;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum BotConfiguration {

    BOT_TOKEN("bot-settings.token", "insert-here"),
    BOT_GUILD("bot-settings.guild", 12030203),
    BOT_ACTIVITY_TYPE("bot-settings.activity.type", "PLAYING"),
    BOT_ACTIVITY_VALUE("bot-settings.activity.value", "dev.lbuddyboy"),
    WELCOME_CHANNEL("bot-configuration.channel-ids.welcome-channel", -1),
    TICKET_CHANNEL("bot-configuration.channel-ids.ticket-channel", -1),
    TICKET_CATEGORY("bot-configuration.channel-ids.ticket-category", -1),
    LOGS_CHANNEL("bot-configuration.channel-ids.logs-channel", -1),
    JOIN_ROLE_ID("bot-configuration.join-role-id", -1),
    ILLEGAL_WORDS("bot-configuration.illegal-words", Arrays.asList(
            "beaner",
            "bean",
            "nigger",
            "nigga"
    )),
    BOT_PREFIX("bot-settings.prefix", "-");

    private final String path;
    private final Object value;

    public <T extends Channel> T getChannelById(@Nonnull Class<T> type) {
        return Bot.getInstance().getGuild().getChannelById(type, getLong());
    }

    public Category getCategoryById() {
        return Bot.getInstance().getGuild().getCategoryById(getLong());
    }

    public String getString(Object... objects) {
        if (Bot.getInstance().getConfig().contains(this.path))
            return CC.format(Bot.getInstance().getConfig().getString(this.path), objects);

        loadDefault();

        return CC.format(String.valueOf(this.value), objects);
    }

    public boolean getBoolean() {
        if (Bot.getInstance().getConfig().contains(this.path))
            return Bot.getInstance().getConfig().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public int getInt() {
        if (Bot.getInstance().getConfig().contains(this.path))
            return Bot.getInstance().getConfig().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public long getLong() {
        if (Bot.getInstance().getConfig().contains(this.path))
            return Bot.getInstance().getConfig().getLong(this.path);

        loadDefault();

        return Long.parseLong(String.valueOf(this.value));
    }

    public List<String> getStringList(Object... objects) {
        if (Bot.getInstance().getConfig().contains(this.path))
            return CC.format(Bot.getInstance().getConfig().getStringList(this.path), objects);

        loadDefault();

        return CC.format((List<String>) this.value, objects);
    }

    public List<String> getStringList() {
        if (Bot.getInstance().getConfig().contains(this.path))
            return Bot.getInstance().getConfig().getStringList(this.path);

        loadDefault();

        return (List<String>) this.value;
    }

    public void update(Object value) {
        Bot.getInstance().getConfig().set(this.path, value);
        Bot.getInstance().getConfig().save();
    }

    public void loadDefault() {
        if (Bot.getInstance().getConfig().contains(this.path)) return;

        Bot.getInstance().getConfig().set(this.path, this.value);
        Bot.getInstance().getConfig().save();
    }

    public static Object[] MEMBER_PLACEHOLDERS(Member member) {
        return new Object[]{
                "%member-effective%", member.getEffectiveName(),
                "%member-nickname%", member.getNickname(),
                "%member-mentioned%", member.getAsMention(),
                "%member-tag%", member.getUser().getAsTag()
        };
    }

}
