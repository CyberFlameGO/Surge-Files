package dev.lbuddyboy.jailcorder.record;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.jailcorder.JailCorder;
import lombok.Getter;
import lombok.Setter;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class RecordingUser {

    private final UUID uuid;
    private String name;
    private List<Recording> recordings;

    private transient BukkitTask recordTask;
    private transient long replayStarted;
    private transient Replay activeReplay;
    @Setter private transient int blocksBroken;

    public RecordingUser(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.recordings = new ArrayList<>();

        load();
    }

    public RecordingUser(UUID uuid) {
        this.uuid = uuid;
        this.recordings = new ArrayList<>();

        load();
    }

    public void load() {
        Document document = JailCorder.getInstance().getMongoHandler().getUserCollection().find(Filters.eq("uuid", this.uuid.toString())).first();

        if (document == null) {
            save(false);
            return;
        }

        this.name = document.getString("name");
        this.recordings = JailCorder.getInstance().getMongoHandler().getGSON().fromJson(document.getString("recordings"), JailCorder.getInstance().getMongoHandler().getRECORDINGS_TYPE());
    }

    public void save(boolean async) {
        if (!async) {
            Bson query = Filters.eq("uuid", this.uuid.toString());
            Document document = new Document();

            document.put("uuid", this.uuid.toString());
            document.put("name", this.name);
            document.put("recordings", JailCorder.getInstance().getMongoHandler().getGSON().toJson(this.recordings, JailCorder.getInstance().getMongoHandler().getRECORDINGS_TYPE()));

            JailCorder.getInstance().getMongoHandler().getUserCollection().replaceOne(query, document, new ReplaceOptions().upsert(true));
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(JailCorder.getInstance(), () -> {
            save(false);
        });
    }

    public void record() {
        this.activeReplay = ReplayAPI.getInstance().recordReplay(null, Bukkit.getConsoleSender(), new ArrayList<>(Bukkit.getOnlinePlayers()));
        this.replayStarted = System.currentTimeMillis();

        this.recordTask = Bukkit.getScheduler().runTaskLater(JailCorder.getInstance(), () -> {
            stopRecording();
            record();
        }, 20 * 60);
    }

    public void stopRecording() {
        ReplayAPI.getInstance().stopReplay(this.activeReplay.getId(), true);
        this.recordings.add(new Recording(this.activeReplay.getId(), this.blocksBroken, this.replayStarted, System.currentTimeMillis()));
        this.activeReplay = null;
        if (this.recordTask != null && !this.recordTask.isCancelled()) this.recordTask.cancel();
        this.blocksBroken = 0;
        save(true);
    }

}
