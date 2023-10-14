package dev.lbuddyboy.crates.model;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.util.*;
import lombok.Getter;
import lombok.Setter;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter @Setter
public class Crate {

    private String name, displayName;
    private ItemStack key;
    private Config config;
    private int minRewards = 3, maxRewards = 5, uses;
    private boolean inCrateMenu;
    private boolean needsClosing;
    private Material crateMaterial;
    private HologramSettings hologramSettings;
    private CrateMenuSettings menuSettings;

    private final Map<Integer, ItemStack> fillers = new HashMap<>();
    private final List<Location> locations = new ArrayList<>();
    private final Map<String, CrateItem> items = new HashMap<>();
    private final Map<Location, Hologram> holograms = new HashMap<>();

    public Crate(String name, String displayName, ItemStack key) {
        this.name = name;
        this.config = new Config(lCrates.getInstance(), name, lCrates.getInstance().getCrateFolder());
        this.displayName = displayName;
        this.inCrateMenu = lCrates.getInstance().isVirtualKeys();
        this.key = key;
        if (!lCrates.getInstance().isVirtualKeys()) {
            this.hologramSettings = new HologramSettings().loadDefault(this);
        } else {
            this.menuSettings = new CrateMenuSettings().loadDefault(this);
        }

        save();
    }

    /*
    Load Constructor
     */

    public Crate(Config config) {
        this.config = config;

        reload();
    }

    public void loadLocations() {
        this.locations.clear();
        this.holograms.values().forEach(Hologram::delete);
        this.holograms.clear();

        for (String s : this.config.getStringList("locations")) {
            createHologram(LocationSerializer.deserializeString(s));
        }
    }

    public void resetLocations() {
        this.locations.clear();
        this.holograms.values().forEach(Hologram::delete);
        this.holograms.clear();
    }

