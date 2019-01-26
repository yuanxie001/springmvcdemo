package thread.pool;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.LongStream;

/**
 * 分支合并框架的例子
 */
public class ForkJoinSumCalculator extends RecursiveTask<Long> {
    private long[] numbers;
    private final int start;
    private final int end;

    public static final long THRESHOLD = 10_000;
    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        int length = end - start;
        // 任务数量小于10000的时候不拆分.
        if (length <= THRESHOLD){
            return computeSequentially();
        }
        // 大于100的时候,拆分成两个任务.
        ForkJoinSumCalculator leftTask =
                new ForkJoinSumCalculator(numbers, start, start + length/2);
        // 提交子任务执行
        leftTask.fork();
        ForkJoinSumCalculator rightTask =
                new ForkJoinSumCalculator(numbers, start + length/2, end);
        // 在当前线程执行另一个子任务.
        Long rightResult = rightTask.compute();
        // 获取切分的子任务的结果.这里会存在阻塞的.需要等待子任务完成.
        // 底层用了一个无锁并发结构
        Long leftResult = leftTask.join();
        // 合并子任务的结果并返回
        return leftResult + rightResult;
    }

    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
                sum += numbers[i];
        }
        return sum;
    }

    public static long forkJoinSum(long n) {
        long[] numbers = LongStream.rangeClosed(1, n).toArray();
        ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
        // 直接new了一个ForkJoinPool来进行执行任务的
        return new ForkJoinPool().invoke(task);
    }

    public static void main(String[] args) {
        long l = forkJoinSum(10000000);
        System.out.println(l);
    }
}
