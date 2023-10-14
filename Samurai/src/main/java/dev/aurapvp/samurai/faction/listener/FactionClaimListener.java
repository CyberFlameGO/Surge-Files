package dev.aurapvp.samurai.faction.listener;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.faction.FactionHandler;
import dev.aurapvp.samurai.faction.claim.ClaimHandler;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import dev.aurapvp.samurai.util.ActionBarAPI;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cuboid;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.text.NumberFormat;
import java.util.*;

public class FactionClaimListener implements Listener {

    @Getter private static final Map<Player, Location> pos1 = new HashMap<>();
    @Getter private static final Map<Player, Location> pos2 = new HashMap<>();
    private static final List<Material> INTERACTABLES = Arrays.asList(
            Material.BEACON,
            Material.ACACIA_FENCE_GATE,
            Material.OAK_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.SPRUCE_FENCE_GATE,
            Material.CRIMSON_FENCE_GATE,
            Material.WARPED_FENCE_GATE,
            Material.ACACIA_TRAPDOOR,
            Material.OAK_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.SPRUCE_TRAPDOOR,
            Material.CRIMSON_TRAPDOOR,
            Material.WARPED_TRAPDOOR,
            Material.ACACIA_DOOR,
            Material.OAK_DOOR,
            Material.DARK_OAK_DOOR,
            Material.JUNGLE_DOOR,
            Material.BIRCH_DOOR,
            Material.SPRUCE_DOOR,
            Material.CRIMSON_DOOR,
            Material.WARPED_DOOR,
            Material.ACACIA_BUTTON,
            Material.OAK_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON,
            Material.IRON_DOOR,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.BARREL,
            Material.FURNACE,
            Material.BREWING_STAND,
            Material.HOPPER,
            Material.CAKE,
            Material.DROPPER,
            Material.DISPENSER,
            Material.STONE_BUTTON,
            Material.ENCHANTING_TABLE,
            Material.CRAFTING_TABLE,
            Material.ANVIL,
            Material.LEVER,
            Material.FIRE
    );

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        Player player = event.getPlayer();
        FactionHandler factionHandler = Samurai.getInstance().getFactionHandler();
        ClaimHandler claimHandler = Samurai.getInstance().getFactionHandler().getClaimHandler();

        if (stack == null) return;
        if (!(stack.isSimilar(Samurai.getInstance().getFactionHandler().getClaimWand()))) return;

        event.setCancelled(true);

        Faction faction = Samurai.getInstance().getFactionHandler().getFactionByPlayer(player);

        if (faction == null) {
            player.getInventory().remove(Samurai.getInstance().getFactionHandler().getClaimWand());
            player.sendMessage(CC.translate(FactionConfiguration.FACTION_NOT_CREATED.getString()));
            return;
        }

        FactionPermission.FactionMember member = faction.getFactionMember(player.getUniqueId());

        if (!member.hasPermission(FactionPermission.CLAIM_LAND)) {
            player.sendMessage(FactionConfiguration.MEMBER_NO_PERMISSION.getString("%permission%", FactionPermission.CLAIM_LAND.getContext()));
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            ClaimHandler.ClaimPurchaseInfo info = claimHandler.purchaseClaim(player);
            if (info != null) {
                Samurai.getInstance().getDynMapImpl().getClaimLayer().upsertMarker(faction);
                faction.sendMessage(FactionConfiguration.LAND_CLAIMED_FACTION.getString(
                        FactionConfiguration.MEMBER_PLAYER_FACTION_PLACEHOLDERS(player, member, faction),
                        "%price%", Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(info.getPrice()),
                        "%size%", info.getSize()
                ));
                return;
            }
            return;
        }

        Block clicked = event.getClickedBlock();

        if (clicked == null) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (pos1.containsKey(player)) claimHandler.removePillar(player, pos1.get(player).clone());

