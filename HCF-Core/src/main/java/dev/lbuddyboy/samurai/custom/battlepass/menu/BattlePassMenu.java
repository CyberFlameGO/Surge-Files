package dev.lbuddyboy.samurai.custom.battlepass.menu;

import dev.lbuddyboy.samurai.custom.battlepass.BattlePassHandler;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.menu.ChallengesMenu;
import dev.lbuddyboy.samurai.custom.battlepass.reward.BattlePassReward;
import dev.lbuddyboy.samurai.custom.battlepass.tier.Tier;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.object.IntRange;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.BattlePassProgress;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.Formats;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class BattlePassMenu extends Menu {

	private static BattlePassHandler handler = Samurai.getInstance().getBattlePassHandler();

	private final BattlePassProgress progress;
	private int page = 1;
	private int[] SLOTS_PREMIUM = {14, 23, 32, 41, 50};
	private int[] SLOTS_TIERS = {15, 24, 33, 42, 51};
	private int[] SLOTS_DAILY = {16, 25, 34, 43, 52};

	@Override
	public String getTitle(Player player) {
		return "Seasonal Pass (Page: " + page + "/" + getMaxPages() + ")";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(5, new GoToChallengesButton(false));
		buttons.put(20, new InfoButton());
		buttons.put(7, new GoToChallengesButton(true));
		buttons.put(38, new ClaimAllRewardsButton());

		int[] ints;
		if (page == 1) {
			ints = IntStream.rangeClosed(1, 5).toArray();
		} else {
			ints = IntStream.rangeClosed(((page - 1) * 5) + 1, page * 5).toArray();
		}

		int slotOffset = 0;
		for (int tierNumber : ints) {
			Tier tier = handler.getTier(tierNumber);
			if (tier != null) {
				buttons.put(SLOTS_PREMIUM[slotOffset], tier.getPremiumReward() != null ? new RewardButton(tier, tier.getPremiumReward()) : new EmptyRewardButton(tier));
				buttons.put(SLOTS_TIERS[slotOffset], new TierButton(tier));
				buttons.put(SLOTS_DAILY[slotOffset], tier.getFreeReward() != null ? new RewardButton(tier, tier.getFreeReward()) : new EmptyRewardButton(tier));
				++slotOffset;
				if (slotOffset >= SLOTS_PREMIUM.length) {
					slotOffset = 0;
				}
			}
		}

		buttons.put(31, new PreviousPageButton());
		buttons.put(35, new NextPageButton());

		for (int i = 0; i < 54; i++) {
			if (!buttons.containsKey(i)) {
				buttons.put(i, Button.placeholder(Material.LEGACY_STAINED_GLASS_PANE, (byte) 15, " "));
			}
		}

		return buttons;
	}

	private class InfoButton extends Button {
		@Override
		public String getName(Player player) {
			return CC.translate("&g&lSeasonal Pass");
		}

		@Override
		public List<String> getDescription(Player player) {
			return CC.translate(Arrays.asList(
					"&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &gCurrent Tier&7: &f" + progress.getCurrentTier().getNumber(),
					"&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &gCurrent XP&7: &f" + Formats.formatNumber(progress.getExperience()),
					"&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &gPass Type&7: &f" + (Samurai.getInstance().getBattlePassHandler().fetchProgress(player.getUniqueId()).isPremium() ? "Premium" : "Free")
			));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.NAME_TAG;
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			ItemStack itemStack = super.getButtonItem(player);
			itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
			ItemMeta meta = itemStack.getItemMeta();
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			itemStack.setItemMeta(meta);
			return itemStack;
		}
	}

	private class ClaimAllRewardsButton extends Button {

		@Override
		public String getName(Player player) {
			return CC.translate("&a&lClaim Seasonal Pass Rewards");
		}

		@Override
		public List<String> getDescription(Player player) {
			return CC.translate(Collections.singletonList("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " Click to claim all of your seasonal pass rewards"));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.CHEST;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			for (Tier tier : Samurai.getInstance().getBattlePassHandler().getTiers()) {

				if ((tier.getNumber() == 1 && progress.getExperience() < tier.getRequiredExperience()) || tier.getRequiredExperience() < tier.getRequiredExperience()) {
					continue;
				}

				if (progress.getExperience() >= tier.getRequiredExperience()) {
					if (!progress.getClaimedRewardsFree().contains(tier)) {
						progress.getClaimedRewardsFree().add(tier);
						progress.requiresSave();

						tier.getFreeReward().execute(player);
					}
					if (progress.isPremium() && !progress.getClaimedRewardsPremium().contains(tier)) {
						progress.getClaimedRewardsPremium().add(tier);
						progress.requiresSave();

						tier.getPremiumReward().execute(player);
					}
				}

			}
		}
	}

	@AllArgsConstructor
	private class GoToChallengesButton extends Button {
		private boolean daily;

		@Override
		public String getName(Player player) {
			if (daily) {
				return CC.translate("&g&lDaily Challenges");
			} else {
				return CC.translate("&g&lPremium Challenges");
			}
		}

		@Override
		public List<String> getDescription(Player player) {
			if (!daily && !progress.isPremium()) {
				return CC.translate(Arrays.asList(
						" ",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &cNo permission",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &cPurchase this on store.minesurge.org",
						" ",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fClick view more information."
				));
			} else {
				if (daily) {
					return CC.translate(Collections.singletonList("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &fClick to view all of the daily seasonal pass challenges"));
				} else {
					return CC.translate(Collections.singletonList("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &fClick to view all of the premium seasonal pass challenges"));
				}
			}
		}

		@Override
		public Material getMaterial(Player player) {
			if (daily) {
				return Material.GOLD_INGOT;
			} else {
				return Material.DIAMOND;
			}
		}

		@Override
		public ItemStack getButtonItem(Player player) {

			String url = daily ? "b86b9d58bcd1a555f93e7d8659159cfd25b8dd6e9bce1e973822824291862" : "acd70ce4818581ca47adf6b81679fd1646fd687c7127fdaae94fed640155e";

			return CC.getCustomHead(getName(player), 1, url, getDescription(player));
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (!daily && !progress.isPremium()) {
				player.sendMessage(CC.RED + "You don't have access to the premium challenges! Purchase on our store at https://store.minesurge.org.");
				return;
			}

			new ChallengesMenu(daily, progress).openMenu(player);
		}
	}

	@AllArgsConstructor
	private class TierButton extends Button {
		private Tier tier;

		@Override
		public String getName(Player player) {
			return CC.MAIN + CC.BOLD + "Tier " + tier.getNumber();
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.singletonList(Formats.formatExperience(Math.min(progress.getExperience(), tier.getRequiredExperience())) + CC.GRAY + "/" + Formats.formatExperience(tier.getRequiredExperience()));
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.LEGACY_STAINED_GLASS_PANE;
		}

		@Override
		public byte getDamageValue(Player player) {
			if (progress.getExperience() >= tier.getRequiredExperience()) {
				return 5;
			} else if ((tier.getNumber() == 1 && progress.getExperience() < tier.getRequiredExperience()) || progress.getCurrentTier().getNumber() == tier.getNumber()) {
				return 4;
			} else {
				return 14;
			}
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			if (progress.getExperience() >= tier.getRequiredExperience()) {
				return CC.getCustomHead(getName(player), 1, "b8fff22c6e6546d0d8eb7f9763398407dd2ab80f74fe3d16b10a983ecaf347e", getDescription(player));
			} else if ((tier.getNumber() == 1 && progress.getExperience() < tier.getRequiredExperience()) || progress.getCurrentTier().getNumber() == tier.getNumber()) {
				return CC.getCustomHead(getName(player), 1, "c641682f43606c5c9ad26bc7ea8a30ee47547c9dfd3c6cda49e1c1a2816cf0ba", getDescription(player));
			} else {
				return CC.getCustomHead(getName(player), 1, "5fde3bfce2d8cb724de8556e5ec21b7f15f584684ab785214add164be7624b", getDescription(player));
			}
		}
	}

	@AllArgsConstructor
	private class RewardButton extends Button {
		private Tier tier;
		private BattlePassReward reward;

		@Override
		public String getName(Player player) {
			if (progress.getExperience() >= tier.getRequiredExperience()) {
				if (reward.hasClaimed(progress)) {
					return CC.GREEN + CC.BOLD + "Redeemed";
				} else {
					if (!reward.isFreeReward() && !progress.isPremium()) {
						return CC.DARK_RED + CC.BOLD + "Locked";
					} else {
						return CC.YELLOW + CC.BOLD + "Redeem";
					}
				}
			} else {
				return CC.DARK_RED + CC.BOLD + "Locked";
			}
		}

		@Override
		public List<String> getDescription(Player player) {
			List<String> description = new ArrayList<>();

			for (String line : reward.getText()) {
				description.add(" " + CC.GRAY + SymbolUtil.UNICODE_ARROW_RIGHT + " " + line);
			}

			if (!reward.isFreeReward() && !progress.isPremium()) {
				description.addAll(Arrays.asList(" ",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &cNo permission",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &cPurchase this on store.minesurge.org"));
			}

			return description;
		}

		@Override
		public Material getMaterial(Player player) {
			if (progress.getExperience() >= tier.getRequiredExperience()) {
				if (reward.hasClaimed(progress)) {
					return Material.MINECART;
				} else {
					if (!reward.isFreeReward() && !progress.isPremium()) {
						return Material.HOPPER_MINECART;
					} else {
						return Material.CHEST_MINECART;
					}
				}
			} else {
				return Material.HOPPER_MINECART;
			}
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			if (progress.getExperience() >= tier.getRequiredExperience()) {
				if (reward.hasClaimed(progress)) {
					return CC.getCustomHead(getName(player), 1, "e3ba7a9d97c52418e9d84654adb04aba6f30b67608ffe041661db3c754c07843", getDescription(player));
				} else {
					if (!reward.isFreeReward() && !progress.isPremium()) {
						return CC.getCustomHead(getName(player), 1, "8199b5ee320e7997d91bb5f8665f3d32ae4920e03c6b3d9b7eeca697119997", getDescription(player));
					} else {
						return CC.getCustomHead(getName(player), 1, "99902499d2a53b6c8b07129b93fbccadd4c29a44388cb261a68d7b71899f78ca", getDescription(player));
					}
				}
			} else {
				return CC.getCustomHead(getName(player), 1, "8199b5ee320e7997d91bb5f8665f3d32ae4920e03c6b3d9b7eeca697119997", getDescription(player));
			}
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if ((tier.getNumber() == 1 && progress.getExperience() < tier.getRequiredExperience()) || tier.getRequiredExperience() < tier.getRequiredExperience()) {
				return;
			}

			if (progress.getExperience() >= tier.getRequiredExperience()) {
				if (reward.isFreeReward() && !progress.getClaimedRewardsFree().contains(tier)) {
					progress.getClaimedRewardsFree().add(tier);
					progress.requiresSave();

					reward.execute(player);
				} else if (progress.isPremium() && !progress.getClaimedRewardsPremium().contains(tier)) {
					progress.getClaimedRewardsPremium().add(tier);
					progress.requiresSave();

					reward.execute(player);
				}
			}
		}
	}

	@AllArgsConstructor
	private class EmptyRewardButton extends Button {
		private Tier tier;

		@Override
		public String getName(Player player) {
			return CC.MAIN + CC.BOLD + "Tier " + tier.getNumber();
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.emptyList();
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.MINECART;
		}
	}

	private class PreviousPageButton extends Button {
		@Override
		public String getName(Player player) {
			if (page > 1) {
				return CC.RED + CC.BOLD + "Previous Page";
			} else {
				return CC.GRAY + CC.BOLD + "No Previous Page";
			}
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.emptyList();
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.BARRIER;
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			return (page > 1 ? CC.getCustomHead(getName(player), 1, "93971124be89ac7dc9c929fe9b6efa7a07ce37ce1da2df691bf8663467477c7") : super.getButtonItem(player));
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (clickType.isLeftClick() && page > 1) {
				page -= 1;
				openMenu(player);
			}
		}
	}

	private class NextPageButton extends Button {
		@Override
		public String getName(Player player) {
			if (page < getMaxPages()) {
				return CC.RED + CC.BOLD + "Next Page";
			} else {
				return CC.GRAY + CC.BOLD + "No Next Page";
			}
		}

		@Override
		public List<String> getDescription(Player player) {
			return Collections.emptyList();
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.BARRIER;
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			return (page < getMaxPages() ? CC.getCustomHead(getName(player), 1, "2671c4c04337c38a5c7f31a5c751f991e96c03df730cdbee99320655c19d") : super.getButtonItem(player));
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (clickType.isLeftClick() && page < getMaxPages()) {
				page += 1;
				openMenu(player);
			}
		}
	}
	private int getMaxPages() {

		List<Tier> items = new ArrayList<>(handler.getTiers());

		if (items.size() == 0) {
			return 1;
		} else {
			return (int) Math.ceil(items.size() / (double) (SLOTS_TIERS.length));
		}
	}

}
