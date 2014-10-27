package sequences;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Sequence<E> extends Iterable<E> {

    public abstract boolean isEmpty();

    public abstract E first();

    public abstract Sequence<E> rest();
    
    public default int size() {
        return Sequences.size(this);
    }

    public default E get(int index) {
        return Sequences.get(this, index);
    }

    public default <R> R reduce(R reduced,
            BiFunction<? super R, ? super E, ? extends R> reducer) {
        return Sequences.reduce(this, reduced, reducer);
    }

    public default void forEach(Consumer<? super E> consumer) {
        Sequences.forEach(this, consumer);
    }

    public default Sequence<E> force() {
        return Sequences.force(this);
    }

    public default Sequence<E> reverse() {
        return Sequences.reverse(this);
    }

    public default Sequence<E> concat(Sequence<E> rest) {
        return Sequences.concat(this, rest);
    }

    public default Sequence<E> filter(Predicate<? super E> predicate) {
        return Sequences.filter(this, predicate);
    }

    public default <F> Sequence<F> map(Function<? super E, ? extends F> function) {
        return Sequences.map(this, function);
    }

    public default Sequence<E> take(int size) {
        return Sequences.take(this, size);
    }

    public default Sequence<E> drop(int size) {
        return Sequences.drop(this, size);
    }

    public default Sequence<E> slice(int from, int to, int step) {
        return Sequences.slice(this, from, to, step);
    }
    
    public default Sequence<E> cycle() {
        return Sequences.cycle(this);
    }

    public default boolean any(Predicate<? super E> predicate) {
        return Sequences.any(this, predicate);
    }

    public default boolean all(Predicate<? super E> predicate) {
        return Sequences.all(this, predicate);
    }

    public default boolean contains(E that) {
        return Sequences.contains(this, that);
    }

    public default Sequence<E> distinct() {
        return Sequences.distinct(this);
    }

    public default Sequence<E> distinct(Function<? super E, ?> function) {
        return Sequences.distinct(this, function);
    }

    public default Sequence<E> sort(Comparator<? super E> comparator) {
        return Sequences.sort(this, comparator);
    }

    public default E min(Comparator<? super E> comparator) {
        return Sequences.min(this, comparator);
    }

    public default E max(Comparator<? super E> comparator) {
        return Sequences.max(this, comparator);
    }

    public default Sequence<Sequence<E>> split(int size) {
        return Sequences.unbraid(this, size);
    }

    public default Sequence<Sequence<E>> partition(int size) {
        return Sequences.partition(this, size);
    }

    public default String makeString() {
        return Sequences.makeString(this);
    }

    public default String makeString(int size) {
        return Sequences.makeString(this, size);
    }

    @Override
    public default Iterator<E> iterator() {
        return new Iterator<E>() {
            private Sequence<E> sequence = Sequence.this;
            public boolean hasNext() {
                return !sequence.isEmpty();
            }
            public E next() {
                E next = sequence.first();
                sequence = sequence.rest();
                return next;
            }
        };
    }
}
