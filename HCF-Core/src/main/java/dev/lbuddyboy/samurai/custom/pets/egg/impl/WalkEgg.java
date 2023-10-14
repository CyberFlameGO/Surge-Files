package dev.lbuddyboy.samurai.custom.pets.egg.impl;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.samurai.custom.pets.PetRarity;
import dev.lbuddyboy.samurai.custom.pets.egg.EggImpl;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class WalkEgg implements EggImpl, Listener {

    private final ItemStack item;

    public WalkEgg(Config config) {
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
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
        this.item.setAmount(1);
        ItemBuilder builder = new ItemBuilder(this.item.clone());
        NBTItem item = new NBTItem(stack);
        PetRarity rarity = Samurai.getInstance().getPetHandler().getPetRarity(item.getString("egg-rarity"));
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
        ItemBuilder builder = new ItemBuilder(applyTags(null, rarity, this.item.clone(), value));

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

        if (Feature.PETS.isDisabled()) {
            return;
        }

        Map<Integer,ItemStack> eggs = Samurai.getInstance().getPetHandler().scanEggs(player, getName());
        if (eggs.isEmpty()) return;

        for (Map.Entry<Integer, ItemStack> entry : eggs.entrySet()) {
            ItemStack stack = advance(player, entry.getValue());

            player.getInventory().setItem(entry.getKey(), stack);
        }
    }

}
