package sequences;

import static sequences.Sequences.cons;
import static sequences.Sequences.delay;
import static sequences.Sequences.empty;
import static sequences.Sequences.concat;
import static sequences.Sequences.integers;
import static sequences.Sequences.sequence;

public class Permutations {

    public static void main(String[] args) {
        System.out.println(
                combinations(integers().take(5), 3)
                        .map(Sequences::makeString)
                        .makeString());
        System.out.println(
                permutations(integers().take(8))
                .get(10000)
                .makeString());
    }

    public static <T> Sequence<Sequence<T>> combinations(
            Sequence<T> sequence) {
        return combinations(sequence, sequence.size());
    }

    public static <T> Sequence<Sequence<T>> combinations(
            Sequence<T> sequence, int size) {
        return delay(() -> size == 0 ? sequence(empty()) : sequence.isEmpty()
                ? empty() : combinations(sequence.rest(), size - 1)
                        .map(c -> cons(sequence.first(), c))
                        .concat(combinations(sequence.rest(), size)));
    }

    public static <T> Sequence<Sequence<T>> permutations(
            Sequence<T> sequence) {
        return permutations(sequence, sequence.size());
    }

    public static <T> Sequence<Sequence<T>> permutations(
            Sequence<T> sequence, int size) {
        if (size != sequence.size()) throw new RuntimeException();
        return sequence.isEmpty() ? sequence(empty()) : concat(eachAtFront(sequence)
                .map(s -> permutations(s.rest())
                        .map(r -> cons(s.first(), r))));
    }

    private static <T> Sequence<Sequence<T>> eachAtFront(Sequence<T> sequence) {
        return delay(() -> cons(sequence, sequence.rest().isEmpty() ? empty()
                : eachAtFront(sequence.rest()).map(r ->
                        cons(r.first(), cons(sequence.first(), r.rest())))));
    }
}
