package dev.lbuddyboy.bunkers.util;

public interface Callable {

    boolean sent = false;

    void call();
}