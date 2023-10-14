package dev.lbuddyboy.bot;

public interface IHandler {

    void load(Bot instance);
    void unload(Bot instance);
    default void save() {

    }
    default void reload() {

    }

}
