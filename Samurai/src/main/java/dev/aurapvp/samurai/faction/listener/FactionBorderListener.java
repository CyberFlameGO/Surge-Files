package dev.aurapvp.samurai.faction.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketWorldBorderCreateNew;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketWorldBorderRemove;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.faction.claim.Claim;
import dev.aurapvp.samurai.util.CC;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import org.bukkit.Color;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FactionBorderListener implements Listener {

    private static Map<UUID, List<Claim>> showedClaims = new ConcurrentHashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        //if (!Samurai.getInstance().getTimerHandler().hasInvincibilityTimer(player)) return;

        List<Claim> claims = Samurai.getInstance().getFactionHandler().getClaimHandler().getClaims(player.getLocation(), 7);

        if (claims.isEmpty()) return;

        for (Claim claim : claims) {
            if (showedClaims.containsKey(player.getUniqueId()) && showedClaims.get(player.getUniqueId()).contains(claim)) continue;

            Faction faction = Samurai.getInstance().getFactionHandler().getFactionByUniqueId(claim.getFaction());

            if (faction == null) continue;
            if (faction.isSystemFaction() && faction.getType() == FactionType.SPAWN) continue;
//            if (faction.getFactionMember(player.getUniqueId()) != null) continue;

            LCPacketWorldBorderCreateNew createNew = new LCPacketWorldBorderCreateNew(claim.getUuid().toString(), player.getWorld().getName(), true, false, true, Color.RED.hashCode(), claim.getCuboid().getLowerX(), claim.getCuboid().getLowerZ(), claim.getCuboid().getUpperX(), claim.getCuboid().getUpperZ());
            LunarClientAPI.getInstance().sendPacket(player, createNew);
        }

        if (showedClaims.containsKey(player.getUniqueId())) {
            List<Claim> existing = new ArrayList<>(showedClaims.get(player.getUniqueId()));
            for (Claim claim : existing) {
                if (claims.contains(claim)) continue;

                if (LunarClientAPI.getInstance().isRunningLunarClient(player)) {
                    LCPacketWorldBorderRemove createNew = new LCPacketWorldBorderRemove(claim.getUuid().toString());

                    LunarClientAPI.getInstance().sendPacket(player, createNew);
                }
            }
        }

        showedClaims.put(player.getUniqueId(), claims);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        showedClaims.remove(event.getPlayer().getUniqueId());
    }

}
