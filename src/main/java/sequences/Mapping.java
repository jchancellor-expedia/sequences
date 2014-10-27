package sequences;

public interface Mapping<K, V> extends KeyedSequence<Tuple.Cons<K, Tuple.Cons<V, Tuple.Empty>>, K> {
    @Override
    public abstract Mapping<K, V> rest();
    
    @Override
    public default K keyFor(Tuple.Cons<K, Tuple.Cons<V, Tuple.Empty>> pair) {
        return Tuple.first(pair);
    }
    
    public default Sequence<V> valuesWithKey(K key) {
        return allWithKey(key).map(Tuple::second);
    }
    
    public default V firstValueWithKey(K key) {
        return Tuple.second(firstWithKey(key));
    }
}
