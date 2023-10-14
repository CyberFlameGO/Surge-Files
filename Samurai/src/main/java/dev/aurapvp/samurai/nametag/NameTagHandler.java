package dev.aurapvp.samurai.nametag;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.nametag.listener.NameTagListener;
import dev.aurapvp.samurai.nametag.packet.ScoreboardTeamPacket;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;
import net.minecraft.EnumChatFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class NameTagHandler implements IModule {

    private final NameTag invisible;
    private final Map<UUID, NameTag> tags;

    public NameTagHandler() {
        this.tags = new ConcurrentHashMap<>();
        this.invisible = new NameTag("INVIS", "invis", "", EnumChatFormat.valueOf("WHITE"));
    }

    @Override
    public String getId() {
        return "name-tags";
    }

    @Override
    public void load(Samurai plugin) {
        plugin.getServer().getPluginManager().registerEvents(new NameTagListener(), plugin);
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public void reload() {
        for (Player target : Bukkit.getOnlinePlayers()) {
            updatePlayers(target);
        }
    }

    public void updatePlayers(Player... targets) {
        updatePlayersForViewers(Arrays.asList(targets), new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    public void updatePlayer(Player target) {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            update(target, viewer);
        }
    }

    public void updateOnline(Player viewer) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            update(target, viewer);
        }
    }

    /*
    More optimized a resource friendly usage.
     */

    public void updatePlayersForViewers(List<Player> targets, List<Player> viewers) {
        for (Player viewer : viewers) {
            for (Player target : targets) {
                update(target, viewer);
            }
        }
    }

    public void update(Player target, Player viewer) {
        NameTag tag = getTag(target, viewer);

        new ScoreboardTeamPacket(tag.getName(), Collections.singletonList(target.getName()), 3).sendToPlayer(viewer);

        tags.put(target.getUniqueId(), tag);
    }

    public void playerJoin(Player player) {
        for (NameTag tag : tags.values()) {
            tag.getTeamPacket().sendToPlayer(player);
        }
        updatePlayer(player);
        updateOnline(player);
    }

    public void playerQuit(Player player) {
        this.tags.remove(player.getUniqueId());
    }

/*    public Tag getInvisible(Player target, EnumChatFormat format) {
        return ;
    }*/

    public NameTag getTag(Player target, Player viewer) {
        Faction targetFaction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(target.getUniqueId());
        Faction viewerFaction = Samurai.getInstance().getFactionHandler().getFactionByPlayerUUID(viewer.getUniqueId());
        String prefix = "", suffix = "";
        ChatColor color = ChatColor.RED;
        boolean friendly = false;

        if (targetFaction != null) {
            if (targetFaction.getFactionMember(viewer.getUniqueId()) != null) {
                color = FactionConfiguration.RELATION_COLOR_FACTION.getChatColor();
                friendly = true;
            }
            if (viewerFaction != null && targetFaction.getAlliances().contains(viewerFaction.getUniqueId())) {
                color = FactionConfiguration.RELATION_COLOR_ALLY.getChatColor();
                friendly = true;
            }
        }

        if (friendly && target.hasPotionEffect(PotionEffectType.INVISIBILITY)) return getInvisible(color);

        return getTag(prefix, suffix, EnumChatFormat.valueOf(color.name()), friendly, friendly && target.hasPotionEffect(PotionEffectType.INVISIBILITY));
    }

    public NameTag getInvisible(ChatColor color) {
        NameTag tag = getInvisible().clone();

        tag.setChatFormat(EnumChatFormat.valueOf(color.name()));

        return tag;
    }

    public NameTag getTag(String prefix, String suffix, EnumChatFormat chatFormat, boolean friendly, boolean friendlyInvis) {
        for (NameTag tag : tags.values()) {
            if (tag.getSuffix().equals(suffix) && tag.getPrefix().equals(prefix) && tag.getChatFormat() == chatFormat)
                return tag;
        }

        NameTag tag = new NameTag(String.valueOf(tags.size()), prefix, suffix, chatFormat, friendly, friendlyInvis);
        ScoreboardTeamPacket addPacket = tag.getTeamPacket();
        for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
            addPacket.sendToPlayer(player);
        }

        return tag;
    }

}
