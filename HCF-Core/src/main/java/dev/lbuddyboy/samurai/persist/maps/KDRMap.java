package dev.lbuddyboy.samurai.persist.maps;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

public class KDRMap extends PersistMap<Double> {

    public KDRMap() {
        super("KDR", "KDR");
    }

    @Override
    public String getRedisValue(Double kdr) {
        return (String.valueOf(kdr));
    }

    @Override
    public Double getJavaObject(String str) {
        return (Double.parseDouble(str));
    }

    @Override
    public Object getMongoValue(Double kdr) {
        return (kdr);
    }

    public void setKDR(UUID update, double kdr) {
        updateValueAsync(update, kdr);
    }

    public void updateKDR(UUID update) {
        setKDR(update, Math.max(((double) Samurai.getInstance().getMapHandler().getStatsHandler().getStats(update).getKills()) / Math.max(Samurai.getInstance().getMapHandler().getStatsHandler().getStats(update).getDeaths(), 1), 0));
    }
}
