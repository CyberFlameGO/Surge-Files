package dev.lbuddyboy.pcore.essential.kit.editor;

import dev.lbuddyboy.pcore.essential.kit.Kit;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

@RequiredArgsConstructor
@Data
public class KitSetup {

    private final Kit kit;
    private final String name;
    private String displayName = null;
    private Material material = null;
    private int slot = -1;
    private long cooldown = -1;

}
