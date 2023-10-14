package dev.lbuddyboy.bunkers.util;

public interface Callback<T> {
    void callback(T t);

    interface TripleCallback<T, V, O> {
        void call(T t, V v, O o);
    }
}
