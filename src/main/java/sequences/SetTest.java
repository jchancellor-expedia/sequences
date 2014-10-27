package sequences;

import static sequences.Sequences.difference;
import static sequences.Sequences.intersection;
import static sequences.Sequences.sequence;
import static sequences.Sequences.union;

public class SetTest {

    public static void main(String[] args) {
        Sequence<Integer> a = sequence(1, 1, 1, 2, 2, 3, 4);
        Sequence<Integer> b = sequence(1, 1, 3, 5);
        System.out.println("a                  = " + a);
        System.out.println("b                  = " + b);
        System.out.println("difference(a, b)   = " + difference(a, b));
        System.out.println("intersection(a, b) = " + intersection(a, b));
        System.out.println("union(a, b)        = " + union(b, a));
    }
}
