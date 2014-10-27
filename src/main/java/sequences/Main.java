package sequences;

import java.util.NavigableSet;

import com.google.common.base.Supplier;

public class Main {

    private static Supplier<Object> foo;

    public static void main(String[] args) throws InterruptedException {
        NavigableSet<String> n;
        foo = () -> {
            System.out.println("getting");
            return foo;
        };
        for (int i = 0; i < 10; i++) {
            Supplier<Object> object = (Supplier<Object>) foo.get();
        }
    }
}
