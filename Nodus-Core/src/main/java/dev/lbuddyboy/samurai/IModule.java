package dev.lbuddyboy.samurai;

public interface IModule {

    void load(Samurai plugin);
    void unload(Samurai plugin);
    default void save() {

    }
    default void reload() {

    }

}
