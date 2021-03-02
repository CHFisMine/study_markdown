package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-04 14:11
 */
@Slf4j
public class test27 {

    private static final Logger log = LoggerFactory.getLogger(test27.class);

    static final Object lock = new Object();
    // 表示 t2 是否运行过
    static boolean t2runned = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            synchronized (lock) {
                while(!t2runned) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.debug("1");
        },"t1");

        Thread t2 = new Thread(()->{
            synchronized (lock) {
                log.debug("2");
                t2runned = true;
                lock.notify();
            }

        },"t2");

        t1.start();
        t2.start();
    }
}
