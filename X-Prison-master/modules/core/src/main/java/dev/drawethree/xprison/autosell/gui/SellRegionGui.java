package dev.drawethree.xprison.autosell.gui;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.autosell.utils.SellPriceComparator;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.drawethree.xprison.utils.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;

public final class SellRegionGui extends Gui {

	public SellRegionGui(Player player) {
		super(player, 6, "All Block Prices");
	}

	@Override
	public void redraw() {
		this.clearItems();

		this.setActionItems();

		this.setBackItem();
	}

	private void setBackItem() {

	}

	private void setActionItems() {
		for (CompMaterial material : XPrisonAutoSell.getInstance().getManager().getSellingMaterialsSorted(new SellPriceComparator())) {
			this.addItemForMaterial(material);
		}
	}


	private void addItemForMaterial(CompMaterial material) {
		double price = XPrisonAutoSell.getInstance().getManager().getSellPriceFor(material);

		this.addItem(ItemStackBuilder.of(material.toItem()).name(material.name()).lore(" ", String.format("&7Sell Price: &2$&a%,.2f", price), " ", "&aLeft-Click &7to edit the price", "&aRight-Click &7to remove.").build(() -> {
			this.deleteSellPrice(material);
			this.redraw();
		}, () -> {
			new UpdateSellPriceGui(this.getPlayer(), material).open();
		}));
	}

	private void deleteSellPrice(CompMaterial material) {
		XPrisonAutoSell.getInstance().getAutoSellConfig().resetItem(material);
	}
}
