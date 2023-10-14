package dev.lbuddyboy.samurai.custom.deepdark.listener;

import dev.lbuddyboy.flash.util.StringUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.airdrop.AirDropReward;
import dev.lbuddyboy.samurai.custom.airdrop.AirdropHandler;
import dev.lbuddyboy.samurai.custom.deepdark.command.DeepDarkCommand;
import dev.lbuddyboy.samurai.custom.deepdark.entity.DarkEntity;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.custom.pets.IPet;
import dev.lbuddyboy.samurai.custom.pets.event.PetActivateEvent;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class DeepDarkListener implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Warden warden)) return;
        if (!warden.hasMetadata("DARK_ENTITY")) return;

        DarkEntity entity = Samurai.getInstance().getDeepDarkHandler().getDarkEntity();
        Map<UUID, List<LootTableItem>> items = new HashMap<>();
        LootTable table = Samurai.getInstance().getDeepDarkHandler().getLootTable();
        List<UUID> damagers = entity.getDamagers();
        UUID one = damagers.size() >= 1 ? damagers.get(0) : null;
        UUID two = damagers.size() >= 2 ? damagers.get(1) : null;
        UUID three = damagers.size() >= 3 ? damagers.get(2) : null;

        event.getDrops().clear();
        event.setDroppedExp(0);

        if (one != null && Bukkit.getPlayer(one) != null) {
            Player player = Bukkit.getPlayer(one);

            items.put(player.getUniqueId(), table.open(player, 2, 4));
        }

        if (two != null && Bukkit.getPlayer(two) != null) {
            Player player = Bukkit.getPlayer(two);

            items.put(player.getUniqueId(), table.open(player, 1, 4));
        }

        if (three != null && Bukkit.getPlayer(three) != null) {
            Player player = Bukkit.getPlayer(three);

            items.put(player.getUniqueId(), table.open(player, 1, 3));
        }

        for (String s : Samurai.getInstance().getDeepDarkHandler().getConfig().getStringList("messages.slain")) {
            Bukkit.broadcastMessage(CC.translate(s,
                    "%slayer%", (event.getEntity().getKiller() == null ? event.getEntity().getKiller().getName() : "N/A"),
                    "%top-damager-1%", (one != null ? UUIDUtils.name(one) : "None"),
                    "%top-damager-2%", (two != null ? UUIDUtils.name(two) : "None"),
                    "%top-damager-3%", (three != null ? UUIDUtils.name(three) : "None"),
                    "%top-damage-1%", (one != null ? entity.getDamage().get(one).intValue() : "0"),
                    "%top-damage-2%", (two != null ? entity.getDamage().get(two).intValue() : "0"),
                    "%top-damage-3%", (three != null ? entity.getDamage().get(three).intValue() : "0"),
                    "%top-rewards-1%", (one != null ? StringUtils.join(items.get(one).stream().map(LootTableItem::getDisplayName).collect(Collectors.toList()), ", ") : "No reward"),
                    "%top-rewards-2%", (two != null ? StringUtils.join(items.get(two).stream().map(LootTableItem::getDisplayName).collect(Collectors.toList()), ", ") : "No reward"),
                    "%top-rewards-3%", (three != null ? StringUtils.join(items.get(three).stream().map(LootTableItem::getDisplayName).collect(Collectors.toList()), ", ") : "No reward")
            ));
        }

        entity.getBossBar().removeAll();

        Samurai.getInstance().getDeepDarkHandler().setDarkEntity(null);
    }

    @EventHandler
    public void onEntityDamageProjectile(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile && event.getEntity() instanceof Warden warden) {
            DarkEntity entity = Samurai.getInstance().getDeepDarkHandler().getDarkEntity();
            if (entity == null) return;
            if (entity.getWarden() != warden) return;

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Warden warden)) return;
        if (!(event.getDamager() instanceof Player player)) return;

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot attack the deep dark boss with pvp timer."));
            return;
        }

        if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot attack the deep dark boss with sotw timer."));
            return;
        }

        DarkEntity entity = Samurai.getInstance().getDeepDarkHandler().getDarkEntity();

        entity.getDamage().put(player.getUniqueId(), entity.getDamage().getOrDefault(player.getUniqueId(), 0D) + event.getDamage());
    }

    @EventHandler
    public void onNerfDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Warden warden)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (SOTWCommand.isSOTWTimer() && !SOTWCommand.hasSOTWEnabled(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        event.setDamage(event.getDamage() / 2);
    }

    @EventHandler
    public void onBuffDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;
        if (!(event.getEntity() instanceof Warden victim)) return;

        event.setDamage(event.getDamage() / 2);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getHand() == null) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!(Samurai.getInstance().getDeepDarkHandler().getSpawnItem().isSimilar(item))) return;

        event.setCancelled(true);

        if (event.getClickedBlock() == null) {
            player.sendMessage(CC.translate("&cPlease click the spawn catalyst in the deep dark. &7/t i DeepDark"));
            return;
        }

        Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());

        if (teamAt == null) {
            player.sendMessage(CC.translate("&cYou have to spawn this in the deep dark. &7/t i DeepDark"));
            return;
        }

        if (!teamAt.getName().equalsIgnoreCase("DeepDark")) {
            player.sendMessage(CC.translate("&cYou have to spawn this in the deep dark. &7/t i DeepDark"));
            return;
        }

        if (Samurai.getInstance().getDeepDarkHandler().getDarkEntity() != null) {
            player.sendMessage(CC.translate("&cThere is already a deep dark warden spawned."));
            return;
        }

        if (teamAt.getHQ() == null) {
            player.sendMessage(CC.translate("&cYou need to place this at least 5 blocks near the Deep Dark headquarters."));
            return;
        }

        if (teamAt.getHQ().distance(event.getClickedBlock().getLocation()) > 5) {
            player.sendMessage(CC.translate("&cYou need to place this at least 5 blocks near the Deep Dark headquarters."));
            return;
        }

        Samurai.getInstance().getDeepDarkHandler().spawnEntity(player, event.getClickedBlock().getLocation());
        ItemUtils.removeAmount(player.getInventory(), item, 1);
    }

}
