package dev.drawethree.xprison.enchants.api.events;

import dev.drawethree.xprison.api.events.player.XPrisonPlayerEvent;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.util.List;

@Getter
public abstract class XPrisonPlayerEnchantTriggerEvent extends XPrisonPlayerEvent implements Cancellable {

	protected final Player player;
	protected final PrivateMine mine;
	protected final Block originBlock;
	protected final List<Block> blocksAffected;

	public XPrisonPlayerEnchantTriggerEvent(Player p, PrivateMine mine, Block originBlock, List<Block> blocksAffected) {
		super(p);
		this.player = p;
		this.mine = mine;
		this.originBlock = originBlock;
		this.blocksAffected = blocksAffected;
	}
}
