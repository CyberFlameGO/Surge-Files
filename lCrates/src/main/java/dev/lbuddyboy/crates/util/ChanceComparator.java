package dev.lbuddyboy.crates.util;

import dev.lbuddyboy.crates.model.CrateItem;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ChanceComparator implements Comparator<CrateItem> {

    @Override
    public int compare(CrateItem c1, CrateItem c2) {
        return Double.compare(c1.getChance(), c2.getChance());
    }

    public static <T> T getByWeight(Map<T, Double> itemsMap) {
        List<Pair<T, Double>> items = CollectionsUtil.sortAscent(itemsMap).entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).collect(Collectors.toList());
        double totalWeight = items.stream().mapToDouble(Pair::getSecond).sum();

        int index = 0;
        for (double chance = ThreadLocalRandom.current().nextDouble() * totalWeight; index < items.size() - 1; ++index) {
            chance -= items.get(index).getSecond();
            if (chance <= 0D) break;
        }
        return items.get(index).getFirst();
    }

}
