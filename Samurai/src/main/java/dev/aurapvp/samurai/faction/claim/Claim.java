package dev.aurapvp.samurai.faction.claim;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lunarclient.bukkitapi.LunarClientAPI;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.util.Cuboid;
import dev.aurapvp.samurai.util.LocationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.border.WorldBorder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftLocation;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Data
public class Claim {

    private final UUID uuid, faction;
    private Cuboid cuboid;
    private transient WorldBorder worldBorder, destroyedBorder;

    public JsonObject toJSON() {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", this.uuid.toString());
        object.addProperty("faction", this.faction.toString());
        object.add("l1", LocationUtils.toJSON(this.cuboid.getUpperSW()));
        object.add("l2", LocationUtils.toJSON(this.cuboid.getLowerNE()));

        return object;
    }

    public static Claim fromJSON(JsonObject object) {
        Claim claim = new Claim(UUID.fromString(object.get("uuid").getAsString()), UUID.fromString(object.get("faction").getAsString()));

        claim.setCuboid(new Cuboid(LocationUtils.fromJSON(object.get("l1").getAsJsonObject()), LocationUtils.fromJSON(object.get("l2").getAsJsonObject())));

        return claim;
    }

    public static JsonArray asArray(List<Claim> claims) {
        JsonArray array = new JsonArray();

        claims.forEach(claim -> array.add(claim.toJSON()));

        return array;
    }

    public static List<Claim> fromArray(JsonArray array) {
        List<Claim> claims = new ArrayList<>();

        array.forEach(element -> claims.add(fromJSON(element.getAsJsonObject())));

        return claims;
    }

    public static long toLong(int msw, int lsw) {
        return ((long) msw << 32) + lsw - Integer.MIN_VALUE;
    }


}
