package cn.chfismine.concurrency.chapter4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

import static java.lang.Thread.sleep;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-22 11:23
 */
public class test36 {

    private static final Logger log = LoggerFactory.getLogger(test36.class);

//    static AtomicReference<String> ref = new AtomicReference<>("A");

    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A",0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start....");
        // 获取值A
        // 这个共享变量被其他线程修改过？
        String prev = ref.getReference();
        // 获取版本号
        int stamp = ref.getStamp();
        log.debug("{}",stamp);
        other();
        sleep(1000);
        //尝试改为C
        log.debug("change A->C {}",ref.compareAndSet(prev,"C",stamp,stamp+1));
    }

    private static void other() throws InterruptedException {
        new Thread(()->{
            int stamp = ref.getStamp();
            log.debug("{}",stamp);
            log.debug("change A->B {}",ref.compareAndSet(ref.getReference(),"B",stamp,stamp+1));
        },"t1").start();

        sleep(500);

        new Thread(()->{
            int stamp = ref.getStamp();
            log.debug("{}",stamp);
            log.debug("change B->A {}",ref.compareAndSet(ref.getReference(),"A",stamp,stamp+1));
        },"t2").start();
    }
}
