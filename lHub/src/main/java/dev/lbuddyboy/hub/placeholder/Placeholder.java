package dev.lbuddyboy.hub.placeholder;

import lombok.Getter;
import lombok.Setter;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 2:16 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.general.placeholder
 */

@Getter
public class Placeholder {

	private final String holder;
	private final PlaceholderType type;
	@Setter private String replacement;

	public Placeholder(String holder, PlaceholderType type) {
		this.holder = holder;
		this.type = type;
	}

}
