package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/**
 * @program: concurrency
 * @description: 可打断
 * @author: CHF
 * @create: 2021-02-04 09:27
 */
@Slf4j
public class Test24 {

    private static final Logger log = LoggerFactory.getLogger(Test24.class);

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            try {
                // 如果没有竞争那么此方法会获得lock对象锁
                // 如果有竞争就进入阻塞队列，可以被其他线程用interrupt方法打断
                log.debug("尝试获得锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁，返回");
                return;
            }

            try {
                log.debug("获取到锁");
            }finally {
                lock.unlock();
            }
        },"t1");
        lock.lock();
        t1.start();

        try {
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("打断 t1");
        t1.interrupt();
    }
}
