package dev.lbuddyboy.pcore.essential.vaults;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class VaultInfo {

    private UUID target;
    private int vaultNumber;

}
