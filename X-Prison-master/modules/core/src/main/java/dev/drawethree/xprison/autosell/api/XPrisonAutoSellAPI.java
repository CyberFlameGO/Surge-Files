package dev.drawethree.xprison.autosell.api;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public interface XPrisonAutoSellAPI {

	/**
	 * Method to get current earnings of player
	 *
	 * @param player Player
	 * @return Current earnings
	 */
	double getCurrentEarnings(Player player);

	/**
	 * Method to get price for Block
	 *
	 * @param block Block
	 * @return Price for block
	 */
	double getPriceForBlock(Block block);

	/**
	 * Sells the given blocks
	 *
	 * @param player Player
	 * @param blocks List of blocks
	 */
	void sellBlocks(Player player, List<Block> blocks);

	/**
	 * Method to get if player has autosell enabled
	 *
	 * @param p Player
	 * @return true if player has autosell enabled, otherwise false
	 */
	boolean hasAutoSellEnabled(Player p);

}
