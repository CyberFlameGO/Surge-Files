package dev.lbuddyboy.lqueue.api.model;

import java.util.Comparator;

public class QueuePriorityComparator implements Comparator<QueuePlayer> {

    @Override
    public int compare(QueuePlayer rank, QueuePlayer otherRank) {
        return Integer.compare(rank.getPriority(), otherRank.getPriority());
    }
}
