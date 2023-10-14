package dev.lbuddyboy.bot.ticket;

import com.google.gson.JsonObject;
import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import dev.lbuddyboy.bot.ticket.packet.TicketClosePacket;
import dev.lbuddyboy.bot.utils.TimeUtils;
import dev.lbuddyboy.bot.utils.gson.GSONUtils;
import dev.lbuddyboy.bot.utils.html.HTMLFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
public class Ticket implements Cloneable {

    private UUID id;
    private long sender, channelId, timeCreated;
    private String type;
    private List<TicketMessage> history;

    public void close(TextChannel channel, Member closer) {
        HTMLFile file = new HTMLFile(channel.getName());
        TextChannel ticketChannel = channel.getGuild().getTextChannelById(this.channelId);

        if (ticketChannel != null) {
            ticketChannel.getHistoryFromBeginning(100).queue(history -> {
                int i = 0;
                for (Message message : history.getRetrievedHistory()) {
                    file.addMessage(message, i++);
                }
            });
        }

        Bot.getInstance().getTicketHandler().removeTicket(sender);

        Bot.getInstance().getJda().openPrivateChannelById("" + this.sender).queue((privateChannel) -> {
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(channel.getName() + ".html")), StandardCharsets.UTF_8))) {
                StringBuilder string = new StringBuilder();
                for (String content : file.getContents()) {
                    string.append(content).append("\n");
                }
                writer.write(string.toString());

                FileUpload upload = FileUpload.fromData(new File(channel.getName() + ".html"));
                TextChannel logs = BotConfiguration.LOGS_CHANNEL.getChannelById(TextChannel.class);

                if (logs == null) {
                    System.out.println("[!] Logs channel could not be found.");
                    return;
                }

                logs.sendMessage("Ticket Closed! " + UserSnowflake.fromId(sender).getAsMention()).queue();
                logs.sendFiles(upload).queue(message-> {
                    privateChannel.sendMessageEmbeds(MessageConfiguration.TICKET_CLOSED_CONFIGURATION.getEmbed().format(privateChannel.getUser(),
                                    "%open-time%", TimeUtils.formatIntoMMSS((int) ((System.currentTimeMillis() - timeCreated) / 1000)),
                                    "%closed-by%", closer.getEffectiveName(),
                                    "%type%", type).build())
                            .setActionRow(Button.link(message.getAttachments().get(0).getUrl(), "Download Transcript"))
                            .queue(m -> {
                            }, mex -> {
                                if (mex != null) mex.printStackTrace();
                            });
                });

                Files.deleteIfExists(Paths.get(channel.getName() + ".html"));
                channel.delete().queueAfter(3, TimeUnit.SECONDS, m -> {

                }, mex -> {
                    if (mex != null) mex.printStackTrace();
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, pex -> {
            if (pex != null) pex.printStackTrace();

        });

        if (!Bot.getInstance().getSyncHandler().isSynced(this.sender)) return;
        new TicketClosePacket(this, Bot.getInstance().getSyncHandler().getSyncInformation(this.sender), false).send(Bot.getInstance().getRedisHandler().getPidginHandler());
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("id", this.id.toString());
        object.addProperty("sender", this.sender);
        object.addProperty("channelId", this.channelId);
        object.addProperty("timeCreated", this.timeCreated);
        object.addProperty("type", this.type);
        object.addProperty("history", GSONUtils.getGSON().toJson(this.history, GSONUtils.TICKET_MESSAGES));

        return object;
    }

    public static Ticket deserialize(JsonObject object) {
        return new Ticket(UUID.fromString(object.get("id").getAsString()), object.get("sender").getAsLong(), object.get("channelId").getAsLong(), object.get("timeCreated").getAsLong(), object.get("type").getAsString(), GSONUtils.getGSON().fromJson(object.get("history").getAsString(), GSONUtils.TICKET_MESSAGES));
    }

    @Override
    public Ticket clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (Ticket) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class TicketMessage {

        private String message, discordName;
        private long sender, timeSent;

    }

}
