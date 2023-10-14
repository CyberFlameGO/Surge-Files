package dev.lbuddyboy.samurai.team.struct;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 15/01/2022 / 10:44 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.team.struct
 */

@Getter
@AllArgsConstructor
public enum ApparatusStage {

	NONE(Collections.emptyList(), 0, "&cNone"),
	FIRST(Arrays.asList(PotionEffectType.INCREASE_DAMAGE), 5, "&cStrength"),
	SECOND(Arrays.asList(PotionEffectType.DAMAGE_RESISTANCE), 10, "&bResistance"),
	THIRD(Arrays.asList(PotionEffectType.REGENERATION), 15, "&dRegeneration");

	private List<PotionEffectType> potionEffectTypes;
	private int requiredKills;
	private String displayName;

}
