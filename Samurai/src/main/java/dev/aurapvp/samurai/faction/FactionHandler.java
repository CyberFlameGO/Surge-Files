package dev.aurapvp.samurai.faction;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.claim.Claim;
import dev.aurapvp.samurai.faction.claim.ClaimHandler;
import dev.aurapvp.samurai.faction.command.FactionCommand;
import dev.aurapvp.samurai.faction.command.completions.FactionCompletion;
import dev.aurapvp.samurai.faction.command.completions.FactionTypeCompletion;
import dev.aurapvp.samurai.faction.command.completions.InviteCompletion;
import dev.aurapvp.samurai.faction.command.contexts.DepositContext;
import dev.aurapvp.samurai.faction.command.contexts.FactionContext;
import dev.aurapvp.samurai.faction.editor.FactionEditor;
import dev.aurapvp.samurai.faction.editor.impl.*;
import dev.aurapvp.samurai.faction.listener.FactionBorderListener;
import dev.aurapvp.samurai.faction.listener.FactionClaimListener;
import dev.aurapvp.samurai.faction.listener.FactionListener;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import dev.aurapvp.samurai.faction.member.FactionRole;
import dev.aurapvp.samurai.faction.thread.FactionDTRThread;
import dev.aurapvp.samurai.faction.thread.FactionTopThread;
import dev.aurapvp.samurai.storage.IStorage;
import dev.aurapvp.samurai.util.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class FactionHandler implements IModule {

    private final Map<UUID, Faction> factions;
    private final Map<String, Faction> factionNames;
    private final Map<UUID, Faction> factionPlayerUUIDs;
    private final ClaimHandler claimHandler;
    private final List<FactionEditor> editors;

    private Config factionsLang;
    private ItemStack claimWand, opClaimWand;

    public FactionHandler() {
        this.factions = new ConcurrentHashMap<>();
        this.factionNames = new ConcurrentHashMap<>();
        this.factionPlayerUUIDs = new ConcurrentHashMap<>();
        this.claimHandler = new ClaimHandler();
        this.editors = new ArrayList<>();
    }

    @Override
    public String getId() {
        return "factions";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadListeners();
        this.loadCommands();
        this.loadConfig();
        this.loadItems();
        this.loadEditors();
        this.loadFactions();
        this.loadThreads();
    }

    @Override
    public void unload(Samurai plugin) {
        IStorage storage = plugin.getStorageHandler().getStorage();

        this.factions.values().forEach(f -> storage.insertFaction(f.getUniqueId(),
                "faction",
                f,
                false
        ));

        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().remove(this.claimWand));
        Bukkit.getOnlinePlayers().forEach(player -> player.getInventory().remove(this.opClaimWand));
    }

    @Override
    public void save() {
        IStorage storage = Samurai.getInstance().getStorageHandler().getStorage();

        this.factions.values().forEach(f -> {
            if (!f.isEdited()) return;

            storage.insertFaction(f.getUniqueId(),
                    "faction",
                    f,
                    true
            );

            f.setEdited(false);
        });
    }

    @Override
    public void reload() {
        this.factions.clear();
        this.factionNames.clear();
        this.factionPlayerUUIDs.clear();
        this.claimHandler.getClaims().clear();
        this.claimHandler.clearAllPillars();

        this.loadConfig();
        this.loadItems();
        this.loadFactions();
    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new FactionBorderListener(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new FactionClaimListener(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new FactionListener(), Samurai.getInstance());
    }

    private void loadItems() {
        this.claimWand = ItemUtils.itemStackFromConfigSect("claim-wand", this.factionsLang);
        this.opClaimWand = new ItemBuilder(Material.GOLDEN_HOE).setName("&a&lOP CLAIM WAND").create();
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("invites", new InviteCompletion());
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("factions", new FactionCompletion());
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("factiontypes", new FactionTypeCompletion());
        Samurai.getInstance().getCommandManager().getCommandContexts().registerContext(Faction.class, new FactionContext());
        Samurai.getInstance().getCommandManager().getCommandContexts().registerContext(DepositContext.Deposit.class, new DepositContext());
        Samurai.getInstance().getCommandManager().registerCommand(new FactionCommand());
    }

    private void loadConfig() {
        this.factionsLang = new Config(Samurai.getInstance(), "factions");
        Arrays.stream(FactionConfiguration.values()).forEach(FactionConfiguration::loadDefault);
    }

    private void loadEditors() {
        this.editors.addAll(List.of(
                new ClaimForEditor(),
                new EventEditor(),
                new NameEditor(),
                new SetHQEditor(),
                new TypeEditor()
        ));
    }

    private void loadFactions() {
        IStorage storage = Samurai.getInstance().getStorageHandler().getStorage();

        this.factions.putAll(storage.loadFactions(
                this.factions,
                "faction",
                false
        ));

        this.factions.forEach((k, v) -> {
            this.factionNames.put(v.getName().toLowerCase(), v);
            v.getMembers().forEach(m -> factionPlayerUUIDs.put(m.getUuid(), v));

            for (Claim claim : v.getClaims()) {
                this.claimHandler.updateClaim(claim, false);
            }
        });
    }

    private void loadThreads() {
        new FactionDTRThread().start();
        new FactionTopThread().start();
    }

    public ItemStack getOPClaimWand(Player player) {
        if (player.hasMetadata(ClaimForEditor.FORCE_CLAIM_METADATA)) {
            return new ItemBuilder(this.opClaimWand).setLore("&7Currently claiming: " + getFactionByUniqueId((UUID) player.getMetadata(ClaimForEditor.FORCE_CLAIM_METADATA).get(0).value()).getName()).create();
        }
        return this.opClaimWand;
    }

    public Faction getFactionByUniqueId(UUID uuid) {
        return this.factions.getOrDefault(uuid, null);
    }

    public Faction getFactionByPlayer(Player player) {
        return getFactionByPlayerUUID(player.getUniqueId());
    }

    public Faction getFactionByMember(FactionPermission.FactionMember member) {
        return getFactionByPlayerUUID(member.getUuid());
    }

    public Faction getFactionByPlayerUUID(UUID uuid) {
        return this.factionPlayerUUIDs.getOrDefault(uuid, null);
    }

    public Faction getFactionByName(String name) {
        return this.factionNames.getOrDefault(name.toLowerCase(), null);
    }

    public Claim getClaimByLocation(Location location) {
        return this.claimHandler.getClaims().get(location.getWorld().getName(), Claim.toLong(location.getBlockX(), location.getBlockZ()));
    }

    public Faction getFactionByLocation(Location location) {
        Claim claim = getClaimByLocation(location);

        if (claim == null) return null;

        return getFactionByUniqueId(claim.getFaction());
    }

    public Faction createFaction(String name, UUID leader) {
        Faction faction = new Faction(name);

        if (leader != null) {
            FactionPermission.FactionMember member = new FactionPermission.FactionMember(leader, Bukkit.getOfflinePlayer(leader).getName());

            member.setRole(FactionRole.LEADER);
            faction.getMembers().add(member);
            this.factionPlayerUUIDs.put(leader, faction);
        }

        faction.triggerUpdate();

        this.factions.put(faction.getUniqueId(), faction);
        this.factionNames.put(faction.getName().toLowerCase(), faction);

        return faction;
    }

    public void disbandFaction(Faction faction) {
        IStorage storage = Samurai.getInstance().getStorageHandler().getStorage();

        this.factions.remove(faction.getUniqueId());
        this.factionNames.remove(faction.getName().toLowerCase());
        for (FactionPermission.FactionMember member : faction.getMembers()) {
            this.factionPlayerUUIDs.remove(member.getUuid());
        }
        new ArrayList<>(faction.getClaims()).forEach(faction::unclaimLand);

        storage.wipeFaction(faction.getUniqueId(), true);
    }

    public void updateFTop() {
        int i = 0;

        for (Faction faction : Samurai.getInstance().getFactionHandler().getFactions().values().stream().sorted(new FactionTopThread.FactionPointComparator().reversed()).collect(Collectors.toList())) {
            faction.setPlace(i++);
        }

        CC.broadcast("&d[Faction Top] &aAll faction top places have been updated!");
    }
}
