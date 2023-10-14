package dev.lbuddyboy.samurai.custom.shop.menu;

import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.object.IntRange;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.shop.ShopCategory;
import dev.lbuddyboy.samurai.custom.shop.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/12/2021 / 9:56 AM
 * SteelHCF-main / com.steelpvp.hcf.shop.menu
 */

public class ShopCategoryMenu extends Menu {

	private final ShopCategory category;
	private static final int[] SLOTS = new int[] { 19, 20, 21, 22, 23, 24, 25,28,29,30,31,32,33,34,37,38,39,40,41,42,43 };
	private int page = 1;

	public ShopCategoryMenu(ShopCategory category) {
		this.category = category;
	}

	@Override
	public String getTitle(Player var1) {
		return CC.translate(category.getMenuTitle());
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(4, new BackButton(new ShopMainMenu()));

		IntRange range;
		if (page == 1) {
			range = new IntRange(1, SLOTS.length);
		} else {
			range = new IntRange(((page - 1) * 21) + 1, page * SLOTS.length);
		}

		int skipped = 1;
		int slotIndex = 0;
		for (ShopItem item : Samurai.getInstance().getShopHandler().getBlocks().get(this.category)) {
			if (skipped < range.getMin()) {
				skipped++;
				continue;
			}

			buttons.put(SLOTS[slotIndex], new ShopCategoryButton(item));
			if (slotIndex >= 20) {
				break;
			} else {
				slotIndex++;
			}
		}

		buttons.put(27, new PreviousPageButton());
		buttons.put(35, new NextPageButton());

		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		int size = super.size(buttons) + 9;
		return Math.min(size, 54);
	}

	@AllArgsConstructor
	public static class ShopCategoryButton extends Button {

		private ShopItem item;

		@Override
		public String getName(Player var1) {
			return null;
		}

		@Override
		public List<String> getDescription(Player var1) {
			return null;
		}

		@Override
		public Material getMaterial(Player var1) {
			return null;
		}

		@Override
		public ItemStack getButtonItem(Player player) {
			ItemStack stack = item.getItem().clone();
			ItemMeta meta = stack.getItemMeta();
			if (item.getCategory() != ShopCategory.SELL) {
				List<String> strings = new ArrayList<>();
				strings.add(CC.translate("&x&1&d&a&4&f&bPrice&7: " + item.getPrice()));
				if (item.getCategory() != ShopCategory.BUY && item.getCategory() != ShopCategory.DECORATION_SHOP) {
					stack.setAmount(64);
				}
				meta.setLore(strings);
			} else {
				meta.setLore(CC.translate(Arrays.asList(
						"&x&1&d&a&4&f&bPrice&7: " + item.getPrice(),
						"&x&1&d&a&4&f&bPrice Per Item&7: " + item.getPricePer(),
						" ",
						"&7Left Click to sell 16",
						"&7Right Click to sell ALL"
				)));
			}
			stack.setItemMeta(meta);

			return stack;
		}

		@Override
		public int getAmount(Player player) {
			if (item.getCategory() != ShopCategory.BUY) {
				return 16;
			}
			return getButtonItem(player).getAmount();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());

