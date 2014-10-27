package sequences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;

public class SieveGuava {

    public static void main(String[] args) {
        System.out.println(Iterables.toString(computePrimes(100)));
    }

    private static Iterable<Integer> computePrimes(int upperBound) {
        List<Integer> range = halfOpenRange(2, upperBound);
        return sieve(range);
    }

    private static Iterable<Integer> sieve(Iterable<Integer> lst) {
        return Iterables.isEmpty(lst) ? lst : Iterables.concat(Arrays.asList(lst.iterator().next()),
                sieve(removeMultiples(lst.iterator().next(), Iterables.skip(lst, 1))));
    }

    private static Iterable<Integer> removeMultiples(Integer factor, Iterable<Integer> list) {
        return Iterables.filter(list, e -> e % factor != 0);
    }

    private static List<Integer> halfOpenRange(int lowerBound, int upperBound) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = lowerBound; i < upperBound; i++) {
            result.add(i);
        }
        return result;
    }

//    private static Iterable<Integer> computePrimes(int upperBound) {
//        return sieve(halfOpenRange(2, upperBound));
//    }
//
//    private static Iterable<Integer> sieve(Iterable<Integer> lst) {
//        return Iterables.isEmpty(lst) ? lst : Iterables.concat(Arrays.asList(lst.iterator().next()),
//                sieve(removeMultiples(lst.iterator().next(), Iterables.skip(lst, 1))));
//    }
//
//    private static Iterable<Integer> removeMultiples(Integer factor, Iterable<Integer> list) {
//        return Iterables.filter(list, e -> e % factor != 0);
//    }
//
//    private static List<Integer> halfOpenRange(int lowerBound, int upperBound) {
//        List<Integer> result = new ArrayList<Integer>();
//        for (int i = lowerBound; i < upperBound; i++) {
//            result.add(i);
//        }
//        return result;
//    }
}
