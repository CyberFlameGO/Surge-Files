package dev.lbuddyboy.crates.util;

public interface Callback<T, O> {
    void call(T t, O t2);

    interface TripleCallback<T, V, O> {
        void call(T t, V v, O o);
    }
}