    public void reload() {
        this.locations.clear();
        if (!lCrates.getInstance().isVirtualKeys()) {
            this.holograms.values().forEach(Hologram::delete);
            this.holograms.clear();
        }
        this.fillers.clear();
        this.items.clear();

        this.name = config.getFileName().replaceAll(".yml", "");
        this.config = new Config(lCrates.getInstance(), this.name, lCrates.getInstance().getCrateFolder());
        this.displayName = config.getString("display-name");
        this.minRewards = config.getInt("minimum-rewards");
        this.maxRewards = config.getInt("maximum-rewards");
        this.inCrateMenu = config.getBoolean("in-crate-menu");
        this.crateMaterial = Material.getMaterial(config.getString("crate-material"));
        this.key = ItemUtils.itemStackFromConfigSect("key", this.config);
        this.hologramSettings = new HologramSettings(this);

        if (this.config.contains("filler-items")) {
            for (String s : this.config.getStringList("filler-items")) {
                String[] parts = s.split(";");
                ItemBuilder builder = new ItemBuilder(CompMaterial.fromString(parts[0]).toItem());
                int slot = Integer.parseInt(parts[1]);
                String name = parts[2];
                boolean glowing = Boolean.parseBoolean(parts[3]);
                boolean hideAll = Boolean.parseBoolean(parts[4]);

                builder.setName(name);
                if (glowing) builder.addEnchant(Enchantment.DURABILITY, 1);
                if (hideAll) builder.addItemFlag(ItemFlag.values());

                fillers.put(slot, builder.create());
            }
        }

        ConfigurationSection section = this.config.getConfigurationSection("items");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection items = section.getConfigurationSection(key);
                if (items == null) continue;
                ItemStack stack = ItemUtils.itemStackArrayFromBase64(items.getString("item"))[0];

                if (items.contains("amount")) {
                    stack.setAmount(items.getInt("amount"));
                }

                CrateItem item = new CrateItem(
                        this,
                        items.getInt("slot"),
                        key,
                        stack,
                        items.getDouble("chance")
                );

                this.items.put(key, item);
            }
        }

        if (isInCrateMenu() || lCrates.getInstance().isVirtualKeys()) {
            this.menuSettings = new CrateMenuSettings(this);
        }

        if (lCrates.getInstance().isVirtualKeys()) return;

        loadLocations();
    }

    public void createHologram(Location location) {
        Hologram hologram = HolographicDisplaysAPI.get(lCrates.getInstance()).createHologram(location.clone().add(0.5, 2.45, 0.5));

        if (hologramSettings.isFloatingKey()) hologram.getLines().appendItem(this.key);

        for (String line : hologramSettings.getLines()) {
            hologram.getLines().appendText(CC.translate(line));
        }

        this.holograms.put(location, hologram);
        this.locations.add(location);
    }

    public void deleteLocation(Location location) {
        this.locations.remove(location);
        this.holograms.remove(location).delete();
        save();
    }

    public ItemStack getCrate() {
        return new ItemBuilder(this.crateMaterial == null ? Material.CHEST : this.crateMaterial)
                .setName(this.name)
                .create();
    }

    public void save() {
        Config config = this.config;

        config.set("items", null);

        config.set("display-name", this.displayName);
        config.set("minimum-rewards", this.minRewards);
        config.set("maximum-rewards", this.maxRewards);
        config.set("in-crate-menu", this.inCrateMenu);
        config.set("crate-material", this.crateMaterial == null ? Material.CHEST.name() : this.crateMaterial.name());
        ItemUtils.itemStackToConfigSect(this.key, -1, "key", this.config);

        for (CrateItem item : this.items.values()) {
            if (item.isRemoved()) continue;

            config.set("items." + item.getId() + ".item", ItemUtils.itemStackArrayToBase64(Collections.singletonList(item.getItem()).toArray(new ItemStack[0])));
            config.set("items." + item.getId() + ".slot", item.getSlot());
            config.set("items." + item.getId() + ".chance", item.getChance());
            config.set("items." + item.getId() + ".amount", item.getAmount());
        }

        config.set("locations", this.locations.stream().map(LocationSerializer::serializeString).collect(Collectors.toList()));
        config.set("uses", this.uses);

        if (!lCrates.getInstance().isVirtualKeys()) {
            this.hologramSettings.save();
        }

        if (isInCrateMenu() || lCrates.getInstance().isVirtualKeys()) this.menuSettings.save();
    }

    public void open(Player player) {
        int min = this.minRewards;
        int max = this.maxRewards;
        int actualRewards = ThreadLocalRandom.current().nextInt(min, max);
        int opened = 0;

        List<CrateItem> rewards = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            List<CrateItem> crateRewards = new ArrayList<>(getCrateItems().values());

            Collections.shuffle(crateRewards);

            for (CrateItem reward : crateRewards) {
                if (reward.getChance() < ThreadLocalRandom.current().nextDouble(100)) continue;

                rewards.add(reward);
                opened++;

                if (opened > actualRewards) {
                    break;
                }
            }
            if (opened > actualRewards) {
                break;
            }
        }

        rewards.forEach(reward -> ItemUtils.tryFit(player, reward.getItem(), false));
        uses++;
    }

    public void setInCrateMenu(boolean inCrateMenu) {
        this.inCrateMenu = inCrateMenu;
        if (this.inCrateMenu) {
            if (this.config.contains("menu-settings.slot")) {
                this.menuSettings = new CrateMenuSettings(this);
                return;
            }
            this.menuSettings = new CrateMenuSettings().loadDefault(this);
        }
    }

    public Map<String, CrateItem> getCrateItems() {
        Map<String, CrateItem> crateItems = new HashMap<>();
        ConfigurationSection section = this.config.getConfigurationSection("items");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection items = section.getConfigurationSection(key);
                if (items == null) continue;
                ItemStack stack = ItemUtils.itemStackArrayFromBase64(items.getString("item"))[0];

                if (items.contains("amount")) {
                    stack.setAmount(items.getInt("amount"));
                }

                CrateItem item = new CrateItem(
                        this,
                        items.getInt("slot"),
                        key,
                        stack,
                        items.getDouble("chance")
                );

                crateItems.put(key, item);
            }
        }
        return crateItems;
    }

    public void delete() {
        this.locations.clear();
        this.holograms.values().forEach(Hologram::delete);
        this.holograms.clear();
        this.fillers.clear();
        this.items.clear();
        if (this.config.getFile().exists()) this.config.getFile().delete();

        lCrates.getInstance().getCrates().remove(this.name);
        lCrates.getInstance().getApi().unregisterCrate(this);
    }

    public ItemStack getOpenKey() {
        NBTItem item = new NBTItem(this.key);

        item.setString("crate", this.name);

        return item.getItem();
    }

}
