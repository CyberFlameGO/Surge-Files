package dev.aurapvp.samurai.events;

import dev.aurapvp.samurai.faction.FactionType;
import dev.aurapvp.samurai.util.Config;
import org.bukkit.Material;

public interface Event {

    String getName();
    void setName(String name);
    Config getConfig();
    void setConfig(Config config);
    EventType getEventType();
    FactionType getFactionType();
    Material getEditorMaterial();
    boolean isActive();
    void setActive(boolean active);
    void end();
    void start();
    void win();

}
