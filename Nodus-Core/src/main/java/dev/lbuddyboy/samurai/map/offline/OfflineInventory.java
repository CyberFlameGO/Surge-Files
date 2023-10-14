package dev.lbuddyboy.samurai.map.offline;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTContainer;
import dev.lbuddyboy.flash.util.bukkit.ItemUtils;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

@AllArgsConstructor
@Data
public class OfflineInventory {

    public static int[] ARMOR_SLOTS = new int[]{
            0, 1, 2, 3
    };

    public static int CONTENTS_START = 9;

    public static int[] EXTRA_SLOTS = new int[]{
            5, 6, 7, 8
    };

    public static int[] GLASS_SLOTS = new int[]{
            4,
            45, 46, 47, 48, 49, 50, 51, 52, 53
    };

    private UUID uuid;
    private ItemStack[] armor, contents, extras;

    public OfflineInventory(UUID uuid, PlayerInventory inventory) {
        this.uuid = uuid;

        if (inventory == null) {
            this.armor = new ItemStack[4];
            this.contents = new ItemStack[36];
            this.extras = new ItemStack[0];
        } else {
            this.armor = inventory.getArmorContents();
            this.contents = inventory.getStorageContents();
            this.extras = inventory.getExtraContents();
        }
    }

    public OfflineInventory(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.armor = ItemUtils.itemStackArrayFromBase64(document.getString("armor"));
        this.contents = ItemUtils.itemStackArrayFromBase64(document.getString("contents"));
        this.extras = ItemUtils.itemStackArrayFromBase64(document.getString("extras"));
    }

    public Document toDocument() {
        Document document = new Document();

        document.put("uuid", uuid.toString());
        document.put("armor", ItemUtils.itemStackArrayToBase64(armor));
        document.put("contents", ItemUtils.itemStackArrayToBase64(contents));
        document.put("extras", ItemUtils.itemStackArrayToBase64(extras));

        return document;
    }

    public Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, 54, UUIDUtils.name(this.uuid) + "'s Offline");

        int i = 0;
        for (int slot : ARMOR_SLOTS) {
            ItemStack stack = armor[i++];
            if (stack == null || stack.getType() == Material.AIR) continue;

            inventory.setItem(slot, stack);
        }

        i = 0;
        for (int slot : EXTRA_SLOTS) {
            if (extras.length <= i) continue;

            ItemStack stack = extras[i++];
            if (stack == null || stack.getType() == Material.AIR) continue;

            inventory.setItem(slot, stack);
        }

        i = 0;
        for (int j = CONTENTS_START; j < 45; j++) {
            ItemStack stack = contents[i++];
            if (stack == null || stack.getType() == Material.AIR) continue;

            inventory.setItem(j, stack);
        }

        for (int slot : GLASS_SLOTS) {
            inventory.setItem(slot, ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).name(" ").build());
        }

        return inventory;
    }

    public void save(Inventory inventory) {
        int i = 0;
        for (int slot : ARMOR_SLOTS) {
            armor[i++] = inventory.getItem(slot);
        }

        i = 0;
        for (int slot : EXTRA_SLOTS) {
            if (inventory.getItem(slot) != null) i++;
        }

        extras = new ItemStack[i];

        i = 0;
        for (int slot : EXTRA_SLOTS) {
            if (extras.length <= i) continue;

            extras[i++] = inventory.getItem(slot);
        }

        i = 0;
        for (int j = CONTENTS_START; j < 45; j++) {
            contents[i++] = inventory.getItem(j);
        }

    }

}
