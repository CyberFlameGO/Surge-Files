package dev.lbuddyboy.samurai.nametag;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffectType;

final class NametagListener
		implements Listener {
	NametagListener() {
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (FrozenNametagHandler.isInitiated()) {
			event.getPlayer().setMetadata("qLibNametag-LoggedIn", new FixedMetadataValue(Samurai.getInstance(), true));
			FrozenNametagHandler.initiatePlayer(event.getPlayer());
			FrozenNametagHandler.reloadPlayer(event.getPlayer());
			FrozenNametagHandler.reloadOthersFor(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.getPlayer().removeMetadata("qLibNametag-LoggedIn", Samurai.getInstance());
		FrozenNametagHandler.getTeamMap().remove(event.getPlayer().getName());
	}

	@EventHandler
	public void onPotionEffect(EntityPotionEffectEvent event) {
		if (event.getEntity() instanceof Player) {
			if (event.getModifiedType() == PotionEffectType.INVISIBILITY) {
				FrozenNametagHandler.reloadPlayer((Player) event.getEntity());
				FrozenNametagHandler.reloadOthersFor((Player) event.getEntity());
			}
		}
	}

}

