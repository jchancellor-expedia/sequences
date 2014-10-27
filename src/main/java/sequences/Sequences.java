package sequences;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Sequences {

    private Sequences() {
    }

    public static Sequence<Integer> integers() {
        return generate(0, n -> true, n -> n, n -> n + 1);
    }
    
    public static <E> Sequence<E> cons(E first, Sequence<E> rest) {
        return ConsSequence.cons(first, rest);
    }

    public static <E> Sequence<E> empty() {
        return EmptySequence.empty();
    }

    public static <E> Sequence<E> delay(Supplier<Sequence<E>> supplier) {
        return DelaySequence.delay(supplier);
    }

    @SafeVarargs
    public static <E> Sequence<E> sequence(E... es) {
        return force(generate(0, i -> i < es.length, i -> es[i], i -> i + 1));
    }

    public static <F> Sequence<F> sequence(Iterable<F> iterable) {
        return sequence(iterable.iterator());
    }

    private static <F> Sequence<F> sequence(Iterator<F> iterator) {
        // This relies on the thunk being evaluated only once.
        return delay(() -> !iterator.hasNext() ? empty()
                : cons(iterator.next(), sequence(iterator)));
    }

    public static <E, S> Sequence<E> generate(S state,
            Predicate<? super S> checker,
            Function<? super S, ? extends E> getter,
            Function<? super S, ? extends S> mover,
            boolean moveFirst) {
        return generate(moveFirst ? state : mover.apply(state),
                checker, getter, mover);
    }
    
    public static <E, S> Sequence<E> generate(S state,
            Predicate<? super S> checker,
            Function<? super S, ? extends E> getter,
            Function<? super S, ? extends S> mover) {
        return delay(() -> !checker.test(state) ? empty() : cons(
                getter.apply(state),
                generate(mover.apply(state), checker, getter, mover)));
    }

    public static int size(Sequence<?> sequence) {
        return reduce(sequence, 0, (r, e) -> r + 1);
    }

    public static <E> E get(Sequence<E> sequence, int index) {
        // return drop(sequence, index).first();
        while (index-- > 0) {
            sequence = sequence.rest();
        }
        return sequence.first();
    }

    public static <E, R> R reduce(Sequence<E> sequence, R reduced,
            BiFunction<? super R, ? super E, ? extends R> reducer) {
        while (!sequence.isEmpty()) {
            reduced = reducer.apply(reduced, sequence.first());
            sequence = sequence.rest();
        }
        return reduced;
    }

    public static <E> void forEach(Sequence<E> sequence,
            Consumer<? super E> consumer) {
        reduce(sequence, null, (r, e) -> {
            consumer.accept(e);
            return null;
        });
    }

    public static <E> Sequence<E> force(Sequence<E> sequence) {
        return reverse(reverse(sequence));
    }

    public static <E> Sequence<E> reverse(Sequence<E> sequence) {
        return reduce(sequence, empty(), (r, e) -> cons(e, r));
    }

    public static <E> Sequence<E> concat(Sequence<E> front, Sequence<E> rest) {
        return delay(() -> front.isEmpty() ? rest : cons(front.first(),
                concat(front.rest(), rest)));
    }

    public static <E> Sequence<E> concat(Sequence<? extends Sequence<E>> sequences) {
        return delay(() -> sequences.isEmpty() ? empty()
                : concat(sequences.first(), concat(sequences.rest())));
    }

    public static <E> Sequence<E> filter(Sequence<E> sequence,
            Predicate<? super E> predicate) {
        return delay(() -> sequence.isEmpty() ? empty()
                : predicate.test(sequence.first())
                        ? cons(sequence.first(),
                                filter(sequence.rest(), predicate))
                        : filter(sequence.rest(), predicate));
    }

    public static <E, F> Sequence<F> map(Sequence<E> sequence,
            Function<? super E, ? extends F> function) {
        return delay(() -> sequence.isEmpty() ? empty()
                : cons(function.apply(sequence.first()),
                        map(sequence.rest(), function)));
    }

    public static <E> Sequence<E> take(Sequence<E> sequence, int size) {
        return delay(() -> sequence.isEmpty() || size <= 0 ? empty()
                : cons(sequence.first(), take(sequence.rest(), size - 1)));
    }

    public static <E> Sequence<E> drop(Sequence<E> sequence, int size) {
        return delay(() -> sequence.isEmpty() || size <= 0 ? sequence
                : drop(sequence.rest(), size - 1));
    }

    public static <E> Sequence<E> slice(Sequence<E> sequence, int from, int to, int step) {
        return get(unbraid(drop(take(sequence, to), from), step), 0);
    }
    
    public static <E> Sequence<E> cycle(Sequence<E> sequence) {
        return concat(sequence, delay(() -> cycle(sequence)));
    }

    public static <E> boolean any(Sequence<E> sequence, Predicate<? super E> predicate) {
        return !filter(sequence, predicate).isEmpty();
    }

    public static <E> boolean all(Sequence<E> sequence, Predicate<? super E> predicate) {
        return !any(sequence, predicate.negate());
    }

    public static <E extends Comparable<? super E>> Sequence<E> sort(Sequence<E> sequence) {
        return sort(sequence, Comparator.naturalOrder());
    }

    public static <E> Sequence<E> sort(Sequence<E> sequence,
            Comparator<? super E> comparator) {
        // merging and comparisons are lazy, but splitting eager currently
        int mid = size(sequence) / 2;
        return mid == 0 ? sequence : merge(
                sort(take(sequence, mid), comparator),
                sort(drop(sequence, mid), comparator), comparator);
    }

    private static <E> Sequence<E> merge(Sequence<E> one, Sequence<E> two,
            Comparator<? super E> comparator) {
        // Arguments must be sorted and non-empty
        return delay(() -> comparator.compare(one.first(), two.first()) <= 0
                ? cons(one.first(), one.rest().isEmpty() ? two : merge(
                        one.rest(), two, comparator))
                : cons(two.first(), two.rest().isEmpty() ? one : merge(
                        one, two.rest(), comparator)));
    }

    public static <E> E min(Sequence<E> sequence, Comparator<? super E> comparator) {
        return sort(sequence, comparator).first();
    }

    public static <E> E max(Sequence<E> sequence, Comparator<? super E> comparator) {
        return min(sequence, comparator.reversed());
    }

    public static <E> Sequence<E> braid(Sequence<Sequence<E>> sequences) {
        return concat(transpose(sequences));
    }

    public static <E> Sequence<Sequence<E>> unbraid(Sequence<E> sequence, int size) {
        return transpose(partition(sequence, size));
    }

    public static <E> Sequence<Sequence<E>> partition(Sequence<E> sequence, int size) {
        return delay(() -> sequence.isEmpty() ? empty() : cons(
                take(sequence, size), partition(drop(sequence, size), size)));
    }

    // precondition: sequences.all(Sequence::nonEmpty)
    public static <E> Sequence<Sequence<E>> transpose(Sequence<Sequence<E>> sequences) {
        return delay(() -> sequences.isEmpty() || sequences.first().isEmpty()
                ? fail("transpose(): sequences.first().first() must exist")
                : sequences.first().rest().isEmpty()
                        ? Sequences.<Sequence<E>>cons(
                                mapFirstHelper(sequences, true),
                                empty())
                        : Sequences.<Sequence<E>>cons(
                                mapFirstHelper(sequences, false),
                                transpose(mapRestHelper(sequences))));
    }

    private static <E> Sequence<E> mapFirstHelper(Sequence<Sequence<E>> sequences,
            boolean restMustBeEmpty) {
        return delay(() -> sequences.isEmpty() ? empty()
                : restMustBeEmpty && !sequences.first().rest().isEmpty()
                        ? fail("transpose(): size of sub-sequences must be decreasing")
                        : cons(sequences.first().first(), mapFirstHelper(sequences.rest(),
                                sequences.first().rest().isEmpty())));
    }

    private static <E> Sequence<Sequence<E>> mapRestHelper(Sequence<Sequence<E>> sequences) {
        return delay(() -> sequences.isEmpty() ? empty()
                : sequences.first().rest().isEmpty() ? empty()
                        : cons(sequences.first().rest(), mapRestHelper(sequences.rest())));
    }

    public static <E> Sequence<E> takeWhile(Sequence<E> sequence,
            Predicate<? super E> predicate) {
        return delay(() -> sequence.isEmpty()
                || !predicate.test(sequence.first()) ? empty()
                : cons(sequence.first(), takeWhile(
                        sequence.rest(), predicate)));
    }

    public static <E> Sequence<E> dropWhile(Sequence<E> sequence,
            Predicate<? super E> predicate) {
        return delay(() -> sequence.isEmpty()
                || !predicate.test(sequence.first()) ? sequence
                : dropWhile(sequence.rest(), predicate));
    }

    public static <E> Sequence<E> distinct(Sequence<E> sequence) {
        return distinct(sequence, Function.identity());
    }

    public static <E> Sequence<E> distinct(Sequence<E> sequence,
            Function<? super E, ?> keyer) {
        return distinct(sequence, keyer, empty());
    }
    
    private static <E, K> Sequence<E> distinct(Sequence<E> sequence,
            Function<? super E, ? extends K> keyer, Sequence<K> forbidden) {
        return delay(() -> {
            if (sequence.isEmpty()) return empty();
            K key = keyer.apply(sequence.first());
            return contains(forbidden, key)
                    ? distinct(sequence.rest(), keyer, forbidden)
                    : cons(sequence.first(), distinct(
                            sequence.rest(), keyer, cons(key, forbidden)));
        });
    }

    public static <E> Sequence<E> distinct(Sequence<E> sequence,
            Comparator<? super E> comparator) {
        return dedup(sort(sequence, comparator), comparator);
    }

    private static <E> Sequence<E> dedup(Sequence<E> sequence,
            Comparator<? super E> comparator) {
        return delay(() -> sequence.isEmpty() || sequence.rest().isEmpty()
                ? empty() : comparator.compare(
                        sequence.first(), sequence.rest().first()) == 0
                        ? dedup(sequence.rest(), comparator)
                        : cons(sequence.first(),
                                dedup(sequence.rest(), comparator)));
    }

    public static <E> boolean contains(Sequence<E> sequence, E that) {
        return any(sequence, that::equals);
    }
    
    // removes the first occurence, if any, from the sequence
    public static <E> Sequence<E> withoutFirst(Sequence<E> sequence, E element) {
        return filterFirst(sequence, e -> !e.equals(element));
    }
    
    // removes the first occurence, if any, from the sequence
    public static <E> Sequence<E> filterFirst(Sequence<E> sequence,
            Predicate<? super E> predicate) {
        return delay(() -> sequence.isEmpty()
                ? empty()
                : !predicate.test(sequence.first())
                        ? sequence.rest()
                        : cons(
                                sequence.first(),
                                filterFirst(sequence.rest(), predicate)));
    }

    public static <E> Sequence<E> difference(Sequence<E> one, Sequence<E> two) {
        return delay(() -> one.isEmpty() ? empty()
                : contains(two, one.first())
                        ? difference(one.rest(), withoutFirst(two, one.first()))
                        : cons(one.first(), difference(one.rest(), two)));
    }

    public static <E> Sequence<E> intersection(Sequence<E> one, Sequence<E> two) {
        // return difference(one, difference(one, two));
        return delay(() -> one.isEmpty() ? empty()
                : contains(two, one.first())
                        ? cons(one.first(), intersection(one.rest(),
                                withoutFirst(two, one.first())))
                        : intersection(one.rest(), two));
    }

    public static <E> Sequence<E> symmetricDifference(Sequence<E> one, Sequence<E> two) {
        return concat(difference(one, two), difference(two, one));
    }

    public static <E> Sequence<E> union(Sequence<E> one, Sequence<E> two) {
        return concat(one, difference(two, one));
    }
    
    public static <E> boolean equalsUnordered(Sequence<E> one, Sequence<E> two) {
        return subset(one, two) && subset(two, one);
    }
    
    public static <E> boolean subset(Sequence<E> one, Sequence<E> two) {
        return difference(one, two).isEmpty();
    }
    
    public static <E> boolean disjoint(Sequence<E> one, Sequence<E> two) {
        return intersection(one, two).isEmpty();
    }

    public static <E> Sequence<E> reduce(Sequence<Sequence<E>> sequences,
            BiFunction<Sequence<E>, Sequence<E>, Sequence<E>> function) {
        return delay(() -> sequences.isEmpty() ? empty()
                : function.apply(
                        sequences.first(),
                        reduce(sequences.rest(), function)));
    }
    
    public static Sequence<Sequence<Double>> matrixMultiply(
            Sequence<Sequence<Double>> one, Sequence<Sequence<Double>> two) {
        Sequence<Sequence<Double>> twoTransposed = transpose(two);
        return map(one, row -> map(twoTransposed, col -> dotProduct(row, col))); 
    }
    
    public static <E, R> Sequence<R> zipWith(Sequence<E> one, Sequence<E> two,
            BiFunction<E, E, R> function) {
        return delay(() -> one.isEmpty() & two.isEmpty() ? empty()
                : one.isEmpty() | two.isEmpty()
                ? fail("sequences must be of the same size")
                : cons(function.apply(one.first(), two.first()),
                        zipWith(one.rest(), two.rest(), function)));
    }
    
    public static double dotProduct(
            Sequence<Double> one, Sequence<Double> two) {
        return reduce(
                zipWith(one, two, (a, b) -> a * b),
                0.0,
                (r, e) -> r + e);
    }

    public static String makeString(Sequence<?> sequence) {
        return makeString(sequence, -1);
    }

    public static String makeString(Sequence<?> sequence, int size) {
        StringBuilder s = new StringBuilder("[");
        boolean first = true;
        while (!sequence.isEmpty()) {
            if (first) first = false;
            else s.append(", ");
            if (size == 0) {
                s.append("...");
                break;
            }
            s.append(sequence.first());
            if (size > 0) size--;
            sequence = sequence.rest();
        }
        return s.append("]").toString();
    }

    public static <E, K> KeyedSequence<E, K> key(Sequence<? extends E> sequence,
            Function<? super E, ? extends K> keyer) {
        class A extends AbstractKeyedSequence<E, K> {
            private Sequence<? extends E> s;
            public A(Sequence<? extends E> s) {
                this.s = s;
            }
            public K keyFor(E element) {
                return keyer.apply(element);
            }
            public boolean isEmpty() {
                return s.isEmpty();
            }
            public E first() {
                return s.first();
            }
            public KeyedSequence<E, K> rest() {
                return new A(s.rest());
            }
        }
        return new A(sequence);
    }

    private static <T> T fail(String message) {
        throw new RuntimeException(message);
    }
}
