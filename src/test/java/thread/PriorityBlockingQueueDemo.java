package thread;


import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

class PriorititedTask implements Runnable, Comparable<PriorititedTask> {
    private Random random = new Random(47);
    private static int counter = 0;
    private final int id = counter++;
    private final int priority;
    protected static List<PriorititedTask> sequeuce = new ArrayList<>();

    public PriorititedTask(int priority) {

        this.priority = priority;
        sequeuce.add(this);
    }

    @Override
    public int compareTo(PriorititedTask o) {
        return priority < o.priority ? 1 : priority > o.priority ? -1 : 0;
    }

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(25));
        } catch (Exception e) {
        }
        System.out.println(this);
    }

    @Override
    public String toString() {
        return String.format("[%1$-3d]", priority) + " Task " + id;
    }

    public String summary() {
        return "(" + id + ": " + priority + ")";
    }

    protected static class EndSentinel extends PriorititedTask {
        private ExecutorService executorService;

        public EndSentinel(ExecutorService executorService) {
            super(-1);
            this.executorService = executorService;
        }

        @Override
        public void run() {
            int count = 0;
            for (PriorititedTask pt :
                    sequeuce) {
                System.out.println(pt.summary());
                if (++count==5){
                    System.out.println();
                }

                System.out.println();
                System.out.println(this+" Calling ShutdownNow");
                executorService.shutdownNow();
            }
        }
    }
}

class PrioritizedTaskProducer implements Runnable{

    private Random random = new Random(47);
    private Queue<Runnable> queue;
    private ExecutorService executorService;

    public PrioritizedTaskProducer(Queue<Runnable> queue, ExecutorService executorService) {
        this.queue = queue;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            queue.add(new PriorititedTask(random.nextInt(10)));
            Thread.yield();
        }
        try {
            for (int i = 0; i <10 ; i++) {
                TimeUnit.MILLISECONDS.sleep(250);
                queue.add(new PriorititedTask(10));

            }
            queue.add(new PriorititedTask.EndSentinel(executorService));
        }catch (Exception e){

        }
        System.out.println("Finished PrioritizedTaskProduce");
    }
}


class PrioritizedTaskConsumer implements Runnable{

    private PriorityBlockingQueue<Runnable> queue;

    public PrioritizedTaskConsumer(PriorityBlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()){
                queue.take().run();
            }
        }catch (Exception e){

        }
        System.out.println("Finished PrioritizedTaskConsumer");
    }
}

public class PriorityBlockingQueueDemo {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        PriorityBlockingQueue<Runnable> queue= new PriorityBlockingQueue<>();
        executorService.execute(new PrioritizedTaskProducer(queue,executorService));
        executorService.execute(new PrioritizedTaskConsumer(queue));
    }

}
