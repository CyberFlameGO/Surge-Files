package dev.lbuddyboy.jailcorder.record;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Recording {

    private String id;
    private int blocksBroken;
    private long startedAt, endedAt;

}
