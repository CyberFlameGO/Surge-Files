package dev.aurapvp.samurai.faction.editor.impl;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.faction.FactionHandler;
import dev.aurapvp.samurai.faction.claim.ClaimHandler;
import dev.aurapvp.samurai.faction.editor.FactionEditor;
import dev.aurapvp.samurai.faction.editor.menu.FactionEditorMenu;
import dev.aurapvp.samurai.faction.editor.menu.FactionSystemTypeEditorMenu;
import dev.aurapvp.samurai.faction.listener.FactionClaimListener;
import dev.aurapvp.samurai.faction.member.FactionPermission;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ConversationBuilder;
import dev.aurapvp.samurai.util.Cuboid;
import dev.aurapvp.samurai.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ClaimForEditor extends FactionEditor implements Listener {

    public static String FORCE_CLAIM_METADATA = "FORCE_CLAIM";
    @Getter
    private static final Map<Player, Location> pos1 = new HashMap<>();
    @Getter private static final Map<Player, Location> pos2 = new HashMap<>();

    public ClaimForEditor() {
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
    }

    @Override
    public void edit(Player sender, Faction faction) {
        ConversationBuilder builder = new ConversationBuilder(sender);

        sender.beginConversation(builder.stringPrompt(CC.translate("&aClaim the land for this faction, type 'cancel' to stop this process."), (ctx, response) -> {
            if (response.equalsIgnoreCase("cancel")) {
                sender.sendMessage(CC.translate("&cProcess cancelled."));
                cancel(sender, faction);
                return Prompt.END_OF_CONVERSATION;
            }

            new FactionEditorMenu(faction).openMenu(sender);
            return Prompt.END_OF_CONVERSATION;
        }).echo(false).build());

        sender.setMetadata(FORCE_CLAIM_METADATA, new FixedMetadataValue(Samurai.getInstance(), faction.getUniqueId()));
        sender.getInventory().setItemInMainHand(Samurai.getInstance().getFactionHandler().getOPClaimWand(sender));
        sender.closeInventory();
    }

    @Override
    public int slot(Player sender, Faction faction) {
        return 1;
    }

    @Override
    public ItemStack displayItem(Player sender, Faction faction) {
        return new ItemBuilder(Material.GOLDEN_HOE).setName(CC.GREEN + "Claim For &7(Click)").create();
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        event.getDrops().remove(Samurai.getInstance().getFactionHandler().getOPClaimWand(player));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!(event.getItemDrop().getItemStack().isSimilar(Samurai.getInstance().getFactionHandler().getOPClaimWand(event.getPlayer())))) return;

        event.getItemDrop().remove();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().remove(Samurai.getInstance().getFactionHandler().getOPClaimWand(event.getPlayer()));
        pos1.remove(event.getPlayer());
        pos2.remove(event.getPlayer());
        Samurai.getInstance().getFactionHandler().getClaimHandler().unMapClaims(event.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        Player player = event.getPlayer();
        FactionHandler factionHandler = Samurai.getInstance().getFactionHandler();
        ClaimHandler claimHandler = Samurai.getInstance().getFactionHandler().getClaimHandler();

        if (stack == null) return;
        if (!player.hasMetadata(FORCE_CLAIM_METADATA)) return;
        if (stack.getType() != factionHandler.getOpClaimWand().getType()) return;

        event.setCancelled(true);
        Faction faction = factionHandler.getFactionByUniqueId((UUID) player.getMetadata(ClaimForEditor.FORCE_CLAIM_METADATA).get(0).value());

        if (faction == null) {
            player.sendMessage(CC.translate("&cCould not find a faction... cancelling claim process."));
            cancel(player, null);
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR && player.isSneaking()) {
            ClaimHandler.ClaimPurchaseInfo info = claimHandler.purchaseClaim(player, faction);
            if (info != null) {
                Samurai.getInstance().getDynMapImpl().getClaimLayer().upsertMarker(faction);
                cancel(player, null);
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
            updateClaimInfo(player);
            return;
        }

        if (pos2.containsKey(player)) claimHandler.removePillar(player, pos2.get(player).clone());

        claimHandler.createPillar(player, clicked.getLocation().clone());
        pos2.put(player, clicked.getLocation());
        updateClaimInfo(player);
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

    public void cancel(Player player, Faction faction) {
        player.getInventory().removeItem(Samurai.getInstance().getFactionHandler().getOPClaimWand(player));
        player.removeMetadata(FORCE_CLAIM_METADATA, Samurai.getInstance());
        if (faction == null) return;
        new FactionEditorMenu(faction).openMenu(player);
    }

}
