package dev.lbuddyboy.samurai.custom.teamwar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@Data
public class WarPlayer {

    private final UUID uuid;
    private WarKit kit;
    private boolean alive;

}
