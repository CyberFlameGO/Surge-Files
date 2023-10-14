package dev.lbuddyboy.gkits.object.kit;

import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.object.User;
import dev.lbuddyboy.gkits.util.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:06 PM
 * GKits / me.lbuddyboy.gkits.object
 */

@AllArgsConstructor
@Data
public class GKit {

    private Config config;

    private String name;
    private int slot;
    private boolean autoequip, glowing;
    private String displayName;
    private String cooldown;
    private ItemStack displayItem;
    private List<String> commands;
    private String description = "A new kit description!";
    private String formattedCooldown;
    private ItemStack[] armor, items, fakeItems;

    public GKit(String name) {
        this.name = name;
    }

    public GKit(Config config) {
        this.config = config;
        this.name = config.getFileName().replaceAll(".yml", "");

        reload();
    }

    public void reload() {
        this.config = new Config(lGKits.getInstance(), this.name, lGKits.getInstance().getKitsFolder());
        this.displayName = CC.translate(config.getString("displayName"));
        this.cooldown = config.getString("cooldown");
        this.description = CC.translate(config.getString("description"));
        this.formattedCooldown = CC.translate(config.getString("formatted-cooldown"));
        this.slot = config.getInt("slot");
        this.autoequip = config.getBoolean("auto-equip");
        this.glowing = config.getBoolean("glowing", true);
        this.displayItem = new ItemBuilder(Material.getMaterial(this.config.getString("display-item.material")), this.config.getInt("display-item.amount"))
                .setName(this.config.getString("display-item.name"))
                .create();
        this.commands = config.getStringList("commands");
        if (this.config.contains("armor")) this.armor = ItemUtils.itemStackArrayFromBase64(this.config.getString("armor"));
        if (this.config.contains("items")) this.items = ItemUtils.itemStackArrayFromBase64(this.config.getString("items"));
        if (this.config.contains("fake-items")) this.fakeItems = ItemUtils.itemStackArrayFromBase64(this.config.getString("fake-items"));
    }

    public void save() {
        this.config = new Config(lGKits.getInstance(), this.name, lGKits.getInstance().getKitsFolder());

        this.config.set("displayName", this.name);
        this.config.set("cooldown", this.cooldown);
        this.config.set("description", this.description);
        this.config.set("formatted-cooldown", this.formattedCooldown);
        this.config.set("slot", this.slot);
        this.config.set("autoequip", this.autoequip);
        this.config.set("glowing", this.glowing);
        this.config.set("commands", this.commands);
        this.config.set("display-item.amount", this.displayItem.getAmount());
        this.config.set("display-item.material", this.displayItem.getType().name());
        this.config.set("display-item.name", ItemUtils.getName(this.displayItem));
        this.config.set("armor", ItemUtils.itemStackArrayToBase64(this.armor));
        this.config.set("items", ItemUtils.itemStackArrayToBase64(this.items));
        this.config.set("fake-items", ItemUtils.itemStackArrayToBase64(this.fakeItems));

        this.config.save();
    }

    public void giveGkit(Player player, boolean bypass) {
        if (!lGKits.getInstance().getApi().attemptUse(player, this)) return;

        User user = User.getByUuid(player.getUniqueId());

        if (user == null) {
            player.sendMessage(CC.translate("&cCould not load your gkit data. Please relog or contact an admin."));
            return;
        }

        if (!bypass) {
            if (user.infoByGkit(this) != null) {
                if (user.onCooldown(user.infoByGkit(this))) {
                    player.sendMessage(CC.translate("&cYou are still on cooldown for this kit."));
                    return;
                }
            }

            GKitInfo info = new GKitInfo(this.name, System.currentTimeMillis() + JavaUtils.parse(getCooldown()));

            user.addCooldown(info);

            user.save(true);
        }

        for (ItemStack item : getArmor()) {
            ItemUtils.tryFit(player, item, this.autoequip);
        }

        for (ItemStack item : getItems()) {
            ItemUtils.tryFit(player, item);
        }

        this.getCommands().forEach(s -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", player.getName()));
        });

        player.updateInventory();
        Bukkit.getScheduler().runTaskLater(lGKits.getInstance(), () -> {
            lGKits.getInstance().getExecutorService().execute(() -> {
                for (ItemStack stack : player.getInventory()) {
                    Map<CustomEnchant, Integer> newEnchants = lGKits.getInstance().getCustomEnchantManager().getCustomEnchants(stack);
                    for (CustomEnchant enchant : newEnchants.keySet()) {
                        Bukkit.getScheduler().runTask(lGKits.getInstance(), () -> enchant.activate(player, stack, newEnchants.get(enchant)));
                    }
                }
            });
        }, 10);
    }

    public void delete() {
        if (this.config.getFile().exists()) this.config.getFile().delete();

        lGKits.getInstance().getGKits().remove(this.name);
    }

    public ItemStack[] getArmor() {
        return ItemUtils.itemStackArrayFromBase64(this.config.getString("armor"));
    }

    public ItemStack[] getFakeItems() {
        return ItemUtils.itemStackArrayFromBase64(this.config.getString("fake-items"));
    }

    public ItemStack[] getItems() {
        return ItemUtils.itemStackArrayFromBase64(this.config.getString("items"));
    }

}
