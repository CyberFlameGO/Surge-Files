package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public final class GrapplingHook extends AbilityItem {

	public GrapplingHook() {
		super("GrapplingHook");
	}

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSnowBallLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof FishHook)) return;
		if (!(event.getEntity().getShooter() instanceof Player)) return;

		FishHook hook = (FishHook) event.getEntity();
		Player player = (Player) event.getEntity().getShooter();

		if (player.getWorld().getEnvironment() == World.Environment.THE_END || player.getWorld().getEnvironment() == World.Environment.NETHER)
			return;

		if (!isPartnerItem(player.getItemInHand()))
			return;

		if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
			player.sendMessage(CC.RED + "You cannot use this in spawn!");
			event.setCancelled(true);
			return;
		}

		if (isOnCooldown(player)) {
			player.sendMessage(getCooldownMessage(player));
			event.setCancelled(true);
			return;
		}

		if (Samurai.getInstance().getServerHandler().isWarzone(player.getLocation())) {
			event.setCancelled(true);
			player.sendMessage(CC.translate("&cYou cannot use ability items in the warzone."));
			return;
		}

		hook.setMetadata("grappling", new FixedMetadataValue(Samurai.getInstance(), true));

	}

	@EventHandler
	public void onGrapple(PlayerFishEvent event) {
		Player player = event.getPlayer();

		if (!event.getHook().hasMetadata("grappling")) return;
		if (event.getState() != PlayerFishEvent.State.IN_GROUND) {
			return;
		}

		Location loc = event.getHook().getLocation();
		Location playerloc = player.getLocation();
		Location hookloc = loc.subtract(playerloc);
		Vector vector = new Vector(hookloc.toVector().normalize().multiply(2).getX(),
				0.5,
				hookloc.toVector().normalize().multiply(2).getZ());

		event.getPlayer().setVelocity(vector);

		setGlobalCooldown(player);
		setCooldown(player);

		sendActivationMessages(player,
				new String[]{
						"You have activated " + getName() + CC.WHITE + "!",
						"Successfully propelled forward."
				}, null, null);

	}

	@Override
	public long getCooldownTime() {
		return SOTWCommand.isPartnerPackageHour() ? 30L : 60;
	}

	@Override
	public ItemStack partnerItem() {
		return ItemBuilder.of(Material.FISHING_ROD)
				.name(CC.translate("&g&lGrappling Hook"))
				.addToLore(
						" ",
						CC.translate("&g&lDescription"),
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fCast this rod and pull it back to",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fbe launched where the hook lands.",
						" "
				)
				.enchant(Enchantment.DURABILITY, 1)
				.modelData(20)
				.data((short) (Material.FISHING_ROD.getMaxDurability() - 10))
				.build();
	}

	@Override
	public ShapedRecipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(getName().toLowerCase().replace("'", "").replace(" ", "_")));
		ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

		recipe.shape("AAA", "CBC", "AAA");
		recipe.setIngredient('A', Material.FEATHER);
		recipe.setIngredient('B', Material.FISHING_ROD);
		recipe.setIngredient('C', Material.SUGAR);

		return recipe;
	}

	@Override
	public List<Material> getRecipeDisplay() {
		return Arrays.asList(
				Material.FEATHER,
				Material.FEATHER,
				Material.FEATHER,

				Material.SUGAR,
				Material.FISHING_ROD,
				Material.SUGAR,

				Material.FEATHER,
				Material.FEATHER,
				Material.FEATHER
		);
	}

	@Override
	public String getName() {
		return CC.translate("&g&lGrappling Hook");
	}

	@Override
	public int getAmount() {
		return 1;
	}
}
