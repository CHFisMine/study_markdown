package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

/**
 * @program: concurrency
 * @description: 改善wait notify代码
 * @author: CHF
 * @create: 2021-01-29 15:48
 */
@Slf4j
public class test20 {

    private static final Logger log = LoggerFactory.getLogger(test20.class);

    // 保证引用不能更换，所以要加final
    static final Object lock = new Object();
    static boolean hasCigarette = false;
    static boolean hasTakeout = false;
    static ReentrantLock ROOM = new ReentrantLock();
    // 等烟休息室
    static Condition waitCigaretteSet = ROOM.newCondition();
    // 等外卖休息室
    static Condition waitTakeoutSet = ROOM.newCondition();

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("有烟没？[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        waitCigaretteSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("(小南)可以开始干活了");
            } finally {
                ROOM.unlock();
            }
        }, "小南").start();

        new Thread(() -> {
            ROOM.lock();
            try {
                log.debug("外卖到了没？[{}]", hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        waitTakeoutSet.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("（小女）可以开始干活了");
            } finally {
                ROOM.unlock();
            }
        }, "小女").start();

        sleep(10);    // 这个不是例程的sleep

        new Thread(() -> {
            ROOM.lock();
            try {
                hasTakeout = true;
                waitTakeoutSet.signal();
            } finally {
                ROOM.unlock();
            }
        }, "送外卖的").start();

        new Thread(() -> {
            ROOM.lock();
            try {
                hasCigarette = true;
                waitCigaretteSet.signal();
            } finally {
                ROOM.unlock();
            }
        }, "送烟的").start();
    }
}
