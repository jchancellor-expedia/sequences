package sequences;

import java.util.NoSuchElementException;

public class EmptySequence<E> extends AbstractSequence<E> {

    private static final EmptySequence<?> EMPTY = new EmptySequence<>();
    
    private EmptySequence() {
    }

    @SuppressWarnings("unchecked")
    public static <E> Sequence<E> empty() {
        return (Sequence<E>) EMPTY;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public E first() {
        throw new NoSuchElementException();
    }

    @Override
    public Sequence<E> rest() {
        throw new NoSuchElementException();
    }
}
