package sequences;

public class Trampoline {
    private static interface RecursiveThunk { public RecursiveThunk apply(); }
    private static interface Continuation { public RecursiveThunk apply(long p); }
    private static Continuation print = p -> { System.out.println(p); return null; };
    public static void main(String[] args) { run(f(5, print)); }
    private static void run(RecursiveThunk f) { while (f != null) f = f.apply(); }
    private static RecursiveThunk f(long n, Continuation c) {
        return n < 2 ? c.apply(n) : () -> f(n - 2, p -> () -> f(n - 1, q -> () -> c.apply(p + q)));
    }
}
