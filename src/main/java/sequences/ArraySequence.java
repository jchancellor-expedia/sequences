package sequences;

import java.util.ArrayList;

public class ArraySequence<E> extends AbstractSequence<E> {
    
    private ArrayList<E> wrapped;
    private int offset;

    public ArraySequence(ArrayList<E> wrapped) {
        this(wrapped, 0);
    }

    private ArraySequence(ArrayList<E> wrapped, int offset) {
        this.wrapped = wrapped;
        this.offset = offset;
    }
    
    public void add(E element) {
        wrapped.add(element);
    }

    @Override
    public boolean isEmpty() {
        return offset == wrapped.size();
    }

    @Override
    public E first() {
        return wrapped.get(offset);
    }

    @Override
    public ArraySequence<E> rest() {
        return new ArraySequence<>(wrapped, offset + 1);
    }
}
