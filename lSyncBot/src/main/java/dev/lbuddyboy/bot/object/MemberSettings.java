package dev.lbuddyboy.bot.object;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MemberSettings {

    private long discordId;
    private boolean queueUpdates;
    private long queueUpdateDelay;

}
