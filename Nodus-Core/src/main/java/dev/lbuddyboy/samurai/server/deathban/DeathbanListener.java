package dev.lbuddyboy.samurai.server.deathban;

import com.mongodb.BasicDBObject;
import dev.lbuddyboy.samurai.commands.staff.EOTWCommand;
import dev.lbuddyboy.samurai.commands.staff.LastInvCommand;
import dev.lbuddyboy.samurai.listener.FoxListener;
import dev.lbuddyboy.samurai.persist.maps.DeathbanMap;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.samurai.util.*;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DeathbanListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.hasMetadata("gaming")) return;

        LastInvCommand.recordInventory(player);
        EnderpearlCooldownHandler.clearEnderpearlTimer(player);

        insertDeath(player, event.getEntity().getKiller());

        if (!Samurai.getInstance().getServerHandler().isEOTW() && Samurai.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        if (Samurai.getInstance().getInDuelPredicate().test(player)) {
            return;
        }

        boolean shouldBypass = player.isOp();

        if (!shouldBypass) {
            shouldBypass = player.hasPermission("foxtrot.staff");
        }

        if (shouldBypass) {
            Samurai.getInstance().getDeathbanMap().revive(player.getUniqueId());
            return;
        }

        int seconds = (int) Samurai.getInstance().getServerHandler().getDeathban(player);

        final String time = TimeUtils.formatIntoDetailedString(seconds);

        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            player.sendMessage(ChatColor.YELLOW + "Come back tomorrow for SOTW!");
            player.sendTitle(CC.translate("&c&lYOU DIED!"), ChatColor.YELLOW + "Come back tomorrow for SOTW!");
            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                player.kickPlayer(CC.translate("&cYou died on EOTW, join back on SOTW!"));
            }, 10);
            EOTWCommand.eotwWhitelist.remove(player.getUniqueId());
        } else {
            Samurai.getInstance().getDeathbanMap().deathban(player.getUniqueId(), seconds);

            player.sendMessage(ChatColor.YELLOW + "Come back in " + time + "!");
            player.sendTitle(CC.translate("&c&lYOU DIED!"), ChatColor.YELLOW + "Come back in " + time + "!");
        }

        player.getWorld().strikeLightning(player.getLocation());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }

                if (Samurai.getInstance().getServerHandler().isPreEOTW()) {
                    return;
                }

                player.teleport(Samurai.getInstance().getArenaHandler().getSpawn());
//                player.kickPlayer(ChatColor.YELLOW + "Come back in " + time + "!");
            }
        }.runTaskLater(Samurai.getInstance(), 10L);
    }

    @EventHandler
    public void onCombust(EntityCombustByEntityEvent event) {
        if (event.getCombuster().getType() == EntityType.LIGHTNING) {
            event.setCancelled(true);
            event.setDuration(0);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!Samurai.getInstance().getMapHandler().isKitMap()) {
            Samurai.getInstance().getDiedMap().setActive(event.getPlayer().getUniqueId(), false);
        }
        event.getPlayer().removeMetadata("gaming", Samurai.getInstance());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Samurai.getInstance().getDiedMap().setActive(event.getPlayer().getUniqueId(), false);
        event.getPlayer().removeMetadata("gaming", Samurai.getInstance());
    }

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        DeathbanMap deathbanMap = Samurai.getInstance().getDeathbanMap();
        UUID uuid = event.getUniqueId();

        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                Player player = Bukkit.getPlayer(uuid);

                if (player == null) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "You cannot join whilst EOTW has already started.");
                    return;
                }

                if (!EOTWCommand.eotwWhitelist.contains(player.getUniqueId()) && !player.hasPermission("foxtrot.staff")) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + "You cannot join whilst EOTW has already started.");
                }

            }, 2L);
            return;
        }

        if (Samurai.getInstance().getMapHandler().isKitMap()) return;

        if (deathbanMap.isDeathbanned(uuid)) {

            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.getInventory().clear();
                        player.teleport(Samurai.getInstance().getArenaHandler().getSpawn());
                        Samurai.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
                    }
                }
            }.runTaskLater(Samurai.getInstance(), 2L);

            if (Samurai.getInstance().getDiedMap().isActive(uuid)) {
                Samurai.getInstance().getDiedMap().setActive(uuid, false);
            }