            claimHandler.createPillar(player, clicked.getLocation().clone());
            pos1.put(player, clicked.getLocation());
            for (String s : FactionConfiguration.CLAIM_SET_FIRST.getStringList(
                    "%x%", clicked.getLocation().getBlockX(),
                    "%y%", clicked.getLocation().getBlockY(),
                    "%z%", clicked.getLocation().getBlockZ()
            )) {
                player.sendMessage(CC.translate(s));
            }
            updateClaimInfo(player);
            return;
        }

        if (pos2.containsKey(player)) claimHandler.removePillar(player, pos2.get(player).clone());

        claimHandler.createPillar(player, clicked.getLocation().clone());
        pos2.put(player, clicked.getLocation());
        for (String s : FactionConfiguration.CLAIM_SET_SECOND.getStringList(
                "%x%", clicked.getLocation().getBlockX(),
                "%y%", clicked.getLocation().getBlockY(),
                "%z%", clicked.getLocation().getBlockZ()
        )) {
            player.sendMessage(CC.translate(s));
        }
        updateClaimInfo(player);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        event.getDrops().remove(Samurai.getInstance().getFactionHandler().getClaimWand());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!(event.getItemDrop().getItemStack().isSimilar(Samurai.getInstance().getFactionHandler().getClaimWand()))) return;

        event.getItemDrop().remove();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(Samurai.getInstance().getFactionHandler().getClaimWand());
        pos1.remove(event.getPlayer());
        pos2.remove(event.getPlayer());
        Samurai.getInstance().getFactionHandler().getClaimHandler().unMapClaims(event.getPlayer());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;
        if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
            Faction toClan = Samurai.getInstance().getFactionHandler().getFactionByLocation(to);
            Faction fromClan = Samurai.getInstance().getFactionHandler().getFactionByLocation(from);

            if (fromClan == null && toClan != null) {
                ActionBarAPI.sendActionBar(player, CC.translate("&fEntering: &6" + toClan.getName(player) + ", &fLeaving: &7Wilderness"));
            }

            if (toClan == null && fromClan != null) {
                ActionBarAPI.sendActionBar(player, CC.translate("&fEntering: &7Wilderness" + ", &fLeaving: &6" + fromClan.getName(player)));
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Faction factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(block.getLocation());

        if (factionAt == null) return;
        if (factionAt.canBypass(player)) return;

        event.setCancelled(true);
        player.sendMessage(FactionConfiguration.CANNOT_BREAK.getString(FactionConfiguration.FACTION_PLACEHOLDERS(factionAt, player)));
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        Faction factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(block.getLocation());

        if (factionAt == null) return;
        if (factionAt.canBypass(player)) return;

        player.setVelocity(player.getVelocity().subtract(new Vector(0, 1, 0)));
        event.setCancelled(true);
        player.sendMessage(FactionConfiguration.CANNOT_PLACE.getString(FactionConfiguration.FACTION_PLACEHOLDERS(factionAt, player)));
    }

    @EventHandler
    public void onInteractClaim(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) return;

        Faction factionAt = Samurai.getInstance().getFactionHandler().getFactionByLocation(block.getLocation());

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) return;
        if (factionAt == null) return;
        if (factionAt.canBypass(player)) return;
        if (!INTERACTABLES.contains(block.getType())) return;

        event.setUseInteractedBlock(Event.Result.DENY);
        player.sendMessage(FactionConfiguration.CANNOT_INTERACT.getString(FactionConfiguration.FACTION_PLACEHOLDERS(factionAt, player)));

        if (player.isSneaking() && isPvP(player.getItemInHand().getType())) return;
        if (!isPvP(player.getItemInHand().getType())) return;

        if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
            player.launchProjectile(EnderPearl.class);
        } else if (player.getItemInHand().getType() == Material.EGG) {
            player.launchProjectile(Egg.class);
        } else if (player.getItemInHand().getType() == Material.SNOWBALL) {
            player.launchProjectile(Snowball.class);
        } else if (player.getItemInHand().getType() == Material.POTION) {
            player.launchProjectile(ThrownPotion.class).setItem(new ItemStack(Material.POTION, 1, player.getItemInHand().getDurability()));
        }

        int amount = player.getItemInHand().getAmount();
        if (amount == 1) {
            player.setItemInHand(new ItemStack(Material.AIR));
        } else {
            player.getItemInHand().setAmount(amount-1);
        }
    }

    private boolean isPvP(Material material) {
        return material == Material.POTION || material == Material.ENDER_PEARL || material == Material.SNOWBALL || material == Material.EGG;
    }

    public void updateClaimInfo(Player player) {
        Location pos1 = FactionClaimListener.getPos1().get(player);
        Location pos2 = FactionClaimListener.getPos2().get(player);
        if (pos1 == null) return;
        if (pos2 == null) return;
        Cuboid cuboid = new Cuboid(pos1, pos2, true);
        double money = (cuboid.getSizeX() + cuboid.getSizeZ()) * FactionConfiguration.LAND_CLAIMED_PRICE_PER_BLOCK.getDouble();

        for (String s : FactionConfiguration.CLAIM_SET_BOTH.getStringList(
                "%cost%", NumberFormat.getInstance(Locale.ENGLISH).format(money),
                "%claim_x%", cuboid.getSizeX(),
                "%claim_z%", cuboid.getSizeZ()
        )) {
            player.sendMessage(CC.translate(s));
        }
    }

}
