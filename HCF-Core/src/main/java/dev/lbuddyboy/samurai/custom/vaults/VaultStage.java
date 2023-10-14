package dev.lbuddyboy.samurai.custom.vaults;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VaultStage {

	CLOSED("Closed", "vaultclosed", 0),
	STAGE_1("One", "vaultstg1", 60 * 6),
	STAGE_2("Two", "vaultstg2", 60 * 4),
	STAGE_3("Three", "vaultstg3", 60 * 2),
	STAGE_4("Four", "vaultstg4", 60),
	STAGE_5("Five", "vaultstg5", 30),
	LOOT_1("Captured", "vaultloot1", 0),
	LOOT_2("Loot Loading", "vaultloot2", 0),
	LOOT_3("Loot Collect", "vaultloot3", 0);

	private String stageName;
	private String schematicName;
	private int seconds;

}
