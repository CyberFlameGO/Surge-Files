package dev.lbuddyboy.samurai.team.roster;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.samurai.util.GSONUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Data
public class Roster {

    private ObjectId teamUUID;
    private List<UUID> members, captains, coleaders;

    public static Roster fromDocument(ObjectId id, Document document) {

        if (document == null) {
            return new Roster(id, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        }

        ObjectId teamUUID = document.getObjectId("teamUUID");


        return new Roster(teamUUID,
                GSONUtils.getGSON().fromJson(document.getString("members"),
                        GSONUtils.UUID), GSONUtils.getGSON().fromJson(document.getString("captains"), GSONUtils.UUID),
                GSONUtils.getGSON().fromJson(document.getString("coleaders"), GSONUtils.UUID)

        );
    }

    public Document toDocument() {
        Document document = new Document();

        document.put("teamUUID", this.teamUUID);
        document.put("members", GSONUtils.getGSON().toJson(this.members, GSONUtils.UUID));
        document.put("captains", GSONUtils.getGSON().toJson(this.captains, GSONUtils.UUID));
        document.put("coleaders", GSONUtils.getGSON().toJson(this.coleaders, GSONUtils.UUID));

        return document;
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            MongoCollection<Document> collection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("Rosters");

            collection.replaceOne(Filters.eq("teamUUID", this.teamUUID.toString()), toDocument(), new ReplaceOptions().upsert(true));
        });
    }

    public boolean contains(UUID uuid) {
        return this.members.contains(uuid) || this.captains.contains(uuid) || this.coleaders.contains(uuid);
    }

}
