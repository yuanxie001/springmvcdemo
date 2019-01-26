package thread;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 用于读写锁和一般的重入锁的性能检测.运行一次即可看到效果.
 */
public class ReadWriteLockDemo {
    private static Lock lock = new ReentrantLock();
    private static ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    private static Lock readLock = reentrantReadWriteLock.readLock();
    private static Lock writeLock = reentrantReadWriteLock.writeLock();

    private int value;

    public Object handleRead(Lock lock) throws InterruptedException {
        try{
            // 模拟读操作
            lock.lock();
            Thread.sleep(100);
            // 读操作耗时越多,效果越明显
            return value;
        }finally {
            lock.unlock();
        }
    }

    public void handleWrite(Lock lock,int v) throws InterruptedException{
        try {
            lock.lock();
            // 模拟写操作
            Thread.sleep(50);
            this.value=v;
        }finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        long begain = System.currentTimeMillis();
        final ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();
        Runnable readTask = ()->{
            try {
                // 这两个可以切换的.所以要切换运行两次才能看到效果
                readWriteLockDemo.handleRead(readLock);
//                readWriteLockDemo.handleRead(lock);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        };
        Runnable writeTask = ()->{
            try {
                int value = RandomUtils.nextInt();
                readWriteLockDemo.handleWrite(writeLock,value);
//                readWriteLockDemo.handleWrite(lock,value);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        };

        for (int i=0;i<18;i++){
            new Thread(readTask).start();
        }
        for (int i=0;i<20;i++){
            new Thread(writeTask).start();
        }
        long end = System.currentTimeMillis();
        System.out.println("程序共运行的时间为:"+(end-begain)+"ms");
    }
}
