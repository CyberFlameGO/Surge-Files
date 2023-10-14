package dev.lbuddyboy.crates.util;

import java.util.Objects;
import java.util.function.Function;

public class Pair<F, S> {

    private final F first;
    private final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }


    public F getFirst() {
        return this.first;
    }


    public S getSecond() {
        return this.second;
    }


    public Pair<S, F> swap() {
        return of(this.second, this.first);
    }

    @Override
    public String toString() {
        return "Pair{" + "first=" + first + ", second=" + second + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair<?, ?>) {
            Pair<?, ?> other = (Pair<?, ?>) obj;
            return Objects.equals(this.first, other.first) && Objects.equals(this.second, other.second);
        }
        return false;
    }


    public <F2> Pair<F2, S> mapFirst(Function<? super F, ? extends F2> function) {
        return of(function.apply(this.first), this.second);
    }


    public <S2> Pair<F, S2> mapSecond(Function<? super S, ? extends S2> function) {
        return of(this.first, function.apply(this.second));
    }


    public static <F, S> Pair<F, S> of(F first, S second) {
        return new Pair<>(first, second);
    }
}