package cn.chfismine.concurrency.chapter4;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-01-29 13:54
 */
public class NotifyTest {

    private static final Logger log = LoggerFactory.getLogger(NotifyTest.class);

    // 作为锁对象
    final static Object obj = new Object();

    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
           synchronized (obj) {
               log.debug("执行.......");
               try {
                   obj.wait(); // 让线程在obj上一直等下去
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               log.debug("其他代码........");
           }
        },"t1").start();

        new Thread(()->{
            synchronized (obj) {
                log.debug("执行.......");
                try {
                    obj.wait(); // 让线程在obj上一直等下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其他代码........");
            }
        },"t2").start();

        //主程序sleep 2秒(没有实现)
        Thread.sleep(2);

        log.debug("唤醒obj上其他线程");
        synchronized (obj) {
//            obj.notify();  //唤醒obj上一个线程
             obj.notifyAll();   //唤醒obj上的所有线程
        }
    }
}
