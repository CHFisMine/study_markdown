package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.LockSupport;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-04 14:53
 */
@Slf4j
public class test28 {
    private static final Logger log = LoggerFactory.getLogger(test28.class);

    public static void main(String[] args) {
      Thread t1 = new Thread(()->{
            LockSupport.park();
            log.debug("1");
        },"t1");
      t1.start();

        new Thread(()->{
            log.debug("2");
            LockSupport.unpark(t1);
        },"t2").start();
    }



}
