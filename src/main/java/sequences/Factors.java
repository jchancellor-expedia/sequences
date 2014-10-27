package sequences;

import static sequences.Primes.allPrimes;
import static sequences.Sequences.cons;
import static sequences.Sequences.empty;
import static sequences.Sequences.intersection;
import static sequences.Sequences.reduce;
import static sequences.Sequences.union;

public class Factors {
    
    public static void main(String[] args) {
        int a = 18;
        int b = 60;
        System.out.println(String.format(
                "Factors of %d are %s",
                a, factors(a).toString()));
        System.out.println(String.format(
                "Highest common factor of %d and %d is %d",
                a, b, highestCommonFactor(a, b)));
        System.out.println(String.format(
                "Lowest common multiple of %d and %d is %d",
                a, b, lowestCommonMultiple(a, b)));
    }

    public static int lowestCommonMultiple(int a, int b) {
        return product(union(factors(a), factors(b)));
    }

    public static int highestCommonFactor(int a, int b) {
        return product(intersection(factors(a), factors(b)));
    }

    public static int product(Sequence<Integer> factors) {
        return reduce(factors, 1, (r, e) -> r * e);
    }

    public static Sequence<Integer> factors(int product) {
        return factors(product, allPrimes());
    }

    private static Sequence<Integer> factors(int product,
            Sequence<Integer> primes) {
        return product == 1 ? empty() : product % primes.first() == 0
                ? cons(primes.first(),
                        factors(product / primes.first(), primes))
                : factors(product, primes.rest());
    }
}
