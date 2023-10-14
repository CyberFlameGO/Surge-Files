package dev.lbuddyboy.pcore.pets.egg.impl;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.pets.egg.EggImpl;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class RightClickEgg implements EggImpl, Listener {

    private final ItemStack item;

    public RightClickEgg(Config config) {
        Bukkit.getPluginManager().registerEvents(this, pCore.getInstance());
        this.item = ItemUtils.itemStackFromConfigSect("settings.pet-eggs.right-click.item-format", config);
    }

    @Override
    public String getName() {
        return "RIGHT_CLICK";
    }

    @Override
    public ItemStack applyTags(Player player, PetRarity rarity, ItemStack stack, Object value) {
        NBTItem item = new NBTItem(stack);

        item.setString("egg-type", getName());
        item.setString("egg-rarity", rarity.getName());

        return item.getItem();
    }

    @Override
    public ItemStack advance(Player player, ItemStack stack) {
        return stack;
    }

    @Override
    public ItemStack getItem(PetRarity rarity) {
        ItemBuilder builder = new ItemBuilder(applyTags(null, rarity, this.item.clone(), null), 1);

        builder.setName(ItemUtils.getName(this.item)
                .replaceAll("%rarity-color%", rarity.getColor())
                .replaceAll("%rarity-display%", rarity.getDisplayName())
        );
        builder.setLore(ItemUtils.getLore(this.item),
                "%rarity-color%", rarity.getColor(),
                "%rarity-display%", rarity.getDisplayName()
        );

        return builder.create();
    }

    @Override
    public ItemStack getItem(PetRarity rarity, Object value) {
        return getItem(rarity);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) return;

        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasTag("egg-type") && nbtItem.getString("egg-type").equalsIgnoreCase(getName()) && nbtItem.hasTag("egg-rarity")) {
            PetRarity rarity = pCore.getInstance().getPetHandler().getPetRarity(nbtItem.getString("egg-rarity"));

            rollPet(player, rarity);
            player.setItemInHand(ItemUtils.takeItem(player.getItemInHand()));
        }
    }

}
