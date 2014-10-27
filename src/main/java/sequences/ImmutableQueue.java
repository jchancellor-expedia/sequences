package sequences;

import static sequences.Sequences.cons;
import static sequences.Sequences.empty;

import java.util.Iterator;

public class ImmutableQueue<E> extends AbstractSequence<E> {
    
    private Sequence<E> front = empty();
    private Sequence<E> back = empty();
    
    public ImmutableQueue(Sequence<E> front, Sequence<E> back) {
        this.front = front;
        this.back = back;
    }

    public ImmutableQueue<E> plus(E element) {
        return new ImmutableQueue<>(front, cons(element, back));
    }
    
    @Override
    public boolean isEmpty() {
        prepareForReading();
        return front.isEmpty();
    }
    
    @Override
    public E first() {
        prepareForReading();
        return front.first();
    }

    @Override
    public ImmutableQueue<E> rest() {
        prepareForReading();
        return new ImmutableQueue<>(front.rest(), back);
    }

    @Override
    public Iterator<E> iterator() {
        // copied from ConsList, could share of course
        return new Iterator<E>() {
            private ImmutableQueue<E> current = ImmutableQueue.this;
            public boolean hasNext() {
                return !current.isEmpty();
            }
            public E next() {
                E next = current.first();
                current = current.rest();
                return next;
            }
        };
    }
    
    private void prepareForReading() {
        if (front.isEmpty()) {
            front = back.reverse();
            back = empty();
        }
    }
}
