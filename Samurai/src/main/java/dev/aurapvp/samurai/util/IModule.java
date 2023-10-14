package dev.aurapvp.samurai.util;

import dev.aurapvp.samurai.Samurai;

public interface IModule {

    String getId();
    void load(Samurai plugin);
    void unload(Samurai plugin);
    default void save() {

    }
    default void reload() {

    }

}
