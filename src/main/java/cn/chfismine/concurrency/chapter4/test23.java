package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

/**
 * @program: concurrency
 * @description: 死锁
 * @author: CHF
 * @create: 2021-02-03 17:09
 */
@Slf4j
public class test23 {

    private static final Logger log = LoggerFactory.getLogger(test23.class);

    public static void main(String[] args) {
        test1();
    }

    private static void test1() {
        Object A = new Object();
        Object B = new Object();

        Thread t1 = new Thread(()->{
            synchronized (A) {
                log.debug("lock A");
                try {
                    sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (B) {
                log.debug("lock B");
                log.debug("其他操作");
            }
        },"t1");


        Thread t2 = new Thread(()->{
            synchronized (B) {
                log.debug("lock B");
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (A) {
                log.debug("lock A");
                log.debug("其他操作");
            }
        },"t2");

        t1.start();
        t2.start();
    }
}
