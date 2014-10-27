package sequences;

import static sequences.Sequences.cons;
import static sequences.Sequences.delay;
import static sequences.Sequences.sequence;

public class ChangeMaker {

    public static void main(String[] args) {
        System.out.println(
                makeChange(1313, sequence(1000, 500, 200, 100, 50, 20, 10, 5, 2, 1))
                        .get(42334)
                        .makeString());
    }

    private static Sequence<Sequence<Integer>> makeChange(int total,
            Sequence<Integer> coins) {
        return delay(() -> total == 0 ? sequence(sequence()) : total < 0 || coins.isEmpty()
                ? sequence() : makeChange(total - coins.first(), coins)
                        .map(c -> cons(coins.first(), c))
                        .concat(makeChange(total, coins.rest())));
    }
}
