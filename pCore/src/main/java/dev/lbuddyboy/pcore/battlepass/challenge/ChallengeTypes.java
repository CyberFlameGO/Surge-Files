package dev.lbuddyboy.pcore.battlepass.challenge;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChallengeTypes {

    KILL_ENTITY("Kill Entity", "Kill Entities"),
    PLACE_BLOCK("Place Block", "Place Blocks"),
    RUN_COMMANDS("Run Command", "Run Commands"),
    USE_PET("Use Pet", "Use Pets"),
    WIN_COINFLIPS("Win CoinFlip", "Win Coin Flips"),
    PLACE_COIN_FLIPS("Place CoinFlip", "Place Coin Flips"),
    MINE_BLOCK("Mine Block", "Mine Blocks");

    private final String singular;
    private final String plural;

}
