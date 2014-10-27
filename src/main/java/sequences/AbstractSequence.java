package sequences;

import java.util.Objects;

public abstract class AbstractSequence<E> implements Sequence<E> {

    @Override
    public boolean equals(Object object) {
        if (object == this) return true;
        if (!(object instanceof Sequence)) return false;
        Sequence<E> s1 = this;
        Sequence<?> s2 = (Sequence<?>) object;
        while (!s1.isEmpty() && !s2.isEmpty()) {
            if (!Objects.equals(s1.first(), s2.first())) return false;
            s1 = s1.rest();
            s2 = s2.rest();
        }
        return s1.isEmpty() && s2.isEmpty();
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        Sequence<E> node = this;
        while (!node.isEmpty()) {
            hashCode = hashCode * 31 + (node.first() == null
                    ? 0 : node.first().hashCode());
            node = node.rest();
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        return makeString(10);
    }
}
