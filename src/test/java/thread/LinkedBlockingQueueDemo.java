package thread;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 运行一个生产者消费者模型. 关闭生产者,消费者持续等待.
 */
public class LinkedBlockingQueueDemo {
    public static void main(String[] args) {
        BlockingQueue<Long> queue = new LinkedBlockingDeque<>(10);
        // 生产者
        Producer producer1 = new Producer(queue);
        Producer producer2 = new Producer(queue);
        Producer producer3 = new Producer(queue);
        // 消费者
        Consumer consumer1 = new Consumer(queue);
        Consumer consumer2 = new Consumer(queue);
        Consumer consumer3 = new Consumer(queue);
        // 建立线程池
        ExecutorService service = Executors.newFixedThreadPool(6);

        // 运行生产者
        service.execute(producer1);
        service.execute(producer2);
        service.execute(producer3);
        // 运行消费者
        service.execute(consumer1);
        service.execute(consumer2);
        service.execute(consumer3);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 停止生产者
        producer1.stop();
        producer2.stop();
        producer3.stop();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdown();
    }

    public static class Producer implements Runnable{
        private volatile boolean running = true;
        private BlockingQueue<Long> queue;

        private static AtomicInteger count = new AtomicInteger(0);

        public Producer(BlockingQueue<Long> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            System.out.println("start producer id="+Thread.currentThread().getName());
            try {
                // 用时间戳,是用来获取这个任务从生产到结束一共花费多长时间.
                while (running){
                    Thread.sleep(RandomUtils.nextInt(0,1000));
                    Long timestamp = System.currentTimeMillis();
                    System.out.println("data offer queue:"+timestamp);
                    if (!queue.offer(timestamp)){
                        System.err.println("fail data offer queue");
                    }
                }

            }catch (InterruptedException e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        public void stop(){
            this.running = false;
        }
    }

    public static class Consumer implements Runnable{
        private BlockingQueue<Long> queue;
        private static final int SLEEPTIME = 10000;

        public Consumer(BlockingQueue<Long> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true){
                    Long timestamp = queue.take();
                    if (timestamp!=null){
                        long end = System.currentTimeMillis();
                        System.out.println("consumer time:"+(end-timestamp));
                    }
                    Thread.sleep(RandomUtils.nextInt(0,1000));
                }
            }catch (InterruptedException e){
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
