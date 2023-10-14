package dev.lbuddyboy.pcore.essential.kit;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Kit {

    private Config file;

    public Kit(Config file) {
        this.file = file;
        this.name = file.getFileName().replaceAll(".yml", "");

        this.load();
    }

    private String name, displayName;
    private MaterialData data;
    private List<String> description = DEF_LORE, descriptionOnCooldown = DEF_LORE, descriptionNoPermission = DEF_LORE;
    private boolean autoEquip = false, glowing = false;
    private int slot;
    private long cooldown = TimeUnit.DAYS.toMillis(1L);
    private ItemStack[] fakeItems = new ItemStack[0], armor, contents;
    private KitCategory category;
    private List<String> commands;

    public void load() {
        FileConfiguration config = this.file;

        this.displayName = config.getString("display-name");
        this.data = new MaterialData(Material.getMaterial(config.getString("display-item.material")), (byte) config.getInt("display-item.data"));
        this.description = config.getStringList("descriptions.normal");
        this.descriptionOnCooldown = config.getStringList("descriptions.on-cooldown");
        this.descriptionNoPermission = config.getStringList("descriptions.no-permission");
        this.glowing = config.getBoolean("display-item.glowing");
        this.autoEquip = config.getBoolean("auto-equip");
        this.slot = config.getInt("display-item.slot");
        this.cooldown = TimeUnit.SECONDS.toMillis(config.getLong("cooldown"));
        try {
            this.fakeItems = ItemUtils.itemStackArrayFromBase64(config.getString("fake-items"));
        } catch (Exception ignored) {

        }
        this.armor = ItemUtils.itemStackArrayFromBase64(config.getString("armor"));
        this.contents = ItemUtils.itemStackArrayFromBase64(config.getString("contents"));
        this.category = config.contains("category") ? pCore.getInstance().getKitHandler().getCategories().get(config.getString("category").toLowerCase()) : pCore.getInstance().getKitHandler().getCategories().values().stream().findFirst().get();
        if (config.contains("commands")) {
            this.commands.addAll(config.getStringList("commands"));
        } else {
            this.commands = new ArrayList<>();
        }
    }

    public void save() {
        FileConfiguration config = this.file;

        config.set("display-item.material", this.data.getItemType().name());
        config.set("display-item.data", this.data.getData());
        config.set("display-item.glowing", this.glowing);
        config.set("display-item.slot", this.slot);
        config.set("descriptions.normal", this.description);
        config.set("descriptions.on-cooldown", this.descriptionOnCooldown);
        config.set("descriptions.no-permission", this.descriptionNoPermission);

        config.set("display-name", this.displayName);
        config.set("auto-equip", this.autoEquip);
        config.set("cooldown", TimeUnit.MILLISECONDS.toSeconds(this.cooldown));
        config.set("fake-items", ItemUtils.itemStackArrayToBase64(this.fakeItems));
        config.set("armor", ItemUtils.itemStackArrayToBase64(this.armor));
        config.set("contents", ItemUtils.itemStackArrayToBase64(this.contents));
        config.set("category", this.category.getName());

        this.file.save();
    }

    public void apply(Player player, boolean forced) {
        Optional<KitCooldown> cooldownOpt = pCore.getInstance().getKitHandler().getCooldown(player.getUniqueId(), this.name);

        if (!player.hasPermission("pcore.kit." + this.name) || cooldownOpt.isPresent()) return;
        if (!forced) pCore.getInstance().getKitHandler().applyCooldown(player.getUniqueId(), this);

        for (ItemStack stack : Arrays.stream(armor).filter(Objects::nonNull).filter(s -> s.getType() != Material.AIR).collect(Collectors.toList())) {
            ItemUtils.tryFit(player, stack, this.autoEquip);
        }

        for (ItemStack stack : Arrays.stream(contents).filter(Objects::nonNull).filter(s -> s.getType() != Material.AIR).collect(Collectors.toList())) {
            ItemUtils.tryFit(player, stack, false);
        }

        for (String command : this.commands) {
            String[] parts = command.split(" ");
            StringBuilder newCommand = new StringBuilder();

            int i = 0;
            for (String part : parts) {
                i++;
                if (part.contains("-")) {
                    String[] partParts = part.split(" ");
                    try {
                        int first = Integer.parseInt(partParts[0]);
                        int second = Integer.parseInt(partParts[1]);

                        newCommand.append(i == 1 ? part : " " + part.replaceAll(first + "-" + second, String.valueOf(ThreadLocalRandom.current().nextInt(Math.min(first, second), Math.max(first, second)))));
                        continue;
                    } catch (NumberFormatException ignored) {}
                }

                newCommand.append(i == 1 ? part : " " + part);
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), newCommand.toString().replaceAll("%player%", player.getName()));
        }

    }

    public static List<String> DEF_LORE = Collections.singletonList("&7Edit in this item's config!");

}
