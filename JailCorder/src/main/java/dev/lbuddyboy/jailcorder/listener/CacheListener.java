package dev.lbuddyboy.jailcorder.listener;

import com.mongodb.client.model.Filters;
import dev.lbuddyboy.jailcorder.JailCorder;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class CacheListener implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();
        Bson query = Filters.eq("uuid", uuid.toString());
        Document document = JailCorder.getInstance().getMongoHandler().getCacheCollection().find(query).first();

        if (document == null) {
            JailCorder.getInstance().getMongoHandler().getCacheCollection().insertOne(new Document().append("uuid", uuid.toString()).append("name", name));
            JailCorder.getInstance().getMongoHandler().getCache().put(name.toLowerCase(), uuid);
            return;
        }

        if (document.getString("name").equals(name)) return;

        JailCorder.getInstance().getMongoHandler().getCacheCollection().replaceOne(query, new Document().append("uuid", uuid.toString()).append("name", name));
        JailCorder.getInstance().getMongoHandler().getCache().put(name.toLowerCase(), uuid);
    }

}
