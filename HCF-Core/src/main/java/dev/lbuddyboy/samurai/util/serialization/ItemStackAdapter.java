//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util.serialization;

import com.google.gson.*;
import dev.lbuddyboy.samurai.util.ItemUtils;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.lang.reflect.Type;
import java.math.BigInteger;

public class ItemStackAdapter implements JsonDeserializer<ItemStack>, JsonSerializer<ItemStack> {
    public ItemStackAdapter() {
    }

    public JsonElement serialize(ItemStack item, Type type, JsonSerializationContext context) {
        return serialize(item);
    }

    public ItemStack deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        return deserialize(element);
    }

    public static JsonElement serialize(ItemStack item) {
        if (item == null) {
            item = new ItemStack(Material.AIR);
        }

        JsonObject element = new JsonObject();

        element.addProperty("item", ItemUtils.itemStackArrayToBase64(new ItemStack[]{item}));

        return element;
    }

    public static ItemStack deserialize(JsonElement object) {
        if (!(object instanceof JsonObject element)) {
            return new ItemStack(Material.AIR);
        }
        return ItemUtils.itemStackArrayFromBase64(element.get("item").getAsString())[0];
    }


}
