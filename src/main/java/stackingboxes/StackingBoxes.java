package stackingboxes;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static sequences.Sequences.cons;
import static sequences.Sequences.empty;
import static sequences.Sequences.integers;
import static sequences.Sequences.zipWith;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import sequences.Sequence;
import sequences.Sequences;

class StackingBoxes {

    public static void main(String[] args) throws IOException {
        try (InputStream testInput = StackingBoxes.class.getClassLoader()
                .getResourceAsStream(
                        "stackingboxes/test-input.txt")) {
            System.setIn(testInput);
            _main();
        }
    }

    public static void _main() {
        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) {
            int boxCount = in.nextInt();
            int dimensionCount = in.nextInt();
            Sequence<Box> boxes = readBoxes(in, boxCount, dimensionCount);
            Sequence<Box> chain = longestChain(boxes);
            writeResult(chain);
        }
    }

    private static void writeResult(Sequence<Box> chain) {
        System.out.print(chain.size() + "\n");
        System.out.print(chain.isEmpty() ? "" : chain.rest().reduce(
                Integer.toString(chain.first().index()), (r, e) ->
                r + " " + Integer.toString(e.index())));
        System.out.print("\n");
    }

    private static Sequence<Box> readBoxes(
            Scanner in, int boxCount, int dimensionCount) {
        return integers().drop(1).take(boxCount)
                .map(boxIndex -> readBox(in, boxIndex, dimensionCount));
    }

    private static Box readBox(Scanner in, int boxIndex, int dimensionCount) {
        return new Box(boxIndex, integers().take(dimensionCount)
                .map(ignore -> in.nextInt())
                .force());
    }

    // Given a sequence of boxes, returns the longest chain of boxes such that
    // each box fits inside the subsequent box. Handles the case of no boxes.
    private static Sequence<Box> longestChain(Sequence<Box> boxes) {
        return longest(cons(empty(), allChains(sort(boxes))));
    }

    // Sorts boxes according to the width of the their first (ie smallest)
    // dimension. This guarantees that if one box fits inside another, they will
    // be in order. The reverse is not true of course, which is what this
    // problem is all about. "Fitting inside" only creates a partial order.
    private static Sequence<Box> sort(Sequence<Box> boxes) {
        return boxes.sort(comparing(Box::widths, comparing(Sequence::first)));
    }

    // Returns a sequence of chains. For each box in the input, there is a
    // chain in the output that begins with that box. Each chain is the longest
    // chain possible such that each box in the chain fits inside the subsequent
    // one. For example, given two medium boxes B and C (neither of which fit
    // inside the other), one tiny box A (which fits inside all other boxes),
    // and one huge box D (in which all other boxes fit), this method would
    // return the following.
    //
    // [[D, B, A],
    //  [C, A],
    //  [B, A],
    //  [A]]
    //
    // This uses O(n^2) comparisons. Also note how the reduction works.
    //
    //                                              [[D, B, A],
    //                              [[C, A],         [C, A],
    //              [[B, A],         [B, A],         [B, A],
    // [[A]]         [A]]            [A]]            [A]]
    private static Sequence<Sequence<Box>> allChains(Sequence<Box> boxes) {
        return boxes.reduce(Sequences.<Sequence<Box>>empty(), (chains, box) ->
                cons(longestChainPlusBox(chains, box), chains));
    }

    // Given some chains which may or may not fit inside the given box, returns
    // the longest chain that does fit inside with the box cons'd onto it. In
    // the case that no chains fit inside the box, just returns a singleton.
    private static Sequence<Box> longestChainPlusBox(
            Sequence<Sequence<Box>> chains, Box box) {
        return cons(box, longest(cons(empty(),
                chainsThatFitInsideBox(chains, box))));
    }

    // Only those chains that fit inside the box. Although the box is "larger"
    // than all those in all the given chains in some sense (given the initial
    // sorting of the boxes), they do not necessarily fit inside (because the
    // order is only a partial order).
    private static Sequence<Sequence<Box>> chainsThatFitInsideBox(
            Sequence<Sequence<Box>> chains, Box box) {
        return chains.filter(chain -> chain.first().fitsInside(box));
    }

    // The longest of the given chains.
    public static Sequence<Box> longest(Sequence<Sequence<Box>> chains) {
        return chains.max(comparing(Sequence::size));
    }
}

class Box {

    private int index;
    private Sequence<Integer> widths;

    public Box(int index, Sequence<Integer> widths) {
        this.index = index;
        this.widths = widths.sort(naturalOrder());
    }

    public boolean fitsInside(Box that) {
        if (widths.size() != that.widths.size()) throw new RuntimeException();
        return zipWith(widths, that.widths, (a, b) -> a < b).all(p -> p);
    }

    public int index() {
        return index;
    }

    public Sequence<Integer> widths() {
        return widths;
    }
}
