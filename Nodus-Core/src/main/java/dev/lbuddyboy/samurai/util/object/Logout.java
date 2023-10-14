package dev.lbuddyboy.samurai.util.object;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Logout {

    private final int taskId;
    private final long logoutTime;
}
