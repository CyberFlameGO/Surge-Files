package dev.lbuddyboy.samurai.util.object;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class IntRange {

    private int min, max;

    public int[] toArray() {
        int[] ints = new int[max - min];

        for (int i = 0; i < max - min; i++) {
            ints[i] = min++;
        }

        return ints;
    }


}
