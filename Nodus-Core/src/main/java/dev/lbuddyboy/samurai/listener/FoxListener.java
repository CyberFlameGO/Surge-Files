package dev.lbuddyboy.samurai.listener;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.team.allies.AllySetting;
import mkremins.fanciful.FancyMessage;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.bounty.Bounty;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.citadel.CitadelHandler;
import dev.lbuddyboy.samurai.server.RegionData;
import dev.lbuddyboy.samurai.server.ServerHandler;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.claims.Subclaim;
import dev.lbuddyboy.samurai.team.commands.TeamCommands;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.team.track.TeamActionTracker;
import dev.lbuddyboy.samurai.team.track.TeamActionType;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockVector;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.bukkit.ChatColor.*;
import static org.bukkit.Material.*;

public class FoxListener implements Listener {

	private static final Map<BlockVector, UUID> pressurePlates = new ConcurrentHashMap<>();
	public static final ItemStack FIRST_SPAWN_BOOK = new ItemStack(WRITTEN_BOOK);
	public static final ItemStack FIRST_SPAWN_FISHING_ROD = new ItemStack(FISHING_ROD);
	public static final ItemStack FIRST_SPAWN_STEAK = new ItemStack(COOKED_BEEF, 16);
	public static final Set<PotionEffectType> DEBUFFS = ImmutableSet.of(
			PotionEffectType.POISON,
			PotionEffectType.SLOW,
			PotionEffectType.WEAKNESS,
			PotionEffectType.LEVITATION,
			PotionEffectType.DARKNESS,
			PotionEffectType.HARM,
			PotionEffectType.WITHER
	);
	public static final Set<Material> NO_INTERACT_WITH = ImmutableSet.of(LAVA_BUCKET, WATER_BUCKET, BUCKET);
	public static Set<Material> ATTACK_DISABLING_BLOCKS = ImmutableSet.of(GLASS, LEGACY_WOOD_DOOR, IRON_DOOR, LEGACY_FENCE_GATE);
	public static Set<Material> NO_INTERACT = ImmutableSet.of(
			Material.BEACON,
			Material.ACACIA_FENCE_GATE,
			Material.OAK_FENCE_GATE,
			Material.DARK_OAK_FENCE_GATE,
			Material.JUNGLE_FENCE_GATE,
			Material.BIRCH_FENCE_GATE,
			Material.SPRUCE_FENCE_GATE,
			Material.CRIMSON_FENCE_GATE,
			Material.WARPED_FENCE_GATE,
			Material.ACACIA_TRAPDOOR,
			Material.OAK_TRAPDOOR,
			Material.DARK_OAK_TRAPDOOR,
			Material.JUNGLE_TRAPDOOR,
			Material.BIRCH_TRAPDOOR,
			Material.SPRUCE_TRAPDOOR,
			Material.CRIMSON_TRAPDOOR,
			Material.WARPED_TRAPDOOR,
			Material.ACACIA_DOOR,
			Material.OAK_DOOR,
			Material.DARK_OAK_DOOR,
			Material.JUNGLE_DOOR,
			Material.BIRCH_DOOR,
			Material.SPRUCE_DOOR,
			Material.CRIMSON_DOOR,
			Material.WARPED_DOOR,
			Material.ACACIA_BUTTON,
			Material.OAK_BUTTON,
			Material.DARK_OAK_BUTTON,
			Material.JUNGLE_BUTTON,
			Material.BIRCH_BUTTON,
			Material.SPRUCE_BUTTON,
			Material.CRIMSON_BUTTON,
			Material.WARPED_BUTTON,
			Material.IRON_DOOR,
			Material.CHEST,
			Material.TRAPPED_CHEST,
			Material.BARREL,
			Material.FURNACE,
			Material.BREWING_STAND,
			Material.HOPPER,
			Material.DROPPER,
			Material.DISPENSER,
			Material.STONE_BUTTON,
			Material.ENCHANTING_TABLE,
			Material.CRAFTING_TABLE,
			Material.ANVIL,
			Material.LEVER,
			Material.FIRE
	);
	private static final List<UUID> processingTeleportPlayers = new CopyOnWriteArrayList<>();

