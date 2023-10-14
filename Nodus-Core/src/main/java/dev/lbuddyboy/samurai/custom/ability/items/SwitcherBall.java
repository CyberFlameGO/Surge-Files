package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.pearl.EnderpearlCooldownHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public final class SwitcherBall extends AbilityItem implements Listener {

	public SwitcherBall() {
		super("Switcher");
		this.name = "switcher";
	}

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onSnowBallLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Snowball snowball && event.getEntity().getShooter() instanceof Player player) {

			if (!isPartnerItem(player.getItemInHand()))
				return;

			if (!canUse(player, event)) return;

			setCooldown(player);
			snowball.setMetadata("LaunchLocation", new FixedMetadataValue(Samurai.getInstance(), snowball.getLocation()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	private void onSnowBallHit(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Snowball snowball))
			return;

		if (event.getEntity() instanceof Player target && snowball.getShooter() instanceof Player shooter) {
			if (!snowball.hasMetadata("LaunchLocation"))
				return;

			Team team = Samurai.getInstance().getTeamHandler().getTeam(shooter);

			if (team != null && team.isMember(target.getUniqueId())) {
				shooter.sendMessage(CC.RED + "You cannot switch this player!");
				return;
			}

			Location shooterLocation = shooter.getLocation();
			if (DTRBitmask.SAFE_ZONE.appliesAt(shooterLocation)) {
				shooter.sendMessage(CC.RED + "You cannot use this in spawn!");
				return;
			}

			Location targetLocation = target.getLocation();
			if (DTRBitmask.SAFE_ZONE.appliesAt(targetLocation)) {
				shooter.sendMessage(CC.RED + "That player is in spawn!");
				return;
			}

			double distance = SOTWCommand.isPartnerPackageHour() ? 16.00D : 8.00D;

			if (shooterLocation.getWorld() != targetLocation.getWorld()) {
				shooter.sendMessage(CC.translate("&cThat player is not in your world!"));
				return;
			}

			if (shooterLocation.distance(targetLocation) > distance) {
				shooter.sendMessage(CC.translate("&cThat player was out of range."));
				return;
			}

			setGlobalCooldown(shooter);

			shooter.teleport(targetLocation);
			target.teleport(shooterLocation);

			MessageConfiguration.SWITCHER_ATTACKER.sendListMessage(shooter
					, "%ability-name%", this.getName()
					, "%target%", target.getName()
			);

			MessageConfiguration.SWITCHER_TARGET.sendListMessage(target
					, "%ability-name%", this.getName()
					, "%attacker%", shooter.getName()
			);

		}
	}

}
