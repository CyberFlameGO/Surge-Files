package dev.drawethree.xprison.autosell.api;

import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public final class XPrisonAutoSellAPIImpl implements XPrisonAutoSellAPI {

	private final XPrisonAutoSell plugin;

	public XPrisonAutoSellAPIImpl(XPrisonAutoSell plugin) {
		this.plugin = plugin;
	}

	@Override
	public double getCurrentEarnings(Player player) {
		return plugin.getManager().getCurrentEarnings(player);
	}

	@Override
	public double getPriceForBlock(Block block) {
		return plugin.getManager().getPriceForBlock(block);
	}

	@Override
	public void sellBlocks(Player player, List<Block> blocks) {
		plugin.getManager().sellBlocks(player, blocks);
	}

	@Override
	public boolean hasAutoSellEnabled(Player p) {
		return plugin.getManager().hasAutoSellEnabled(p);
	}


}
