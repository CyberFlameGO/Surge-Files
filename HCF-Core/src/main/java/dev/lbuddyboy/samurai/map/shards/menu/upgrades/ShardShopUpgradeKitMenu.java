package dev.lbuddyboy.samurai.map.shards.menu.upgrades;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.Kit;
import dev.lbuddyboy.samurai.map.kits.upgrades.Upgrades;
import dev.lbuddyboy.samurai.map.shards.menu.GlassButton;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShardShopUpgradeKitMenu extends Menu {

    public static final Map<String, Integer> LEVEL_MAP = new HashMap<>();
    private static final Map<String, ItemStack> ICON_MAP = new HashMap<>();
    private static final Map<String, Integer> COST_MAP = new HashMap<>();

    static {
        LEVEL_MAP.put("PROTECTION_ENVIRONMENTAL", 2);
        LEVEL_MAP.put("DAMAGE_ALL", 2);
        LEVEL_MAP.put("ARROW_DAMAGE", 5);
        LEVEL_MAP.put("DIG_SPEED", 6);
        LEVEL_MAP.put("IMPALING", 5);
        LEVEL_MAP.put("RIPTIDE", 3);
        LEVEL_MAP.put("INFINITY", 1);
        LEVEL_MAP.put("PIERCING", 4);

        LEVEL_MAP.put("Inferno", 1);
        LEVEL_MAP.put("Repair", 1);
        LEVEL_MAP.put("Replenish", 5);
        LEVEL_MAP.put("Aquatic", 1);
        LEVEL_MAP.put("Recover", 1);
        LEVEL_MAP.put("Speed", 2);

        ICON_MAP.put("PROTECTION_ENVIRONMENTAL", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.GOLD + CC.BOLD + "Protection II").build());
        ICON_MAP.put("DAMAGE_ALL", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.RED + CC.BOLD + "Sharpness II").build());
        ICON_MAP.put("ARROW_DAMAGE", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.DARK_RED + CC.BOLD + "Power V").build());
        ICON_MAP.put("DIG_SPEED", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.YELLOW + CC.BOLD + "Efficiency VI").build());
        ICON_MAP.put("DEPTH_STRIDER", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.AQUA + CC.BOLD + "Depth Strider").build());
        ICON_MAP.put("IMPALING", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.YELLOW + CC.BOLD + "Impaling").build());
        ICON_MAP.put("RIPTIDE", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.BLUE + CC.BOLD + "Riptide").build());
        ICON_MAP.put("INFINITY", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.GREEN + CC.BOLD + "Infinity").build());
        ICON_MAP.put("PIERCING", ItemBuilder.of(Material.ENCHANTED_BOOK).name(CC.PINK + CC.BOLD + "Piercing").build());

        ICON_MAP.put("Inferno", ItemBuilder.of(Material.MAGMA_CREAM).name(CC.GOLD + CC.BOLD + "Inferno I").build());
        ICON_MAP.put("Repair", ItemBuilder.of(Material.ANVIL).name(CC.YELLOW + CC.BOLD + "Repair I").build());
        ICON_MAP.put("Replenish", ItemBuilder.of(Material.COOKED_BEEF).name(CC.GOLD + CC.BOLD + "Replenish V").build());
        ICON_MAP.put("Aquatic", ItemBuilder.of(Material.WATER_BUCKET).name(CC.BLUE + CC.BOLD + "Aquatic I").build());
        ICON_MAP.put("Recover", ItemBuilder.of(Material.GOLDEN_APPLE).name(CC.PINK + CC.BOLD + "Recover I").build());
        ICON_MAP.put("Speed", ItemBuilder.of(Material.SUGAR).name(CC.AQUA + CC.BOLD + "Speed II").build());

        COST_MAP.put("PROTECTION_ENVIRONMENTAL", 750);
        COST_MAP.put("INFINITY", 1250);
        COST_MAP.put("PIERCING", 1850);
        COST_MAP.put("DEPTH_STRIDER", 1250);
        COST_MAP.put("DAMAGE_ALL", 1000);
        COST_MAP.put("ARROW_DAMAGE", 1000);
        COST_MAP.put("DIG_SPEED", 200);
        COST_MAP.put("IMPALING", 1000);
        COST_MAP.put("RIPTIDE", 1000);

        COST_MAP.put("Inferno", 1000);
        COST_MAP.put("Repair", 200);
        COST_MAP.put("Replenish", 300);
        COST_MAP.put("Aquatic", 100);
        COST_MAP.put("Recover", 200);
        COST_MAP.put("Speed", 2000);
    }

    private final String kitName;
    private final Map<Material, Upgrades> map;

    public ShardShopUpgradeKitMenu(String kitName, Map<Material, Upgrades> map) {
        this.kitName = kitName;
        this.map = map;
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GREEN + CC.BOLD + "Upgrades";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < map.size() * 9; i++) {
            buttons.put(i, new GlassButton(7));
        }

        buttons.put(map.size() * 9 - 1, new BackButton(new ShardShopUpgradesMenu()));

        Kit kit = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit(kitName);

        if (kit == null) {
            return buttons;
        }

        ItemStack[] items = kit.getAllContents();
        int index = 0;

        Map<Material, Upgrades> upgradesMap = Samurai.getInstance().getMapHandler().getKitUpgradesHandler().getOrComputeUpgrades(player);

        for (Map.Entry<Material, Upgrades> entry : map.entrySet()) {
            ItemStack icon = null;

            for (ItemStack stack : items) {
                if (stack != null && entry.getKey() == stack.getType()) {
                    icon = stack.clone();
                    break;
                }
            }

            if (icon == null) continue; // Item is not in the kit

            if (upgradesMap != null) {
                Kit.doKitUpgradesMagic(upgradesMap, icon);
            }

            final ItemStack finalIcon = icon;

            buttons.put(index, new Button() {

                @Override
                public ItemStack getButtonItem(Player player) {
                    return finalIcon;
                }

                @Override
                public String getName(Player player) {
                    return null;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
                }

                @Override
                public Material getMaterial(Player player) {
                    return null;
                }
            });

            int x = 1;

            for (Enchantment enchantment : entry.getValue().getEnchantmentList()) {
                Integer cost = COST_MAP.get(enchantment.getName());
                if (cost == null) continue;

                buttons.put(index + 1 + x++, new EnchantmentUpgradeButton(
                        entry.getKey(),
                        enchantment,
                        cost,
                        ICON_MAP.get(enchantment.getName())
                ));
            }

            for (String enchantment : entry.getValue().getCustomEnchantmentList()) {
                Integer cost = COST_MAP.get(enchantment);
                if (cost == null) continue;

                buttons.put(index + 1 + x++, new CustomEnchantmentUpgradeButton(
                        entry.getKey(),
                        enchantment,
                        cost,
                        ICON_MAP.get(enchantment)
                ));
            }

            index += 9;
        }

        return buttons;
    }
}
