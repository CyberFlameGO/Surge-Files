package dev.lbuddyboy.bot.sync.cache;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Data
public class SyncInformation {

    private UUID playerUUID;
    private String playerName;
    private long discordId;
    private Map<String, Long> inGameRanks;
    private Map<Long, String> discordRanks;

}
