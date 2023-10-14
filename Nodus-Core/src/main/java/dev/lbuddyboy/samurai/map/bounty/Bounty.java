package dev.lbuddyboy.samurai.map.bounty;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Bounty implements Comparable<Bounty> {
    private final UUID placedBy;
    private final int shards;

    @Override
    public int compareTo(Bounty bounty) {
        return Integer.compare(shards, bounty.shards);
    }
}
