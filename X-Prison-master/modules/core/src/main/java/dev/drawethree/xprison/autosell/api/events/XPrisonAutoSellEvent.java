package dev.drawethree.xprison.autosell.api.events;

import dev.drawethree.xprison.api.events.player.XPrisonPlayerEvent;
import dev.drawethree.xprison.autosell.model.AutoSellItemStack;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.Map;


@Getter
public final class XPrisonAutoSellEvent extends XPrisonPlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	@Setter
	private Map<AutoSellItemStack, Double> itemsToSell;
	@Setter
	private boolean cancelled;

    /**
     * Called when mined blocks are automatically sold
     *
     * @param player      Player
     * @param itemsToSell ItemStacks to sell with prices
     */
    public XPrisonAutoSellEvent(Player player, Map<AutoSellItemStack, Double> itemsToSell) {
		super(player);
		this.player = player;
        this.itemsToSell = itemsToSell;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
