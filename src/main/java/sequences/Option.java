package sequences;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public final class Option<T> extends AbstractSequence<T> {
    
    private static final Option<?> NONE = new Option<Object>(null);
    
    private T value;
        
    private Option(T value) {
        this.value = value;
    }

    public static <U> Option<U> some(U value) {
        return new Option<>(Objects.requireNonNull(value));
    }
    
    @SuppressWarnings("unchecked")
    public static <U> Option<U> none() {
        return (Option<U>) NONE;
    }

    @Override
    public <U> Option<U> map(Function<? super T, ? extends U> function) {
        return value != null ? some(function.apply(value)) : none();
    }
    
    @Override
    public Option<T> filter(Predicate<? super T> predicate) {
        return value != null && predicate.test(value) ? this : none();
    }
    
    public <U> Option<U> flatMap(Function<? super T, Option<U>> function) {
        return value != null ? function.apply(value) : none();
    }

    public T get() {
        if (value == null) throw new NoSuchElementException();
        return value;
    }
    
    public T orElse(T alternative) {
        return value != null ? value : alternative;
    }

    @Override
    public boolean isEmpty() {
        return map(v -> false).orElse(true);
    }

    @Override
    public T first() {
        return get();
    }

    @Override
    public Option<T> rest() {
        if (value == null) throw new NoSuchElementException();
        return none();
    }
}
