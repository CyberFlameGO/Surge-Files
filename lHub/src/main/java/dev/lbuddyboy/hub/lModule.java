package dev.lbuddyboy.hub;

public interface lModule {

    void load(lHub plugin);
    void unload(lHub plugin);
    default void save() {

    }
    default void reload() {

    }

}
