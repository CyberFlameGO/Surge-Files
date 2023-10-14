package dev.aurapvp.samurai.util;

import java.util.List;

public class StringUtils {

    public static String join(List<String> strings) {
        boolean once = false;
        String s = "";

        for (String string : strings) {
            if (once) s += ", " + string;
            else s += string;
            once = true;
        }

        return s;
    }

}
