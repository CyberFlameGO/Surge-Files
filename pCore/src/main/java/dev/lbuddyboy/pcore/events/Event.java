package dev.lbuddyboy.pcore.events;

import dev.lbuddyboy.pcore.util.Config;

public interface Event {

    String getName();
    void setName(String name);
    Config getConfig();
    void setConfig(Config config);
    EventType getEventType();
    boolean isActive();
    void setActive(boolean active);
    void end();
    void start();
    void win();

}
