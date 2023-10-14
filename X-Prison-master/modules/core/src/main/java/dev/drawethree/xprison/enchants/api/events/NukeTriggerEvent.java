package dev.drawethree.xprison.enchants.api.events;


import dev.lbuddyboy.pcore.mines.PrivateMine;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.List;

@Getter
public final class NukeTriggerEvent extends XPrisonPlayerEnchantTriggerEvent {

	private static final HandlerList HANDLERS_LIST = new HandlerList();
	private boolean cancelled;

	/**
	 * Called when nuke enchant procs
	 *
	 * @param p           Player
	 * @param mine  Private Mine where it was triggered
	 * @param originBlock Original block broken that triggered it
	 * @param blocks      List of affected blocks (marked for removal)
	 */
	public NukeTriggerEvent(Player p, PrivateMine mine, Block originBlock, List<Block> blocks) {
		super(p, mine, originBlock, blocks);
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
