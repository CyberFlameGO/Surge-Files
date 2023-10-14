package dev.lbuddyboy.crates.util;

public interface Callable {

    boolean sent = false;

    void call();
}