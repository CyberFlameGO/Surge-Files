package dev.lbuddyboy.samurai.custom.ability;

import com.comphenix.protocol.ProtocolLibrary;
import dev.lbuddyboy.samurai.custom.ability.items.*;
import dev.lbuddyboy.samurai.custom.ability.items.exotic.KitDisabler;
import dev.lbuddyboy.samurai.custom.ability.items.exotic.NinjaStarTwo;
import dev.lbuddyboy.samurai.custom.ability.items.exotic.RestraintBone;
import dev.lbuddyboy.samurai.custom.ability.items.retired.ExoticBone;
import dev.lbuddyboy.samurai.custom.ability.items.retired.Levitator;
import dev.lbuddyboy.samurai.custom.ability.offhand.type.Damage;
import dev.lbuddyboy.samurai.custom.ability.offhand.type.Debuff;
import dev.lbuddyboy.samurai.custom.ability.profile.ProfileListener;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.items.bard.PortableBard;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

@Getter
public final class AbilityItemHandler implements Listener {

    private final List<AbilityItem> abilityItems = new ArrayList<>(), offHandItems = new ArrayList<>();
    private final Cooldown GLOBAL_COOLDOWN;
    private File ABILITY_FOLDER, OFFHAND_FOLDER;

    public AbilityItemHandler() {
        this.GLOBAL_COOLDOWN = new Cooldown();
        this.loadDirectories();
        this.abilityItems.addAll(Arrays.asList(
                new AntiBuildStick(),
                new GuardianAngel(),
                new RestraintBone(),
                new Invisibility(),
                new KitDisabler(),
                new FocusMode(),
                new LevitatorTwo(),
                new RageMode(),
                new NinjaStarTwo(),
                new RageBall(),
                new Rocket(),
                new PortableBard(),
                new SupportGoats(),
                new SwitcherBall(),
                new TimeWarpTwo(),
                new CrawlerTwo(),
                new EffectStealer(),
                new Scrambler()
        ));
        this.offHandItems.addAll(Arrays.asList(
                new Damage(),
                new Debuff()
        ));

        this.abilityItems.forEach(ai -> ai.reload(this.ABILITY_FOLDER));
        this.offHandItems.forEach(ai -> ai.reload(this.OFFHAND_FOLDER));

        abilityItems.forEach(partnerPackage -> {
            if (partnerPackage.getRecipe() != null) {
                Samurai.getInstance().getServer().addRecipe(partnerPackage.getRecipe());
            }
            partnerPackage.loadFromRedis();
            if (partnerPackage instanceof Listener) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) partnerPackage, Samurai.getInstance());
            }
        });

        offHandItems.forEach(partnerPackage -> {
            if (partnerPackage.getRecipe() != null) {
                Samurai.getInstance().getServer().addRecipe(partnerPackage.getRecipe());
            }
            partnerPackage.loadFromRedis();
            if (partnerPackage instanceof Listener) {
                Bukkit.getServer().getPluginManager().registerEvents((Listener) partnerPackage, Samurai.getInstance());
            }
        });

        Bukkit.getServer().getPluginManager().registerEvents(this, Samurai.getInstance());
        Bukkit.getServer().getPluginManager().registerEvents(new ProfileListener(), Samurai.getInstance());

        // clean up cooldown map
        Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            private void onPlayerQuit(PlayerQuitEvent event) {
                GLOBAL_COOLDOWN.cleanUp();
            }
        }, Samurai.getInstance());

        ProtocolLibrary.getProtocolManager().addPacketListener(new Invisibility.InvisibilityPacketAdapter());
    }

    private void loadDirectories() {
        ABILITY_FOLDER = new File(Samurai.getInstance().getDataFolder(), "ability");
        if (!ABILITY_FOLDER.exists()) ABILITY_FOLDER.mkdir();

        OFFHAND_FOLDER = new File(Samurai.getInstance().getDataFolder(), "offhand");
        if (!OFFHAND_FOLDER.exists()) OFFHAND_FOLDER.mkdir();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        AbilityItem heldPackage = null;
        for (AbilityItem partnerPackage : abilityItems) {
            if (partnerPackage.isPartnerItem(item)) {
                heldPackage = partnerPackage;
            }
        }

        if (heldPackage == null) {
            return;
        }

        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (heldPackage.isOnCooldown(player)) {
                player.sendMessage(heldPackage.getCooldownMessage(player));
            }
        } else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!heldPackage.canUse(player, event)) return;

            if (!(heldPackage instanceof PortableBard) && heldPackage.isOnCooldown(player)) {
                event.setCancelled(true);
                event.setUseItemInHand(Event.Result.DENY);
                player.sendMessage(heldPackage.getCooldownMessage(player));
                return;
            }

            if (heldPackage instanceof PortableBard) {
                if (heldPackage.onUse(event)) {
                    if (Samurai.getInstance().getBattlePassHandler() != null) {
                        Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                            progress.setPartnerItemsUsed(progress.getPartnerItemsUsed() + 1);
                            progress.requiresSave();

                            Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                        });
                    }
                }
                return;
            }

            if (heldPackage.onUse(event)) {
                if (Samurai.getInstance().getBattlePassHandler() != null) {
                    Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                        progress.setPartnerItemsUsed(progress.getPartnerItemsUsed() + 1);
                        progress.requiresSave();

                        Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                    });
                }
            }
        }

    }
//
//    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
//    private void onPlayerHitPlayer(EntityDamageByEntityEvent event) {
//        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
//            FixedMetadataValue metadata = new FixedMetadataValue(Foxtrot.getInstance(),
//                    new Pair<>(event.getDamager().getUniqueId(), Instant.now())
//            );
//            event.getEntity().setMetadata("last_attack", metadata);
//        }
//    }

    public AbilityItem getPartnerPackageByName(String name) {
        for (AbilityItem partnerPackage : abilityItems) {
            String packageName = ChatColor.stripColor(partnerPackage.getName())
                    .toLowerCase()
                    .replace(" ", "_")
                    .replace("'", "");
            if (name.equalsIgnoreCase(packageName))
                return partnerPackage;
        }
        return null;
    }

    public AbilityItem getOffHandByName(String name) {
        for (AbilityItem partnerPackage : this.offHandItems) {
            String packageName = ChatColor.stripColor(partnerPackage.getName())
                    .toLowerCase()
                    .replace(" ", "_")
                    .replace("'", "");
            if (name.equalsIgnoreCase(packageName))
                return partnerPackage;
        }
        return null;
    }

}
