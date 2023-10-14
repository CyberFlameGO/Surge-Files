package dev.lbuddyboy.samurai.team;

import com.google.common.collect.ImmutableSet;
import dev.lbuddyboy.samurai.util.object.Callback;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
public enum TeamFilter {

    DEFAULT("Filter all teams by the most online!", (list) -> {
        list.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));
    }),
    LEAST_DTR("Filter all teams by the least dtr!", (list) -> {
        list.sort(Comparator.comparingInt(o -> o.getKey().getDTR()));
    }),
    MOST_DTR("Filter all teams by the most dtr!", (list) -> {
        list.sort((o1, o2) -> (Integer.compare(o2.getKey().getDTR(), o1.getKey().getDTR())));
    }),
    LEAST_ONLINE("Filter all teams by the least online!", (list) -> {
        list.sort(Comparator.comparingInt(o -> o.getKey().getOnlineMemberAmount()));
    });

    public String lore;
    public Callback<LinkedList<Map.Entry<Team, Integer>>> callback;

}