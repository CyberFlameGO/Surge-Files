package dev.drawethree.xprison.autosell.api.events;

import dev.drawethree.xprison.api.events.player.XPrisonPlayerEvent;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.Map;


@Getter
public final class XPrisonSellAllEvent extends XPrisonPlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final Map<CompMaterial, Double> prices;

	@Getter
	@Setter
	private double sellPrice;

	@Getter
	@Setter
	private boolean cancelled;

	/**
	 * Called when mined blocks are automatically sold
	 *
	 * @param player    Player
	 * @param prices       Prices where block was mined
	 * @param sellPrice Amount what will player receive after selling
	 */
	public XPrisonSellAllEvent(Player player, Map<CompMaterial, Double> prices, double sellPrice) {
		super(player);
		this.player = player;
		this.prices = prices;
		this.sellPrice = sellPrice;
	}

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
