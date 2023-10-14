package dev.lbuddyboy.pcore.essential.offline;

import com.google.gson.JsonObject;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.LocationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Data
public class OfflineData {

    private ItemStack[] armor, contents;
    private Location location;
    private boolean edited;

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("armor", ItemUtils.itemStackArrayToBase64(this.armor));
        object.addProperty("contents", ItemUtils.itemStackArrayToBase64(this.contents));
        object.addProperty("location", LocationUtils.serializeString(this.location));
        object.addProperty("edited", this.edited);

        return object;
    }

    public static OfflineData deserialize(JsonObject object) {
        return new OfflineData(
                ItemUtils.itemStackArrayFromBase64(object.get("armor").getAsString()),
                ItemUtils.itemStackArrayFromBase64(object.get("contents").getAsString()),
                LocationUtils.deserializeString(object.get("location").getAsString()),
                object.get("edited").getAsBoolean()
        );
    }

}
