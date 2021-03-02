package cn.chfismine.concurrency.partten;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: concurrency
 * @description: 自定义线程池
 * @author: CHF
 * @create: 2021-03-01 20:33
 */
public class TestPool {
}


class ThreadPool {
    // 任务队列
    private BlockingQueue<Runnable> taskQueue;

    // 线程集合
    private HashSet<Worker> workers = new HashSet<>();

    // 核心线程数
    private int coreSize;

    // 获取任务的超时时间
    private long timeout;

    // 时间单位
    private TimeUnit timeUnit;


    public ThreadPool(int coreSize, long timeout, TimeUnit timeUnit, int queueCapacity) {
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
        this.taskQueue = new BlockingQueue<>(queueCapacity);
    }

    class Worker extends Thread{

        private Runnable task;

    }
}


class BlockingQueue<T> {
    // 1.任务队列
    private Deque<T> queue = new ArrayDeque<>();

    // 2.锁
    private ReentrantLock lock = new ReentrantLock();

    // 3.生产者条件变量
    private Condition fullWaitSet = lock.newCondition();

    // 4.消费者条件变量
    private Condition emptyWaitSet = lock.newCondition();

    // 5.容量
    private int capacity;

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    // 带超时的阻塞获取
    public T poll(long timeout, TimeUnit unit) {
        lock.lock();
        try {
            // 将timeout 统一转换为 纳秒
            long nanos = unit.toNanos(timeout);
            while (queue.isEmpty()) {
                try {
                    if(nanos <= 0) {
                        return null;
                    }
                    // 返回的是剩余时间
                    nanos = emptyWaitSet.awaitNanos(nanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 获取队列中的第一个元素就是 移除队列中的第一个元素
            T t = queue.removeFirst();
            // 队列有空位了 唤醒生产者队列
            fullWaitSet.signal();
            return t ;
        }
        finally {
            lock.unlock();
        }
    }

    // 阻塞获取
    public T take() {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 获取队列中的第一个元素就是 移除队列中的第一个元素
            T t = queue.removeFirst();
            // 队列有空位了 唤醒生产者队列
            fullWaitSet.signal();
            return t ;
        }
        finally {
            lock.unlock();
        }
    }


    // 阻塞添加
    public void put(T element) {
        lock.lock();
        try {
           while (queue.size() == capacity) {
               try {
                   fullWaitSet.await();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           // 队列末尾增加元素
           queue.addLast(element);
           // 通知消费者线程
            emptyWaitSet.signal();
        } finally {
            lock.unlock();
        }
    }

    // 获取大小
    public int size() {
        lock.lock();
        try {
           return queue.size();
        } finally {
            lock.unlock();
        }
    }
}