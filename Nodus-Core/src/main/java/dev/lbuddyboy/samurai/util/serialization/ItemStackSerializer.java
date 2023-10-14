//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util.serialization;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class ItemStackSerializer {
    public static final BasicDBObject AIR = new BasicDBObject();

    private ItemStackSerializer() {
    }

    public static BasicDBObject serialize(ItemStack itemStack) {
        if (itemStack != null && itemStack.getType() != Material.AIR) {
            BasicDBObject item = (new BasicDBObject("type", itemStack.getType().toString())).append("amount", itemStack.getAmount()).append("data", itemStack.getDurability());
            BasicDBList enchants = new BasicDBList();
            Iterator var3 = itemStack.getEnchantments().entrySet().iterator();

            while(var3.hasNext()) {
                Entry entry = (Entry)var3.next();
                enchants.add((new BasicDBObject("enchantment", ((Enchantment)entry.getKey()).getName())).append("level", entry.getValue()));
            }

            if (itemStack.getEnchantments().size() > 0) {
                item.append("enchants", enchants);
            }

            if (itemStack.hasItemMeta()) {
                ItemMeta m = itemStack.getItemMeta();
                BasicDBObject meta = new BasicDBObject("displayName", m.getDisplayName());
                BasicDBObject lore = new BasicDBObject("lore", m.getLore());
                if (m.getLore() != null) {
                    item.append("meta", lore);
                }

                item.append("meta", meta);
            }

            return item;
        } else {
            return AIR;
        }
    }

    public static ItemStack deserialize(BasicDBObject dbObject) {
        if (dbObject != null && !dbObject.isEmpty()) {
            Material type = Material.valueOf(dbObject.getString("type"));
            ItemStack item = new ItemStack(type, dbObject.getInt("amount"));
            ItemMeta m = item.getItemMeta();
            item.setDurability(Short.parseShort(dbObject.getString("data")));
            if (dbObject.containsField("enchants")) {
                BasicDBList enchs = (BasicDBList)dbObject.get("enchants");
                Iterator var5 = enchs.iterator();

                while(var5.hasNext()) {
                    Object o = var5.next();
                    BasicDBObject enchant = (BasicDBObject)o;
                    item.addUnsafeEnchantment(Enchantment.getByName(enchant.getString("enchantment")), enchant.getInt("level"));
                }
            }

            if (dbObject.containsField("meta")) {
                BasicDBObject meta = (BasicDBObject)dbObject.get("meta");
                if (meta.containsField("displayName")) {
                    m.setDisplayName(meta.getString("displayName"));
                }

                if (meta.containsField("lore")) {
                    List<String> l = new ArrayList();
                    Iterator var10 = meta.keySet().iterator();

                    while(var10.hasNext()) {
                        String ll = (String)var10.next();
                        l.add(ll);
                    }

                    m.setLore(l);
                }

                item.setItemMeta(m);
            }

            return item;
        } else {
            return new ItemStack(Material.AIR);
        }
    }

    static {
        AIR.put("type", "AIR");
        AIR.put("amount", 1);
        AIR.put("data", 0);
    }
}
