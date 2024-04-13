package store.xiaolan.spring.threadpool;

import java.util.concurrent.Callable;

public interface OrderedCallable<T> extends Callable<T> {
    Object getPartitionKey();
}
