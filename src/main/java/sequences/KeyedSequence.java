package sequences;

public interface KeyedSequence<E, K> extends Sequence<E> {

    public abstract K keyFor(E element);

    @Override
    public abstract KeyedSequence<E, K> rest();
    
    public default Sequence<E> allWithKey(K key) {
        return filter(e -> keyFor(e).equals(key));
    }

    public default E firstWithKey(K key) {
        return allWithKey(key).first();
    }

    public default boolean containsKey(K key) {
        return !allWithKey(key).isEmpty();
    }
    
    public default Sequence<K> keys() {
        return map(this::keyFor).distinct();
    }
}
