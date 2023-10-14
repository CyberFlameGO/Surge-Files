package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 17/02/2022 / 9:55 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.partner.impl
 */
public class GoblinBarrel extends AbilityItem {

	private final Map<UUID, List<Entity>> goblinMap = new HashMap<>();
	private final int GOBLIN_DESPAWN_DELAY = 30;

	public GoblinBarrel() {
		super("GoblinBarrel");
	}

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSnowBallLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Egg && event.getEntity().getShooter() instanceof Player) {
			Egg egg = (Egg) event.getEntity();
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

			setCooldown(player);
			egg.setMetadata("goblinbarrel", new FixedMetadataValue(Samurai.getInstance(), true));
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();

		if (!(event.getDamager() instanceof Piglin)) return;
		if (!(event.getEntity() instanceof Player)) return;

		Player victim = (Player) entity;

		victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 3, 0));
		victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 3, 0));

	}

	@EventHandler
	public void onTarget(EntityTargetEvent event) {

		if (!(event.getEntity() instanceof Piglin)) return;

		Piglin goblin = (Piglin) event.getEntity();

		for (Map.Entry<UUID, List<Entity>> entry : this.goblinMap.entrySet()) {
			List<Entity> goblins = entry.getValue();
			if (goblins.contains(goblin)) {
				Player shooter = Bukkit.getPlayer(entry.getKey());
				if (shooter == null) return;
				if (shooter.isOnline()) {
					if (!shooter.getUniqueId().equals(event.getTarget().getUniqueId())) {
						continue;
					}
					goblin.setAI(true);
					event.setTarget(null);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onSnowBallHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Egg))
			return;

		Egg egg = (Egg) event.getEntity();
		if (event.getEntity() instanceof Egg && egg.getShooter() instanceof Player) {
			Player shooter = (Player) egg.getShooter();

			if (!egg.hasMetadata("goblinbarrel"))
				return;

			setGlobalCooldown(shooter);

			List<Entity> goblins = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				Entity goblin = egg.getWorld().spawnEntity(egg.getLocation().clone().add(i, 0, i), EntityType.PIGLIN);
				goblin.setCustomName(CC.translate("&a" + shooter.getName() + "'s Goblin"));
				goblin.setCustomNameVisible(true);
				if (goblin instanceof Piglin) {
					((Piglin) goblin).setBaby();
				}

				goblins.add(goblin);
			}

			goblinMap.put(shooter.getUniqueId(), goblins);

			Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
				goblins.forEach(Entity::remove);
			}, 20L * GOBLIN_DESPAWN_DELAY);

		}
	}

	@Override
	public ItemStack partnerItem() {
		return ItemBuilder.of(Material.EGG)
				.name(CC.translate("&g&lGoblin Barrel"))
				.addToLore(" ")
				.addToLore(CC.translate("&g&lDescription"))
				.addToLore(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fThrow this item at the ground")
				.addToLore(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fand it will spawn 3 goblins where")
				.addToLore(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fthe projectile lands. If you are damaged")
				.addToLore(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fby these mobs. You will receive Poison 2")
				.addToLore(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fand Weakness 1 for 3 seconds.")
				.addToLore(" ")
				.addToLore(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fNOTE: These goblins despawn after " + this.GOBLIN_DESPAWN_DELAY + " seconds.")
				.addToLore(" ")
				.modelData(13)
				.build();
	}

	@Override
	public ShapedRecipe getRecipe() {
		NamespacedKey key = new NamespacedKey(Samurai.getInstance(), ChatColor.stripColor(getName().toLowerCase().replace("'", "").replace(" ", "_")));
		ShapedRecipe recipe = new ShapedRecipe(key, getPartnerItem());

		recipe.shape("AAA", "CBC", "AAA");
		recipe.setIngredient('A', Material.SPIDER_EYE);
		recipe.setIngredient('B', Material.BARREL);
		recipe.setIngredient('C', Material.GOLD_BLOCK);

		return recipe;
	}

	@Override
	public List<Material> getRecipeDisplay() {
		return Arrays.asList(
				Material.SPIDER_EYE,
				Material.SPIDER_EYE,
				Material.SPIDER_EYE,

				Material.GOLD_BLOCK,
				Material.BARREL,
				Material.GOLD_BLOCK,

				Material.SPIDER_EYE,
				Material.SPIDER_EYE,
				Material.SPIDER_EYE
		);
	}

	@Override
	public String getName() {
		return CC.translate("&g&lGoblin Barrel");
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	public long getCooldownTime() {
		return SOTWCommand.isPartnerPackageHour() ? 45L : 90L;
	}

}
