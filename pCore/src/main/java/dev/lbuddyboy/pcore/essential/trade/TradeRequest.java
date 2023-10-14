package dev.lbuddyboy.pcore.essential.trade;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class TradeRequest {

    private UUID sender, target;
    private long sentAt;

    public boolean isExpired() {
        return this.sentAt + 30_000L < System.currentTimeMillis();
    }

}
