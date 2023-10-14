package me.lbuddyboy.staff.editor;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.lbuddyboy.staff.lStaff;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/07/2021 / 12:29 AM
 * oStaff / rip.orbit.ostaff.editor
 */

@Getter
@Setter
public class EditItem {

	@Getter private static Map<UUID, EditItem> itemEdits = new HashMap<>();

	private final lStaff plugin = lStaff.getInstance();
	private final MongoCollection<Document> collection = lStaff.getInstance().getMongoDatabase().getCollection("edititems");

	private final UUID uuid;

	private int compassSlot = 0;
	private int randomTPSlot = 1;
	private int vanishSlot = 2;
	private int worldEditSlot = 2;
	private int betterViewSlot = 4;
	private int lastPvPSlot = 5;
	private int freezerSlot = 7;
	private int inspectorSlot = 8;
	private int betterViewData = 0;

	private boolean staffModeOnJoin = true;
	private boolean betterViewEnabled = true;
	private boolean freezerEnabled = true;
	private boolean inspectorEnabled = true;
	private boolean randomTPEnabled = true;
	private boolean thruCompassEnabled = true;
	private boolean vanishEnabled = true;
	private boolean worldEditEnabled = true;
	private boolean lastPvPEnabled = true;

	public EditItem(UUID uuid) {
		this.uuid = uuid;

		load();
	}

	public void load() {
		Document document = collection.find(Filters.eq("uuid", this.uuid.toString())).first();

		if (document == null)
			return;

		this.compassSlot = document.getInteger("compassSlot");
		this.randomTPSlot = document.getInteger("randomTPSlot");
		this.vanishSlot = document.getInteger("vanishSlot");
		this.betterViewSlot = document.getInteger("betterViewSlot");
		this.freezerSlot = document.getInteger("freezerSlot");
		this.inspectorSlot = document.getInteger("inspectorSlot");
		this.worldEditSlot = document.getInteger("worldEditSlot");
		this.betterViewData = document.getInteger("betterViewData");
		if (document.containsKey("lastPvPSlot")) {
			this.lastPvPSlot = document.getInteger("lastPvPSlot");
		}

		this.betterViewEnabled = document.getBoolean("betterViewEnabled");
		this.freezerEnabled = document.getBoolean("freezerEnabled");
		this.inspectorEnabled = document.getBoolean("inspectorEnabled");
		this.randomTPEnabled = document.getBoolean("randomTPEnabled");
		this.thruCompassEnabled = document.getBoolean("thruCompassEnabled");
		this.vanishEnabled = document.getBoolean("vanishEnabled");
		this.worldEditEnabled = document.getBoolean("worldEditEnabled");
		this.staffModeOnJoin = document.getBoolean("staffModeOnJoin");
		if (document.containsKey("lastPvPEnabled")) {
			this.lastPvPEnabled = document.getBoolean("lastPvPEnabled");
		}
	}

	public void save() {
		CompletableFuture.runAsync(() -> {
			Document document = new Document();

			document.put("uuid", this.uuid.toString());
			document.put("compassSlot", this.compassSlot);
			document.put("randomTPSlot", this.randomTPSlot);
			document.put("vanishSlot", this.vanishSlot);
			document.put("betterViewSlot", this.betterViewSlot);
			document.put("freezerSlot", this.freezerSlot);
			document.put("inspectorSlot", this.inspectorSlot);
			document.put("worldEditSlot", this.worldEditSlot);
			document.put("lastPvPSlot", this.lastPvPSlot);
			document.put("betterViewData", this.betterViewData);

			document.put("betterViewEnabled", this.betterViewEnabled);
			document.put("freezerEnabled", this.freezerEnabled);
			document.put("inspectorEnabled", this.inspectorEnabled);
			document.put("randomTPEnabled", this.randomTPEnabled);
			document.put("thruCompassEnabled", this.thruCompassEnabled);
			document.put("vanishEnabled", this.vanishEnabled);
			document.put("worldEditEnabled", this.worldEditEnabled);
			document.put("staffModeOnJoin", this.staffModeOnJoin);
			document.put("lastPvPEnabled", this.lastPvPEnabled);

			collection.replaceOne(Filters.eq("uuid", this.uuid.toString()), document, new ReplaceOptions().upsert(true));
		});
	}

	public static EditItem byUUID(UUID toSearch) {
		return getItemEdits().getOrDefault(toSearch, new EditItem(toSearch));
	}

}
