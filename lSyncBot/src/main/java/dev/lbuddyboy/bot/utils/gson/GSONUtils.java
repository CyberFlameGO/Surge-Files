package dev.lbuddyboy.bot.utils.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.lbuddyboy.bot.invite.DiscordInvite;
import dev.lbuddyboy.bot.sync.SyncCode;
import dev.lbuddyboy.bot.sync.cache.SyncInformation;
import dev.lbuddyboy.bot.ticket.Ticket;
import lombok.Getter;
import net.dv8tion.jda.internal.entities.GuildImpl;
import net.dv8tion.jda.internal.entities.MemberImpl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class GSONUtils {

    @Getter public static Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().enableComplexMapKeySerialization().create();

    public static final Type SYNC_CODE = new TypeToken<SyncCode>() {}.getType();
    public static final Type SYNC_CODES = new TypeToken<List<SyncCode>>() {}.getType();
    public static final Type SYNC_INFORMATION = new TypeToken<SyncInformation>() {}.getType();
    public static final Type DISCORD_INVITES = new TypeToken<List<DiscordInvite>>() {}.getType();
    public static final Type TICKETS = new TypeToken<List<Ticket>>() {}.getType();
    public static final Type TICKET = new TypeToken<Ticket>() {}.getType();
    public static final Type DISCORD_INVITE = new TypeToken<DiscordInvite>() {}.getType();
    public static final Type TICKET_MESSAGE = new TypeToken<Ticket.TicketMessage>() {}.getType();
    public static final Type TICKET_MESSAGES = new TypeToken<List<Ticket.TicketMessage>>() {}.getType();
    public static final Type MEMBER = new TypeToken<MemberImpl>() {}.getType();
    public static final Type GUILD = new TypeToken<GuildImpl>() {}.getType();

    public static final Type STRING = new TypeToken<List<String>>() {}.getType();
    public static final Type LONGS = new TypeToken<List<Long>>() {}.getType();
    public static final Type UUID = new TypeToken<List<UUID>>() {}.getType();

}
