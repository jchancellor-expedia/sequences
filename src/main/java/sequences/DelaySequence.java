package sequences;

import static java.util.Objects.requireNonNull;

import java.util.function.Supplier;

public class DelaySequence<E> extends AbstractSequence<E> {

    private Supplier<Sequence<E>> supplier;
    private Sequence<E> supplied;
    private Object lock = new Object();

    private DelaySequence(Supplier<Sequence<E>> supplier) {
        this.supplier = requireNonNull(supplier);
    }

    public static <F> Sequence<F> delay(Supplier<Sequence<F>> supplier) {
        return new DelaySequence<>(supplier);
    }
    
    private Sequence<E> get() {
        DelaySequence<E> ds = this;
        while (true) {
            Sequence<E> s = ds.supply();
            if (s.getClass() != DelaySequence.class) {
                return s;
            }
            ds = (DelaySequence<E>) s;
        }
    }

    private Sequence<E> supply() {
        synchronized (lock) {
            if (supplied == null) {
                supplied = requireNonNull(supplier.get());
            }
            return supplied;
        }
    }

    @Override
    public boolean isEmpty() {
        return get().isEmpty();
    }

    public E first() {
        return get().first();
    }

    public Sequence<E> rest() {
        return get().rest();
    }
}
