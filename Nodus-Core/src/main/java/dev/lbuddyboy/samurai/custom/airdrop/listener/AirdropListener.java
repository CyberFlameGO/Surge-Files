package dev.lbuddyboy.samurai.custom.airdrop.listener;

import dev.lbuddyboy.samurai.custom.airdrop.AirDropReward;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.airdrop.AirdropHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.*;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/02/2022 / 11:04 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.airdrop.listener
 */
public class AirdropListener implements Listener {

    private final List<Location> airdropLocs = new ArrayList<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        AirdropHandler handler = Samurai.getInstance().getAirdropHandler();

        if (event.getItemInHand().getType() == Material.DISPENSER && event.getItemInHand().hasItemMeta() && event.getItemInHand().getItemMeta().hasDisplayName() && CC.translate(event.getItemInHand().getItemMeta().getDisplayName()).equals(handler.getItem().getItemMeta().getDisplayName())) {
            Team team = Samurai.getInstance().getTeamHandler().getTeam(player);
            Team teamAt = LandBoard.getInstance().getTeam(event.getBlockPlaced().getLocation());

            if (teamAt != null && team == null) {
                event.setCancelled(true);
                player.sendMessage(CC.translate("&cYou cannot place that here."));
                return;
            }

            if (teamAt != null && teamAt != team) {
                event.setCancelled(true);
                player.sendMessage(CC.translate("&cYou cannot place that here."));
                return;
            }

            if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.AIR_DROPS)) {
                event.setCancelled(true);
                player.sendMessage(CC.translate("&cThis feature is currently disabled."));
                return;
            }

            if (airdropLocs.contains(event.getBlock().getLocation())) {
                event.setCancelled(true);
                player.sendMessage(CC.translate("&cThis location is currently occupied by an airdrop."));
                return;
            }

            airdropLocs.add(event.getBlock().getLocation());

            InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItemInHand(), 1);

            final int[] i = {0};

            new BukkitRunnable() {
                @Override
                public void run() {

                    if (i[0] <= 3) {
                        player.sendMessage(CC.translate(AirdropHandler.PREFIX + " &fYour &3airdrop &fwill drop in &3" + (3 - i[0]) + "..."));
                        Firework fireWork = (Firework) player.getWorld().spawnEntity(player.getLocation().add(0, 3, 0), EntityType.FIREWORK);
                        FireworkMeta fwMeta = fireWork.getFireworkMeta();

                        fwMeta.addEffect(FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.BALL).
                                with(FireworkEffect.Type.BALL_LARGE)
                                .with(FireworkEffect.Type.STAR).withColor(Color.ORANGE).withColor(Color.YELLOW).withFade(Color.PURPLE).withFade(Color.RED).build());

                        fireWork.setFireworkMeta(fwMeta);

                        ++i[0];
                        return;
                    }

                    int max = 9;
                    int opened = 0;

                    boolean finished = false;

                    String name = player.getName();

                    event.getBlock().setType(Material.DISPENSER);

                    Dispenser dispenser = (Dispenser) event.getBlock().getState();
                    AirdropHandler handler = Samurai.getInstance().getAirdropHandler();

                    List<AirDropReward> rewards = new ArrayList<>();
                    for (int i = 0; i < 500; i++) {
                        List<AirDropReward> airDropRewards = new ArrayList<>(handler.getLootTable());

                        Collections.shuffle(airDropRewards);

                        for (AirDropReward reward : airDropRewards) {
                            if (reward.getChance() < ThreadLocalRandom.current().nextDouble(100)) continue;

                            if (reward.getCommand() != null && !reward.getCommand().isEmpty()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.getCommand().replaceAll("%player%", name));
                            }
                            if (reward.isBroadcast()) {
                                Bukkit.broadcastMessage(CC.translate(AirdropHandler.PREFIX + " &3" + name + " &fhas just &6&lWON&f " + reward.getDisplayName() + " &ffrom an &3&lAIRDROP&f!"));
                            }
                            rewards.add(reward);
                            opened++;
                            if (opened >= max) {
                                opened = 0;
                                finished = true;
                                break;
                            }
                        }
                        if (finished) {
                            break;
                        }
                    }

                    for (AirDropReward reward : rewards) {
                        if (reward.getCommand().isEmpty()) {
                            dispenser.getInventory().addItem(reward.getStack());
                        }
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            airdropLocs.remove(event.getBlock().getLocation());
                            event.getBlock().breakNaturally();
                        }
                    }.runTaskLater(Samurai.getInstance(), 20 * 10);

                    cancel();

                }
            }.runTaskTimer(Samurai.getInstance(), 20, 20);

            event.setCancelled(true);

        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        locations.remove(event.getPlayer().getUniqueId());
    }

    public static final Cooldown countdown = new Cooldown();
    private static final Map<UUID, Location> locations = new HashMap<>();

}
