package dev.lbuddyboy.samurai.deathmessage.util;

import dev.lbuddyboy.samurai.deathmessage.objects.Damage;

public class UnknownDamage extends Damage {

    public UnknownDamage(String damaged, double damage) {
        super(damaged, damage);
    }

    public String getDeathMessage() {
        return (wrapName(getDamaged()) + " died.");
    }

}