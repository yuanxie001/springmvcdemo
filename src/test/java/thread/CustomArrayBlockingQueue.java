package thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 重构阻塞队列,完成线程池添加任务添加不进去就阻塞的逻辑.这样以实现更好的限流降级.
 * 同时避免因为走拒绝策略导致的一系列高可用问题.
 * @param <E>
 */
public class CustomArrayBlockingQueue<E> extends ArrayBlockingQueue<E> {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public CustomArrayBlockingQueue(int capacity) {
        super(capacity);
    }

    public CustomArrayBlockingQueue(int capacity, boolean fair) {
        super(capacity, fair);
    }

    public CustomArrayBlockingQueue(int capacity, boolean fair, Collection c) {
        super(capacity, fair, c);
    }

    /**
     * 这里是将offer委托给同类的put方法进行处理的.
     * offer如果添加不成功则返回特殊值,但不阻塞方法,
     * put添加不成功就阻塞,直到成功为止.
     * 这样线程池就永远不会走拒绝策略从而完成往线程池添加任务时的阻塞,从而达到降级的效果
     * @param o
     * @return
     */
    @Override
    public boolean offer(E o) {
        try {
            this.put(o);
        } catch (InterruptedException e) {
            logger.error("offer queue is error.",e);
            return false;
        }
        return true;
    }
}
