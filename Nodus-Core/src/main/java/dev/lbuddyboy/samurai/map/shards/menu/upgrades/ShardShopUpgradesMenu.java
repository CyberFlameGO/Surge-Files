package dev.lbuddyboy.samurai.map.shards.menu.upgrades;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.upgrades.Upgrades;
import dev.lbuddyboy.samurai.map.shards.menu.GlassButton;
import dev.lbuddyboy.samurai.map.shards.menu.ShardMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ShardShopUpgradesMenu extends Menu {

    private static final Map<Material, Upgrades> ARCHER_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> BARD_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> DIAMOND_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> ROGUE_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> MINER_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> HUNTER_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> RIDER_MAP = new LinkedHashMap<>();
    private static final Map<Material, Upgrades> WITHER_SKELETON = new LinkedHashMap<>();
    private static UpgradableKit[] upgradableKits;

    static {
        ARCHER_MAP.put(Material.LEATHER_HELMET, new Upgrades().protection().repair());
        ARCHER_MAP.put(Material.LEATHER_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        ARCHER_MAP.put(Material.LEATHER_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        ARCHER_MAP.put(Material.LEATHER_BOOTS, new Upgrades().protection().depth().repair());
        ARCHER_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());
        ARCHER_MAP.put(Material.BOW, new Upgrades().power().repair());

        HUNTER_MAP.put(Material.TURTLE_HELMET, new Upgrades().protection().repair());
        HUNTER_MAP.put(Material.LEATHER_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        HUNTER_MAP.put(Material.LEATHER_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        HUNTER_MAP.put(Material.LEATHER_BOOTS, new Upgrades().protection().depth().repair());
        HUNTER_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());
        HUNTER_MAP.put(Material.CROSSBOW, new Upgrades().infinity().piercing().repair());

        RIDER_MAP.put(Material.LEATHER_HELMET, new Upgrades().protection().repair());
        RIDER_MAP.put(Material.DIAMOND_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        RIDER_MAP.put(Material.DIAMOND_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        RIDER_MAP.put(Material.LEATHER_BOOTS, new Upgrades().protection().depth().repair());
        RIDER_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());
        RIDER_MAP.put(Material.TRIDENT, new Upgrades().riptide().impaling().repair());

        BARD_MAP.put(Material.GOLDEN_HELMET, new Upgrades().protection().repair());
        BARD_MAP.put(Material.GOLDEN_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        BARD_MAP.put(Material.GOLDEN_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        BARD_MAP.put(Material.GOLDEN_BOOTS, new Upgrades().protection().depth().repair());
        BARD_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());

        DIAMOND_MAP.put(Material.DIAMOND_HELMET, new Upgrades().protection().repair());
        DIAMOND_MAP.put(Material.DIAMOND_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        DIAMOND_MAP.put(Material.DIAMOND_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        DIAMOND_MAP.put(Material.DIAMOND_BOOTS, new Upgrades().protection().repair().depth().speed());
        DIAMOND_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());

        ROGUE_MAP.put(Material.CHAINMAIL_HELMET, new Upgrades().protection().repair());
        ROGUE_MAP.put(Material.CHAINMAIL_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        ROGUE_MAP.put(Material.CHAINMAIL_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        ROGUE_MAP.put(Material.CHAINMAIL_BOOTS, new Upgrades().protection().depth().repair());
        ROGUE_MAP.put(Material.DIAMOND_SWORD, new Upgrades().sharpness().repair());

        MINER_MAP.put(Material.IRON_HELMET, new Upgrades().protection().repair());
        MINER_MAP.put(Material.IRON_CHESTPLATE, new Upgrades().protection().repair().recover().saturation());
        MINER_MAP.put(Material.IRON_LEGGINGS, new Upgrades().protection().repair().fireResistance());
        MINER_MAP.put(Material.IRON_BOOTS, new Upgrades().protection().repair().depth().speed());
        MINER_MAP.put(Material.DIAMOND_PICKAXE, new Upgrades().efficiency().repair());

        upgradableKits = new UpgradableKit[]{
                new UpgradableKit("Diamond",
                        ItemBuilder.of(Material.DIAMOND_CHESTPLATE)
                                .name(CC.AQUA + CC.BOLD + "Upgrade Diamond Class")
                                .enchant(Enchantment.DURABILITY, 10)
                                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ARMOR_TRIM)
                                .trim(TrimMaterial.EMERALD, TrimPattern.EYE)
                                .build(),
                        DIAMOND_MAP
                ),
                new UpgradableKit("Archer",
                        ItemBuilder.of(Material.LEATHER_CHESTPLATE)
                                .name(CC.PINK + CC.BOLD + "Upgrade Archer Class")
                                .enchant(Enchantment.DURABILITY, 10)
                                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        ARCHER_MAP
                ),
                new UpgradableKit("Bard",
                        ItemBuilder.of(Material.GOLDEN_CHESTPLATE)
                                .name(CC.GOLD + CC.BOLD + "Upgrade Bard Class")
                                .enchant(Enchantment.DURABILITY, 10)
                                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        BARD_MAP
                ),
                new UpgradableKit("Rogue",
                        ItemBuilder.of(Material.CHAINMAIL_CHESTPLATE)
                                .name(CC.DARK_AQUA + CC.BOLD + "Upgrade Rogue Class")
                                .enchant(Enchantment.DURABILITY, 10)
                                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        ROGUE_MAP
                ),
                new UpgradableKit("Miner",
                        ItemBuilder.of(Material.IRON_HELMET)
                                .name(CC.WHITE + CC.BOLD + "Upgrade Miner Class")
                                .enchant(Enchantment.DURABILITY, 10)
                                .build(),
                        MINER_MAP),
                new UpgradableKit("Hunter",
                        ItemBuilder.of(Material.CROSSBOW)
                                .name(CC.DARK_PURPLE + CC.BOLD + "Upgrade Hunter Class")
                                .enchant(Enchantment.DURABILITY, 10)
                                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        HUNTER_MAP
                ),
                new UpgradableKit("WaveRider",
                        ItemBuilder.of(Material.TRIDENT)
                                .name(CC.BLUE + CC.BOLD + "Upgrade Wave Rider Class")
                                .enchant(Enchantment.DURABILITY, 10)
                                .flags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                                .build(),
                        RIDER_MAP
                )
        };
    }

    public static int[] ORANGE_SLOTS = new int[]{
            0, 9, 18, 27, 36, 45,
            8, 17, 26, 35, 44, 53
    };

    public static int[] WHITE_SLOTS = new int[]{
            1, 2, 3, 4, 5, 6, 7,
            46, 47, 48, 49, 50, 51, 52
    };

    private Menu backMenu;

    @Override
    public String getTitle(Player player) {
        return CC.GOLD + CC.BOLD + "Kit Upgrades";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (int slot : ORANGE_SLOTS) {
            buttons.put(slot, new GlassButton(1));
        }

        for (int slot : WHITE_SLOTS) {
            buttons.put(slot, new GlassButton(0));
        }

        buttons.put(24, new Button() {
            @Override
            public String getName(Player var1) {
                return CC.translate("&c&lComing soon...");
            }

            @Override
            public List<String> getDescription(Player var1) {
                return null;
            }

            @Override
            public Material getMaterial(Player var1) {
                return Material.BARRIER;
            }
        });

        if (backMenu != null) {
            buttons.put(4, new BackButton(backMenu));
        }

        int[] slots = new int[]{20, 21, 23, 29, 30, 32, 33};

        for (int i = 0; i < upgradableKits.length; i++) {
            UpgradableKit upgradableKit = upgradableKits[i];

            buttons.put(slots[i], new Button() {

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new ShardShopUpgradeKitMenu(upgradableKit.getKitName(), upgradableKit.getUpgrades(), backMenu).openMenu(player);
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return upgradableKit.getIcon();
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
        }
        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}
