package sequences;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Bags {
    
    public static interface F { public int apply(int n); }
    public static interface R { public F apply(R r); }
    private static Function<Function<F, F>, F> fix = f ->
            ((R) r -> f.apply(v -> r.apply(r).apply(v))).apply(
                 r -> f.apply(v -> r.apply(r).apply(v)));

    public static void main(String[] args) {
        Predicate<Integer> set = x -> x == 0 | x == 1 | x == 2;
        BiFunction<Predicate<Integer>, Predicate<Integer>, Predicate<Integer>> or = (s, t) -> x -> s.test(x) | t.test(x);
    }

    private static void a() {
        System.out.println(fix.apply(f -> n -> n == 0 ? 0 : n + f.apply(n - 1)).apply(10));
    }

    private static void b() {
        Function<Integer, Function<Integer, Function<Function<Integer, Function<Integer, Integer>>, Integer>>>
                a = p -> q -> f -> f.apply(p).apply(q);
        Function<Function<Function<Integer, Function<Integer, Integer>>, Integer>, Integer>
                b = r -> r.apply(p -> q -> p);
                Function<Function<Function<Integer, Function<Integer, Integer>>, Integer>, Integer>
                c = r -> r.apply(p -> q -> q);
        System.out.println(b.apply(a.apply(1).apply(2)));
        
    }
}
