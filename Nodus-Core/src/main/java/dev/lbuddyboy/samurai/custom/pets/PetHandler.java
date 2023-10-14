package dev.lbuddyboy.samurai.custom.pets;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.flash.util.menu.GUISettings;
import dev.lbuddyboy.flash.util.menu.PagedGUISettings;
import dev.lbuddyboy.samurai.custom.pets.command.PetCommand;
import dev.lbuddyboy.samurai.custom.pets.command.completions.PetsCompletion;
import dev.lbuddyboy.samurai.custom.pets.impl.*;
import dev.lbuddyboy.samurai.custom.pets.thread.PetThread;
import dev.lbuddyboy.samurai.util.ItemUtils;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.pets.command.completions.EggTypesCompletion;
import dev.lbuddyboy.samurai.custom.pets.command.completions.RaritiesCompletion;
import dev.lbuddyboy.samurai.custom.pets.egg.EggImpl;
import dev.lbuddyboy.samurai.custom.pets.egg.impl.RandomEgg;
import dev.lbuddyboy.samurai.custom.pets.egg.impl.RightClickEgg;
import dev.lbuddyboy.samurai.custom.pets.egg.impl.WalkEgg;
import dev.lbuddyboy.samurai.custom.pets.listener.PetListener;
import dev.lbuddyboy.samurai.util.object.Config;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PetHandler {

    private final HashMap<String, IPet> pets;
    private final HashMap<String, PetRarity> rarities;
    private Config config;
    private File petFolder;
    private List<EggImpl> eggImpls;
    private GUISettings rarityGUISettings;
    private PagedGUISettings petGUISettings;
    private ItemStack petCandy;

    public PetHandler() {
        this.pets = new HashMap<>();
        this.rarities = new HashMap<>();
        this.eggImpls = new ArrayList<>();

        this.loadListeners();
        this.loadCommands();
        this.loadThreads();
        reload();
    }

    public void reload() {
        this.loadDirectories();
        this.loadConfigs();
        this.loadSettings();
        this.loadPetRarities();
        this.loadPets();
        this.loadItems();
    }

    private void loadDirectories() {
        this.petFolder = new File(Samurai.getInstance().getDataFolder(), "pets");

        if (!this.petFolder.exists()) this.petFolder.mkdir();
    }

    private void loadConfigs() {
        this.config = new Config(Samurai.getInstance(), "pets");
    }

    private void loadSettings() {
        this.eggImpls = Arrays.asList(
                new RightClickEgg(this.config),
                new WalkEgg(this.config),
                new RandomEgg(this.config)
        );
        this.rarityGUISettings = new GUISettings(this.config);
        this.petGUISettings = new PagedGUISettings(this.config, "pet-menu-settings");
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PetListener(), Samurai.getInstance());
    }

    private void loadCommands() {
        Samurai.getInstance().getPaperCommandManager().getCommandCompletions().registerCompletion("eggTypes", new EggTypesCompletion());
        Samurai.getInstance().getPaperCommandManager().getCommandCompletions().registerCompletion("pets", new PetsCompletion());
        Samurai.getInstance().getPaperCommandManager().getCommandCompletions().registerCompletion("petRarities", new RaritiesCompletion());
        Samurai.getInstance().getPaperCommandManager().registerCommand(new PetCommand());
    }

    private void loadPets() {
        this.pets.clear();

        this.pets.putAll(new HashMap<>() {{
            put("wizard", new WizardPet(new Config(Samurai.getInstance(), "wizard", petFolder)));
            put("scout", new ScoutPet(new Config(Samurai.getInstance(), "scout", petFolder)));
            put("stone", new StonePet(new Config(Samurai.getInstance(), "stone", petFolder)));
            put("blaze", new BlazePet(new Config(Samurai.getInstance(), "blaze", petFolder)));
            put("flame", new FlamePet(new Config(Samurai.getInstance(), "flame", petFolder)));
        }});
    }

    private void loadPetRarities() {
        this.rarities.putAll(new HashMap<>() {{
            for (String s : config.getConfigurationSection("rarities").getKeys(false)) {
                put(s.toUpperCase(), new PetRarity(s, config));
            }
        }});
    }

    private void loadItems() {
        this.petCandy = ItemUtils.itemStackFromConfigSect("items.pet-candy", this.config);
    }

    private void loadThreads() {
        new PetThread().start();
//        new PetUpdateThread().start();
    }

    public PetRarity getPetRarity(String name) {
        return this.rarities.getOrDefault(name.toUpperCase(), null);
    }

    public Map<Integer, ItemStack> scanEggs(Player player, String eggType) {
        return new HashMap<Integer, ItemStack>(){{
            int i = -1;
            for (ItemStack content : player.getInventory().getContents()) {
                i++;
                if (content == null || content.getType() == Material.AIR) continue;

                NBTItem item = new NBTItem(content);
                if (!item.hasTag("egg-type")) continue;
                if (!item.getString("egg-type").equalsIgnoreCase(eggType)) continue;

                put(i, content);
            }
        }};
    }

    public List<IPet> getPetsByRarity(PetRarity rarity) {
        return this.pets.values().stream().filter(pet -> pet.getPetRarity() == rarity).collect(Collectors.toList());
    }

    public EggImpl getEggImpl(String name) {
        for (EggImpl eggImpl : this.eggImpls) {
            if (eggImpl.getName().equalsIgnoreCase(name)) return eggImpl;
        }
        return null;
    }

}
