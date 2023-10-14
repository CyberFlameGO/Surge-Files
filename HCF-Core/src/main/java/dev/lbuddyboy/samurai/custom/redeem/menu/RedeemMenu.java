/*
package dev.lbuddyboy.samurai.custom.redeem.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.redeem.object.Partner;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.math.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

*/
/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 15/01/2022 / 2:05 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.redeem.menu
 *//*

public class RedeemMenu extends Menu {

	private static int[] SLOTS = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
	private int page = 1;

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		IntRange range;
		if (page == 1) {
			range = new IntRange(1, SLOTS.length);
		} else {
			range = new IntRange(((page - 1) * 21) + 1, page * SLOTS.length);
		}

		int skipped = 1;
		int slotIndex = 0;

		for (Partner partner : Samurai.getInstance().getRedeemHandler().getPartners()) {
			if (skipped < range.getMinimumInteger()) {
				skipped++;
				continue;
			}

			buttons.put(SLOTS[slotIndex], new Button() {
				@Override
				public String getName(Player player) {
					return CC.translate("&g" + WordUtils.capitalize(partner.getName()));
				}

				@Override
				public List<String> getDescription(Player player) {
					return CC.translate(Arrays.asList(
							"",
							" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &gRedeem&7: &f" + partner.getRedeemedAmount(),
							""
					));
				}

				@Override
				public Material getMaterial(Player player) {
					return null;
				}

				@Override
				public ItemStack getButtonItem(Player player) {
					ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta meta = (SkullMeta) stack.getItemMeta();
					meta.setOwner(partner.getName());
					meta.setDisplayName(getName(player));
					meta.setLore(getDescription(player));
					stack.setItemMeta(meta);

					return stack;
				}

				@Override
				public void clicked(Player player, int slot, ClickType clickType) {

					if (Samurai.getInstance().getRedeemHandler().getRedeemMap().isToggled(player.getUniqueId())) {
						player.sendMessage(CC.translate("&cYou have already redeemed a partner."));
						return;
					}

					partner.setRedeemedAmount(partner.getRedeemedAmount() + 1);
					partner.save();

					if (Samurai.getInstance().getMapHandler().isKitMap()) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "goldencrates givekey " + player.getName() + " Partner 3");
					} else {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "goldencrates givekey " + player.getName() + " Winter 3");
					}

					for (Player p : Bukkit.getOnlinePlayers()) {
						if (Samurai.getInstance().getRedeemBCToggleMap().isToggled(p.getUniqueId())) {
							p.sendMessage(CC.translate("&g&l[REDEEM] &g" + player.getName() + "&f has just redeemed &g" + partner.getName() + "&f's rewards! &7(/redeem)"));
						}
					}

					Samurai.getInstance().getRedeemHandler().getRedeemMap().setToggled(player.getUniqueId(), true);
				}
			});

			buttons.put(18, new PreviousPageButton());
			buttons.put(26, new NextPageButton());

			if (slotIndex >= 20) {
				break;
			} else {
				slotIndex++;
			}
		}

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		return super.size(buttons) + 9;
	}

	@Override
	public String getTitle(Player player) {
		return "Partners";
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

		List<Partner> items = new ArrayList<>(Samurai.getInstance().getRedeemHandler().getPartners());

		if (items.size() == 0) {
			return 1;
		} else {
			return (int) Math.ceil(items.size() / (double) 21);
		}
	}
}
*/
