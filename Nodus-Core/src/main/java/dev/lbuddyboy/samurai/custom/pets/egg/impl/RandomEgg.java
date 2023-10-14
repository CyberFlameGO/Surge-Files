package dev.lbuddyboy.samurai.custom.pets.egg.impl;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.custom.pets.IPet;
import dev.lbuddyboy.samurai.custom.pets.PetRarity;
import dev.lbuddyboy.samurai.custom.pets.egg.EggImpl;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomEgg implements EggImpl, Listener {

    private final ItemStack item;

    public RandomEgg(Config config) {
        Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
        this.item = ItemUtils.itemStackFromConfigSect("settings.pet-eggs.random.item-format", config);
    }

    @Override
    public String getName() {
        return "RANDOM";
    }

    @Override
    public ItemStack applyTags(Player player, PetRarity rarity, ItemStack stack, Object value) {
        stack.setAmount(1);
        NBTItem item = new NBTItem(stack);

        item.setString("egg-type", getName());

        return item.getItem();
    }

    @Override
    public ItemStack advance(Player player, ItemStack stack) {
        return stack;
    }

    @Override
    public ItemStack getItem(PetRarity rarity) {
        ItemBuilder builder = new ItemBuilder(applyTags(null, null, this.item.clone(), null));

        builder.setName(ItemUtils.getName(this.item));
        builder.setLore(ItemUtils.getLore(this.item));

        return builder.create();
    }

    @Override
    public ItemStack getItem(PetRarity rarity, Object value) {
        return getItem(null);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (item == null || item.getType() == Material.AIR || item.getAmount() <= 0) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        NBTItem nbtItem = new NBTItem(item);
        if (nbtItem.hasTag("egg-type") && nbtItem.getString("egg-type").equalsIgnoreCase(getName())) {
            if (Feature.PETS.isDisabled()) {
                player.sendMessage(CC.translate("&cPets are currently disabled."));
                return;
            }

            event.setCancelled(true);
            rollPet(player, null);
            player.setItemInHand(ItemUtils.takeItem(player.getItemInHand()));
        }
    }

    @Override
    public void rollPet(Player player, PetRarity rarity) {
        List<IPet> pets = Samurai.getInstance().getPetHandler().getPets().values().stream().toList();
        IPet pet = pets.get(ThreadLocalRandom.current().nextInt(0, pets.size()));

        player.getInventory().addItem(pet.createPet(1));
        player.getWorld().playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 0);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 2.0f, 2.0f);
    }
}
