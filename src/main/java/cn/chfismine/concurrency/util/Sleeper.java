package cn.chfismine.concurrency.util;

import ch.qos.logback.core.util.TimeUtil;

/**
 * @program: concurrency
 * @description: 睡眠
 * @author: CHF
 * @create: 2021-02-04 11:22
 */
public class Sleeper {

    public static void sleep(int seconds) {
        try {
            Thread.currentThread().sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
