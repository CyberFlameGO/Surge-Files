package dev.aurapvp.samurai.faction.member;

import dev.aurapvp.samurai.faction.FactionConfiguration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class FactionInvitation {

    private final UUID sender, target;
    private final long sentAt = System.currentTimeMillis();

    public boolean isExpired() {
        return this.sentAt + (FactionConfiguration.MEMBER_INVITATION_DELAY_SECONDS.getInt() * 1000L) < System.currentTimeMillis();
    }

}
