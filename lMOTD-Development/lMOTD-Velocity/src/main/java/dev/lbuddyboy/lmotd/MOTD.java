package dev.lbuddyboy.lmotd;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MOTD {

    private List<String> legacyLines, newLines;

}