	static {
		BookMeta bookMeta = (BookMeta) FIRST_SPAWN_BOOK.getItemMeta();

		String serverName = Samurai.getInstance().getServerHandler().getServerName();

		bookMeta.setTitle(GOLD + "Welcome to " + serverName);
		bookMeta.setPages(

				BLUE + "Welcome to " + serverName + "!"

		);
		bookMeta.setAuthor(Samurai.getInstance().getServerHandler().getServerName());

		FIRST_SPAWN_BOOK.setItemMeta(bookMeta);
		FIRST_SPAWN_FISHING_ROD.addEnchantment(Enchantment.LURE, 2);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		processTerritoryInfo(event); // this only works because I'm lucky and PlayerTeleportEvent extends PlayerMoveEvent :0
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getTo() == null) return;

		if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
			return;
		}

		if (ServerHandler.getTasks().containsKey(event.getPlayer().getName())) {
			Samurai.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(event.getPlayer().getName()).getTaskId());
			ServerHandler.getTasks().remove(event.getPlayer().getName());
			event.getPlayer().sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
		}

		processTerritoryInfo(event);
	}

    /*@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerPressurePlate(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.STONE_PLATE) {
            BlockVector vector = event.getClickedBlock().getLocation().toVector().toBlockVector();
    
            if (!pressurePlates.containsKey(vector)) {
                pressurePlates.put(vector, event.getPlayer().getUniqueId()); // when this person steps off the plate, it will be depressed
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerMoveOffPressurePlate(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
    
        if (event.getFrom().getBlock().getType() == Material.STONE_PLATE) {
            BlockVector vector = event.getFrom().toVector().toBlockVector();
    
            if (pressurePlates.containsKey(vector) && event.getPlayer().getUniqueId().equals(pressurePlates.get(vector))) {
                final Block block = event.getFrom().getBlock();
                pressurePlates.remove(vector);
    
                new BukkitRunnable() {
    
                    @Override
                    public void run() {
                        // pop pressure plate up
                        block.setType(Material.STONE_PLATE);
                        block.setData((byte) 0);
                        block.getState().update(true);
                    }
                }.runTaskLater(Foxtrot.getInstance(), 1L);
            }
        }
    }*/

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		Samurai.getInstance().getPlaytimeMap().playerQuit(event.getPlayer().getUniqueId(), true);
	}

	@EventHandler
	public void onEffect(EntityPotionEffectEvent event) {
		if (event.getAction() == EntityPotionEffectEvent.Action.REMOVED || event.getAction() == EntityPotionEffectEvent.Action.REMOVED) return;
		if (event.getCause() != EntityPotionEffectEvent.Cause.POTION_DRINK && event.getCause() != EntityPotionEffectEvent.Cause.POTION_SPLASH) return;
		if (!(event.getEntity() instanceof Player player)) return;
		if (event.getNewEffect().getType() != PotionEffectType.INVISIBILITY) return;

		event.setCancelled(true);
		PotionEffect effect = new PotionEffect(PotionEffectType.INVISIBILITY, event.getNewEffect().getDuration(), event.getNewEffect().getAmplifier(), true, false);
		player.addPotionEffect(effect);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.SPECTATOR) player.setGameMode(GameMode.SURVIVAL);

		Samurai.getInstance().getLastJoinMap().setLastJoin(player.getUniqueId());
		Samurai.getInstance().getPlaytimeMap().playerJoined(event.getPlayer().getUniqueId());

		if (!player.hasPlayedBefore()) {
			
//			player.sendTitle(CC.translate(Foxtrot.getInstance().getAnimationHandler().getTitle()), CC.translate("&fWelcome to Reforged!"));
			Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
				player.sendTitle(CC.translate("&6&lTUTORIAL"), CC.translate("&fCheck out our tutorial &7(/tutorial)"));
			}, 105);

			Samurai.getInstance().getFirstJoinMap().setFirstJoin(player.getUniqueId());
			FrozenEconomyHandler.setBalance(player.getUniqueId(), 1000.0);

			if (!Samurai.getInstance().getMapHandler().isKitMap()) {
				player.getInventory().addItem(FIRST_SPAWN_BOOK);
                player.getInventory().addItem(FIRST_SPAWN_FISHING_ROD);
                player.getInventory().addItem(FIRST_SPAWN_STEAK);
			}

			if (SOTWCommand.getCustomTimers().get("&a&lSOTW") == null) {
				if (Samurai.getInstance().getServerHandler().isStartingTimerEnabled()) {
					Samurai.getInstance().getPvPTimerMap().createStartingTimer(player.getUniqueId(), (int) TimeUnit.HOURS.toSeconds(1));
				} else {
					Samurai.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
				}
			}

			player.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());

		} else {
//			player.sendTitle(CC.translate(Foxtrot.getInstance().getAnimationHandler().getTitle()), CC.translate("&fWelcome back to Reforged!"));
		}

	}

	@EventHandler
	public void onBookDrop(PlayerDropItemEvent event) {
		if (event.getItemDrop().getItemStack().equals(FIRST_SPAWN_BOOK)) {
			event.getItemDrop().remove(); // kill the book
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onStealthPickaxe(BlockBreakEvent event) {
		Block block = event.getBlock();
		ItemStack inHand = event.getPlayer().getItemInHand();
		if (inHand.getType() == GOLDEN_PICKAXE && inHand.hasItemMeta()) {
			if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
				event.setCancelled(true);

				block.breakNaturally(inHand);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onStealthItemPickup(PlayerPickupItemEvent event) {
		ItemStack inHand = event.getPlayer().getItemInHand();
		if (inHand.getType() == GOLDEN_PICKAXE && inHand.hasItemMeta()) {
			if (inHand.getItemMeta().getDisplayName().startsWith(ChatColor.AQUA.toString())) {
				event.setCancelled(true);
				event.getPlayer().getInventory().addItem(event.getItem().getItemStack());
				event.getItem().remove();
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) return;
		if (event.getEntity() instanceof Warden) return;

		event.setDamage(event.getDamage() * 2.50);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();

			if (ServerHandler.getTasks().containsKey(player.getName())) {
				Samurai.getInstance().getServer().getScheduler().cancelTask(ServerHandler.getTasks().get(player.getName()).getTaskId());
				ServerHandler.getTasks().remove(player.getName());
				player.sendMessage(YELLOW.toString() + BOLD + "LOGOUT " + RED.toString() + BOLD + "CANCELLED!");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onProjectileInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			if (event.getItem().getType() == POTION) {
				try { // Ensure that any errors with Potion.fromItemStack don't mess with the rest of the code.
					ItemStack i = event.getItem();

					// We can't run Potion.fromItemStack on a water bottle.
					if (i.getDurability() != (short) 0) {
						Potion pot = Potion.fromItemStack(i);

						if (pot != null && pot.isSplash() && pot.getType() != null && DEBUFFS.contains(pot.getType().getEffectType())) {
							if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
								player.sendMessage(RED + "You cannot do this while your PVP Timer is active!");
								player.sendMessage(RED + "Type '" + GRAY + "/pvp enable" + RED + "' to remove your timer.");
								event.setCancelled(true);
								return;
							}

							if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
								event.setCancelled(true);
								event.getPlayer().sendMessage(RED + "You cannot launch debuffs from inside spawn!");
								event.getPlayer().updateInventory();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (event.getClickedBlock() == null) {
			return;
		}

		if (event.getClickedBlock().getType() == ENCHANTING_TABLE && event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (event.getItem() != null) {
				if (event.getItem().getType() == ENCHANTED_BOOK) {
					event.getItem().setType(BOOK);

					event.getPlayer().sendMessage(GREEN + "You reverted this book to its original form!");
					event.setCancelled(true);
				}
			}

			return;
		}

		if (Samurai.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getClickedBlock().getLocation()) || Samurai.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
			return;
		}

		if (Samurai.getInstance().getServerHandler().isEOTW()) {
			return;
		}

		Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());

		if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
			if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
				if (team.isAlly(event.getPlayer().getUniqueId())) {
					if (team.getAllySettings().contains(AllySetting.ALLY_BUILD_CLAIM)) return;
				}

				if (event.getClickedBlock().getType().name().contains("BUTTON") || event.getClickedBlock().getType().name().contains("CHEST") || event.getClickedBlock().getType().name().contains("SHULKER") || event.getClickedBlock().getType().name().contains("DOOR")) {
					CitadelHandler citadelHandler = Samurai.getInstance().getCitadelHandler();

					if (DTRBitmask.CITADEL.appliesAt(event.getClickedBlock().getLocation()) && citadelHandler.canLootCitadel(event.getPlayer())) {
						return;
					}
					if (DTRBitmask.KOTH.appliesAt(event.getClickedBlock().getLocation()) && team.getName().equals(VaultHandler.TEAM_NAME))
						return;

					if (DTRBitmask.ROAD.appliesAt(event.getClickedBlock().getLocation()) && team.getName().equals("BufferZone"))
						return;
				}

				if (event.getItem() != null && event.getItem().getType() == SPLASH_POTION) {
					splashPotion(player, event.getItem());
				}

				if (event.getItem() != null && event.getItem().getType() == ENDER_PEARL) {
					EnderPearl pearl = player.launchProjectile(EnderPearl.class);
					pearl.setShooter(player);
					player.setCooldown(Material.ENDER_PEARL, 1);
					pearl.setMetadata("fencegateclick", new FixedMetadataValue(Samurai.getInstance(), true));
				}

				if (event.getItem() != null && event.getItem().getType() == SNOWBALL) {
					Snowball pearl = player.launchProjectile(Snowball.class);
					pearl.setShooter(player);
				}

				if (event.getItem() != null && event.getItem().getType() == EGG) {
					Egg pearl = player.launchProjectile(Egg.class);
					pearl.setShooter(player);
				}

				event.setCancelled(true);
				event.getPlayer().sendMessage(YELLOW + "You cannot do this in " + team.getName(event.getPlayer()) + YELLOW + "'s territory.");

				if (event.getMaterial() == LEGACY_TRAP_DOOR || event.getMaterial() == LEGACY_FENCE_GATE || event.getMaterial().name().contains("DOOR")) {
					Samurai.getInstance().getServerHandler().disablePlayerAttacking(event.getPlayer(), 1);
				}

				return;
			}

			if (event.getAction() == Action.PHYSICAL) {
				event.setCancelled(true);
			}
		} else if (event.getMaterial() == LAVA_BUCKET) {
			if (team == null || !team.isMember(event.getPlayer().getUniqueId())) {
				if (team.isAlly(event.getPlayer().getUniqueId())) {
					if (team.getAllySettings().contains(AllySetting.ALLY_BUILD_CLAIM)) return;
				}

				event.setCancelled(true);
				event.getPlayer().sendMessage(RED + "You can only do this in your own claims!");
			}
		} else {
			UUID uuid = player.getUniqueId();

			if (team != null && !team.isCaptain(uuid) && !team.isCoLeader(uuid) && !team.isOwner(uuid)) {
				Subclaim subclaim = team.getSubclaim(event.getClickedBlock().getLocation());

				if (subclaim != null && !subclaim.isMember(event.getPlayer().getUniqueId())) {
					if (NO_INTERACT.contains(event.getClickedBlock().getType()) || NO_INTERACT_WITH.contains(event.getMaterial())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(YELLOW + "You do not have access to the subclaim " + GREEN + subclaim.getName() + YELLOW + "!");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignInteract(final PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getState() instanceof Sign) {
				Sign s = (Sign) event.getClickedBlock().getState();

				if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
					if (s.getLine(0).contains("Kit")) {
						Samurai.getInstance().getServerHandler().handleKitSign(s, event.getPlayer());
					} else if (s.getLine(0).contains("Buy") || s.getLine(0).contains("Sell")) {
						Samurai.getInstance().getServerHandler().handleShopSign(s, event.getPlayer());
					}

					event.setCancelled(true);
				}
			}
		}

		if (event.getItem() != null && event.getMaterial() == LEGACY_SIGN) {
			if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().getLore() != null) {
				ArrayList<String> lore = (ArrayList<String>) event.getItem().getItemMeta().getLore();

				if (lore.size() > 1 && lore.get(1).contains("§e")) {
					if (event.getClickedBlock() != null) {
						event.getClickedBlock().getRelative(event.getBlockFace()).getState().setMetadata("noSignPacket", new FixedMetadataValue(Samurai.getInstance(), true));

						new BukkitRunnable() {

							@Override
							public void run() {
								event.getClickedBlock().getRelative(event.getBlockFace()).getState().removeMetadata("noSignPacket", Samurai.getInstance());
							}

						}.runTaskLater(Samurai.getInstance(), 20L);
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSignPlace(BlockPlaceEvent event) {
		ItemStack hand = event.getItemInHand();
		if (hand.getType() == OAK_SIGN) {
			if (hand.hasItemMeta() && hand.getItemMeta().getLore() != null) {

				ArrayList<String> lore = (ArrayList<String>) hand.getItemMeta().getLore();

				Sign s = (Sign) event.getBlock().getState();

				for (int i = 0; i < 4; i++) {
					s.setLine(i, lore.get(i));
				}

				s.setMetadata("deathSign", new FixedMetadataValue(Samurai.getInstance(), true));
				s.update();

				event.getPlayer().closeInventory();
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpawnerPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();

		ItemStack hand = event.getItemInHand();
		if (hand.getType() == SPAWNER) {
			if (!(event.isCancelled())) {
				if (hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
					String name = stripColor(hand.getItemMeta().getDisplayName());
					String entName = name.replace(" Spawner", "");
					EntityType type = EntityType.valueOf(entName.toUpperCase().replaceAll(" ", "_"));

					CreatureSpawner spawner = (CreatureSpawner) block.getState();
					spawner.setSpawnedType(type);
					spawner.update();

					event.getPlayer().sendMessage(AQUA + "You placed a " + entName + " spawner!");
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (e.getBlock().getState().hasMetadata("deathSign") || ((Sign) e.getBlock().getState()).getLine(1).contains("§e")) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == OAK_SIGN || e.getBlock().getType() == OAK_WALL_SIGN) {
			if (e.getBlock().getState().hasMetadata("deathSign") || ((e.getBlock().getState() instanceof Sign && ((Sign) e.getBlock().getState()).getLine(1).contains("§e")))) {
				e.setCancelled(true);

				Sign sign = (Sign) e.getBlock().getState();

				ItemStack deathsign = new ItemStack(OAK_SIGN);
				ItemMeta meta = deathsign.getItemMeta();

				if (sign.getLine(1).contains("Captured")) {
					meta.setDisplayName("§dKOTH Capture Sign");
				} else {
					meta.setDisplayName("§dDeath Sign");
				}

				meta.setLore(Arrays.asList(sign.getLines()));
				deathsign.setItemMeta(meta);
				e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), deathsign);

				e.getBlock().setType(AIR);
				e.getBlock().getState().removeMetadata("deathSign", Samurai.getInstance());
			}
		}
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getEntityType() == EntityType.ENDERMAN || event.getEntityType() == EntityType.CREEPER) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		SpawnTagHandler.removeTag(event.getEntity());
		Team playerTeam = Samurai.getInstance().getTeamHandler().getTeam(event.getEntity());
		Player killer = event.getEntity().getKiller();

		if (Samurai.getInstance().getMapHandler().getDuelHandler().isInDuel(event.getEntity())) {
			event.setDeathMessage(null);
			return;
		}

		if (event.getEntity().hasMetadata("gaming")) {
			event.setDeathMessage(null);
			return;
		}

		if (Samurai.getInstance().getMapHandler().getGameHandler() != null
				&& Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()
				&& Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame().isPlayingOrSpectating(event.getEntity().getUniqueId())) {
			event.setDeathMessage(null);
			return;
		}

		if (killer != null) {
			Team killerTeam = Samurai.getInstance().getTeamHandler().getTeam(killer);
			Location deathLoc = event.getEntity().getLocation();
			int deathX = deathLoc.getBlockX();
			int deathY = deathLoc.getBlockY();
			int deathZ = deathLoc.getBlockZ();

			if (killerTeam != null) {
				TeamActionTracker.logActionAsync(killerTeam, TeamActionType.MEMBER_KILLED_ENEMY_IN_PVP, ImmutableMap.of("playerId", killer.getUniqueId(), "playerName", killer.getName(), "killedId", event.getEntity().getUniqueId(), "killedName", event.getEntity().getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));
			}

			if (playerTeam != null) {
				TeamActionTracker.logActionAsync(playerTeam, TeamActionType.MEMBER_KILLED_BY_ENEMY_IN_PVP, ImmutableMap.of("playerId", event.getEntity().getUniqueId(), "playerName", event.getEntity().getName(), "killerId", killer.getUniqueId(), "killerName", killer.getName(), "coordinates", deathX + ", " + deathY + ", " + deathZ));

			}

			// Add kills to sword lore if the victim does not equal the killer
			if (!event.getEntity().equals(killer)) {
				ItemStack hand = killer.getItemInHand();

				if (hand != null && (hand.getType().name().contains("SWORD") || hand.getType() == BOW)) {
					InventoryUtils.addKill(hand, killer.getDisplayName() + YELLOW + " " + (hand.getType() == BOW ? "shot" : "killed") + " " + event.getEntity().getDisplayName());
				}
			}

			if (Samurai.getInstance().getMapHandler().isKitMap()) {
				Bounty bounty = Samurai.getInstance().getMapHandler().getBountyManager().removeBounty(event.getEntity());

				if (bounty != null) {
					Samurai.getInstance().getShardMap().addShards(killer.getUniqueId(), bounty.getShards(), true);
					Bukkit.broadcastMessage(CC.GRAY + "[" + CC.GOLD + "Bounty" + CC.GRAY + "] " + killer.getDisplayName() + CC.YELLOW + " killed "
							+ event.getEntity().getDisplayName() + CC.YELLOW + " and received the bounty of "
							+ CC.GREEN + bounty.getShards() + " shards" + CC.YELLOW + "!");
				}
			}
		}

		if (playerTeam != null) {
			playerTeam.playerDeath(event.getEntity().getName(), playerTeam.getDTR(), Samurai.getInstance().getServerHandler().getDTRLoss(event.getEntity()), killer);
		}

		if (killer == null || (!event.getEntity().equals(killer))) {
			// Add deaths to armor
			String deathMsg = YELLOW + event.getEntity().getName() + RESET + " " + (event.getEntity().getKiller() != null ? "killed by " + YELLOW + event.getEntity().getKiller().getName() : "died") + " " + GOLD +
					InventoryUtils.DEATH_TIME_FORMAT.format(new Date());

			for (ItemStack armor : event.getEntity().getInventory().getArmorContents()) {
				if (armor != null && armor.getType() != AIR) {
					InventoryUtils.addDeath(armor, deathMsg);
				}
			}
		}

		// Transfer money
		double bal = FrozenEconomyHandler.getBalance(event.getEntity().getUniqueId());
		FrozenEconomyHandler.withdraw(event.getEntity().getUniqueId(), bal);

		// Only tell player they earned money if they actually earned something
		if ((killer = event.getEntity().getKiller()) != null && !Double.isNaN(bal) && bal > 0) {
			FrozenEconomyHandler.deposit(killer.getUniqueId(), bal);
			killer.sendMessage(GOLD + "You were rewarded " + BOLD + "$" + bal + GOLD + " due to " + event.getEntity().getDisplayName() + GOLD + " having the funds!");
		}
	}

	private void processTerritoryInfo(PlayerMoveEvent event) {
		Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

		if (Samurai.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {

            /*
            //prevent stack overflow
            if (ownerTo != null && ownerTo.getKitName().equalsIgnoreCase("spawn")) {
                return;
            }
            
            //prevent staff from being teleported during the claiming process
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                return;
            }
            */

			if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getTo())) {

				if (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo())) {
					Samurai.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());

					event.getPlayer().sendMessage(ChatColor.RED + "Your PvP Protection has been removed for entering claimed land.");
				} else if (ownerTo != null && ownerTo.getOwner() != null) {
					if (!ownerTo.getMembers().contains(event.getPlayer().getUniqueId())) {
						event.setCancelled(true);

						for (Claim claim : ownerTo.getClaims()) {
							if (claim.contains(event.getFrom()) && !ownerTo.isMember(event.getPlayer().getUniqueId())) {
								Location nearest = TeamCommands.nearestSafeLocation(event.getPlayer().getLocation());
								boolean spawn = false;

								if (nearest == null) {
									nearest = Samurai.getInstance().getServerHandler().getSpawnLocation();
									spawn = true;
								}

								event.getPlayer().teleport(nearest);
								event.getPlayer().sendMessage(ChatColor.RED + "Moved you to " + (spawn ? "spawn" : "nearest unclaimed territory") + " because you were in land that was claimed.");
								return;
							}
						}

						event.getPlayer().sendMessage(ChatColor.RED + "You cannot enter another team's territory with PvP Protection.");
						event.getPlayer().sendMessage(ChatColor.RED + "Use " + ChatColor.YELLOW + "/pvp enable" + ChatColor.RED + " to remove your protection.");
						return;
					}
				}
			}
		}

		Team ownerFrom = LandBoard.getInstance().getTeam(event.getFrom());

		if (ownerFrom != ownerTo) {
			ServerHandler sm = Samurai.getInstance().getServerHandler();
			RegionData from = sm.getRegion(ownerFrom, event.getFrom());
			RegionData to = sm.getRegion(ownerTo, event.getTo());

			if (from.equals(to)) return;

			if (!to.getRegionType().getMoveHandler().handleMove(event)) {
				return;
			}

			boolean fromReduceDeathban = from.getData() != null && (from.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || from.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));
			boolean toReduceDeathban = to.getData() != null && (to.getData().hasDTRBitmask(DTRBitmask.FIVE_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.FIFTEEN_MINUTE_DEATHBAN) || to.getData().hasDTRBitmask(DTRBitmask.SAFE_ZONE));

			if (fromReduceDeathban && from.getData() != null) {
				Event fromLinkedKOTH = Samurai.getInstance().getEventHandler().getEvent(from.getData().getName());

				if (fromLinkedKOTH != null && !fromLinkedKOTH.isActive()) {
					fromReduceDeathban = false;
				}
			}

			if (toReduceDeathban && to.getData() != null) {
				Event toLinkedKOTH = Samurai.getInstance().getEventHandler().getEvent(to.getData().getName());

				if (toLinkedKOTH != null && !toLinkedKOTH.isActive()) {
					toReduceDeathban = false;
				}
			}

			if (Samurai.getInstance().getToggleClaimMessageMap().areClaimMessagesEnabled(event.getPlayer().getUniqueId())) {
				// create leaving message

				String leaving = "&eNow leaving: " + from.getName(event.getPlayer()) + (fromReduceDeathban ? " &a(Non-Deathban)" : " &c(Deathban)");

				FancyMessage changed = new FancyMessage(" ");

				if (ownerFrom != null) {
					changed.then(CC.translate(leaving)).command("/t i " + ownerFrom.getName()).tooltip(GREEN + "View team info");
				} else {
					changed.then(CC.translate(leaving));
				}

				changed.then(", ").color(YELLOW);

				String entering = "&eNow entering: " + to.getName(event.getPlayer()) + (toReduceDeathban ? " &a(Non-Deathban)" : " &c(Deathban)");

				if (ownerTo != null) {
					changed.then(CC.translate(entering)).command("/t i " + ownerTo.getName()).tooltip(GREEN + "View team info");
				} else {
					changed.then(CC.translate(entering));
				}

				changed.send(event.getPlayer());

			}

			if (event.getPlayer().hasMetadata("modmode")) {
				return;
			}

			if (SOTWCommand.isSOTWTimer()) {
				if (DTRBitmask.SAFE_ZONE.appliesAt(event.getTo()) && !(DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom()))) {
					for (Player target : Bukkit.getOnlinePlayers()) {
						target.hidePlayer(Samurai.getInstance(), event.getPlayer());
					}
				}
				if (DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom()) && !(DTRBitmask.SAFE_ZONE.appliesAt(event.getTo()))) {
					for (Player target : Bukkit.getOnlinePlayers()) {
						target.showPlayer(Samurai.getInstance(), event.getPlayer());
					}
				}
			}

		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasMetadata("modmode")) {
			return;
		}
		if (SOTWCommand.isSOTWTimer()) {
			if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (DTRBitmask.SAFE_ZONE.appliesAt(target.getLocation())) {
						event.getPlayer().hidePlayer(Samurai.getInstance(), target);
					}
					target.hidePlayer(Samurai.getInstance(), event.getPlayer());
				}
			} else {
				for (Player target : Bukkit.getOnlinePlayers()) {
					if (DTRBitmask.SAFE_ZONE.appliesAt(target.getLocation())) {
						event.getPlayer().hidePlayer(Samurai.getInstance(), target);
					}
				}
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.getPlayer().hasMetadata("modmode")) {
			return;
		}
		if (SOTWCommand.isSOTWTimer()) {
			if (DTRBitmask.SAFE_ZONE.appliesAt(event.getTo()) && !(DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom()))) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					target.hidePlayer(Samurai.getInstance(), event.getPlayer());
				}
			}
			if (DTRBitmask.SAFE_ZONE.appliesAt(event.getFrom()) && !(DTRBitmask.SAFE_ZONE.appliesAt(event.getTo()))) {
				for (Player target : Bukkit.getOnlinePlayers()) {
					target.showPlayer(Samurai.getInstance(), event.getPlayer());
				}
			}
		}
	}

	public void splashPotion(Player player, ItemStack itemStack) {
		if (player.getInventory().getItemInMainHand().getAmount() <= 1) {
			player.getInventory().setItemInMainHand(null);
		} else {
			player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
		}
		ThrownPotion pot = player.launchProjectile(ThrownPotion.class);
		pot.setItem(itemStack);
	}
}
