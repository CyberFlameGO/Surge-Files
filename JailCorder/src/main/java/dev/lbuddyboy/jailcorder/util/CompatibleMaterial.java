package dev.lbuddyboy.jailcorder.util;

import org.bukkit.Material;

public class CompatibleMaterial {

    public static Material getMaterial(String name) {
        return CompMaterial.fromString(name).getMaterial();
    }

}
