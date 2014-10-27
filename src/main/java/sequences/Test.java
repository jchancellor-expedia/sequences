package sequences;

import static sequences.Sequences.integers;

public class Test {

    public static void main(String[] args) {
        testDistinct();
    }

    private static void testDistinct() {
        System.out.println(
                integers()
                        .distinct()
                        .drop(10000)
                        .take(3)
                        .makeString());
    }
}
