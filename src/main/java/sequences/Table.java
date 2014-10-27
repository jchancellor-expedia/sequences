package sequences;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import com.google.common.collect.Multimap;

public class Table<E> {

    private static class Person {
        private String name;
        private int age;
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public String getName() {
            return name;
        }
        public int getAge() {
            return age;
        }
        public String toString() {
            return name + " (" + age + ")";
        }
    }

    public static void main(String[] args) {
        Function<Person, Object> name = Person::getName;
        Function<Person, Object> age = Person::getAge;
        Table<Person> people = new Table<>(Arrays.asList(name, age));
        people.add(new Person("Adam", 20));
        people.add(new Person("Adam", 21));
        people.add(new Person("Brian", 23));
        people.add(new Person("Charlie", 21));
        System.out.println(people.get(name, "Adam"));
        System.out.println(people.get(name, "Brian"));
        System.out.println(people.get(age, 21));
    }
    
    public static class Index<E> {
        
        private Function<E, Object> function;

        public Index(Function<E, Object> function) {
            this.function = function;
        }

        public Function<E, Object> getFunction() {
            return function;
        }
    }
    
    private Map<Function<E, Object>, Multimap<Object, E>> indexes = new LinkedHashMap<>();
    
    public Table(Iterable<Function<E, Object>> indexers) {
        throw new RuntimeException();
//        indexes = ImmutableMap.copyOf(Iterables.transform(indexers, f -> entry(f, Multimaps.newListMultimap(
//                new LinkedHashMap<>(), () -> new ArrayList<>()))));
    }
    
    public <K> void add(E element) {
        indexes.entrySet().forEach(e -> {
            e.getValue().put(e.getKey().apply(element), element);
        });
    }

    public <K> Collection<E> get(Function<E, K> indexer, K key) {
        return Objects.requireNonNull(indexes.get(indexer), "No such indexer").get(key);
    }
}
