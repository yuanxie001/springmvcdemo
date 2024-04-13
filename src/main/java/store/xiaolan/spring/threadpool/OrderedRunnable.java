package store.xiaolan.spring.threadpool;

public interface OrderedRunnable extends Runnable{
    Object getPartitionKey();
}
