package sequences;

import java.util.Objects;

public class ConsSequence<E> extends AbstractSequence<E> {

    private E first;
    private Sequence<E> rest;

    private ConsSequence(E first, Sequence<E> rest) {
        this.first = first;
        this.rest = Objects.requireNonNull(rest);
    }
    
    public static <F> ConsSequence<F> cons(F first, Sequence<F> rest) {
        return new ConsSequence<>(first, rest);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    public E first() {
        return first;
    }

    public Sequence<E> rest() {
        return rest;
    }
}
