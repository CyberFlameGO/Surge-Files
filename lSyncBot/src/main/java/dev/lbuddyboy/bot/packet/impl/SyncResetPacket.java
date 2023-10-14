package dev.lbuddyboy.bot.packet.impl;

import com.google.gson.JsonObject;
import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.redis.RedisHandler;
import dev.lbuddyboy.bot.sync.cache.SyncInformation;
import dev.lbuddyboy.bot.utils.gson.GSONUtils;
import dev.lbuddyboy.bot.utils.pidgin.PidginHandler;
import dev.lbuddyboy.bot.utils.pidgin.packet.Packet;
import dev.lbuddyboy.bot.utils.pidgin.packet.handler.IncomingPacketHandler;
import dev.lbuddyboy.bot.utils.pidgin.packet.listener.PacketListener;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;

import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class SyncResetPacket implements Packet, PacketListener {

    private SyncInformation information;
    private boolean discord;
    private List<String> roles;
    private JsonObject object = new JsonObject();

    public SyncResetPacket(SyncInformation information, boolean discord, List<String> roles) {
        this.information = information;
        this.discord = discord;
        this.roles = roles;
    }

    @Override
    public int id() {
        return 3000;
    }

    @Override
    public JsonObject serialize() {

        this.object.addProperty("discord", this.discord);
        this.object.addProperty("information", GSONUtils.getGSON().toJson(this.information, GSONUtils.SYNC_INFORMATION));
        this.object.addProperty("roles", GSONUtils.getGSON().toJson(this.roles, GSONUtils.STRING));

        return this.object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.object = object;
    }

    public SyncInformation information() {
        return GSONUtils.getGSON().fromJson(this.object.get("information").getAsString(), GSONUtils.SYNC_INFORMATION);
    }

    public List<String> roles() {
        return GSONUtils.getGSON().fromJson(this.object.get("roles").getAsString(), GSONUtils.STRING);
    }

    public boolean discord() {
        return this.object.get("discord").getAsBoolean();
    }

    @Override
    public void send(PidginHandler pidgin) {
        if (!RedisHandler.isEnabled()) {
            serialize();
            onReceive(this);
            return;
        }

        Packet.super.send(pidgin);
    }

    @IncomingPacketHandler
    public void onReceive(SyncResetPacket packet) {
        if (!packet.discord()) return;

        SyncInformation information = packet.information();
        Map<String, Long> ingame = information.getInGameRanks();
        UserSnowflake userSnowflake = UserSnowflake.fromId(information.getDiscordId());

        for (Guild guild : Bot.getInstance().getJda().getGuilds()) {
            for (Map.Entry<String, Long> entry : ingame.entrySet()) {
                Role role = guild.getRoleById(entry.getValue());

                if (role == null) {
                    System.out.println("[SYNC ERROR] Tried unsyncing minecraft name " + information.getPlayerName() + " with the discord account " + userSnowflake.getAsMention() + ".");
                    System.out.println("[SYNC ERROR] , but the role in the conversion is not valid.");
                    continue;
                }

                guild.removeRoleFromMember(userSnowflake, role).queue();
            }

            Bot.getInstance().getSyncHandler().removeSynced(guild, userSnowflake);
        }

        User user = Bot.getInstance().getJda().getUserById(userSnowflake.getIdLong());
        if (user != null) {
            user.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("You have successfully unsynced your discord account with the " + information.getPlayerName() + " Minecraft Account.").queue();
            });
        }

    }

}
