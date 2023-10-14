package dev.lbuddyboy.pcore.coinflip;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public enum CoinFlipType {

    HEADS("Heads"),
    TAILS("Tails");

    private final String name;

}
