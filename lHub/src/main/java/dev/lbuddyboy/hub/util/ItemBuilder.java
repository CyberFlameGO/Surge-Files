package dev.lbuddyboy.hub.util;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 30/06/2021 / 7:13 PM
 * xParty / services.xenlan.party.util
 */

@Getter
public class ItemBuilder {

	private final Material material;
	private final ItemStack stack;

	public ItemBuilder(Material material) {
		this.material = material;

		stack = new ItemStack(getMaterial());
	}

	public ItemBuilder(ItemStack stack) {
		this.stack = stack.clone();
		this.material = stack.getType();
	}


	/*
	Sets the display name of the provided ItemStack when creating a new instance of the ItemBuilder.java Class.
	 */
	public ItemBuilder setDisplayName(String newName) {
		ItemMeta meta = this.stack.getItemMeta();
		meta.setDisplayName(CC.translate(newName));
		this.stack.setItemMeta(meta);
		return this;
	}

	/*
	Add to the lore of the provided ItemStack when creating a new instance of the ItemBuilder.java Class.
 	*/
	public ItemBuilder addLore(String toAdd) {
		ItemMeta meta = this.stack.getItemMeta();

		List<String> newLore = new ArrayList<>();
		if (meta.getLore() == null) {
			meta.setLore(CC.translate(Collections.singletonList(toAdd)));
		} else {
			newLore.add(CC.translate(toAdd));
			meta.setLore(newLore);
		}
		this.stack.setItemMeta(meta);
		return this;
	}

	public ItemBuilder addEnchant(Enchantment enchantment, int level) {
		this.stack.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	/*
	Sets the lore of the provided ItemStack when creating a new instance of the ItemBuilder.java Class.
 	*/
	public ItemBuilder setLore(List<String> toSet) {
		ItemMeta meta = this.stack.getItemMeta();

		meta.setLore(CC.translate(toSet));

		this.stack.setItemMeta(meta);
		return this;
	}

	/*
	Sets the lore of the provided ItemStack when creating a new instance of the ItemBuilder.java Class.
 	*/
	public ItemBuilder setItemFlags(ItemFlag... flags) {
		ItemMeta meta = this.stack.getItemMeta();

		meta.addItemFlags(flags);

		this.stack.setItemMeta(meta);
		return this;
	}

	/*
	Sets the data of the provided ItemStack when creating a new instance of the ItemBuilder.java Class.
	*/
	public ItemBuilder setData(int newData) {
		this.stack.setDurability((short) newData);
		return this;
	}

	/*
	Sets the data of the provided ItemStack when creating a new instance of the ItemBuilder.java Class.
	*/
	public ItemBuilder setModelData(int newData) {
		ItemMeta meta = this.stack.getItemMeta();

		meta.setCustomModelData(newData);

		this.stack.setItemMeta(meta);
		return this;
	}

	/*
	Sets the amount of the provided ItemStack when creating a new instance of the ItemBuilder.java Class.
	*/
	public ItemBuilder setAmount(int newAmount) {
		this.stack.setAmount(newAmount);
		return this;
	}

	public ItemStack create() {
		return this.stack;
	}

}
