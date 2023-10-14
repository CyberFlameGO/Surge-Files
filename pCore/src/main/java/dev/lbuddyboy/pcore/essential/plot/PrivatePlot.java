package dev.lbuddyboy.pcore.essential.plot;

import com.google.gson.JsonObject;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cuboid;
import dev.lbuddyboy.pcore.util.LocationUtils;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PrivatePlot {

    private UUID owner;
    private boolean open;
    private List<UUID> allowedUUIDs;
    private Location spawnLocation;
    private Cuboid bounds;

    private transient long lastResetMillis;

    public PrivatePlot(UUID owner) {
        this.owner = owner;
    }

    public List<Player> getPlayers() {
        return this.bounds.getWorld().getPlayers().stream().filter(player -> this.bounds.contains(player.getLocation())).collect(Collectors.toList());
    }

    public long getNextResetMillis() {
        return (this.lastResetMillis + (60_000 * 2)) - System.currentTimeMillis();
    }

    public boolean isResettable() {
        return getNextResetMillis() <= 0;
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("owner", this.owner.toString());
        object.addProperty("open", this.open);
        object.addProperty("allowedUUIDs", GSONUtils.getGSON().toJson(this.allowedUUIDs, GSONUtils.UUID));
        object.addProperty("spawnLocation", LocationUtils.serializeString(this.spawnLocation));
        object.addProperty("bounds", LocationUtils.serializeStringCuboid(this.bounds));

        return object;
    }

    public static PrivatePlot deserialize(JsonObject object) {
        PrivatePlot mine = new PrivatePlot();

        mine.setOwner(UUID.fromString(object.get("owner").getAsString()));
        mine.setOpen(object.get("open").getAsBoolean());
        mine.setAllowedUUIDs(GSONUtils.getGSON().fromJson(object.get("allowedUUIDs").getAsString(), GSONUtils.UUID));
        mine.setSpawnLocation(LocationUtils.deserializeString(object.get("spawnLocation").getAsString()));
        mine.setBounds(LocationUtils.deserializeStringCuboid(object.get("bounds").getAsString()));

        return mine;
    }

}
