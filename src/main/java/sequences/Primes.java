package sequences;

import static sequences.Sequences.cons;
import static sequences.Sequences.delay;
import static sequences.Sequences.empty;
import static sequences.Sequences.integers;

public class Primes {
    
    public static void main(String[] args) {
        System.out.println(allPrimes().drop(1000).take(10));
        System.out.println(quickerPrimes().drop(1000).take(10));
    }

    public static Sequence<Integer> allPrimes() {
        return sieve(integers().drop(2));
    }

    private static Sequence<Integer> sieve(Sequence<Integer> seq) {
        return delay(() -> seq.isEmpty() ? empty() : cons(seq.first(),
                sieve(seq.rest().filter(e -> e % seq.first() != 0))));
    }

    private static Sequence<Integer> quickerPrimes() {
        return quickerSieve(2, empty());
    }

    private static Sequence<Integer> quickerSieve(int candidate, Sequence<Integer> primes) {
        return delay(() -> primes.any(p -> candidate % p == 0)
                ? quickerSieve(candidate + 1, primes)
                : cons(candidate, quickerSieve(candidate + 1, cons(candidate, primes))));
    }
}
