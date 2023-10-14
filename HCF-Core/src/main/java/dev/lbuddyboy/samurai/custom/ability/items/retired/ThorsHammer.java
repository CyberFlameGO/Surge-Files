package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 15/03/2022 / 11:14 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.ability.items.spring
 */
public class ThorsHammer extends AbilityItem {

	private final Cooldown cooldown = new Cooldown();

	public ThorsHammer() {
		super("ThorsHammer");
	}

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		setGlobalCooldown(player);
		setCooldown(player);
		sendActivationMessages(player,
				new String[]{
						"You have activated " + getName() + CC.WHITE + "!",
						"Everyone within 8 blocks of you have been blinded and paralyzed."
				}, null, null);

		for (Entity ent : player.getNearbyEntities(8, 0, 8)) {
			if (!(ent instanceof Player target)) continue;
			if (target.getName().equals(player.getName())) continue;
			Team team = Samurai.getInstance().getTeamHandler().getTeam(target);
			if (team != null && team.getOnlineMembers().contains(player)) continue;

			target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 70, 0));
			target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 70, 1));

			cooldown.applyCooldown(target, 3);
		}

		return true;
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		Location from = event.getFrom();
		Location to = event.getTo();
		if (cooldown.onCooldown(player)) {
			if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
				event.setTo(from);
				player.sendMessage(CC.translate("&cYou are released in " + cooldown.getRemaining(player)));
			}
		}
	}

	@Override
	public long getCooldownTime() {
		return SOTWCommand.isPartnerPackageHour() ? 30L : 60;
	}

	@Override
	public ItemStack partnerItem() {
		return ItemBuilder.of(Material.IRON_AXE)
				.name(CC.translate("&g&lThor's Hammer"))
				.addToLore(
						" ",
						CC.translate("&g&lDescription"),
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fUpon right-clicking, anyone within",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &f8 blocks of you. Will be paralyzed",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fand blinded for 3.5 seconds.",
						" "
				)
				.enchant(Enchantment.DURABILITY, 1)
				.modelData(19)
				.build();
	}

	@Override
	public ShapedRecipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(getName().toLowerCase().replace("'", "").replace(" ", "_")));
		ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

		recipe.shape("AAA", "BBB", "AAA");
		recipe.setIngredient('A', Material.FLINT_AND_STEEL);
		recipe.setIngredient('B', Material.IRON_AXE);

		return recipe;
	}

	@Override
	public List<Material> getRecipeDisplay() {
		return Arrays.asList(
				Material.FLINT_AND_STEEL,
				Material.FLINT_AND_STEEL,
				Material.FLINT_AND_STEEL,

				Material.IRON_AXE,
				Material.IRON_AXE,
				Material.IRON_AXE,

				Material.FLINT_AND_STEEL,
				Material.FLINT_AND_STEEL,
				Material.FLINT_AND_STEEL
		);
	}

	@Override
	public String getName() {
		return CC.translate("&g&lThor's Hammer");
	}

	@Override
	public int getAmount() {
		return 1;
	}
}