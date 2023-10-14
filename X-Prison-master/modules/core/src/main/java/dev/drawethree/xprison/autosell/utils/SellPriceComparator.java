package dev.drawethree.xprison.autosell.utils;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.utils.compat.CompMaterial;

import java.util.Comparator;

public class SellPriceComparator implements Comparator<CompMaterial> {

	@Override
	public int compare(CompMaterial o1, CompMaterial o2) {
		double sellPrice1 = XPrisonAutoSell.getInstance().getManager().getSellPriceFor(o1);
		double sellPrice2 = XPrisonAutoSell.getInstance().getManager().getSellPriceFor(o2);
		return Double.compare(sellPrice1, sellPrice2);
	}
}
