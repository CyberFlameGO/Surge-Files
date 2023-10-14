package dev.lbuddyboy.pcore.util;

import dev.lbuddyboy.pcore.pCore;

public interface IModule {

    void load(pCore plugin);
    void unload(pCore plugin);
    default void save() {

    }
    default void reload() {

    }
    default long cooldown() {
        return 30_000L;
    }

}
