package sequences;

import java.util.Objects;

public abstract class Tuple {
    
    public static class Empty extends Tuple {

        private static final Empty EMPTY = new Empty();
        
        private Empty() {
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }
    }

    public static class Cons<F, R extends Tuple> extends Tuple {

        private F first;
        private R rest;

        private Cons(F first, R rest) {
            this.first = first;
            this.rest = Objects.requireNonNull(rest);
        }

        public F first() {
            return first;
        }

        public R rest() {
            return rest;
        }

        @Override
        public int hashCode() {
            return (first == null ? 0 : first.hashCode()) + rest.hashCode() * 31;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(this instanceof Cons)) return false;
            @SuppressWarnings("unchecked")
            Cons<F, R> that = (Cons<F, R>) obj;
            return Objects.equals(first, that.first)
                    && rest.equals(that.rest);
        }
    }

    public static Empty tuple() {
        return tupleEmpty();
    }

    public static <A> Cons<A, Empty> tuple(A first) {
        return tupleCons(first, tuple());
    }

    public static <A, B> Cons<A, Cons<B, Empty>> tuple(A first, B second) {
        return tupleCons(first, tuple(second));
    }

    public static <A, B, C> Cons<A, Cons<B, Cons<C, Empty>>> tuple(A first, B second, C third) {
        return tupleCons(first, tuple(second, third));
    }

    public static <A, B, C, D> Cons<A, Cons<B, Cons<C, Cons<D, Empty>>>> tuple(A first, B second, C third, D fourth) {
        return tupleCons(first, tuple(second, third, fourth));
    }

    public static <A, B, C, D, E> Cons<A, Cons<B, Cons<C, Cons<D, Cons<E, Empty>>>>>
            tuple(A first, B second, C third, D fourth, E fifth) {
        return tupleCons(first, tuple(second, third, fourth, fifth));
    }
    
    public static <A, R extends Tuple> A first(Cons<A, R> tuple) {
        return tuple.first();
    }
    
    public static <A, B, R extends Tuple> B second(Cons<A, Cons<B, R>> tuple) {
        return tuple.rest().first();
    }
    
    public static <A, B, C, R extends Tuple> C third(Cons<A, Cons<B, Cons<C, R>>> tuple) {
        return tuple.rest().rest().first();
    }
    
    public static <A, B, C, D, R extends Tuple> D fourth(Cons<A, Cons<B, Cons<C, Cons<D, R>>>> tuple) {
        return tuple.rest().rest().rest().first();
    }
    
    public static <A, B, C, D, E, R extends Tuple> E fifth(Cons<A, Cons<B, Cons<C, Cons<D, Cons<E, R>>>>> tuple) {
        return tuple.rest().rest().rest().rest().first();
    }

    public static Empty tupleEmpty() {
        return Empty.EMPTY;
    }

    public static <F, R extends Tuple> Cons<F, R> tupleCons(F first, R rest) {
        return new Cons<>(first, rest);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("(");
        Tuple t = this;
        boolean first = true;
        while (t instanceof Cons) {
            if (first) first = false;
            else s.append(", ");
            s.append(((Cons<?, ?>) t).first());
            t = ((Cons<?, ?>) t).rest();
        }
        return s.append(")").toString();
    }
}
