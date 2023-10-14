package dev.aurapvp.samurai.faction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.events.Event;
import dev.aurapvp.samurai.faction.claim.Claim;
import dev.aurapvp.samurai.faction.command.FactionCommand;
import dev.aurapvp.samurai.faction.logs.FactionLog;
import dev.aurapvp.samurai.faction.member.FactionInvitation;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import dev.aurapvp.samurai.faction.member.FactionRole;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.LocationUtils;
import dev.aurapvp.samurai.util.TimeUtils;
import dev.aurapvp.samurai.util.gson.GSONUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class Faction {

    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");

    private final UUID uniqueId;

    private FactionType type = FactionType.PLAYER;
    private Event boundEvent;
    private String name;
    private int place, kills, deaths;
    private double points, balance, dtr;
    private long dtrRegen, createdAt;
    private Location home, rallyLocation;
    private LCWaypoint rallyWayPoint, focusWaypoint;
    private UUID focusPlayer, focusFaction;
    private List<Claim> claims = new ArrayList<>();
    private List<FactionPermission.FactionMember> members = new ArrayList<>();
    private List<FactionInvitation> invitations = new ArrayList<>();
    private List<UUID> alliances = new ArrayList<>(), truces = new ArrayList<>();
    private List<FactionLog> logs = new ArrayList<>();

    private boolean edited;

    public Faction(String name) {
        this.uniqueId = UUID.randomUUID();
        this.name = name;
        this.createdAt = System.currentTimeMillis();
    }

    public Faction(UUID uniqueId, JsonElement element) {
        this.uniqueId = uniqueId;
        this.loadJSON(element);
    }

    public List<FactionPermission.FactionMember> getOnlineMembers() {
        return this.members.stream().map(FactionPermission.FactionMember::getOfflinePlayer).filter(OfflinePlayer::isOnline).map(p -> getFactionMember(p.getUniqueId())).collect(Collectors.toList());
    }

    public List<Player> getOnlinePlayers() {
        return this.members.stream().map(FactionPermission.FactionMember::getOfflinePlayer).filter(OfflinePlayer::isOnline).map(OfflinePlayer::getPlayer).collect(Collectors.toList());
    }

    public List<FactionPermission.FactionMember> getOfflineMembers() {
        return this.members.stream().map(FactionPermission.FactionMember::getOfflinePlayer).filter(of -> !of.isOnline()).map(p -> getFactionMember(p.getUniqueId())).collect(Collectors.toList());
    }

    public List<OfflinePlayer> getOfflinePlayers() {
        return this.members.stream().map(FactionPermission.FactionMember::getOfflinePlayer).filter(of -> !of.isOnline()).collect(Collectors.toList());
    }

    public FactionPermission.FactionMember getLeader() {
        for (FactionPermission.FactionMember member : getMembers()) {
            if (member.getRole() == FactionRole.LEADER) return member;
        }
        return null;
    }

    public FactionPermission.FactionMember getFactionMember(UUID uuid) {
        for (FactionPermission.FactionMember member : this.members.stream().filter(member -> member.getUuid().toString().equals(uuid.toString())).collect(Collectors.toList())) {
            return member;
        }
        return null;
    }

    public FactionInvitation getInvitation(UUID player) {
        for (FactionInvitation invitation : this.invitations) {
            if (invitation.getTarget().toString().equals(player.toString()) && !invitation.isExpired())
                return invitation;
        }
        return null;
    }

    public Claim getClaim(Location location) {
        for (Claim claim : this.claims) {
            if (claim.getCuboid().contains(location)) return claim;
        }
        return null;
    }

    public boolean isSystemFaction() {
        return getLeader() == null;
    }

    public void sendMessage(String message) {
        getOnlinePlayers().forEach(player -> player.sendMessage(message));
    }

    public FactionPermission.FactionMember addMember(UUID target) {
        if (this.members.stream().anyMatch(m -> m.getUuid().toString().equals(target.toString())))
            return getFactionMember(target);
        FactionPermission.FactionMember member = new FactionPermission.FactionMember(target, Bukkit.getOfflinePlayer(target).getName());

        this.members.add(member);
        Samurai.getInstance().getFactionHandler().getFactionPlayerUUIDs().put(target, this);

        return member;
    }

    public String getName(Player sender) {
        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(sender);

        if (this.getLeader() == null && this.type != FactionType.PLAYER) {
            if (this.type == FactionType.KOTH) return this.name + " " + this.type.getColor() + CC.BOLD + this.type.getDisplayName();

            return this.type.getColor() + CC.BOLD + this.type.getDisplayName();
        }

        if (faction == null) return FactionConfiguration.RELATION_COLOR_ENEMY.getChatColor() + this.name;
        if (this.focusFaction != null && this.focusFaction == faction.getUniqueId())
            return FactionConfiguration.RELATION_COLOR_ENEMY.getChatColor() + CC.BOLD + this.name;
        if (this.alliances.contains(faction.getUniqueId()))
            return FactionConfiguration.RELATION_COLOR_ALLY.getChatColor() + CC.BOLD + this.name;
        if (this.truces.contains(faction.getUniqueId()))
            return FactionConfiguration.RELATION_COLOR_TRUCE.getChatColor() + CC.BOLD + this.name;
        if (faction == this) return FactionConfiguration.RELATION_COLOR_FACTION.getChatColor() + CC.BOLD + this.name;

        return FactionConfiguration.RELATION_COLOR_ENEMY.getChatColor() + CC.BOLD + this.name;
    }

    public Faction getFocusedFaction() {
        if (this.focusFaction == null) return null;

        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByUniqueId(this.focusFaction);

        if (faction == null) {
            this.focusFaction = null;
            return null;
        }

        return faction;
    }

    public boolean canBypass(Player player) {
        if (player.hasPermission("samurai.admin") && player.getGameMode() == GameMode.CREATIVE) return true;

        return getFactionMember(player.getUniqueId()) != null;
    }

    public int getMaxDTR() {
        return this.members.size() + 1;
    }

    public boolean isRegeneratingDTR() {
        return this.dtrRegen > System.currentTimeMillis();
    }

    public String getDTRColored() {
        if (this.dtr <= 0.99) {
            if (this.dtr <= 0) return CC.RED + DTR_FORMAT.format(this.dtr);
            return CC.YELLOW + DTR_FORMAT.format(this.dtr);
        }
        return CC.GREEN + DTR_FORMAT.format(this.dtr);
    }

    public void triggerUpdate() {
        this.edited = true;
    }

    public void unclaimLand(Claim claim) {
        this.claims.remove(claim);
        if (this.home != null && claim.getCuboid().contains(this.home)) {
            this.home = null;
            sendMessage(CC.translate("&cYour faction home was deleted due to it being in the land being unclaimed."));
        }
        Samurai.getInstance().getFactionHandler().getClaimHandler().updateClaim(claim, true);
        Samurai.getInstance().getDynMapImpl().getClaimLayer().deleteMarker(claim);
        triggerUpdate();
    }

    public void disband() {
        Samurai.getInstance().getFactionHandler().disbandFaction(this);
    }

    public void info(Player player) {
        if (isSystemFaction()) {
            CC.translate(this.type.getInfoLines(
                    FactionConfiguration.FACTION_PLACEHOLDERS(this)
            )).forEach(player::sendMessage);
            return;
        }

        List<String> message = new ArrayList<>(Arrays.asList(
                "&r",
                "&d&l%faction-display%&7: [%faction-online%&7/%faction-size%&7] &8- &d&lHQ&7: &f%faction-home%",
                "&7» &fOnline Members&7: &a%faction-members%",
                "&7» &fOffline Members&7: %faction-offline%",
                "&dDTR&7: %faction-dtr% &8- &dBalance&7: &2$%faction-balance% &8- &dPoints&7: &6%faction-points% &7(#%faction-place%)"
        ));

        if (isRegeneratingDTR()) {
            message.add("&7» &eDTR Regeneration&7: &c" + TimeUtils.formatLongIntoHHMMSS((this.dtrRegen - System.currentTimeMillis()) / 1000));
        }
        message.add("&r");

        CC.translate(
                message, FactionConfiguration.FACTION_PLACEHOLDERS(this, player)
        ).forEach(player::sendMessage);
    }

    public void rename(String name) {
        Samurai.getInstance().getFactionHandler().getFactionNames().remove(this.name);
        Samurai.getInstance().getFactionHandler().getFactionNames().put(name, this);
        this.name = name;
    }

    public JsonObject saveJSON() {
        JsonObject object = new JsonObject();

        object.addProperty("uniqueId", this.uniqueId.toString());
        object.addProperty("name", this.name);
        object.addProperty("type", this.type.name());
        object.addProperty("place", this.place);
        object.addProperty("points", this.points);
        object.addProperty("balance", this.balance);
        object.addProperty("dtr", this.dtr);
        object.addProperty("createdAt", this.createdAt);
        if (this.home != null) object.add("home", LocationUtils.toJSON(this.home));
        object.add("claims", Claim.asArray(this.claims));
        object.addProperty("members", GSONUtils.getGSON().toJson(this.members, GSONUtils.FACTION_MEMBERS));
        object.addProperty("invitations", GSONUtils.getGSON().toJson(this.invitations, GSONUtils.FACTION_INVITES));
        object.addProperty("alliances", GSONUtils.getGSON().toJson(this.alliances, GSONUtils.UUID));
        object.addProperty("truces", GSONUtils.getGSON().toJson(this.truces, GSONUtils.UUID));

        return object;
    }

    private void loadJSON(JsonElement element) {
        JsonObject object = element.getAsJsonObject();

        this.name = object.get("name").getAsString();
        if (object.has("type")) this.type = FactionType.valueOf(object.get("type").getAsString());
        if (object.has("place")) this.place = object.get("place").getAsInt();
        if (object.has("createdAt")) this.createdAt = object.get("createdAt").getAsLong();
        if (object.has("points")) this.points = object.get("points").getAsDouble();
        if (object.has("balance")) this.balance = object.get("balance").getAsDouble();
        if (object.has("dtr")) this.dtr = object.get("dtr").getAsDouble();
        if (object.has("home")) this.home = LocationUtils.fromJSON(object.get("home").getAsJsonObject());
        if (object.has("claims")) this.claims = Claim.fromArray(object.get("claims").getAsJsonArray());
        if (object.has("members"))
            this.members.addAll(GSONUtils.getGSON().fromJson(object.get("members").getAsString(), GSONUtils.FACTION_MEMBERS));
        if (object.has("invitations"))
            this.invitations.addAll(GSONUtils.getGSON().fromJson(object.get("invitations").getAsString(), GSONUtils.FACTION_INVITES));
        if (object.has("alliances"))
            this.alliances.addAll(GSONUtils.getGSON().fromJson(object.get("alliances").getAsString(), GSONUtils.UUID));
        if (object.has("truces"))
            this.truces.addAll(GSONUtils.getGSON().fromJson(object.get("truces").getAsString(), GSONUtils.UUID));

        this.invitations.removeIf(FactionInvitation::isExpired);
    }

}
