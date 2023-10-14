package dev.lbuddyboy.samurai.map.shards.coinflip;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CoinFlipType {

    HEADS("Heads"),
    TAILS("Tails");

    private final String name;

}
