package dev.lbuddyboy.samurai.nametag;

import net.minecraft.EnumChatFormat;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

public abstract class NametagProvider {
    private final String name;
    private final int weight;

    public abstract NametagInfo fetchNametag(Player var1, Player var2);

    public static NametagInfo createNametagNoPlayer(String prefix, String suffix, EnumChatFormat color) {
        return FrozenNametagHandler.getOrCreate(prefix, suffix, color);
    }

    @ConstructorProperties(value = {"name", "weight"})
    public NametagProvider(String name, int weight) {
        this.name = name;
        this.weight = weight;
    }

    public String getName() {
        return this.name;
    }

    public int getWeight() {
        return this.weight;
    }

}

