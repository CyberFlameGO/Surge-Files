package dev.aurapvp.samurai.nametag;

import dev.aurapvp.samurai.nametag.packet.ScoreboardTeamPacket;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.EnumChatFormat;

import java.util.ArrayList;

@Getter
public class NameTag implements Cloneable {

    private final String name, prefix, suffix;
    @Setter private EnumChatFormat chatFormat;
    private final ScoreboardTeamPacket teamPacket;
    private boolean friendly, friendlyInvis;

    public NameTag(String name, String prefix, String suffix, EnumChatFormat chatFormat) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.chatFormat = chatFormat;
        this.teamPacket = new ScoreboardTeamPacket(this, new ArrayList<>(), 0);
    }

    public NameTag(String name, String prefix, String suffix, EnumChatFormat chatFormat, boolean friendly, boolean friendlyInvis) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.chatFormat = chatFormat;
        this.teamPacket = new ScoreboardTeamPacket(this, new ArrayList<>(), 0);
        this.friendly = friendly;
        this.friendlyInvis = friendlyInvis;
    }

    @Override
    public NameTag clone() {
        try {
            NameTag clone = (NameTag) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