/*            int friendLives = Samurai.getInstance().getFriendLivesMap().getLives(event.getUniqueId());
            if (friendLives > 0) {
                Samurai.getInstance().getFriendLivesMap().setLives(event.getUniqueId(), friendLives - 1);
                deathbanMap.revive(event.getUniqueId());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Player player = Bukkit.getPlayer(event.getUniqueId());
                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have used a Friend Life to revive yourself!");
                        }
                    }
                }.runTaskLaterAsynchronously(Samurai.getInstance(), 2L);


                if (Samurai.getInstance().getDiedMap().isActive(uuid)) {
                    Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                        Player player = Bukkit.getPlayer(uuid);

                        if (player == null) {
                            return;
                        }

                        player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
                    }, 2L);

                    Samurai.getInstance().getDiedMap().setActive(uuid, false);
                }

            } else {
                long seconds = (deathbanMap.getDeathban(event.getUniqueId()) - System.currentTimeMillis()) / 1000;
                String message = "You are currently deathbanned! Come back in " + TimeUtils.formatLongIntoDetailedString(seconds) + "!";
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, CC.RED + message);
            }*/
        } else {

            if (Samurai.getInstance().getDiedMap().isActive(uuid)) {
                Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                    Player player = Bukkit.getPlayer(uuid);

                    if (player == null) {
                        return;
                    }

                    player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
                }, 2L);

                Samurai.getInstance().getDiedMap().setActive(uuid, false);
            }

        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (Samurai.getInstance().getDeathbanMap().isDeathbanned(event.getPlayer().getUniqueId())) return;

        event.getPlayer().getInventory().clear();
        event.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());

        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Samurai.getInstance().getServerHandler().getSpectateManager().addSpectator(event.getPlayer());
                }
            }.runTaskLater(Samurai.getInstance(), 2L);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            if (event.getPlayer().hasPermission("foxtrot.staff")) return;

            new BukkitRunnable() {
                @Override
                public void run() {
                    Samurai.getInstance().getServerHandler().getSpectateManager().addSpectator(event.getPlayer());
                }
            }.runTaskLater(Samurai.getInstance(), 2L);
        }
    }

    public static void insertDeath(Player player, Player killer) {
        final BasicDBObject playerDeath = new BasicDBObject();

        playerDeath.put("_id", UUID.randomUUID().toString().substring(0, 7));

        if (player.getKiller() != null) {
            playerDeath.append("healthLeft", (int) player.getKiller().getHealth());
            playerDeath.append("killerUUID", player.getKiller().getUniqueId().toString().replace("-", ""));
            playerDeath.append("killerLastUsername", player.getKiller().getName());

            playerDeath.append("killerArmor", ItemUtils.itemStackArrayToBase64(player.getKiller().getInventory().getArmorContents()));
            playerDeath.append("killerInventory", ItemUtils.itemStackArrayToBase64(player.getKiller().getInventory().getStorageContents()));

            playerDeath.append("killerPing", player.getPing());
            playerDeath.append("killerHunger", player.getKiller().getFoodLevel());
        } else {
            try {
                playerDeath.append("reason", player.getLastDamageCause().getCause().toString());
            } catch (NullPointerException ignored) {
            }
        }

        playerDeath.append("playerArmor", ItemStackSerializer.itemStackArrayToBase64(player.getInventory().getArmorContents()));
        playerDeath.append("playerInventory", ItemStackSerializer.itemStackArrayToBase64(player.getInventory().getStorageContents()));
        playerDeath.append("playerExtras", ItemStackSerializer.itemStackArrayToBase64(player.getInventory().getExtraContents()));

        playerDeath.append("uuid", player.getUniqueId().toString().replace("-", ""));
        playerDeath.append("lastUsername", player.getName());
        playerDeath.append("hunger", player.getFoodLevel());
        playerDeath.append("when", new Date());

        try {
            playerDeath.append("location", LocationSerializer.serialize(player.getLocation()));
        } catch (Exception ignored) {
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

    private static List<ItemStack> armors = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.LEATHER_HELMET),
            new ItemStack(Material.DIAMOND_HELMET),
            new ItemStack(Material.LEATHER_CHESTPLATE),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.LEATHER_LEGGINGS),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.LEATHER_BOOTS),
            new ItemStack(Material.DIAMOND_BOOTS)
    ));

    private static List<ItemStack> inv = new ArrayList<>(Arrays.asList(
            new ItemStack(Material.DIAMOND_SWORD),
            new ItemStack(Material.DIAMOND_PICKAXE),
            new ItemStack(Material.LEATHER, 10),
            new ItemStack(Material.COOKED_BEEF, 7),
            new ItemStack(Material.IRON_BLOCK, 9),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_BOOTS)
    ));

    public static void insertSpoofedDeath(UUID player) {
        final BasicDBObject playerDeath = new BasicDBObject();

        playerDeath.put("_id", UUID.randomUUID().toString().substring(0, 7));

        List<ItemStack> armor = new ArrayList<>(), inventory = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (ThreadLocalRandom.current().nextInt(3) <= 1) {
                armor.add(armors.get(ThreadLocalRandom.current().nextInt(0, armors.size())));
            }
        }
        for (int i = 0; i < 10; i++) {
            if (ThreadLocalRandom.current().nextInt(3) <= 1) {
                inventory.add(inv.get(ThreadLocalRandom.current().nextInt(0, inv.size())));
            }
        }

        playerDeath.append("playerArmor", ItemStackSerializer.itemStackArrayToBase64(armor.toArray(new ItemStack[0])));
        playerDeath.append("playerInventory", ItemStackSerializer.itemStackArrayToBase64(inventory.toArray(new ItemStack[0])));
        playerDeath.append("playerExtras", ItemStackSerializer.itemStackArrayToBase64(inventory.toArray(new ItemStack[0])));

        playerDeath.append("uuid", player.toString().replace("-", ""));
        playerDeath.append("lastUsername", UUIDUtils.name(player));
        playerDeath.append("hunger", 19);
        playerDeath.append("when", new Date());

        try {
            playerDeath.append("location", LocationSerializer.serialize(new Location(Bukkit.getWorlds().get(0), ThreadLocalRandom.current().nextInt(100, 800), Bukkit.getWorlds().get(0).getSpawnLocation().getY(), ThreadLocalRandom.current().nextInt(100, 800))));
        } catch (Exception ignored) {
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths").insert(playerDeath);
            }

        }.runTaskAsynchronously(Samurai.getInstance());
    }

}