package bot.database.objects;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class ExtraSettings {

    public String guildId, suggestionChannel, rankRenewalChannel;

    public ExtraSettings(Document doc) {
        this.guildId = doc.getString("guild_id");
        this.suggestionChannel = doc.getString("suggestionChannel");
        this.rankRenewalChannel = doc.getString("rankRenewalChannel");
    }

}
