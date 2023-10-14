package dev.lbuddyboy.samurai.custom.vaults;

import dev.lbuddyboy.samurai.custom.vaults.listener.VaultListener;
import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.boosters.TeamBoosterType;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.util.object.CuboidRegion;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.object.Reward;
import dev.lbuddyboy.samurai.util.object.YamlDoc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 5:36 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.vaults
 */

@Data
public class VaultHandler {

	public static String PREFIX = "&x&7&a&9&9&c&1&l[VAULT]";
	public static String TEAM_NAME = "Vault";

	private KOTH cap = null;
	private final YamlDoc config;
	private final List<Reward> rewards;
	private ItemStack key;

	private List<ItemStack> loottable;
	private Team systemTeam;
	private Team capping;
	private Location crateLocation;
	private VaultStage vaultStage = VaultStage.CLOSED;

	public VaultHandler() {
		this.loottable = new ArrayList<>();
		this.rewards = new ArrayList<>();
		this.config = new YamlDoc(Samurai.getInstance().getDataFolder(), "vault-loot.yml");

		loottable.addAll(ItemStackSerializer.deserializeConfig(getConfig().getStringList("loot")));

		reload();

		this.rewards.addAll(Arrays.asList(
				new Reward("&2&lFull DTR Regeneration", true).addCommand("startdtrregen {playerName}"),
				new Reward("&c&lx2 Kill Point Booster &7(Lasts 30 minutes)", true).addCommand("giveteambooster {playerName} " + TeamBoosterType.X2_KILL_POINT.name() + " 30m"),
				new Reward("&5&l20 minute DTR Regeneration &7(Lasts 30 minutes)", true).addCommand("giveteambooster {playerName} " + TeamBoosterType.TWENTY_MIN_DTR_REGEN.name() + " 30m")
		));


		Samurai.getInstance().getServer().getPluginManager().registerEvents(new VaultListener(), Samurai.getInstance());
	}

	public void reload() {
		if (Samurai.getInstance().getTeamHandler().getTeam(TEAM_NAME) != null) {
			this.systemTeam = Samurai.getInstance().getTeamHandler().getTeam(TEAM_NAME);
		}
		if (this.systemTeam != null) {
			if (Samurai.getInstance().getEventHandler().getEvent(TEAM_NAME) == null) {
				this.cap = new KOTH("Vault", this.systemTeam.getHQ());
				this.cap.setCapDistance(8);
				this.cap.setCapTime(60 * 8);
			} else {
				this.cap = (KOTH) Samurai.getInstance().getEventHandler().getEvent(TEAM_NAME);
			}
			Bukkit.getConsoleSender().sendMessage(CC.translate("&x&7&a&9&9&c&1[Vault]&a Successfully loaded the &6Vault Event&f."));
		} else {
			Bukkit.getConsoleSender().sendMessage(CC.translate("&x&7&a&9&9&c&1[Vault]&c UnSuccessfully loaded the &6Vault Event&f."));
		}
		if (this.systemTeam != null) {
			this.systemTeam.getClaims().forEach(claim -> {
				for (Location loc : new CuboidRegion(TEAM_NAME, claim.getMinimumPoint(), claim.getMaximumPoint())) {
					if (loc.getBlock().getType() == Material.DAYLIGHT_DETECTOR) {
						this.crateLocation = loc;
					}
				}
			});
		}
		this.key = new ItemBuilder(Material.getMaterial(getConfig().getString("key.material")))
				.displayName(CC.translate(getConfig().getString("key.name")))
				.lore(CC.translate(getConfig().getStringList("key.lore")))
				.build();
	}

	public void setVaultStage(VaultStage vaultStage) {
		if (this.vaultStage == vaultStage) return;
		this.vaultStage = vaultStage;
		Team team = Samurai.getInstance().getTeamHandler().getTeam(TEAM_NAME);
		if (team != null) {
			for (Claim claim : team.getClaims()) {
				for (Player player : claim.getPlayers()) {
					player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 20f, 20f);
				}
			}
		}
	}

	public void saveLoot(List<ItemStack> stacks) {
		this.loottable = stacks.stream().filter(s -> s != null && !s.getType().equals(Material.AIR) || s != null).collect(Collectors.toList());

		getConfig().set("loot", null);

		getConfig().set("loot", ItemStackSerializer.serializeList(loottable));
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLoot(ItemStack stack) {
		this.loottable.add(stack);

		getConfig().set("loot", ItemStackSerializer.serializeList(loottable));
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLoot(List<ItemStack> stacks) {
		this.loottable = stacks.stream().filter(s -> s != null && !s.getType().equals(Material.AIR) || s != null).collect(Collectors.toList());

		getConfig().set("loot", ItemStackSerializer.serializeList(loottable));
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isContested() {
		if (this.capping == null) return false;
		Team team = Samurai.getInstance().getTeamHandler().getTeam(TEAM_NAME);
		if (team == null) return false;

		List<Claim> claims = team.getClaims();
		boolean contested = false;
		for (Claim claim : claims) {
			for (Player player : claim.getPlayers()) {
				if (ModUtils.isModMode(player) || ModUtils.isInvisible(player)) continue;
				Team contesterTeam = Samurai.getInstance().getTeamHandler().getTeam(player);
				if (!this.capping.getOnlineMembers().contains(player) && contesterTeam != null && !this.capping.getAllies().isEmpty() && !this.capping.getAllies().contains(contesterTeam.getUniqueId())) {
					contested = true;
				}
			}
		}
		return contested;
	}

	public YamlConfiguration getConfig() {
		return this.config.gc();
	}

}
