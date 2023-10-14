package bot.database.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class Ticket {

    public String channelId;
    public String messageId;
    public String title;
    public String generalSupportId, kitmapSupportId, practiceSupportId, hcfSupportId, bunkersSupportId;
    public String logChannelId;
    public int ticketLimit;
    public boolean adminOnly;

    public Ticket(Document doc) {
        this.channelId = doc.getString("channel_id");
        this.messageId = doc.getString("message_id");
        this.title = doc.getString("title");
        this.generalSupportId = doc.getString("generalSupportId");
        this.kitmapSupportId = doc.getString("kitmapSupportId");
        this.practiceSupportId = doc.getString("practiceSupportId");
        this.hcfSupportId = doc.getString("hcfSupportId");
        this.bunkersSupportId = doc.getString("bunkersSupportId");
        this.logChannelId = doc.getString("log_channel");
        this.ticketLimit = doc.getInteger("ticket_limit", 1000);
        this.adminOnly = doc.get("admin_only", false);
    }

    public List<Role> getAllRoles(Guild guild) {
        List<Role> roles = new ArrayList<>();

        if (this.generalSupportId != null) {
            roles.add(guild.getRoleById(this.generalSupportId));
        }

        if (this.kitmapSupportId != null) {
            roles.add(guild.getRoleById(this.kitmapSupportId));
        }

        if (this.practiceSupportId != null) {
            roles.add(guild.getRoleById(this.practiceSupportId));
        }

        if (this.hcfSupportId != null) {
            roles.add(guild.getRoleById(this.hcfSupportId));
        }

        if (this.bunkersSupportId != null) {
            roles.add(guild.getRoleById(this.bunkersSupportId));
        }

        return roles;
    }

}
