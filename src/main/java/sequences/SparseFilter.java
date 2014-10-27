package sequences;

import static sequences.Sequences.cons;
import static sequences.Sequences.delay;
import static sequences.Sequences.integers;

public class SparseFilter {

    public static void main(String[] args) {
        printFibs();
        doSparseFilter();
    }

    private static void printFibs() {
        System.out.println(fibs(1, 1).take(10));
    }

    private static Sequence<Integer> fibs(int a, int b) {
        return delay(() -> cons(a, fibs(b, a + b)));
    }

    private static void doSparseFilter() {
        System.out.println(usedMemory());
        Sequence<Integer> filtered = integers()
                .filter(n -> n >= 1_000_000);
        System.out.println(usedMemory());
        System.out.println(filtered.first());
        System.out.println(usedMemory());
        System.out.println(filtered.first());
        System.out.println(usedMemory());
    }

    private static long usedMemory() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
