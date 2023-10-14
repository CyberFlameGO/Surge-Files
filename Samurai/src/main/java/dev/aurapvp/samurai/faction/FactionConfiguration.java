package dev.aurapvp.samurai.faction;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.LocationUtils;
import dev.aurapvp.samurai.util.StringUtils;
import dev.aurapvp.samurai.util.fanciful.FancyMessage;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public enum FactionConfiguration {

    CLAIM_STARTS("CLAIM.CLAIMS_START", 1000),
    WILDERNESS_ENDS("CLAIM.WILDERNESS_ENDS", 1000),
    WARZONE_RADIUS("CLAIM.WARZONE_RADIUS", 750),
    FACTION_MAP_RADIUS("CLAIM.FACTION_MAP.RADIUS", 22),
    MAX_CLAIMS("CLAIM.MAX_CLAIMS", 2),
    CLAIM_SET_FIRST("CLAIM.SET_FIRST", Arrays.asList(
            "&6You have just set the &f1st&6 claim selection point at &f(%x%, %y%, %z%)"
    )),
    CLAIM_SET_SECOND("CLAIM.SET_SECOND", Arrays.asList(
            "&6You have just set the &f2nd&6 claim selection point &f(%x%, %y%, %z%)"
    )),
    CLAIM_SET_BOTH("CLAIM.SET_BOTH", Arrays.asList(
            "&eYou have set both points of your claim selection.",
            "&fClaim cost: &a$%cost%",
            "&fClaim size: &d%claim_x%x%claim_z%",
            " ",
            "&7&oShift + Left Click the air to confirm your selection."
    )),

    MEMBER_NO_PERMISSION("MEMBER.NO_PERMISSION", "&cYou do not have permission to %permission%!"),
    MEMBER_INVITATION_TARGET("MEMBER.INVITATION.TARGET", new FancyMessage(CC.translate("&fYou have been &einvited&f to the join the &r%faction-display%&f faction! &fClick "))
            .then(CC.translate("&ahere"))
            .tooltip("&fClick here to join!")
            .command("/f join %faction-name%")
            .then(CC.translate(" &fhere to join!")).toJSONString()
    ),
    MEMBER_INVITATION_FACTION_INVITED("MEMBER.INVITATION.FACTION_INVITED", "&c%target-name%&f has been &ainvited&f to join the faction by &a%member-display%&f!"),
    MEMBER_INVITATION_FACTION_UNINVITED("MEMBER.INVITATION.FACTION_UNINVITED", "&c%target-name%&f has been &cun-invited&f from the faction by %member-display%"),
    MEMBER_INVITATION_ALREADY_INVITED("MEMBER.INVITATION.ALREADY_INVITED", "&c%target-name% is already invited to the faction!"),
    MEMBER_INVITATION_ALREADY_IN_FACTION("MEMBER.INVITATION.ALREADY_IN_FACTION", "&c%target-name% is already a part of the faction!"),
    MEMBER_INVITATION_NOT_INVITED("MEMBER.INVITATION.NOT_INVITED", "&cYou have not been invited to the %faction-display% faction!"),
    MEMBER_INVITATION_DELAY_SECONDS("MEMBER.INVITATION.DELAY_SECONDS", 60),

    MEMBER_JOINED_BROADCAST("MEMBER.JOINED.BROADCAST", "&a%member-display%&fhas just &2connected&f to the server!"),
    MEMBER_QUIT_BROADCAST("MEMBER.QUIT.BROADCAST", "&a%member-display%&fhas just &cleft&f the server!"),
    MEMBER_DAMAGE_CANCELLED("MEMBER.DAMAGE.CANCELLED", "&a%member-display%&f is in your faction!"),

    RELATION_COLOR_ENEMY("RELATION_COLORS.ENEMY", "RED"),
    RELATION_COLOR_FOCUSED("RELATION_COLORS.ENEMY", "DARK_PURPLE"),
    RELATION_COLOR_FACTION("RELATION_COLORS.FACTION", "GREEN"),
    RELATION_COLOR_ALLY("RELATION_COLORS.ALLY", "LIGHT_PURPLE"),
    RELATION_COLOR_TRUCE("RELATION_COLORS.TRUCE", "BLUE"),

    LAND_CLAIMED_FACTION("LAND_CLAIMED.BROADCAST", "&a%member-display%&f has just created a claim. &7%size% &c(-$%price%)"),
    LAND_CLAIMED_PRICE_PER_BLOCK("LAND_CLAIMED.PRICE_PER_BLOCK", 20.0),
    LAND_CLAIMED_MAX_CLAIM_SIZE("LAND_CLAIMED.MAX_CLAIM_SIZE", 50),
    LAND_UNCLAIMED_BROADCAST("LAND_UNCLAIMED.BROADCAST", "&a%member-display%&f has just unclaimed land! &7%size% &a(+$%price% to the faction balance)"),
    LAND_UNCLAIMED_NOT_IN("LAND_UNCLAIMED.NOT_IN", "&cYou cannot unclaim land that isn't yours."),
    LAND_CLAIMS_NO_CLAIMS("LAND_CLAIMS.NO_CLAIMS", "&cYour faction has no claims."),
    LAND_CLAIMS_CLAIMS_FORMAT("LAND_CLAIMS.CLAIMS_FORMAT", "&e#%place%'s Center - &7(%x%, %y%, %z%) "),

    FACTION_FOCUSED_BROADCAST("FACTION_FOCUSED.BROADCAST", "&d&l[FACTION FOCUS] &a%member-display% &fhas just focused &d%faction-display%&f!"),
    FACTION_UNFOCUSED_BROADCAST("FACTION_UNFOCUSED.BROADCAST", "&d&l[FACTION FOCUS] &a%member-display% &fhas just un-focused &d%faction-display%&f!"),
    FACTION_UNFOCUSED_NO_FACTION("FACTION_UNFOCUSED.NO_FACTION", "&d&l[FACTION FOCUS] &cYou do not currently have a faction focused!"),

    CANNOT_BREAK("CLAIM_PROTECTION.CANNOT_BREAK", "&cYou cannot break blocks in %faction-display%&c's territory."),
    CANNOT_PLACE("CLAIM_PROTECTION.CANNOT_PLACE", "&cYou cannot place blocks in %faction-display%&c's territory."),
    CANNOT_INTERACT("CLAIM_PROTECTION.CANNOT_INTERACT", "&cYou cannot interact with blocks in %faction-display%&c's territory."),

    FACTION_NOT_FOUND("FACTION.NOT_FOUND", "&cThe faction %faction-name% could not be found!"),
    FACTION_NOT_FOUND_PLAYER("FACTION.NOT_FOUND_PLAYER", "&cThe faction %faction-name% could not be found!"),
    FACTION_ALREADY_IN("FACTION.ALREADY_IN", "&cYou are already in a faction!"),
    FACTION_ALREADY_IN_TARGET("FACTION.ALREADY_IN_TARGET", "&c%player% is already in a faction!"),
    FACTION_ALREADY_EXISTS("FACTION.ALREADY_EXISTS", "&cA faction with that name already exists!"),
    FACTION_NOT_CREATED("FACTION.NOT_CREATED", "&cYou need to create a faction to do this."),
    FACTION_MEMBER_NOT_IN_TERRITORY("FACTION.NOT_IN_TERRITORY", "&cYou must be in one of your factions claim to do this."),
    FACTION_MEMBER_ADDED("FACTION.MEMBER_ADDED", "&a%player-name%&f has just &bjoined&f the faction!"),
    FACTION_MEMBER_REMOVED("FACTION.MEMBER_REMOVED", "&a%player-name%&f has just &cleft&f the faction!"),
    FACTION_MEMBER_DEPOSITED("FACTION.BANK_DEPOSIT", "&a%member-display%&f has just &2deposited&f in to the faction bank! &a(+$%amount%)"),
    FACTION_MEMBER_WITHDRAW("FACTION.BANK_WITHDRAW", "&a%member-display%&f has just &3withdrawn&f from the faction bank! &c(-$%amount%)"),
    FACTION_MEMBER_SET_HOME("FACTION.SET_HOME", "&a%member-display%&f has just &5set home&f from the faction bank! &7(%x%, %y%, %z%)"),

    DISBAND_FACTION_SENDER("DISBAND_FACTION.SENDER", "&aYou have just disbanded the %faction-name% faction!"),
    DISBAND_FACTION_BROADCAST("DISBAND_FACTION.BROADCAST", "&fFaction &r%faction-display% &fhas just been &c&lDELETED&f by &r%player-display%&f!"),

    CREATED_FACTION_SENDER("CREATED_FACTION.SENDER", "&aYou have just created the %faction-name% faction!"),
    CREATED_FACTION_BROADCAST("CREATED_FACTION.BROADCAST", "&fFaction &r%faction-display% &fhas just been &a&lCREATED&f by &r%player-display%&f!");

    private final String path;
    private final Object value;

    public String getString(Object... objects) {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return CC.translate(Samurai.getInstance().getFactionHandler().getFactionsLang().getString(this.path), objects);

        loadDefault();

        return CC.translate(String.valueOf(this.value), objects);
    }

    public String getString(Object[] objects, Object... os) {
        List<Object> list = new ArrayList<>();

        list.addAll(Arrays.asList(objects));
        list.addAll(Arrays.asList(os));

        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return CC.translate(Samurai.getInstance().getFactionHandler().getFactionsLang().getString(this.path), list.toArray());

        loadDefault();

        return CC.translate(String.valueOf(this.value), list.toArray());
    }

    public boolean getBoolean() {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return Samurai.getInstance().getFactionHandler().getFactionsLang().getBoolean(this.path);

        loadDefault();

        return Boolean.parseBoolean(String.valueOf(this.value));
    }

    public int getInt() {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return Samurai.getInstance().getFactionHandler().getFactionsLang().getInt(this.path);

        loadDefault();

        return Integer.parseInt(String.valueOf(this.value));
    }

    public double getDouble() {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return Samurai.getInstance().getFactionHandler().getFactionsLang().getDouble(this.path);

        loadDefault();

        return Double.parseDouble(String.valueOf(this.value));
    }

    public List<String> getStringList(Object... objects) {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return CC.translate(Samurai.getInstance().getFactionHandler().getFactionsLang().getStringList(this.path), objects);

        loadDefault();

        return CC.translate((List<String>) this.value, objects);
    }

    public ChatColor getChatColor() {
        return ChatColor.valueOf(getString());
    }

    public Location getLocation() {
        return LocationUtils.deserializeString(getString());
    }

    public FancyMessage getFancyMessage(Object... objects) {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path))
            return FancyMessage.deserialize(CC.format(Samurai.getInstance().getFactionHandler().getFactionsLang().getString(this.path), objects));

        loadDefault();

        return FancyMessage.deserialize(CC.format(Samurai.getInstance().getFactionHandler().getFactionsLang().getString(String.valueOf(this.value)), objects));
    }

    public void update(Object value) {
        Samurai.getInstance().getFactionHandler().getFactionsLang().set(this.path, value);
        Samurai.getInstance().getFactionHandler().getFactionsLang().save();
    }

    public void loadDefault() {
        if (Samurai.getInstance().getFactionHandler().getFactionsLang().contains(this.path)) return;

        Samurai.getInstance().getFactionHandler().getFactionsLang().set(this.path, this.value);
        Samurai.getInstance().getFactionHandler().getFactionsLang().save();
    }

    public static Object[] FACTION_PLACEHOLDERS(Faction faction) {
        return new Object[]{
                "%faction-name%", faction.getName(),
                "%faction-size%", "" + faction.getMembers().size(),
                "%faction-online%", "" + faction.getOnlinePlayers().size(),
                "%faction-balance%", "" + Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(faction.getBalance()),
                "%faction-points%", "" + Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(faction.getPoints()),
                "%faction-place%", "" + Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(faction.getPlace()),
                "%faction-members%", "" + StringUtils.join(faction.getOnlineMembers().stream().sorted(new FactionPermission.FactionMemberComparator().reversed()).map(FactionPermission.FactionMember::getName).collect(Collectors.toList())),
                "%faction-offline%", "" + StringUtils.join(faction.getOfflineMembers().stream().sorted(new FactionPermission.FactionMemberComparator().reversed()).map(FactionPermission.FactionMember::getName).collect(Collectors.toList())),
                "%faction-dtr%", "" + faction.getDTRColored(),
                "%faction-home%", LocationUtils.formatLocation(faction.getHome())
        };
    }

    public static Object[] FACTION_PLACEHOLDERS(Faction faction, Player player) {
        return new Object[]{
                "%faction-display%", faction.getName(player),
                "%faction-name%", faction.getName(),
                "%faction-size%", "" + faction.getMembers().size(),
                "%faction-online%", "" + faction.getOnlinePlayers().size(),
                "%faction-balance%", "" + Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(faction.getBalance()),
                "%faction-points%", "" + Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(faction.getPoints()),
                "%faction-place%", "" + Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(faction.getPlace()),
                "%faction-members%", "" + StringUtils.join(faction.getOnlineMembers().stream().sorted(new FactionPermission.FactionMemberComparator().reversed()).map(m -> m.getRole().getAstrix() + m.getName()).collect(Collectors.toList())),
                "%faction-offline%", "" + StringUtils.join(faction.getOfflineMembers().stream().sorted(new FactionPermission.FactionMemberComparator().reversed()).map(m -> m.getRole().getAstrix() + m.getName()).collect(Collectors.toList())),
                "%faction-dtr%", "" + faction.getDTRColored(),
                "%faction-home%", LocationUtils.formatLocation(faction.getHome())
        };
    }

    public static Object[] PLAYER_PLACEHOLDERS(Player player) {
        return new Object[]{
                "%player-name%", player.getName(),
                "%player-display%", player.getDisplayName()
        };
    }

    public static Object[] MEMBER_PLACEHOLDERS(FactionPermission.FactionMember member) {
        return new Object[]{
                "%member-name%", member.getName(),
                "%member-role%", member.getRole().getDisplayName(),
                "%member-display%", member.getDisplay()
        };
    }

    public static Object[] TARGET_PLACEHOLDERS(OfflinePlayer player) {
        return new Object[]{
                "%target-name%", player.getName(),
        };
    }

    public static Object[] TARGET_PLACEHOLDERS(Player player) {
        return new Object[]{
                "%target-name%", player.getName(),
                "%target-name%", player.getDisplayName()
        };
    }

    public static Object[] PLAYER_FACTION_PLACEHOLDERS(Player player, Faction faction) {
        List<Object> objects = new ArrayList<>();

        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction)));
        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction, player)));
        objects.addAll(Arrays.asList(FactionConfiguration.PLAYER_PLACEHOLDERS(player)));

        return objects.toArray();
    }

    public static Object[] PLAYER_TARGET_FACTION_PLACEHOLDERS(Player player, OfflinePlayer target, Faction faction) {
        List<Object> objects = new ArrayList<>();

        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction)));
        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction, player)));
        objects.addAll(Arrays.asList(FactionConfiguration.PLAYER_PLACEHOLDERS(player)));
        objects.addAll(Arrays.asList(FactionConfiguration.TARGET_PLACEHOLDERS(target)));

        return objects.toArray();
    }

    public static Object[] PLAYER_MEMBER_TARGET_FACTION_PLACEHOLDERS(Player player, FactionPermission.FactionMember member, OfflinePlayer target, Faction faction) {
        List<Object> objects = new ArrayList<>();

        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction)));
        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction, player)));
        objects.addAll(Arrays.asList(FactionConfiguration.MEMBER_PLACEHOLDERS(member)));
        objects.addAll(Arrays.asList(FactionConfiguration.PLAYER_PLACEHOLDERS(player)));
        objects.addAll(Arrays.asList(FactionConfiguration.TARGET_PLACEHOLDERS(target)));

        return objects.toArray();
    }

    public static Object[] MEMBER_PLAYER_FACTION_PLACEHOLDERS(Player player, FactionPermission.FactionMember member, Faction faction) {
        List<Object> objects = new ArrayList<>();

        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction)));
        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction, player)));
        objects.addAll(Arrays.asList(FactionConfiguration.PLAYER_PLACEHOLDERS(player)));
        objects.addAll(Arrays.asList(FactionConfiguration.MEMBER_PLACEHOLDERS(member)));

        return objects.toArray();
    }

    public static Object[] MEMBER_FACTION_PLACEHOLDERS(FactionPermission.FactionMember member, Faction faction) {
        List<Object> objects = new ArrayList<>();

        objects.addAll(Arrays.asList(FactionConfiguration.FACTION_PLACEHOLDERS(faction)));
        objects.addAll(Arrays.asList(FactionConfiguration.MEMBER_PLACEHOLDERS(member)));

        return objects.toArray();
    }

}
