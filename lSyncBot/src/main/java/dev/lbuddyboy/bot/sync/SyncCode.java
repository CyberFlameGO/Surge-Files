package dev.lbuddyboy.bot.sync;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class SyncCode {

    private final UUID playerUUID;
    private final String playerName;
    private final int code;
    private final long createdAt;

}
