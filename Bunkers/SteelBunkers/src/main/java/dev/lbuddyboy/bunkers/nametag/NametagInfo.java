package dev.lbuddyboy.bunkers.nametag;

import dev.lbuddyboy.bunkers.nametag.packet.ScoreboardTeamPacketMod;
import net.minecraft.EnumChatFormat;

import java.util.ArrayList;

public final class NametagInfo {
    private final String name;
    private final String prefix;
    private final String suffix;
    private final EnumChatFormat color;
    private final ScoreboardTeamPacketMod teamAddPacket;

    public NametagInfo(String name, String prefix, String suffix, EnumChatFormat color) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.color = color;
        this.teamAddPacket = new ScoreboardTeamPacketMod(this, new ArrayList<>(), 0);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof NametagInfo) {
            NametagInfo otherNametag = (NametagInfo) other;
            return this.name.equals(otherNametag.name) && this.prefix.equals(otherNametag.prefix) && this.suffix.equals(otherNametag.suffix);
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public EnumChatFormat getColor() {
        return this.color;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public ScoreboardTeamPacketMod getTeamAddPacket() {
        return this.teamAddPacket;
    }
}

