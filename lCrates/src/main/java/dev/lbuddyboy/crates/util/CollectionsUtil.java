package dev.lbuddyboy.crates.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionsUtil {


    public static List<String> playerNames() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }


    public static List<String> playerNames(Player viewer) {
        return Bukkit.getServer().getOnlinePlayers().stream().filter(viewer::canSee).map(Player::getName).collect(Collectors.toList());
    }

    public static <T> List<List<T>> split(List<T> list, int targetSize) {
        List<List<T>> lists = new ArrayList<>();
        if (targetSize <= 0) return lists;

        for (int index = 0; index < list.size(); index += targetSize) {
            lists.add(list.subList(index, Math.min(index + targetSize, list.size())));
        }
        return lists;
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortAscent(Map<K, V> map) {
        return sort(map, Map.Entry.comparingByValue());
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescent(Map<K, V> map) {
        return sort(map, Collections.reverseOrder(Map.Entry.comparingByValue()));
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sort(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
        return new LinkedList<>(map.entrySet()).stream().sorted(comparator)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (old, nev) -> nev, LinkedHashMap::new));
    }


    public static List<String> getEnumsList(Class<?> clazz) {
        return new ArrayList<>(Stream.of(clazz.getEnumConstants()).map(Object::toString).collect(Collectors.toList()));
    }


    public static <T extends Enum<T>> T next(Enum<T> numeration) {
        return shifted(numeration, 1);
    }


    public static <T extends Enum<T>> T next(Enum<T> numeration, Predicate<T> predicate) {
        return shifted(numeration, 1, predicate);
    }


    public static <T extends Enum<T>> T previous(Enum<T> numeration) {
        return shifted(numeration, -1);
    }


    public static <T extends Enum<T>> T previous(Enum<T> numeration, Predicate<T> predicate) {
        return shifted(numeration, -1, predicate);
    }


    public static <T extends Enum<T>> T shifted(Enum<T> numeration, int shift) {
        return shifted(numeration, shift, null);
    }


    private static <T extends Enum<T>> T shifted(Enum<T> numeration, int shift, Predicate<T> predicate) {
        T[] values = numeration.getDeclaringClass().getEnumConstants();
        return shifted(values, numeration.ordinal(), shift, predicate);
    }


    private static <T extends Enum<T>> T shifted(T[] values, int currentIndex, int shift, Predicate<T> predicate) {
        if (predicate != null) {
            List<T> filtered = Stream.of(values).filter(predicate).collect(Collectors.toList());
            if (filtered.isEmpty()) return values[currentIndex];

            return shifted(filtered, currentIndex, shift);
        }
        return shifted(values, currentIndex, shift);
    }


    public static <T> T shifted(T[] values, int currentIndex, int shift) {
        int index = currentIndex + shift;
        return values[index >= values.length || index < 0 ? 0 : index];
    }


    public static <T> T shifted(List<T> values, int currentIndex, int shift) {
        int index = currentIndex + shift;
        return values.get(index >= values.size() || index < 0 ? 0 : index);
    }

}