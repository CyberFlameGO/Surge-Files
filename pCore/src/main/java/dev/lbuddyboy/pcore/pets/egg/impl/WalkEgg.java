package dev.lbuddyboy.pcore.pets.egg.impl;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.pets.egg.EggImpl;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class WalkEgg implements EggImpl, Listener {

    private final ItemStack item;

    public WalkEgg(Config config) {
        Bukkit.getPluginManager().registerEvents(this, pCore.getInstance());
        this.item = ItemUtils.itemStackFromConfigSect("settings.pet-eggs.walk.item-format", config);
    }

    @Override
    public String getName() {
        return "WALK_EGG";
    }

    @Override
    public ItemStack applyTags(Player player, PetRarity rarity, ItemStack stack, Object value) {
        NBTItem item = new NBTItem(stack);

        item.setString("egg-type", getName());
        item.setString("egg-rarity", rarity.getName());
        item.setInteger("blocks-left", (Integer) value);
        item.setUUID("uuid", UUID.randomUUID());

        return item.getItem();
    }

    @Override
    public ItemStack advance(Player player, ItemStack stack) {
        ItemBuilder builder = new ItemBuilder(this.item.clone(), 1);
        NBTItem item = new NBTItem(stack);
        PetRarity rarity = pCore.getInstance().getPetHandler().getPetRarity(item.getString("egg-rarity"));
        int blocksLeft = item.getInteger("blocks-left");

        if (blocksLeft <= 0) {
            rollPet(player, rarity);
            return new ItemStack(Material.AIR);
        }

        builder.setName(ItemUtils.getName(this.item)
                .replaceAll("%rarity-color%", rarity.getColor())
                .replaceAll("%rarity-display%", rarity.getDisplayName())
                .replaceAll("%blocks-left%", "" + (blocksLeft - 1))
        );
        builder.setLore(ItemUtils.getLore(this.item),
                "%rarity-color%", rarity.getColor(),
                "%rarity-display%", rarity.getDisplayName(),
                "%blocks-left%", (blocksLeft - 1)
        );

        NBTItem newItem = new NBTItem(builder.create());

        newItem.setString("egg-type", getName());
        newItem.setString("egg-rarity", rarity.getName());
        newItem.setInteger("blocks-left", blocksLeft - 1);

        return newItem.getItem();
    }

    @Override
    public ItemStack getItem(PetRarity rarity) {
        return getItem(rarity, "" + rarity.getDefaultWalk());
    }

    @Override
    public ItemStack getItem(PetRarity rarity, Object value) {
        ItemBuilder builder = new ItemBuilder(applyTags(null, rarity, this.item.clone(), value), 1);

        builder.setName(ItemUtils.getName(this.item)
                .replaceAll("%rarity-color%", rarity.getColor())
                .replaceAll("%rarity-display%", rarity.getDisplayName())
                .replaceAll("%blocks-left%", String.valueOf(value))
        );

        builder.setLore(ItemUtils.getLore(this.item),
                "%rarity-color%", rarity.getColor(),
                "%rarity-display%", rarity.getDisplayName(),
                "%blocks-left%", value
        );

        return builder.create();
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();
        Player player = event.getPlayer();

        if ((int) from.getX() == (int) to.getX() && (int) from.getZ() == (int) to.getZ()) return;

        Map<Integer,ItemStack> eggs = pCore.getInstance().getPetHandler().scanEggs(player, getName());
        if (eggs.isEmpty()) return;

        for (Map.Entry<Integer, ItemStack> entry : eggs.entrySet()) {
            ItemStack stack = advance(player, entry.getValue());

            player.getInventory().setItem(entry.getKey(), stack);
        }
    }

}
