package thread.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 测试线程池 用来捕获多线程里面发生的异常以及产生异常的任务是哪个线程在哪提交的.
 *
 * CompletableFuture也能实现类似的将多线程中出现的异常抓取出来.然后抛出来.但获取不到异常任务提交的点.所以还是没这个好用.
 * 这个记录的信息更加详细.出现异常更能捕获解决.
 */
public class TraceThreadPoolExecutor extends ThreadPoolExecutor{
    private static final Logger logger = LoggerFactory.getLogger(TraceThreadPoolExecutor.class);

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue){
        this(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,Executors.defaultThreadFactory(),new CallerRunsPolicy());
    }

    public TraceThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    private Exception clientTrace(){
        return new Exception("client stack trace");
    }

    public Runnable warp(final Runnable task,final Exception clientStack,String clientThreadName){
        return ()->{
            try {
                task.run();
            }catch (Exception e){
                logger.error("client stack:",clientStack);
                logger.error("task stack:",e);
                throw e;
            }
        };
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(warp(task,clientTrace(),Thread.currentThread().getName()));
    }

    @Override
    public void execute(Runnable command) {
        super.execute(warp(command,clientTrace(),Thread.currentThread().getName()));
    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = new TraceThreadPoolExecutor(0,Integer.MAX_VALUE,0L,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
        for (int i=0;i<5;i++){
            threadPoolExecutor.execute(new DivTask(100,i));
        }
    }

    private static class DivTask implements Runnable{

        private int a;
        private int b;

        public DivTask(int a,int b){
            this.a=a;
            this.b=b;
        }

        @Override
        public void run() {
            logger.info("task information:{}",this);
            double result = a/b;
            logger.info("task result:{}",result);
        }

        @Override
        public String toString() {
            return "DivTask:a="+a+",b="+b;
        }
    }
}
