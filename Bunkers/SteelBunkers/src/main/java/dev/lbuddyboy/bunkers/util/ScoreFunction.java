package dev.lbuddyboy.bunkers.util;

public interface ScoreFunction<T> {

    ScoreFunction<Float> TIME_FANCY=value -> {
        if (value.floatValue() >= 60.0f) {
            return TimeUtils.formatIntoMMSS(value.intValue());
        }
        return (double) Math.round(10.0 * (double) value.floatValue()) / 10.0 + "s";
    };

    ScoreFunction<Float> TIME_SIMPLE=value -> TimeUtils.formatIntoMMSS(value.intValue());

    String apply(T var1);
}

