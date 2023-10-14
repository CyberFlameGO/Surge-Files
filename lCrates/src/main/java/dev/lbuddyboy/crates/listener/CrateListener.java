package dev.lbuddyboy.crates.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.crates.command.CrateCommand;
import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.util.CC;
import dev.lbuddyboy.crates.util.ItemUtils;
import dev.lbuddyboy.crates.util.XSound;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateListener implements Listener {

    @EventHandler
    public void onKeyInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInHand();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
        if (stack.getAmount() < 1) return;
        if (stack.getType() == Material.AIR) return;
        if (player.isSneaking() && player.isOp()) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("crate")) return;
        //if (event.getHand() != EquipmentSlot.HAND) return;

        Crate crate = lCrates.getInstance().getCrates().get(item.getString("crate"));

        if (crate == null) {
            player.sendMessage(CC.translate("&cThis crate does not exist anymore, if this is a bug contact an admin."));
            return;
        }

        if (event.getClickedBlock() != null && crate.getCrateMaterial() != event.getClickedBlock().getType()) return;
        if (!lCrates.getInstance().getApi().attemptUse(player, crate)) return;

        if (lCrates.getInstance().isVirtualKeys()) {
            lCrates.getInstance().getApi().addKeys(player.getUniqueId(), crate, 1);
            return;
        }

        crate.open(player);
        XSound.BLOCK_CHEST_OPEN.play(player, 2.0f, 2.0f);
        player.getInventory().setItemInHand(ItemUtils.takeItem(stack));
    }

/*    @EventHandler
    public void onKeyPreview(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInHand();

        if (lCrates.getInstance().isVirtualKeys()) return;
        if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
        if (stack.getAmount() < 1) return;
        if (stack.getType() == Material.AIR) return;
        if (player.isSneaking() && player.isOp()) return;

        NBTItem item = new NBTItem(stack);

        if (!item.hasTag("crate")) return;
        //if (event.getHand() != EquipmentSlot.HAND) return;

        Crate crate = lCrates.getInstance().getCrates().get(item.getString("crate"));

        if (crate == null) {
            player.sendMessage(CC.translate("&cThis crate does not exist anymore, if this is a bug contact an admin."));
            return;
        }

        if (event.getClickedBlock() == null && crate.getCrateMaterial() != event.getClickedBlock().getType()) return;
        if (!lCrates.getInstance().getApi().attemptUse(player, crate)) return;

        crate.open(player);
        XSound.BLOCK_CHEST_OPEN.play(player, 2.0f, 2.0f);
        player.getInventory().setItemInHand(ItemUtils.takeItem(stack));
    }*/

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        if (player.isOp()) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                ItemStack item = event.getItemInHand();
                Crate crate = lCrates.getInstance().getCrates().get(ItemUtils.getName(item));
                if (crate != null) {
                    player.sendMessage(CC.translate("&aPlaced a new " + crate.getDisplayName() + "&a crate."));
                    crate.createHologram(block.getLocation());
                    crate.save();
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (lCrates.getInstance().isVirtualKeys()) return;

        for (Crate crate : lCrates.getInstance().getCrates().values()) {
            for (Location location : crate.getLocations()) {
                if (!location.equals(block.getLocation())) continue;
                event.setCancelled(true);

                if (!player.isOp()) continue;
                if (!player.isSneaking()) continue;
                if (player.getGameMode() != GameMode.CREATIVE) continue;

                event.setCancelled(false);
                player.sendMessage(CC.translate("&cDestroyed an existing " + crate.getDisplayName() + "&c crate."));
                crate.deleteLocation(location);
                crate.save();
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Block block = event.getClickedBlock();
        Action action = event.getAction();
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItemInHand();

        if (lCrates.getInstance().isVirtualKeys()) return;

        for (Crate crate : lCrates.getInstance().getCrates().values()) {
            for (Location location : crate.getLocations()) {
                if (!location.equals(block.getLocation())) continue;
                if (player.isSneaking() && player.isOp()) return;

                event.setCancelled(true);

                if (action == Action.LEFT_CLICK_BLOCK) {
                    CrateCommand.preview(player, crate);
                    return;
                }

                return;
            }
        }
    }

/*    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (!(event.getEntity() instanceof EnderCrystal)) return;

        event.setCancelled(true);
    }*/

}
