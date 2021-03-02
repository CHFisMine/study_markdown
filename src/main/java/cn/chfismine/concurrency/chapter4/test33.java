package cn.chfismine.concurrency.chapter4;

import cn.chfismine.concurrency.util.Sleeper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.sleep;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-05 09:23
 */
@Slf4j
public class test33 {

    private static final Logger log = LoggerFactory.getLogger(test33.class);

    volatile static boolean run = true;

    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread(()->{
            while (run) {
               log.debug("运行中。。。");
                System.out.println();
            }
        });

        t.start();

        Sleeper.sleep(1);

        log.debug("停止");

        run = false;
    }
}
