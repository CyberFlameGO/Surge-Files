package dev.lbuddyboy.samurai.team.logs;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;

import java.util.UUID;

@RequiredArgsConstructor
@Data
public class TeamLog {

    private final UUID id, executor;
    private final ObjectId teamUUID;
    private final String action;
    private final long executedAt;
    private final String command;

    private UUID reviewedBy;
    private long reviewedAt;

    public static TeamLog fromDocument(Document document) {
        UUID id = UUID.fromString(document.getString("id"));
        ObjectId teamUUID = document.getObjectId("teamUUID");
        UUID executor = UUID.fromString(document.getString("executor"));

        TeamLog log = new TeamLog(id, executor, teamUUID, document.getString("action"), document.getLong("executedAt"), document.getString("command"));

        if (document.containsKey("reviewedBy")) {
            log.setReviewedAt(document.getLong("reviewedAt"));
            log.setReviewedBy(UUID.fromString(document.getString("reviewedBy")));
        }

        return log;
    }

    public Document toDocument() {
        Document document = new Document();

        document.put("id", this.id.toString());
        document.put("teamUUID", this.teamUUID);
        document.put("executor", this.executor.toString());
        document.put("action", this.action);
        document.put("executedAt", this.executedAt);
        document.put("command", this.command);

        if (isReviewed()) {
            document.put("reviewedAt", this.reviewedAt);
            document.put("reviewedBy", this.reviewedBy.toString());

        }

        return document;
    }

    public void save() {
        Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            MongoCollection<Document> collection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("TeamLogs");

            collection.replaceOne(Filters.eq("id", this.id.toString()), toDocument(), new ReplaceOptions().upsert(true));
        });
    }

    public boolean isReviewed() {
        return reviewedBy != null;
    }

}
