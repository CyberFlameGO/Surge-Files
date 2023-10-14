package dev.lbuddyboy.pcore.util;

public interface Callback<T, O> {
    void call(T t, O t2);

    interface SingleCallback<T> {
        void call(T t);
    }
    interface TripleCallback<T, V, O> {
        void call(T t, V v, O o);
    }
}
