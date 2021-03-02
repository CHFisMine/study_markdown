package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: concurrency
 * @description: 多把锁
 * @author: CHF
 * @create: 2021-02-03 16:32
 */
public class test32 {

    public static void main(String[] args) {
        BigRoom bigRoom = new BigRoom();

        new Thread(()->{
            bigRoom.study();
        },"小南").start();

        new Thread(()->{
            bigRoom.sleep();
        },"小女").start();
    }

}



class BigRoom {

    private static final Logger log = LoggerFactory.getLogger(BigRoom.class);

    private final Object studyRoom = new Object();

    private final Object bedRoom = new Object();

    public void sleep(){
        synchronized (bedRoom){
            log.debug("sleeping 2 hour");
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void study(){
        synchronized (studyRoom){
            log.debug("studying 1 hour");
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}