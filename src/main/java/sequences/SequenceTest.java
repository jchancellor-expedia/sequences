package sequences;

import static sequences.Sequences.*;
import static sequences.Tuple.third;
import static sequences.Tuple.tuple;
import sequences.Tuple.Cons;
import sequences.Tuple.Empty;

import java.util.Comparator;

public class SequenceTest {

    public static void main(String[] args) {
        a();
        b();
        c();
        d();
        e();
        f();
    }

    private static void f() {
        System.out.println(
                matrixMultiply(
                        sequence(
                                sequence(1.0, 2.0),
                                sequence(3.0, 4.0)),
                        sequence(
                                sequence(0.0, -1.0),
                                sequence(1.0, 0.0))));
    }

    private static void e() {
        Sequence<Integer> a = sequence(1, 1, 1, 2, 3, 4, 5, 8);
        Sequence<Integer> b = sequence(1, 1, 2, 2, 2, 6, 7);
        System.out.println(a);
        System.out.println(b);
        System.out.println(difference(a, b));
        System.out.println(intersection(a, b));
        System.out.println(union(a, b));
        System.out.println(symmetricDifference(a, b));
        check(equalsUnordered(concat(difference(a, b), b), union(a, b)));
        check(equalsUnordered(concat(symmetricDifference(a, b), intersection(a, b)), union(a, b)));
    }

    private static void d() {
        System.out.println(integers()
                .take(10000)
                .distinct(n -> n % 5));
        System.out.println(integers()
                .slice(10, 20, 3));
        System.out.println(
                transpose(transpose(transpose(
                        integers().take(133).partition(3)))));
        try {
            System.out.println(
                    transpose(
                            integers().take(5).partition(2).reverse()));
        } catch (RuntimeException expected) {
        }
    }

    private static void c() {
        class A extends AbstractSequence<Cons<Integer, Cons<String, Empty>>>
                implements Mapping<Integer, String> {
            private int n;
            public A(int n) {
                this.n = n;
            }
            public Mapping<Integer, String> rest() {
                return new A(n + 1);
            }
            public boolean isEmpty() {
                return false;
            }
            public Cons<Integer, Cons<String, Empty>> first() {
                return Tuple.tuple(n, "numero " + Integer.toString(n));
            }
        }
        System.out.println(new A(0)
                .valuesWithKey(2)
                .first());
    }

    private static void b() {
        System.out.println(integers()
                .take(10)
                .key(e -> e % 3)
                .allWithKey(0));
        System.out.println(integers()
                .take(10)
                .key(e -> e % 3)
                .keys());
        System.out.println(integers()
                .take(100)
                .map(e -> e % 8)
                .distinct());
        System.out.println(integers()
                .take(23)
                .map(n -> n * 19 % 23)
                .sort(Comparator.naturalOrder()));
        System.out.println(integers()
                .take(11)
                .map(n -> n * 7 % 11)
                .sort(Comparator.naturalOrder())
                .first()); // O(n)!
        System.out.println(integers()
                .take(11)
                .map(n -> n * 7 % 11));
        System.out.println(integers()
                .slice(15, 25, 3));
    }

    private static void a() {
        System.out.println(third(tuple(1, 2, 3)));
        System.out.println(integers()
                .take(10)
                .map(n -> n * n)
                .reduce(Sequences.<Integer>empty(), (r, e) ->
                        cons(r.isEmpty() ? e : r.first() + e, r)));
        System.out.println(integers()
                .take(10)
                .map(n -> n * n)
                .first());
        System.out.println(integers()
                .take(10)
                .filter(n -> n < 2)
                .map(n -> n ^ 3)
                .any(n -> n > 3));
        System.out.println(integers()
                .take(10)
                .filter(n -> n < 2)
                .map(n -> n ^ 3)
                .any(n -> n > 3));
        System.out.println(
                any(
                        map(
                                filter(
                                        take(
                                                integers(),
                                                10),
                                        n -> n < 2),
                                n -> n ^ 3),
                        n -> n > 3));
        System.out.println(integers()
                .take(10)
                .force());
    }

    private static void check(boolean condition) {
        if (!condition) throw new RuntimeException();
    }
}
