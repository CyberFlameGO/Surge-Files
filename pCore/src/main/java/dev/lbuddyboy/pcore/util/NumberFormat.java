package dev.lbuddyboy.pcore.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class NumberFormat {

    private static final ArrayList<String> suffixes = new ArrayList<>(Arrays.asList("", "k", "M", "B", "T", "q", "Q", "QT", "S", "SP", "O",
            "N", "D"));

    public static String formatNumber(double amount) {
        if (amount <= 1000.0D)
            return String.valueOf((int) amount);
        double chunks = Math.floor(Math.floor(Math.log10(amount) / 3.0D));
        amount /= Math.pow(10.0D, chunks * 3.0D - 1.0D);
        amount /= 10.0D;
        String suffix = suffixes.get((int) chunks);
        String format = String.valueOf(amount);

        if (format.replace(".", "").length() > 5)
            format = format.substring(0, 5);
        return format + suffix;
    }
//    public static final DecimalFormat DTR_FORMAT = new DecimalFormat("0.00");
//    public static final DecimalFormat DTR_FORMAT2 = new DecimalFormat("0");

}
