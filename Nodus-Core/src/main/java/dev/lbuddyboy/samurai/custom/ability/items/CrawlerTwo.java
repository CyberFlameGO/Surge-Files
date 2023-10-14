package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class CrawlerTwo extends AbilityItem implements Listener {

    public CrawlerTwo() {
        super("CrawlerTwo");

        this.name = "crawler-two";
    }

    private static final Map<UUID, List<Location>> crawlerLocations = new HashMap<>();
    public static List<String> blockedMaterials = Arrays.asList("BEDROCK", "CHEST", "_GATE", "MOB_SPAWNER", "_DOOR", "WATER", "LAVA");

    @Override
    public long getCooldownTime() {
        return this.cooldownSeconds;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(this.material)
                .name(getName())
                .setLore(CC.translate(this.lore)).modelData(10).build();
    }

    @Override
    public String getName() {
        return CC.translate(this.displayName);
    }

    @Override
    public int getAmount() {
        return 3;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Location loc = player.getEyeLocation();
        for (String mat : blockedMaterials) {
            if (loc.getBlock().getType().name().contains(mat)) {
                player.sendMessage(CC.translate("&cCould not use this ability item due to a blocked material being on your body."));
                return false;
            }
        }

        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());

        List<Location> locations = crawlerLocations.getOrDefault(player.getUniqueId(), new ArrayList<>());

        locations.add(loc);
        loc.getBlock().setType(Material.GLASS);

        MessageConfiguration.CRAWLER_CLICKER.sendListMessage(player, "%ability-name%", this.getName());
        player.setMetadata("crawler-two", new FixedMetadataValue(Samurai.getInstance(), true));

        new BukkitRunnable() {
            @Override
            public void run() {
                crawlerLocations.get(player.getUniqueId()).forEach(l -> l.getBlock().setType(Material.AIR));
                player.removeMetadata("crawler-two", Samurai.getInstance());
                crawlerLocations.remove(player.getUniqueId());
            }
        }.runTaskLater(Samurai.getInstance(), 20 * 8);

        crawlerLocations.put(player.getUniqueId(), locations);
        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!player.hasMetadata("crawler-two")) return;

        Location to = event.getTo();

        if (to == null) return;

        Location location = player.getEyeLocation().clone().add(0, 1, 0);

        if (location.getBlock().getType() != Material.AIR) return;

        List<Location> locations = crawlerLocations.getOrDefault(player.getUniqueId(), new ArrayList<>());
        if (locations.contains(location)) return;

        locations.add(location);
        location.getBlock().setType(Material.GLASS);

        crawlerLocations.put(player.getUniqueId(), locations);
    }

}
