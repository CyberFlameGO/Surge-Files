package dev.lbuddyboy.samurai.nametag.impl;

import com.lunarclient.bukkitapi.LunarClientAPI;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.HunterClass;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.RangerClass;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.bounty.Bounty;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.nametag.NametagInfo;
import dev.lbuddyboy.samurai.nametag.NametagProvider;
import dev.lbuddyboy.samurai.nametag.packet.ScoreboardTeamPacketMod;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import net.minecraft.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FoxtrotNametagProvider extends NametagProvider {

    public FoxtrotNametagProvider() {
        super("Foxtrot Provider", 5);
    }

    @Override
    public NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.NORMAL_NAMETAGS)) {
            return createNametag(toRefresh, "" + ChatColor.RED, "", EnumChatFormat.m);
        }

        boolean invis = false;
        if (toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY) && refreshFor.hasMetadata("modmode")) {
            invis = true;
        }

        Team viewerTeam = Samurai.getInstance().getTeamHandler().getTeam(refreshFor);
        NametagInfo nametagInfo = null;

        boolean isMember = false;
        boolean isAlly = false;

        if (viewerTeam != null) {
            if (viewerTeam.isMember(toRefresh.getUniqueId())) {
                nametagInfo = createNametag(toRefresh, "" + ChatColor.GREEN, "", EnumChatFormat.b("GREEN"));
                isMember = true;
            } else if (viewerTeam.isAlly(toRefresh.getUniqueId())) {
                nametagInfo = createNametag(toRefresh, "" + ChatColor.BLUE, "", EnumChatFormat.b("BLUE"));
                isAlly = true;
            }
        }

/*        if (isMember && toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY) || isAlly && toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            return FrozenNametagHandler.getInvisible(nametagInfo.getColor());
        }*/

        // If we already found something above they override these, otherwise we can do these checks.
        if (RangerClass.getMarkedPlayers().containsKey(toRefresh.getUniqueId()) && RangerClass.getMarkedPlayers().get(toRefresh.getUniqueId()) > System.currentTimeMillis()) {
            nametagInfo = createNametag(toRefresh, "", "", EnumChatFormat.b("BLUE"));
        } else if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && ArcherClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
            nametagInfo = createNametag(toRefresh, "" + ChatColor.YELLOW, "", EnumChatFormat.b("YELLOW"));
        } else if (HunterClass.getMarkedPlayers().containsKey(toRefresh.getName()) && HunterClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
            nametagInfo = createNametag(toRefresh, "" + ChatColor.DARK_PURPLE, "", EnumChatFormat.b("DARK_PURPLE"));
        } else if (viewerTeam != null && viewerTeam.getFocused() != null && viewerTeam.getFocused().equals(toRefresh.getUniqueId())) {
            nametagInfo = createNametag(toRefresh, "" + ChatColor.LIGHT_PURPLE, "", EnumChatFormat.b("LIGHT_PURPLE"));
        } else if (viewerTeam != null && viewerTeam.getFocusedTeam() != null && viewerTeam.getFocusedTeam().isMember(toRefresh.getUniqueId())) {
            nametagInfo = createNametag(toRefresh, "" + ChatColor.LIGHT_PURPLE, "", EnumChatFormat.b("LIGHT_PURPLE"));
        } else if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(toRefresh.getUniqueId())) {
            nametagInfo = createNametag(toRefresh, "" + ChatColor.GOLD, "", EnumChatFormat.b("GOLD"));
        } else if (Samurai.getInstance().getPvPTimerMap().hasTimer(toRefresh.getUniqueId())) {
            nametagInfo = createNametag(toRefresh, "" + ChatColor.GOLD, "", EnumChatFormat.b("GOLD"));
        }

        // You always see yourself as green.
        if (refreshFor == toRefresh) {
            nametagInfo = createNametag(toRefresh, "" + ChatColor.GREEN, "", EnumChatFormat.b("GREEN"));
        }

