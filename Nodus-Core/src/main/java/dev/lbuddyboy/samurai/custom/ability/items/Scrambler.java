package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.*;

public final class Scrambler extends AbilityItem implements Listener {

	private static int RADIUS; // how many hits it takes for the item to activate
	private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();

	public Scrambler() {
		super("Scrambler");

		this.name = "scrambler";
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onShoot(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player shooter) {

			if (!isPartnerItem(shooter.getItemInHand()))
				return;

			if (Samurai.getInstance().getServerHandler().isWarzone(shooter.getLocation())) {
				if (!Samurai.getInstance().getMapHandler().isKitMap()) {
					event.setCancelled(true);
					shooter.sendMessage(CC.translate("&cYou cannot use ability items in the warzone."));
					return;
				}
			}

			if (isOnCooldown(shooter)) {
				event.setCancelled(true);
				shooter.sendMessage(getCooldownMessage(shooter));
				return;
			}

			setCooldown(shooter);
			event.getEntity().setMetadata("scrambler", new FixedMetadataValue(Samurai.getInstance(), true));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onSnowBallHit(ProjectileHitEvent event) {
		if (!(event.getEntity() instanceof Egg egg))
			return;

		if (event.getEntity() instanceof Egg && egg.getShooter() instanceof Player shooter) {

			if (!egg.hasMetadata("scrambler"))
				return;

			Team team = Samurai.getInstance().getTeamHandler().getTeam(shooter);

			setGlobalCooldown(shooter);

			for (Entity entity : egg.getNearbyEntities(RADIUS, RADIUS, RADIUS)) {
				if (entity instanceof Player target) {
					if (target == shooter) continue;

					if (team != null && team.isMember(target.getUniqueId())) {
						continue;
					}

					if (!canUse(target, null)) continue;

					List<ItemStack> hi = new ArrayList<>();

					for (int i = 0; i < 9; i++) {
						hi.add(target.getInventory().getItem(i));
					}

					Collections.shuffle(hi);

					for (int i = 0; i < 9; i++) {
						target.getInventory().setItem(i, hi.get(i));
					}

					for (String s : new String[]{target.getName() + " has hit you with " + getName() + CC.WHITE + "!", "Your inventory has been scrambled."}) {
						target.sendMessage(CC.translate(s));
					}
				}
			}
		}
	}

	@EventHandler // cleanup attack map
	public void onQuit(PlayerQuitEvent event) {
		attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
	}

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		return false;
	}

	@Override
	public void reload(File folder) {
		super.reload(folder);

		RADIUS = config.getInt("radius", 4);
	}
}