			if (item.getCategory() == ShopCategory.SELL) {

				int inInventory = 0;
				if (clickType == ClickType.LEFT) {
					for (ItemStack content : player.getInventory().getContents()) {
						if (content != null && content.getType() == item.getItem().getType()) {
							inInventory = inInventory + content.getAmount();
						}
					}

					if (inInventory == 0) {
						player.sendMessage(CC.translate("&cYou do not have enough of this item."));
						return;
					}

					if (inInventory < item.getItem().getAmount()) {
						player.sendMessage(CC.translate("&cYou do not have enough of this item."));
						return;
					}

					Map<Integer, Integer> slotMap = new HashMap<>();
					int i = 0;
					int slotWithMore = Integer.MAX_VALUE;
					int slotWithMoreAmount = 0;
					for (ItemStack content : player.getInventory().getContents()) {
						if (content != null && content.getType() == item.getItem().getType()) {
							if (content.getAmount() >= item.getItem().getAmount()) {
								slotWithMore = i;
								slotWithMoreAmount = content.getAmount();
								break;
							} else {
								slotMap.put(i, content.getAmount());
							}
						}
						++i;
					}

					if (slotWithMore != Integer.MAX_VALUE) {
						ItemStack stack = player.getInventory().getItem(slotWithMore);
						stack.setAmount(slotWithMoreAmount - item.getItem().getAmount());
						if (stack.getAmount() == 0) {
							player.getInventory().setItem(slotWithMore, null);
						} else {
							player.getInventory().setItem(slotWithMore, stack);
						}
					} else {
						int amount = 0;
						List<Integer> slots = new ArrayList<>();
						for (Map.Entry<Integer, Integer> entry : slotMap.entrySet()) {
							amount = amount + player.getInventory().getItem(entry.getKey()).getAmount();
							slots.add(entry.getKey());
							if (amount >= item.getItem().getAmount()) {
								int current = 0;
								int subtracted = 0;
								for (Integer integer : slots) {
									if ((current + 1) == slots.size()) {
										ItemStack stack = player.getInventory().getItem(integer);
										stack.setAmount(stack.getAmount() - subtracted);
										if (stack.getAmount() <= 0) {
											player.getInventory().setItem(integer, null);
										} else {
											player.getInventory().setItem(integer, stack);
										}
										break;
									} else {
										subtracted = subtracted + player.getInventory().getItem(integer).getAmount();
										player.getInventory().setItem(integer, null);
									}
									++current;
								}
								break;
							}
						}
					}

					FrozenEconomyHandler.deposit(player.getUniqueId(), (item.getItem().getAmount() * item.getPricePer()));

					if (Samurai.getInstance().getBattlePassHandler() != null) {
						Samurai.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
							progress.setValuablesSold(progress.getValuablesSold() + (item.getItem().getAmount() * item.getPricePer()));
							progress.requiresSave();

							Samurai.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
						});
					}

					player.sendMessage(CC.translate("&2&lSOLD&a Earned: +$" + (item.getItem().getAmount() * item.getPricePer())));
				} else if (clickType == ClickType.RIGHT) {

					//  Slot      Amount
					Map<Integer, Integer> slotMap = new HashMap<>();

					int i = 0;
					for (ItemStack content : player.getInventory().getContents()) {
						if (content != null && content.getType() == item.getItem().getType()) {
							inInventory = inInventory + content.getAmount();
							slotMap.put(i, content.getAmount());
						}
						++i;
					}

					if (inInventory == 0) {
						player.sendMessage(CC.translate("&cYou do not have enough of this item."));
						return;
					}

					for (Map.Entry<Integer, Integer> entry : slotMap.entrySet()) {
						player.getInventory().setItem(entry.getKey(), null);
					}

					FrozenEconomyHandler.deposit(player.getUniqueId(), (inInventory * item.getPricePer()));
					player.sendMessage(CC.translate("&2&lSOLD ALL&a Earned: +$" + (inInventory * item.getPricePer())));

				}

			} else if (item.getCategory() == ShopCategory.BUY || item.getCategory() == ShopCategory.CONCRETE_SHOP || item.getCategory() == ShopCategory.GLASS_SHOP || item.getCategory() == ShopCategory.WOOL_SHOP || item.getCategory() == ShopCategory.CLAY_SHOP || item.getCategory() == ShopCategory.DECORATION_SHOP) {

				if (balance < (item.getPrice())) {
					player.sendMessage(CC.translate("&cInsufficient funds."));
					return;
				}

				ItemStack stack = item.getItem().clone();
				stack.setAmount(getButtonItem(player).getAmount());

				player.getInventory().addItem(stack);

				FrozenEconomyHandler.withdraw(player.getUniqueId(), item.getPrice());

				player.sendMessage(CC.translate("&aNew Balance: " + FrozenEconomyHandler.getBalance(player.getUniqueId())));
			}

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

		List<ShopItem> items = Samurai.getInstance().getShopHandler().getBlocks().get(this.category);

		if (items.size() == 0) {
			return 1;
		} else {
			return (int) Math.ceil(items.size() / (double) 21);
		}
	}

}
