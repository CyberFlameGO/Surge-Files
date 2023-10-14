package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public final class AntiBard extends AbilityItem implements Listener {

	private static final int HITS = 3; // how many hits it takes for the item to activate

	private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();
	public static final List<UUID> ANTI_BARD_PLAYERS = new ArrayList<>();

	public AntiBard() {
		super("AntiBard");

		this.name = "anti-bard";
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onPlayerHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player entity && event.getDamager() instanceof Player attacker) {

			Pair<UUID, UUID> key = new Pair<>(attacker.getUniqueId(), entity.getUniqueId());

			ItemStack item = attacker.getItemInHand();
			boolean partnerItem = isPartnerItem(item);

			if (attackMap.containsKey(key) && !partnerItem) {
				attackMap.remove(key);
				return;
			}

			if (!partnerItem) {
				return;
			}

			if (DTRBitmask.KOTH.appliesAt(attacker.getLocation()) || DTRBitmask.CITADEL.appliesAt(attacker.getLocation())) {
				attacker.sendMessage(CC.RED + "You cannot use this in koth/citadel!");
				return;
			}

			if (isOnCooldown(attacker)) {
				attacker.sendMessage(getCooldownMessage(attacker));
				return;
			}

			if (ANTI_BARD_PLAYERS.contains(entity.getUniqueId())) {
				attacker.sendMessage(ChatColor.RED + "That player is already tagged by " + getName() + "!");
				return;
			}

			int hits = attackMap.getOrDefault(key, 0);

			if (++hits < HITS) {
				attackMap.put(key, hits);
				return;
			}

			attackMap.remove(key);

			setCooldown(attacker);
			consume(attacker, item);

			ANTI_BARD_PLAYERS.add(entity.getUniqueId());
			Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> ANTI_BARD_PLAYERS.remove(entity.getUniqueId()), 20 * 20);

			MessageConfiguration.ANTI_BARD_ATTACKER.sendListMessage(attacker
					, "%ability-name%", this.getName()
					, "%target%", entity.getName()
			);

			MessageConfiguration.ANTI_BARD_TARGET.sendListMessage(entity
					, "%ability-name%", this.getName()
					, "%attacker%", attacker.getName()
			);

		}
	}

	@EventHandler // cleanup attack map
	public void onQuit(PlayerQuitEvent event) {
		attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
	}

	@Override
	public ShapedRecipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(getName().toLowerCase().replace(" ", "_")));
		ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

		recipe.shape("AAA", "ABA", "AAA");
		recipe.setIngredient('A', Material.GOLD_NUGGET);
		recipe.setIngredient('B', Material.GOLDEN_CHESTPLATE);

		return recipe;
	}

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		return false;
	}
}
