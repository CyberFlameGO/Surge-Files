package dev.lbuddyboy.bot.invite;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.IHandler;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoUnit.DAYS;

@Getter
public class InviteHandler implements IHandler {

    private static final long FAKE_INVITE_DAYS_OFFSET = 1;

    private Map<String, DiscordInvite> invites;
    private final Map<Long, DiscordInvite> joins;

    public InviteHandler() {
        this.invites = new ConcurrentHashMap<>();
        this.joins = new ConcurrentHashMap<>();
    }

    @Override
    public void load(Bot instance) {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                updateGuildInvites();
            }
        }, 2000);
    }

    @Override
    public void unload(Bot plugin) {
        for (Map.Entry<String, DiscordInvite> entry : this.invites.entrySet()) {
            insertInvite(entry.getValue());
        }
        for (Map.Entry<Long, DiscordInvite> entry : this.joins.entrySet()) {
            insertJoin(entry.getKey(), entry.getValue());
        }
        this.invites.clear();
        this.joins.clear();
    }

    @Override
    public void save() {
        for (Map.Entry<String, DiscordInvite> entry : this.invites.entrySet()) {
            insertInvite(entry.getValue());
        }
        for (Map.Entry<Long, DiscordInvite> entry : this.joins.entrySet()) {
            insertJoin(entry.getKey(), entry.getValue());
        }
    }

    public void updateGuildInvites() {
        Guild guild = Bot.getInstance().getGuild();
        this.invites = new ConcurrentHashMap<>();

        guild.retrieveInvites().queue(invites -> {
            for (Invite invite : invites) {
                DiscordInvite discordInvite = new DiscordInvite(invite);

                this.invites.put(invite.getCode(), discordInvite);
            }
        });
    }

    public void handleJoin(User user) {
        Map<String, DiscordInvite> oldCache = new ConcurrentHashMap<>();
        oldCache.putAll(this.invites);
        this.updateGuildInvites();
        DiscordInvite inviteUsed = null;

        for (Map.Entry<String, DiscordInvite> entry : this.invites.entrySet()) {
            String code = entry.getKey();
            DiscordInvite invite = entry.getValue();

            if (!oldCache.containsKey(code)) {
                if (invite.getJoined() == 1) {
                    inviteUsed = invite;
                    break;
                }
            } else {
                if (invite.getJoined() > oldCache.get(code).getJoined()) {
                    inviteUsed = invite;
                    break;
                }
            }
        }

        if (inviteUsed == null) {
            System.out.println("Failed to track invite");
            return;
        }

        if (inviteUsed.getInviter() != null) {
            User inviter = inviteUsed.getInviter();

            // check possible fake invite
            if (isFakeInvite(user)) {
                increaseDiscordInvite(inviteUsed, InviteJoinType.FAKE, 1);
            } else {
                increaseDiscordInvite(inviteUsed, InviteJoinType.TRACKED, 1);
            }

            this.updateJoin(user.getIdLong(), inviteUsed);

            TextChannel channel = BotConfiguration.LOGS_CHANNEL.getChannelById(TextChannel.class);

            if (channel == null) {
                System.out.println("[!] Logs channel could not be found.");
                return;
            }

            channel.sendMessage("New Join! " + user.getAsMention() + " \nInvite Code: " + inviteUsed.getCode() + "\nSender: " + inviter.getAsMention() + "\n Uses: " + inviteUsed.getJoined()).queue();
        } else {
            System.out.println("No user found for invite " + inviteUsed.getCode());
        }
    }

    public void handleLeave(User user) {
        DiscordInvite invite = this.joins.get(user.getIdLong());
        TextChannel channel = BotConfiguration.LOGS_CHANNEL.getChannelById(TextChannel.class);

        if (invite == null) {
            System.out.println("Error ");
            return;
        }

        increaseDiscordInvite(invite, InviteJoinType.LEAVES, 1);
        insertInvite(invite);
        this.joins.remove(user.getIdLong());

        if (channel == null) {
            System.out.println("[!] Logs channel could not be found.");
            return;
        }

        channel.sendMessage("Member Left! " + user.getAsMention() + " \nInvite Code: " + invite.getCode() + "\nSender: " + invite.getInviter().getAsMention() + "\n Uses: " + invite.getJoined()).queue();

    }

    public void updateJoin(long id, DiscordInvite invite) {
        this.joins.put(id, invite);
    }

    public int increaseDiscordInvite(DiscordInvite invite, InviteJoinType type, int amount) {
        int i = 0;
        if (type == InviteJoinType.LEAVES) {
            invite.setLeaves(invite.getLeaves() + amount);
            i = invite.getLeaves();
        } else if (type == InviteJoinType.TRACKED) {
            invite.setJoined(invite.getJoined() + amount);
            i = invite.getJoined();
        } else if (type == InviteJoinType.FAKE) {
            invite.setBots(invite.getBots() + amount);
            i = invite.getBots();
        }

        insertInvite(invite);
        return i;
    }

    public void insertInvite(DiscordInvite invite) {
        Bson bson = Filters.eq("code", invite.getCode());
        Document document = Bot.getInstance().getSyncHandler().getInvitesCollection().find(bson).first();
        boolean insert = false;

        if (document == null) {
            insert = true;
            document = new Document();
        }

        document.put("code", invite.getCode());
        document.put("joins", invite.getJoined());
        document.put("leaves", invite.getLeaves());
        document.put("bots", invite.getBots());
        document.put("createdAt", invite.getCreatedAt());

        if (insert) {
            Bot.getInstance().getSyncHandler().getInvitesCollection().insertOne(document);
            return;
        }

        Bot.getInstance().getSyncHandler().getInvitesCollection().replaceOne(bson, document, new ReplaceOptions().upsert(true));
    }

    public int loadInviteBots(Invite invite) {
        Bson bson = Filters.eq("code", invite.getCode());
        Document document = Bot.getInstance().getSyncHandler().getInvitesCollection().find(bson).first();
        boolean insert = false;

        if (document == null) return 0;

        return document.getInteger("bots");
    }

    public int loadInviteLeaves(Invite invite) {
        Bson bson = Filters.eq("code", invite.getCode());
        Document document = Bot.getInstance().getSyncHandler().getInvitesCollection().find(bson).first();
        boolean insert = false;

        if (document == null) return 0;

        return document.getInteger("leaves");
    }

    public void deleteInvite(DiscordInvite invite) {
        Bson bson = Filters.eq("code", invite.getCode());

        Bot.getInstance().getSyncHandler().getInvitesCollection().deleteOne(bson);
    }

    public void insertJoin(long id, DiscordInvite invite) {
        Bson bson = Filters.eq("id", id);
        Document document = Bot.getInstance().getSyncHandler().getInvitesCollection().find(bson).first();
        boolean insert = false;

        if (document == null) {
            insert = true;
            document = new Document();
        }

        document.put("id", id);
        document.put("code", invite.getCode());
        document.put("joins", invite.getJoined());
        document.put("leaves", invite.getLeaves());
        document.put("bots", invite.getBots());
        document.put("createdAt", invite.getCreatedAt());

        if (insert) {
            Bot.getInstance().getSyncHandler().getInvitesCollection().insertOne(document);
            return;
        }

        Bot.getInstance().getSyncHandler().getInvitesCollection().replaceOne(bson, document, new ReplaceOptions().upsert(true));
    }

    private boolean isFakeInvite(User user) {
        final String avatar = user.getAvatarUrl();
        final OffsetDateTime timeCreated = user.getTimeCreated();
        final long days = DAYS.between(timeCreated, OffsetDateTime.now());
        return days > FAKE_INVITE_DAYS_OFFSET && avatar == null;
    }

}
