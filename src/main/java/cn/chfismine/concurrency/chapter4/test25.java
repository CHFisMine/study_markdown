package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: concurrency
 * @description: 锁超时
 * @author: CHF
 * @create: 2021-02-04 10:01
 */
@Slf4j
public class test25 {

    private static final Logger log = LoggerFactory.getLogger(test25.class);

    private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
       Thread  t1 = new Thread(()->{
            log.debug("尝试获得锁");
           try {
               if (!lock.tryLock(1, TimeUnit.SECONDS)) {
                   log.debug("获取不到锁");
                   return;
               }
           } catch (InterruptedException e) {
               e.printStackTrace();
               return;
           }
           try {
                log.debug("获得到锁");
            }finally {
                lock.unlock();
            }
        },"t1");

        lock.lock();
        log.debug("获得到锁");
        t1.start();
    }
}