/*        if (invis) {
            nametagInfo = FrozenNametagHandler.getInvisible(nametagInfo == null ? EnumChatFormat.b("RED") : nametagInfo.getColor());
        }*/

        return (nametagInfo == null ? createNametag(toRefresh, "" + ChatColor.RED, "", EnumChatFormat.b("RED")) : nametagInfo);
    }

    private static NametagInfo createNametag(Player displayed, String prefix, String suffix, EnumChatFormat color) {
        String invis = ModUtils.isInvisible(displayed) ? ChatColor.GRAY + "*" : "";
        prefix = invis + prefix;

        if (Samurai.getInstance().getMapHandler().isKitMap()) {
            Bounty bounty = Samurai.getInstance().getMapHandler().getBountyManager().getBounty(displayed);

            if (bounty != null) {
                suffix += " " + ChatColor.GOLD + bounty.getShards() + "♦";
            }
        }

        Map<Integer, UUID> placesMap = Samurai.getInstance().getMapHandler().getStatsHandler() != null ? Samurai.getInstance().getMapHandler().getStatsHandler().getTopKills() : null;
        if (placesMap == null) {
            return createNametagNoPlayer(prefix, suffix, color);
        }

        int place = placesMap.size() == 3 ? displayed.getUniqueId().equals(placesMap.get(1)) ? 1 : displayed.getUniqueId().equals(placesMap.get(2)) ? 2 : displayed.getUniqueId().equals(placesMap.get(3)) ? 3 : 99 : 99;
        if (place == 99) {
            return createNametagNoPlayer(prefix, suffix, color);
        }

        String coloredPrefix = ChatColor.translateAlternateColorCodes('&', place == 1 ? "&8[&6#1&8] " : place == 2 ? "&8[&7#2&8] " : "&8[&f#3&8] ");

        return createNametagNoPlayer(coloredPrefix + prefix, suffix, color);
    }

    public static List<String> updateLunarTag(Player toRefresh, Player refreshFor) {
        boolean runningLunar = Bukkit.getPluginManager().getPlugin("LunarClient-API") != null;
        List<String> tags = new ArrayList<>();

        if (FoxtrotConfiguration.LUNAR_CLIENT_NAMETAGS.getBoolean()) {

            if (toRefresh.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                if (runningLunar) LunarClientAPI.getInstance().resetNametag(toRefresh, refreshFor);
                return tags;
            }

            Team refreshTeam = Samurai.getInstance().getTeamHandler().getTeam(toRefresh);
            Team refreshForTeam = Samurai.getInstance().getTeamHandler().getTeam(refreshFor);

            if (toRefresh.hasMetadata("modmode")) {
                tags.add(CC.translate("&3[Mod Mode]"));
            }
            ChatColor color;
            if (refreshTeam != null) {
                if (refreshForTeam != null && refreshTeam == refreshForTeam) {
                    color = ChatColor.GREEN;
                } else if (refreshTeam.getAllies() != null && refreshForTeam != null && refreshTeam.getAllies().contains(refreshForTeam.getUniqueId())) {
                    color = ChatColor.AQUA;
                } else {
                    color = ChatColor.RED;
                }
                tags.add(ChatColor.GRAY + "[" + color + refreshTeam.getName() + ChatColor.GRAY + " " + SymbolUtil.STICK + " " + refreshTeam.getDTRColor() + refreshTeam.getDTR() + refreshTeam.getDTRSuffix() + ChatColor.GRAY + "]");
            }

            Team viewerTeam = Samurai.getInstance().getTeamHandler().getTeam(refreshFor);
            ChatColor chatColor = ChatColor.RED;
            String prefix = "", placePrefix = "", suffix = "";

            boolean isMember = false;
            boolean isAlly = false;

            if (viewerTeam != null) {
                if (viewerTeam.isMember(toRefresh.getUniqueId())) {
                    chatColor = ChatColor.GREEN;
                    isMember = true;
                } else if (viewerTeam.isAlly(toRefresh.getUniqueId())) {
                    chatColor = ChatColor.BLUE;
                    isAlly = true;
                }
            }

            if (!isMember && !isAlly) {
                if (RangerClass.getMarkedPlayers().containsKey(toRefresh.getUniqueId()) && RangerClass.getMarkedPlayers().get(toRefresh.getUniqueId()) > System.currentTimeMillis()) {
                    chatColor = ChatColor.BLUE;
                } else if (ArcherClass.getMarkedPlayers().containsKey(toRefresh.getName()) && ArcherClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
                    chatColor = ChatColor.YELLOW;
                } else if (HunterClass.getMarkedPlayers().containsKey(toRefresh.getName()) && HunterClass.getMarkedPlayers().get(toRefresh.getName()) > System.currentTimeMillis()) {
                    chatColor = ChatColor.DARK_PURPLE;
                } else if (viewerTeam != null && viewerTeam.getFocused() != null && viewerTeam.getFocused().equals(toRefresh.getUniqueId())) {
                    chatColor = ChatColor.LIGHT_PURPLE;
                } else if (viewerTeam != null && viewerTeam.getFocusedTeam() != null && viewerTeam.getFocusedTeam().isMember(toRefresh.getUniqueId())) {
                    chatColor = ChatColor.LIGHT_PURPLE;
                } else if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(toRefresh.getUniqueId())) {
                    chatColor = ChatColor.GOLD;
                } else if (Samurai.getInstance().getPvPTimerMap().hasTimer(toRefresh.getUniqueId())) {
                    chatColor = ChatColor.GOLD;
                } else if (toRefresh == refreshFor) {
                    chatColor = ChatColor.GREEN;
                }
            }

            String invis = ModUtils.isInvisible(toRefresh) ? ChatColor.GRAY + "*" : "";
            prefix = invis + prefix;

            if (Samurai.getInstance().getMapHandler().isKitMap()) {
                Bounty bounty = Samurai.getInstance().getMapHandler().getBountyManager().getBounty(toRefresh);

                if (bounty != null) {
                    suffix += " " + ChatColor.GOLD + bounty.getShards() + "♦";
                }
            }

            Map<Integer, UUID> placesMap = Samurai.getInstance().getMapHandler().getStatsHandler() != null ? Samurai.getInstance().getMapHandler().getStatsHandler().getTopKills() : null;
            if (placesMap != null) {
                int place = placesMap.size() == 3 ? toRefresh.getUniqueId().equals(placesMap.get(1)) ? 1 : toRefresh.getUniqueId().equals(placesMap.get(2)) ? 2 : toRefresh.getUniqueId().equals(placesMap.get(3)) ? 3 : 99 : 99;
                if (place != 99) {
                    placePrefix += ChatColor.translateAlternateColorCodes('&', place == 1 ? "&8[&6#1&8] " : place == 2 ? "&8[&7#2&8] " : "&8[&f#3&8] ");
                }
            }
            tags.add(placePrefix + prefix + chatColor + toRefresh.getName() + suffix);

            if (runningLunar) LunarClientAPI.getInstance().overrideNametag(toRefresh, tags, refreshFor);
        }

        return tags;
    }
}