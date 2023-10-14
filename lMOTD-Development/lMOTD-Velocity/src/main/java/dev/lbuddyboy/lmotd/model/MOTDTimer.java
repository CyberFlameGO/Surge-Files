package dev.lbuddyboy.lmotd.model;

import dev.lbuddyboy.lmotd.lMOTD;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MOTDTimer {

    private UUID id;
    private String title;
    private long startedAt, duration;

    public long getRemaining() {
        return (startedAt + duration) - System.currentTimeMillis();
    }

    public boolean isOver() {
        return getRemaining() <= 0;
    }

    public void delete() {
        lMOTD.getInstance().setActiveTimer(null);
    }

}
