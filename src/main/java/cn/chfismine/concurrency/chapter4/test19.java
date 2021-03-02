package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: concurrency
 * @description: wait 和 sleep 的区别
 * @author: CHF
 * @create: 2021-01-29 15:23
 */

public class test19 {
    private static final Logger log = LoggerFactory.getLogger(test19.class);
    // 保证引用不能更换，所以要加final
    static final Object lock = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;

    public static void main(String[] args) {

        new Thread(()-> {
            synchronized (lock) {
                log.debug("获得锁");
                try {
//                    Thread.sleep(2000);
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"t1").start();

        //Sleeper.sleep(2);

        synchronized (lock) {
            log.debug("获得锁");
        }
    }
}
