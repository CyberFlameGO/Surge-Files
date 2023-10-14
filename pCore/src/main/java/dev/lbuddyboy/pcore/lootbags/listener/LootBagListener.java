package dev.lbuddyboy.pcore.lootbags.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.lootbags.LootBag;
import dev.lbuddyboy.pcore.lootbags.menu.LootBagRewardsMenu;
import dev.lbuddyboy.pcore.lootbags.menu.sub.LootBagEditChooseMenu;
import dev.lbuddyboy.pcore.lootbags.menu.sub.LootBagRewardEditorMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.RewardItem;
import dev.lbuddyboy.pcore.util.Tasks;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

public class LootBagListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata("lootbag_editor_command")) return;
        if (!player.hasMetadata("lootbag_editor")) return;

        MetadataValue metadata = player.getMetadata("lootbag_editor_command").get(0);
        MetadataValue lootbag_editor = player.getMetadata("lootbag_editor").get(0);
        if (metadata == null) return;
        if (lootbag_editor == null) return;

        LootBag lootBag = (LootBag) lootbag_editor.value();
        RewardItem reward = (RewardItem) metadata.value();

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            player.removeMetadata("lootbag_editor_command", pCore.getInstance());
            player.removeMetadata("lootbag_editor", pCore.getInstance());
            Tasks.run(() -> new LootBagRewardEditorMenu(lootBag, reward).openMenu(player));
            return;
        }

        reward.getCommands().add(event.getMessage());
        player.removeMetadata("lootbag_editor_command", pCore.getInstance());
        player.removeMetadata("lootbag_editor", pCore.getInstance());
        Tasks.run(() -> {
            lootBag.save();
            new LootBagRewardEditorMenu(lootBag, reward).openMenu(player);
        });
    }

    @EventHandler
    public void setChance(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata("lootbag_editor_chance")) return;
        if (!player.hasMetadata("lootbag_editor")) return;

        MetadataValue metadata = player.getMetadata("lootbag_editor_chance").get(0);
        MetadataValue lootbag_editor = player.getMetadata("lootbag_editor").get(0);
        if (metadata == null) return;
        if (lootbag_editor == null) return;

        LootBag lootBag = (LootBag) lootbag_editor.value();
        RewardItem reward = (RewardItem) metadata.value();

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            player.removeMetadata("lootbag_editor_chance", pCore.getInstance());
            player.removeMetadata("lootbag_editor", pCore.getInstance());
            Tasks.run(() -> new LootBagRewardEditorMenu(lootBag, reward).openMenu(player));
            return;
        }

        double chance = 100.0;

        try {
            chance = Double.parseDouble(event.getMessage());
        } catch (NumberFormatException ignored) {
            player.sendMessage(CC.translate("&cThat is not a valid number."));
            return;
        }

        reward.setChance(chance);
        player.removeMetadata("lootbag_editor_chance", pCore.getInstance());
        player.removeMetadata("lootbag_editor", pCore.getInstance());
        Tasks.run(() -> {
            lootBag.save();
            new LootBagRewardEditorMenu(lootBag, reward).openMenu(player);
        });
    }

    @EventHandler
    public void onRename(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata("lootbag_editor_rename")) return;
        MetadataValue metadata = player.getMetadata("lootbag_editor_rename").get(0);
        if (metadata == null) return;
        LootBag lootBag = (LootBag) metadata.value();

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            player.removeMetadata("lootbag_editor_rename", pCore.getInstance());
            Tasks.run(() -> new LootBagEditChooseMenu(lootBag).openMenu(player));
            return;
        }

        if (pCore.getInstance().getLootBagHandler().getLootBags().containsKey(event.getMessage())) {
            Tasks.run(() -> new LootBagEditChooseMenu(lootBag).openMenu(player));
            player.sendMessage(CC.translate("&aA lootbag with that name already exists."));
            return;
        }

        lootBag.rename(event.getMessage());
        player.removeMetadata("lootbag_editor_rename", pCore.getInstance());
        Tasks.run(() -> {
            lootBag.save();
            new LootBagEditChooseMenu(lootBag).openMenu(player);
        });
    }

    @EventHandler
    public void setDisplayName(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata("lootbag_editor_display")) return;
        MetadataValue metadata = player.getMetadata("lootbag_editor_display").get(0);
        if (metadata == null) return;
        LootBag lootBag = (LootBag) metadata.value();

        event.setCancelled(true);

        if (event.getMessage().equalsIgnoreCase("cancel")) {
            player.removeMetadata("lootbag_editor_display", pCore.getInstance());
            Tasks.run(() -> new LootBagEditChooseMenu(lootBag).openMenu(player));
            return;
        }

        lootBag.setDisplayName(event.getMessage());
        player.removeMetadata("lootbag_editor_display", pCore.getInstance());
        Tasks.run(() -> {
            lootBag.save();
            new LootBagEditChooseMenu(lootBag).openMenu(player);
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null) return;

        for (LootBag lootbag : pCore.getInstance().getLootBagHandler().getLootBags().values()) {
            if (lootbag.getDisplayItem() == null) continue;
            if (!lootbag.getDisplayItem().isSimilar(item)) continue;

            if (event.getAction().name().contains("LEFT_")) {
                new LootBagRewardsMenu(lootbag).openMenu(player);
            } else {
                ItemUtils.removeAmount(player.getInventory(), item, 1);
                lootbag.roll(player);
                player.playEffect(player.getLocation(), Effect.FIREWORKS_SPARK, 0);
                event.setUseItemInHand(Event.Result.DENY);
            }
        }
    }

}
