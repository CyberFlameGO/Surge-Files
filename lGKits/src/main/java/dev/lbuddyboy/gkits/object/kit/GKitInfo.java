package dev.lbuddyboy.gkits.object.kit;

import dev.lbuddyboy.gkits.lGKits;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:07 PM
 * GKits / me.lbuddyboy.gkits.object
 */

@AllArgsConstructor
@Data
public class GKitInfo {

	private String kit;
	private long duration;

	public GKit getKit() {
		return lGKits.getInstance().gkitByName(this.kit);
	}

}
