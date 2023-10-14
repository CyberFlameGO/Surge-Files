//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util;

import com.mongodb.BasicDBObject;
import com.mongodb.util.JSON;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Iterator;

public class PlayerInventorySerializer {
    public PlayerInventorySerializer() {
    }

    public static String serialize(Player player) {
        return Samurai.PLAIN_GSON.toJson(new PlayerInventorySerializer.PlayerInventoryWrapper(player));
    }

    public static PlayerInventorySerializer.PlayerInventoryWrapper deserialize(String json) {
        return Samurai.PLAIN_GSON.fromJson(json, PlayerInventoryWrapper.class);
    }

    public static BasicDBObject getInsertableObject(Player player) {
        return (BasicDBObject)JSON.parse(serialize(player));
    }

    public static class PlayerInventoryWrapper {
        private final PotionEffect[] effects;
        private final ItemStack[] contents;
        private final ItemStack[] armor;
        private final int health;
        private final int hunger;

        public PlayerInventoryWrapper(Player player) {
            this.contents = player.getInventory().getContents();

            ItemStack stack;
            int i;
            for(i = 0; i < this.contents.length; ++i) {
                stack = this.contents[i];
                if (stack == null) {
                    this.contents[i] = new ItemStack(Material.AIR, 0, (short)0);
                }
            }

            this.armor = player.getInventory().getArmorContents();

            for(i = 0; i < this.armor.length; ++i) {
                stack = this.armor[i];
                if (stack == null) {
                    this.armor[i] = new ItemStack(Material.AIR, 0, (short)0);
                }
            }

            this.effects = (PotionEffect[])((PotionEffect[])player.getActivePotionEffects().toArray(new PotionEffect[player.getActivePotionEffects().size()]));
            this.health = (int)player.getHealth();
            this.hunger = player.getFoodLevel();
        }

        public void apply(Player player) {
            player.getInventory().setContents(this.contents);
            player.getInventory().setArmorContents(this.armor);

            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            PotionEffect[] var6 = this.effects;
            int var7 = var6.length;

            for (PotionEffect effect : var6) {
                player.addPotionEffect(effect);
            }

        }

        public PotionEffect[] getEffects() {
            return this.effects;
        }

        public ItemStack[] getContents() {
            return this.contents;
        }

        public ItemStack[] getArmor() {
            return this.armor;
        }

        public int getHealth() {
            return this.health;
        }

        public int getHunger() {
            return this.hunger;
        }
    }
}
