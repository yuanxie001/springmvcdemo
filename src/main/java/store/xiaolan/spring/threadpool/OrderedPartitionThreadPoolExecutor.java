package store.xiaolan.spring.threadpool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class OrderedPartitionThreadPoolExecutor extends AbstractExecutorService {
    private static final Logger logger = LoggerFactory.getLogger(OrderedPartitionThreadPoolExecutor.class);

    public final static String PREFIX = "ordered-partition-";
    private final static int SINGLE_EXECUTOR_QUEUE_SIZE = 10_000;
    private final static int DEFAULT_KEEPALIVE_TIME_SECONDS = 5 * 60;

    private final int executorGroupSize;

    private final ExecutorService[] executorGroup;

    private final String executorGroupName;

    private volatile int runState = RUNNING;
    // runState
    private static final int RUNNING = -1;
    private static final int SHUTDOWN = 0;

    public OrderedPartitionThreadPoolExecutor(String poolName, int executorGroupSize) {
        this(poolName, executorGroupSize, SINGLE_EXECUTOR_QUEUE_SIZE);
    }

    public OrderedPartitionThreadPoolExecutor(String poolName, int executorGroupSize, int singleExecutorQueueSize) {
        this(poolName, executorGroupSize, singleExecutorQueueSize, DEFAULT_KEEPALIVE_TIME_SECONDS);
    }

    public OrderedPartitionThreadPoolExecutor(String poolName, int executorGroupSize, int singleExecutorQueueSize, long keepAliveTimeSeconds) {
        this.executorGroupSize = executorGroupSize;
        if (keepAliveTimeSeconds < 0) {
            keepAliveTimeSeconds = DEFAULT_KEEPALIVE_TIME_SECONDS;
        }
        executorGroup = new ExecutorService[executorGroupSize];
        this.executorGroupName = PREFIX + poolName;
        for (int i = 0; i < executorGroupSize; i++) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, keepAliveTimeSeconds, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(singleExecutorQueueSize));
            //executor.setExecutorGroupName(executorGroupName);
            executorGroup[i] = executor;
        }
    }

    public OrderedPartitionThreadPoolExecutor(String poolName, int executorGroupSize, int singleExecutorQueueSize,
                                              long keepAliveTimeSeconds, boolean allowCoreThreadTimeout, ThreadFactory threadFactory,
                                              RejectedExecutionHandler handler) {

        this.executorGroupSize = executorGroupSize;
        if (keepAliveTimeSeconds < 0) {
            keepAliveTimeSeconds = DEFAULT_KEEPALIVE_TIME_SECONDS;
        }
        executorGroup = new ExecutorService[executorGroupSize];
        this.executorGroupName = PREFIX + poolName;
        for (int i = 0; i < executorGroupSize; i++) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, keepAliveTimeSeconds, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(singleExecutorQueueSize), threadFactory, handler);
            executor.allowCoreThreadTimeOut(allowCoreThreadTimeout);
            executorGroup[i] = executor;
        }
    }

    @Override
    public void execute(Runnable command) {
        if (!(command instanceof OrderedRunnable)) {
            throw new IllegalArgumentException("task must instanceof OrderedRunnable!");
        }

        checkRunState();

        OrderedRunnable task = (OrderedRunnable) command;
        Object partitionKey = task.getPartitionKey();
        selectExecutor(partitionKey).execute(task);
    }

    @Override
    public Future<?> submit(Runnable command) {
        if (!(command instanceof OrderedRunnable)) {
            throw new IllegalArgumentException("task must instanceof OrderedRunnable!");
        }
        checkRunState();

        OrderedRunnable task = (OrderedRunnable) command;
        Object partitionKey = task.getPartitionKey();
        return selectExecutor(partitionKey).submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable command, T result) {
        if (!(command instanceof OrderedRunnable)) {
            throw new IllegalArgumentException("task must instanceof OrderedRunnable!");
        }
        checkRunState();

        OrderedRunnable task = (OrderedRunnable) command;
        Object partitionKey = task.getPartitionKey();
        return selectExecutor(partitionKey).submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> command) {
        if (!(command instanceof OrderedCallable)) {
            throw new IllegalArgumentException("task must instanceof ZoomOrderedCallable!");
        }
        checkRunState();

        OrderedCallable<T> task = (OrderedCallable<T>) command;
        Object partitionKey = task.getPartitionKey();
        return selectExecutor(partitionKey).submit(task);
    }

    @Override
    public synchronized void shutdown() {
        checkRunState();
        for (ExecutorService executorService : executorGroup) {
            executorService.shutdown();
        }
        runState = SHUTDOWN;
    }

    @Override
    public synchronized List<Runnable> shutdownNow() {
        checkRunState();
        List<Runnable> resultList = new ArrayList<>();
        for (ExecutorService executorService : executorGroup) {
            List<Runnable> tasks = executorService.shutdownNow();
            resultList.addAll(tasks);
        }
        runState = SHUTDOWN;
        return resultList;
    }

    @Override
    public boolean isShutdown() {
        if (runState == RUNNING) {
            return false;
        }
        return Arrays.stream(executorGroup).allMatch(ExecutorService::isShutdown);
    }

    @Override
    public boolean isTerminated() {
        if (runState == RUNNING) {
            return false;
        }
        return Arrays.stream(executorGroup).allMatch(ExecutorService::isTerminated);
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        if (runState != SHUTDOWN) {
            throw new IllegalStateException("must pre invoke shutdown method");
        }
        long timeoutMillis = System.currentTimeMillis() + unit.toMillis(timeout);
        while (true) {
            if (isTerminated()) {
                return true;
            }
            if (System.currentTimeMillis() - timeoutMillis > 0) {
                if (isTerminated()) {
                    return true;
                }
                logger.info("timeout for await [{}] termination!  executors: {}", executorGroupName, Arrays.toString(executorGroup));
                return false;
            }
            TimeUnit.MILLISECONDS.sleep(100);

        }
    }


    protected int index(Object partitionKey) {
        int hash = hash(partitionKey);
        if (Integer.MIN_VALUE == hash) {
            return 0;
        }
        int index = Math.abs(hash) % executorGroupSize;
        if (logger.isDebugEnabled()) {
            logger.debug("select executor,partition-key: {},selected-index: {}", partitionKey, index);
        }
        return Math.abs(index);
    }

    public ExecutorService[] getExecutorGroup() {
        return executorGroup;
    }

    private ExecutorService selectExecutor(Object partitionKey) {
        int index = index(partitionKey);
        return executorGroup[index];
    }

    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    private void checkRunState() {
        if (runState >= 0) {
            throw new IllegalStateException("[" + executorGroupName + "] is shutdown");
        }
    }
}

