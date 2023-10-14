package dev.lbuddyboy.samurai.util;

import java.util.HashMap;
import java.util.Map;

public class RomanUtil {

    public static Map<String, Integer> map = new HashMap<>();
    public static Map<Integer, String> mapInt = new HashMap<>();

    static {
        map.put("I", 1);
        map.put("II", 2);
        map.put("III", 3);
        map.put("IV", 4);
        map.put("V", 5);
        map.put("VI", 6);
        map.put("VII", 7);
        map.put("VIII", 8);
        map.put("IX", 9);
        map.put("X", 10);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            mapInt.put(entry.getValue(), entry.getKey());
        }
    }

}