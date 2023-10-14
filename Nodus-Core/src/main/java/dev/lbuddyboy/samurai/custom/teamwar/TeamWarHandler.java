package dev.lbuddyboy.samurai.custom.teamwar;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.teamwar.command.TeamWarCommand;
import dev.lbuddyboy.samurai.custom.teamwar.listener.TeamWarListener;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarPlayer;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarState;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarTeam;
import dev.lbuddyboy.samurai.custom.teamwar.thread.WarThread;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.LocationSerializer;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class TeamWarHandler {

    private final World world;

    private List<WarTeam> queuedTeams;
    private WarTeam lastWinner;
    private WarState state;
    private Location spawnA, spawnB;
    private int round = 1, minimumTeamSize = 1, minimumStartTeams = 2;
    private ItemStack CLASS_EDITOR_ITEM, LEAVE_ITEM, SUMMON_TEAM;

    private transient WarTeam fightingA, fightingB;

    public TeamWarHandler() {
        this.queuedTeams = new ArrayList<>();
        this.state = WarState.NOT_STARTED;
        this.world = Bukkit.createWorld(new WorldCreator("team_war"));

        FileConfiguration configuration = Samurai.getInstance().getConfig();

        if (configuration.contains("team-war.spawnA")) this.spawnA = LocationSerializer.deserializeString(Objects.requireNonNull(configuration.getString("team-war.spawnA")));
        if (configuration.contains("team-war.spawnB")) this.spawnB = LocationSerializer.deserializeString(Objects.requireNonNull(configuration.getString("team-war.spawnB")));
        if (configuration.contains("team-war.minimumTeamSize")) this.minimumTeamSize = configuration.getInt("team-war.minimumTeamSize");
        if (configuration.contains("team-war.minimumStartTeams")) this.minimumStartTeams = configuration.getInt("team-war.minimumStartTeams");
        if (configuration.contains("team-war.class-editor")) this.CLASS_EDITOR_ITEM = ItemUtils.itemStackFromConfigSect("team-war.class-editor", configuration);
        else {
            CLASS_EDITOR_ITEM = new ItemBuilder(Material.CLOCK).displayName("&6&lMember Class Editor &7(Right Click)").build();
            save();
        }
        if (configuration.contains("team-war.leave-item")) this.LEAVE_ITEM = ItemUtils.itemStackFromConfigSect("team-war.leave-item", configuration);
        else {
            LEAVE_ITEM = new ItemBuilder(Material.RED_DYE).displayName("&c&lLeave Team War &7(Right Click)").build();
            save();
        }

        Samurai.getInstance().getServer().getPluginManager().registerEvents(new TeamWarListener(), Samurai.getInstance());
        Samurai.getInstance().getPaperCommandManager().registerCommand(new TeamWarCommand());
        Tasks.run(() -> new WarThread().start());
    }

    public void addToQueue(Player owner, Team team) {

        WarTeam warTeam = getWarTeam(owner.getUniqueId());
        if (warTeam != null) {
            owner.sendMessage(CC.translate("&cYou are already apart of the team war event."));
            return;
        }

        boolean queueAble = true;
        for (Player member : team.getOnlineMembers()) {
            if (!isAvailable(member)) queueAble = false;
        }

        if (!queueAble) {
            owner.sendMessage(CC.translate("&cYou cannot queue due to one of your teammates not being available."));
            owner.sendMessage(CC.translate("&c| Ask them to check their chats for more information."));
            return;
        }

        queuedTeams.add(new WarTeam(team.getUniqueId()));
        for (Player member : team.getOnlineMembers()) {
            spamMessage(member, "&c[!] Warning [!] If you leave at any point your whole team will be kicked from the event. [!] Warning [!] ");
            handleJoin(member);
        }
        queuedTeams = queuedTeams.stream().sorted(Comparator.comparingInt(WarTeam::getMemberSize)).collect(Collectors.toList());

        owner.sendMessage(CC.translate("&aYou are now apart of the team event!"));
        for (Player player : getPlayers()) {
            player.sendMessage(CC.translate("&a[Team War] &f" + team.getName() + " has just &ajoined&f the team war event!"));
        }
    }

    public void handleJoin(Player player) {
        WarTeam warTeam = getWarTeam(player.getUniqueId());

        if (warTeam == null) {
            return;
        }

        if (state != WarState.LOBBY && state != WarState.COUNTING) {
            return;
        }

        handleSpectate(player);
    }

    public void handleQuit(Player player) {
        WarTeam warTeam = getWarTeam(player.getUniqueId());

        if (state == WarState.FIGHTING) {
            leaveSpectate(player);
        } else if (state == WarState.ENDING) {
            leaveSpectate(player);
        } else {
            for (Player member : warTeam.getTeam().getOnlineMembers()) {
                leaveSpectate(member);
                member.sendMessage(CC.translate("&cA player in your team has left and you have all been kicked."));
            }

            this.queuedTeams.remove(warTeam);
            for (Player other : getPlayers()) {
                other.sendMessage(CC.translate("&a[Team War] &f" + player.getName() + "'s team has just &cleft&f the team war event!"));
            }
        }

        boolean valid = fightingA != null && fightingB != null;
        if (valid) {
            if (fightingA == warTeam || fightingB == warTeam)
                warTeam.checkStatus();
        }
    }

    public void handleSpectate(Player player) {
        Tasks.run(() -> {
            player.getInventory().setHeldItemSlot(0);
            player.getInventory().clear();
            player.setFireTicks(0);

            for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
            player.teleport(this.spawnA);
            player.getInventory().clear();
            player.setGameMode(GameMode.SURVIVAL);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.getInventory().setItem(3, this.LEAVE_ITEM);
            player.getInventory().setItem(5, this.CLASS_EDITOR_ITEM);
        });
    }

    public void leaveSpectate(Player player) {
        SpawnTagHandler.removeTag(player);
        WarTeam warTeam = getWarTeam(player.getUniqueId());

        player.getInventory().setHeldItemSlot(0);
        player.getInventory().clear();
        player.setFireTicks(0);
        for (PotionEffect effect : player.getActivePotionEffects()) player.removePotionEffect(effect.getType());
        player.setMetadata("leaving", new FixedMetadataValue(Samurai.getInstance(), true));
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
        player.removeMetadata("leaving", Samurai.getInstance());
        player.removeMetadata("spectating_team_war", Samurai.getInstance());
        player.getInventory().clear();
        if (warTeam != null) warTeam.getMembers().remove(player.getUniqueId());
    }

    public void handleNext(WarTeam loser) {
        this.lastWinner = this.fightingA == loser ? this.fightingB : this.fightingA;

        for (Map.Entry<UUID, WarPlayer> entry : loser.getMembers().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getValue().getUuid());
            if (player == null) continue;

            handleSpectate(player);
        }

        for (Map.Entry<UUID, WarPlayer> entry : lastWinner.getMembers().entrySet()) {
            Player player = Bukkit.getPlayer(entry.getValue().getUuid());
            if (player == null) continue;

            handleSpectate(player);
        }

        this.fightingA = null;
        this.fightingB = null;
        this.round++;
        this.state = WarState.COUNTING;
        WarThread.countdown = System.currentTimeMillis();
        WarThread.reminders.clear();
        this.queuedTeams.remove(loser);
    }

    public void endGame() {
        Tasks.run(() -> {
            this.queuedTeams.clear();

            this.getPlayers().forEach(this::handleQuit);
            Samurai.getInstance().getTeamWarHandler().setState(WarState.NOT_STARTED);
        });
    }

    public void pickNext() {
        Tasks.run(() -> {
            for (Entity entity : this.world.getEntities()) {
                if (!(entity instanceof Item item)) continue;

                item.remove();
            }

            List<WarTeam> warTeams = this.queuedTeams;
            WarTeam teamA = warTeams.get(0), teamB = warTeams.get(1);

            this.fightingA = teamA;
            this.fightingB = teamB;

            this.getPlayers().forEach(player -> {
                player.sendTitle(CC.translate("&7" + CC.UNICODE_ARROWS_RIGHT + " &6&lTEAM WAR &7" + CC.UNICODE_ARROWS_LEFT),
                        CC.translate(CC.YELLOW + "Round #" + Samurai.getInstance().getTeamWarHandler().getRound() + "&f " + CC.UNICODE_ARROWS_RIGHT + " " + teamA.getTeam().getName(player) + "&7 vs. " + teamB.getTeam().getName(player))
                );
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            });

            for (Map.Entry<UUID, WarPlayer> entry : fightingA.getMembers().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getValue().getUuid());
                if (player == null) continue;

                player.setAllowFlight(false);
                player.setFlying(false);
                player.setGameMode(GameMode.SURVIVAL);
                entry.getValue().getKit().loadKit(player);
                player.teleport(this.spawnA);
            }

            for (Map.Entry<UUID, WarPlayer> entry : fightingB.getMembers().entrySet()) {
                Player player = Bukkit.getPlayer(entry.getValue().getUuid());
                if (player == null) continue;

                player.setAllowFlight(false);
                player.setFlying(false);
                player.setGameMode(GameMode.SURVIVAL);
                entry.getValue().getKit().loadKit(player);
                player.teleport(this.spawnB);
            }

            this.state = WarState.FIGHTING;
        });
    }

    public WarTeam getWarTeam(UUID player) {
        for (WarTeam team : this.queuedTeams) {
            if (team.getMembers().containsKey(player)) return team;
        }
        return null;
    }

    public boolean isAtWar(UUID player) {
        return getWarTeam(player) != null;
    }

    public boolean isAvailable(Player member) {
        if (member.getInventory().firstEmpty() != 0) {
            spamMessage(member, "&cClear your inventory so your team can queue for the team war.");
            return false;
        }

        if (SpawnTagHandler.isTagged(member)) {
            spamMessage(member, "&cGet out of combat so your team can queue for the team war.");
            return false;
        }

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(member.getUniqueId())) {
            spamMessage(member, "&cEnable your pvp timer so your team can queue for the team war.");
            return false;
        }

        if (member.hasMetadata("gaming")) {
            spamMessage(member, "&cGet out of the hosted event so your team can queue for the team war.");
            return false;
        }

        Team teamAt = LandBoard.getInstance().getTeam(member.getLocation()), team = Samurai.getInstance().getTeamHandler().getTeam(member);
        if (teamAt == null || teamAt != team) {
            if (teamAt != null) {
                if (DTRBitmask.SAFE_ZONE.appliesAt(member.getLocation())) {
                    return true;
                }

                spamMessage(member, "&cGet into a safe zone or your teams claim so your team can queue for the team war.");
                return false;
            }
        }

        return true;
    }

    public void spamMessage(Player player, String message) {
        for (int i = 0; i < 3; i++) {
            player.sendMessage(CC.translate(message));
        }
        player.playSound(player, Sound.ENTITY_WARDEN_DIG, 2.0f, 2.0f);
    }

    public void save() {
        if (this.spawnA != null) Samurai.getInstance().getConfig().set("team-war.spawnA", LocationSerializer.serializeString(this.spawnA));
        if (this.spawnB != null) Samurai.getInstance().getConfig().set("team-war.spawnB", LocationSerializer.serializeString(this.spawnB));
        Samurai.getInstance().getConfig().set("team-war.minimumTeamSize", this.minimumTeamSize);
        Samurai.getInstance().getConfig().set("team-war.minimumStartTeams", this.minimumStartTeams);
        if (CLASS_EDITOR_ITEM != null) ItemUtils.itemStackToConfigSect(CLASS_EDITOR_ITEM, -1, "team-war.class-editor", Samurai.getInstance().getConfig());
        if (LEAVE_ITEM != null) ItemUtils.itemStackToConfigSect(LEAVE_ITEM, -1, "team-war.leave-item", Samurai.getInstance().getConfig());

        Samurai.getInstance().saveConfig();
    }

    public List<Player> getPlayers() {
        return this.world.getPlayers();
    }

}
