//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package dev.lbuddyboy.bot.utils;

import java.util.concurrent.TimeUnit;

public class JavaUtils {
    public JavaUtils() {
    }

    public static long parse(String input) {
        if (input != null && !input.isEmpty()) {
            long result = 0L;
            StringBuilder number = new StringBuilder();

            for(int i = 0; i < input.length(); ++i) {
                char c = input.charAt(i);
                if (Character.isDigit(c)) {
                    number.append(c);
                } else {
                    String str;
                    if (Character.isLetter(c) && !(str = number.toString()).isEmpty()) {
                        result += convert(Integer.parseInt(str), c);
                        number = new StringBuilder();
                    }
                }
            }

            return result;
        } else {
            return -1L;
        }
    }

    private static long convert(int value, char unit) {
        switch(unit) {
        case 'M':
            return (long)value * TimeUnit.DAYS.toMillis(30L);
        case 'd':
            return (long)value * TimeUnit.DAYS.toMillis(1L);
        case 'h':
            return (long)value * TimeUnit.HOURS.toMillis(1L);
        case 'm':
            return (long)value * TimeUnit.MINUTES.toMillis(1L);
        case 's':
            return (long)value * TimeUnit.SECONDS.toMillis(1L);
        case 'y':
            return (long)value * TimeUnit.DAYS.toMillis(365L);
        default:
            return -1L;
        }
    }
}
