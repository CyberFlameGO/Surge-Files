package dev.lbuddyboy.pcore.listener;

import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SelectionListener implements Listener {

    public static ItemStack CLAIM_WAND = new ItemBuilder(Material.DIAMOND_AXE).setName("&6&lClaim Wand")
            .setLore("&7Left Click to set Position 1", "&7Right Click to set Position 2")
            .create();

    @Getter private static final Map<Player, Location> pos1 = new HashMap<>();
    @Getter private static final Map<Player, Location> pos2 = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();

        if (stack == null) return;
        if (!(stack.isSimilar(CLAIM_WAND))) return;
        if (clicked == null) return;

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            pos1.put(player, clicked.getLocation());
            player.sendMessage(CC.translate("&aSet position 1 at the clicked block."));
            return;
        }

        pos2.put(player, clicked.getLocation());
        player.sendMessage(CC.translate("&aSet position 2 at the clicked block."));
    }

}
