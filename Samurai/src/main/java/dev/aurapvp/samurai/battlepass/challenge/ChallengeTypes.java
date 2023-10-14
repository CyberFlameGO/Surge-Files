package dev.aurapvp.samurai.battlepass.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Getter
public enum ChallengeTypes {

    KILL_ENTITY("Kill Entity", "Kill Entities"),
    PLACE_BLOCK("Place Block", "Place Blocks"),
    USE_PET("Use Pet", "Use Pets"),
    WIN_COINFLIPS("Win CoinFlip", "Win Coin Flips"),
    PLACE_COIN_FLIPS("Place CoinFlip", "Place Coin Flips"),
    MINE_BLOCK("Mine Block", "Mine Blocks");

    private final String singular;
    private final String plural;

}
