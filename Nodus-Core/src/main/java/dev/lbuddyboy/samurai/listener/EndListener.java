package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.util.LocationSerializer;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SamuraiCommand;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;


public class EndListener implements Listener {

	public static boolean endActive = true;
	@Getter
	@Setter
	private static Location endReturn; // end -> overworld teleport location
	@Getter
	@Setter
	private static Location creepers; // end -> overworld teleport location
	@Getter
	@Setter
	private static Location endExit; // end -> overworld teleport location
	@Getter
	@Setter
	private static Location tutorialLoc; // end -> overworld teleport location

	private static final long PORTAL_MESSAGE_DELAY_THRESHOLD = 2500L;

	private final Samurai plugin;

	public EndListener(Samurai plugin) {
		this.plugin = plugin;
		loadEndReturn();
		loadCreeperSpawner();
		loadEndexit();
		loadTutorialLoc();
	}

	@EventHandler
	public void onBlockMultiPlaceEvent(BlockMultiPlaceEvent event) {
		if (event.getBlock().getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPhysic(BlockPhysicsEvent event) {
		if (event.getSourceBlock().getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onEntityPortal(EntityPortalEvent event) {
		if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(true);
		}
	}

/*	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityCreatePortal(EntityCreatePortalEvent e) {
		if (e.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
			if (e.getPortalType() == PortalType.ENDER) {
				e.setCancelled(true);
			}
		}
	}*/

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPortalCreate(PortalCreateEvent event) {
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound @a");
		if (event.getEntity() != null) {
			event.getEntity().sendMessage(CC.translate("&cYou cannot create an end portal. You need to purchase an end portal summoner for the shop at spawn."));
		}
	}

	@EventHandler
	public void onBlockForm(EntityBlockFormEvent event) {
		if (event.getBlock().getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(true);
		}
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatePortal(EntityCreatePortalEvent event) {
		if (event.getPortalType() != PortalType.ENDER) return;

		if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(true);
			for (BlockState block : event.getBlocks()) {
				block.setType(Material.AIR);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Team team = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());
		if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(!event.getPlayer().isOp());
			return;
		}
		if (event.getItemInHand().isSimilar(SamuraiCommand.endportalsummoner)) {
			if (team == null) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(CC.translate("&cYou need to be on a team to place this."));
				return;
			}
			Team teamAt = LandBoard.getInstance().getTeam(event.getBlockPlaced().getLocation());
			if (teamAt == null) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(CC.translate("&cYou placed this somewhere it wouldn't be in your claim. Try again."));
				return;
			}
			Block placedBlock = event.getBlockPlaced();
			List<Block> needToSetFrame = Arrays.asList(
					placedBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH),
					placedBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST),
					placedBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST),

					placedBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH),
					placedBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST),
					placedBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST),

					placedBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST),
					placedBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH),
					placedBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH),

					placedBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST),
					placedBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH),
					placedBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH)
			);
			List<Block> needToSetPortal = Arrays.asList(
					placedBlock,
					placedBlock.getRelative(BlockFace.SOUTH),
					placedBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST),
					placedBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST),

					placedBlock.getRelative(BlockFace.NORTH),
					placedBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST),
					placedBlock.getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST),

					placedBlock.getRelative(BlockFace.WEST),
					placedBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH),
					placedBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH),

					placedBlock.getRelative(BlockFace.EAST),
					placedBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH),
					placedBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH)
			);
			if (teamAt == team) {
				boolean canPlace = true;
				for (Block block : needToSetFrame) {
					Team frameAt = LandBoard.getInstance().getTeam(block.getLocation());
					if (frameAt == null || frameAt != team) {
						canPlace = false;
						event.setCancelled(true);
						event.getPlayer().sendMessage(CC.translate("&cYou placed this somewhere it wouldn't be in your claim. Try again."));
						break;
					}
				}
				if (canPlace) {

					event.setCancelled(true);

					InventoryUtils.removeAmountFromInventory(event.getPlayer().getInventory(), SamuraiCommand.endportalsummoner, 1);

					needToSetFrame.forEach(block -> {
						block.setType(Material.END_PORTAL_FRAME);
					});

					needToSetPortal.forEach(block -> {
						block.setType(Material.END_PORTAL);
					});
					placedBlock.setType(Material.END_PORTAL);

					event.getPlayer().sendMessage(CC.translate("&aSuccessfully created an end portal!"));

				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
			if (Feature.NETHER_WORLD.isDisabled()) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(CC.translate("&cThe nether world is currently disabled."));
				return;
			}
		}

		if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			if (Feature.END_WORLD.isDisabled()) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(CC.translate("&cThe end world is currently disabled."));
				return;
			}
			World toWorld = event.getTo().getWorld();
			if (toWorld != null && toWorld.getEnvironment() == World.Environment.THE_END) {
//                event.useTravelAgent(false);
				event.setTo(toWorld.getSpawnLocation().clone().add(0.5, 0, 0.5));
				event.setCanCreatePortal(false);
				event.setSearchRadius(0);
				event.setCreationRadius(0);
				return;
			}

			World fromWorld = event.getFrom().getWorld();
			if (fromWorld != null && fromWorld.getEnvironment() == World.Environment.THE_END) {
				event.setTo(new Location(Bukkit.getWorld("world"), 0.5, 75, 200.5));
				event.setCanCreatePortal(false);
				event.setSearchRadius(0);
				event.setCreationRadius(0);
			}
		}
	}

	// Prevent players jumping the End with Strength.
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onWorldChanged(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		World from = event.getFrom();
		World to = player.getWorld();
		if (from.getEnvironment() != World.Environment.THE_END && to.getEnvironment() == World.Environment.THE_END && player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
			player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPortalEnter(PlayerPortalEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			return;
		}

		Location to = event.getTo();
		World toWorld = to.getWorld();
		if (toWorld == null) return; // safe-guard if the End or Nether is disabled

		if (toWorld.getEnvironment() == World.Environment.THE_END) {
			Player player = event.getPlayer();

			// Prevent entering the end if the player is Spawn Tagged.

			if (Samurai.getInstance().getMapHandler().isKitMap()) {
				if (!isEmpty(player)) {
					message(player, ChatColor.RED + "You cannot enter the CrystalPvP End when your inventory isn't empty.");
					event.setCancelled(true);
					return;
				}
			}

			if (SpawnTagHandler.isTagged(player)) {
				message(player, ChatColor.RED + "You cannot enter the End whilst your Spawn Tag" +
						ChatColor.RED + " is active.");
				event.setCancelled(true);
				return;
			}
			if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId()) && !Samurai.getInstance().getStartingPvPTimerMap().get(player.getUniqueId())) {
				message(player, ChatColor.RED + "You cannot enter the End whilst your PvP Timer" +
						ChatColor.RED + " is active.");
				event.setCancelled(true);
				return;
			}

			event.setTo(toWorld.getSpawnLocation().add(0.5, 0, 0.5));
			messageDelays.remove(player);
		}
	}

	public boolean isEmpty(Player player) {
		for (ItemStack content : player.getInventory().getArmorContents()) {
			if (content != null && content.getType() != Material.AIR) return false;
		}
		for (ItemStack content : player.getInventory().getContents()) {
			if (content != null && content.getType() != Material.AIR) return false;
		}
		for (ItemStack content : player.getInventory().getExtraContents()) {
			if (content != null && content.getType() != Material.AIR) return false;
		}

		return true;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		messageDelays.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMove(PlayerMoveEvent event) {
		Block block = event.getTo().getBlock();

		Location from = event.getFrom();
		Location to = event.getTo();

		Player player = event.getPlayer();

		if (from.getWorld().getEnvironment() != World.Environment.THE_END) return;
		if (block == null || !block.isLiquid()) return;

		if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ() || from.getBlockY() != to.getBlockY()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					player.setFallDistance(0);
					player.setNoDamageTicks(0);
					player.teleport(getEndReturn());
				}
			}.runTask(plugin);

			event.setCancelled(true);
		}
	}

	public static Map<Player, Long> messageDelays = new HashMap<>();

	private void message(Player player, String message) {
		long millis = System.currentTimeMillis();
		if (messageDelays.containsKey(player)) {
			long last = messageDelays.get(player);
			if ((last + PORTAL_MESSAGE_DELAY_THRESHOLD) - millis > 0L) {
				return;
			}
		}

		messageDelays.put(player, millis);
		player.sendMessage(message);
	}

	public static void saveEndReturn() {
		FileConfiguration config = Samurai.getInstance().getConfig();

		config.set("locations.end-return", LocationSerializer.serializeString(endReturn));
		Samurai.getInstance().saveConfig();
	}

	public static void saveCreeper() {
		FileConfiguration config = Samurai.getInstance().getConfig();

		config.set("locations.creeper-spawners", LocationSerializer.serializeString(creepers));
		Samurai.getInstance().saveConfig();
	}

	public static void saveEndExit() {
		FileConfiguration config = Samurai.getInstance().getConfig();

		config.set("locations.end-exit", LocationSerializer.serializeString(endExit));
		Samurai.getInstance().saveConfig();
	}

	public static void saveTutorialLoc() {
		FileConfiguration config = Samurai.getInstance().getConfig();

		config.set("locations.tutorial", LocationSerializer.serializeString(tutorialLoc));
		Samurai.getInstance().saveConfig();
	}

	public static void loadEndReturn() {
		if (endReturn != null) {
			return;
		}

		FileConfiguration config = Samurai.getInstance().getConfig();

		if (config.contains("locations.end-return")) {
			endReturn = LocationSerializer.deserializeString(Objects.requireNonNull(config.getString("locations.end-return")));
		} else {
			endReturn = new Location(Bukkit.getWorlds().get(0), 0.6, 64, 346.5);
		}
	}

	public static void loadCreeperSpawner() {
		if (creepers != null) {
			return;
		}

		FileConfiguration config = Samurai.getInstance().getConfig();

		if (config.contains("locations.creeper-spawners")) {
			creepers = LocationSerializer.deserializeString(Objects.requireNonNull(config.getString("locations.creeper-spawners")));
		} else {
			creepers = new Location(Bukkit.getWorld("world_the_end"), 0.6, 64, 346.5);
		}
	}

	public static void loadEndexit() {
		if (endExit != null) {
			return;
		}

		FileConfiguration config = Samurai.getInstance().getConfig();

		if (config.contains("locations.end-exit")) {
			endExit = LocationSerializer.deserializeString(Objects.requireNonNull(config.getString("locations.end-exit")));
		} else {
			endExit = new Location(Bukkit.getWorlds().get(0), 0.6, 64, 346.5);
		}
	}

	public static void loadTutorialLoc() {
		if (tutorialLoc != null) {
			return;
		}

		FileConfiguration config = Samurai.getInstance().getConfig();

		if (config.contains("locations.tutorial")) {
			tutorialLoc = LocationSerializer.deserializeString(Objects.requireNonNull(config.getString("locations.tutorial")));
		} else {
			tutorialLoc = new Location(Bukkit.getWorlds().get(0), 0.5, 10, 0.5);
		}
	}

	@EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END && Samurai.getInstance().getMapHandler().isKitMap()) {
			if (event.getEntity() instanceof EnderDragon) {
				event.setCancelled(true);
				((EnderDragon) event.getEntity()).damage(100000);
			}
			return;
		}
		if (event.getEntity() instanceof EnderDragon) {
			event.setCancelled(true);
		}

		event.blockList().clear();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockChange(BlockFromToEvent event) {
		if (event.getBlock().getType() == Material.DRAGON_EGG) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityPortalEnter(EntityPortalEvent event) {
		if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
			event.setCancelled(true);
		}
		if (event.getEntity() instanceof EnderDragon) {
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBedEnter(PlayerBedEnterEvent event) {
		event.setCancelled(true);
		event.getPlayer().sendMessage(ChatColor.RED + "Beds are disabled on this server.");
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onWitherChangeBlock(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Wither || entity instanceof EnderDragon) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if ((event.getWorld().getEnvironment() == World.Environment.NORMAL) && event.getWorld().isClearWeather()) {
			event.setCancelled(true);
		}
	}

/*	@EventHandler
	public void onSpawn(EntitySpawnEvent event) {
		if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
			event.setCancelled(true);
		}
	}*/

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockFade(BlockFadeEvent event) {
		switch (event.getBlock().getType()) {
			case SNOW:
			case ICE:
				event.setCancelled(true);
				break;
			default:
				break;
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD) {
			event.setCancelled(true);
		}
	}

}