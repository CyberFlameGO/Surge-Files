package dev.lbuddyboy.samurai.custom.power.map;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.util.CC;

import java.util.UUID;

public class PowerMap extends PersistMap<String> {

    public PowerMap() {
        super("ActivePower", "ActivePower");
    }

    @Override
    public String getRedisValue(String power) {
        return power;
    }

    @Override
    public String getJavaObject(String str) {
        return str;
    }

    @Override
    public Object getMongoValue(String power) {
        return power;
    }


    public String getPower(UUID check) {
        return (contains(check) ? getValue(check) : "None");
    }

    public void setPower(UUID update, String power) {
        updateValueAsync(update, power);
        String fancy = (power.equalsIgnoreCase("Strength") ? "&cStrength" : (power.equalsIgnoreCase("Mixture") ? "&5Mixture" : "&hTrapper"));
        CC.broadcast("&g&lPOWER " + CC.STAR + " &h" + Flash.getInstance().getCacheHandler().getUserCache().getName(update) + " &fhas just selected the " + fancy + " &fPower!");
        CC.broadcast(" &7" + CC.STAR + " &fSelect using &e/power selector");
    }

    public boolean hasPower(UUID update) {
        return contains(update) && !getPower(update).equals("None");
    }

}
