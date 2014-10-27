package sequences;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import com.google.common.util.concurrent.Uninterruptibles;

public class Async {

    private static interface Promise<T> extends Future<T> {

        public static <U> Promise<U> of(U value) {
            return of(() -> value);
        }

        public static <U> Promise<U> of(Callable<U> task) {
            return new Promise<U>() {
                private boolean gotten;
                private U result;
                private ExecutionException ee;
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }
                public boolean isCancelled() {
                    return false;
                }
                public boolean isDone() {
                    return true;
                }
                public U get() throws InterruptedException, ExecutionException {
                    if (gotten) {
                        if (ee != null) throw ee;
                        return result;
                    } else {
                        gotten = true;
                        try {
                            return result = task.call();
                        } catch (Exception e) {
                            throw ee = new ExecutionException(e);
                        }
                    }
                }
                public U get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return get();
                }
            };
        }

        public static <U> Promise<U> fromFuture(Future<U> future) {
            return new Promise<U>() {
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return future.cancel(mayInterruptIfRunning);
                }
                public boolean isCancelled() {
                    return future.isCancelled();
                }
                public boolean isDone() {
                    return future.isDone();
                }
                public U get() throws InterruptedException, ExecutionException {
                    return future.get();
                }
                public U get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return future.get(timeout, unit);
                }
            };
        }

        public static <U> Promise<U> flatten(Promise<Promise<U>> promise) {
            return of(() -> promise.get().get());
        }

        public default <U> Promise<U> map(Function<T, U> function) {
            // return flatMap(function.andThen(Promise::of));
            return of(() -> function.apply(get()));
        }

        public default <U> Promise<U> flatMap(Function<T, Promise<U>> function) {
            // return flatten(map(function));
            return of(() -> function.apply(get()).get());
        }

        public default Promise<T> force() {
            try {
                get();
            } catch (InterruptedException | ExecutionException ignore) {
            }
            return this;
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Promise<Integer> promise = Promise.of(3)
                .map(n -> {
                    System.out.println("forcing first promise");
                    return n;
                })
                .map(n -> n * 2)
                .flatMap(Async::callService)
                .map(n -> n / 2);
        System.out.println("got composed promise");
        System.out.println("value of composed promise is " + promise.get());
    }

    private static Promise<Integer> callService(int request) {
        return submit(() -> {
            Uninterruptibles.sleepUninterruptibly(1000, TimeUnit.MILLISECONDS);
            return request * request;
        });
    }

    private static <T> Promise<T> submit(Callable<T> task) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            return Promise.fromFuture(pool.submit(task));
        } finally {
            pool.shutdown();
        }
    }
}
