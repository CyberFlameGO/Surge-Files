package dev.lbuddyboy.pcore.pets;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.command.PetCommand;
import dev.lbuddyboy.pcore.pets.command.completions.EggTypesCompletion;
import dev.lbuddyboy.pcore.pets.command.completions.PetsCompletion;
import dev.lbuddyboy.pcore.pets.command.completions.RaritiesCompletion;
import dev.lbuddyboy.pcore.pets.egg.EggImpl;
import dev.lbuddyboy.pcore.pets.egg.impl.RightClickEgg;
import dev.lbuddyboy.pcore.pets.egg.impl.WalkEgg;
import dev.lbuddyboy.pcore.pets.impl.*;
import dev.lbuddyboy.pcore.pets.impl.*;
import dev.lbuddyboy.pcore.pets.listener.PetListener;
import dev.lbuddyboy.pcore.pets.thread.PetThread;
import dev.lbuddyboy.pcore.pets.thread.PetUpdateThread;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.menu.GUISettings;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class PetHandler implements IModule {

    private final HashMap<String, IPet> pets;
    private final HashMap<String, PetRarity> rarities;
    private Config config;
    private File petFolder;
    private List<EggImpl> eggImpls;
    private GUISettings rarityGUISettings;
    private PagedGUISettings petGUISettings;

    public PetHandler() {
        this.pets = new HashMap<>();
        this.rarities = new HashMap<>();
        this.eggImpls = new ArrayList<>();
    }

    @Override
    public void load(pCore plugin) {
        this.loadDirectories();
        this.loadConfigs();
        this.loadSettings();
        this.loadListeners();
        this.loadCommands();
        this.loadPetRarities();
        this.loadPets();
        this.loadThreads();
    }

    @Override
    public void unload(pCore plugin) {

    }

    @Override
    public void reload() {
        load(pCore.getInstance());
    }

    private void loadDirectories() {
        this.petFolder = new File(pCore.getInstance().getDataFolder(), "pets");

        if (!this.petFolder.exists()) this.petFolder.mkdir();
    }

    private void loadConfigs() {
        this.config = new Config(pCore.getInstance(), "pets");
    }

    private void loadSettings() {
        this.eggImpls = Arrays.asList(
                new RightClickEgg(this.config),
                new WalkEgg(this.config)
        );
        this.rarityGUISettings = new GUISettings(this.config);
        this.petGUISettings = new PagedGUISettings(this.config, "pet-menu-settings");
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new PetListener(), pCore.getInstance());
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("eggTypes", new EggTypesCompletion());
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("pets", new PetsCompletion());
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("petRarities", new RaritiesCompletion());
        pCore.getInstance().getCommandManager().registerCommand(new PetCommand());
    }

    private void loadPets() {
        this.pets.putAll(new HashMap<String, IPet>(){{
            put("wizard", new WizardPet(new Config(pCore.getInstance(), "wizard", petFolder)));
            put("scout", new ScoutPet(new Config(pCore.getInstance(), "scout", petFolder)));
            put("stone", new StonePet(new Config(pCore.getInstance(), "stone", petFolder)));
            put("blaze", new BlazePet(new Config(pCore.getInstance(), "blaze", petFolder)));
            put("flame", new FlamePet(new Config(pCore.getInstance(), "flame", petFolder)));
        }});
    }

    private void loadPetRarities() {
        this.rarities.putAll(new HashMap<String, PetRarity>(){{
            for (String s : config.getConfigurationSection("rarities").getKeys(false)) {
                put(s.toUpperCase(), new PetRarity(s, config));
            }
        }});
    }

    private void loadThreads() {
        new PetThread().start();
        new PetUpdateThread().start();
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
