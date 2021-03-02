package cn.chfismine.concurrency.chapter4;

import cn.chfismine.concurrency.util.Sleeper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: concurrency
 * @description: 死锁--哲学家就餐问题
 * @author: CHF
 * @create: 2021-02-04 10:56
 */

public class test26 {

    public static void main(String[] args) {
        ChopsLick c1 = new ChopsLick("1");
        ChopsLick c2 = new ChopsLick("2");
        ChopsLick c3 = new ChopsLick("3");
        ChopsLick c4 = new ChopsLick("4");
        ChopsLick c5 = new ChopsLick("5");

        new Philosopher("苏格拉底",c1,c2).start();
        new Philosopher("柏拉图",c2,c3).start();
        new Philosopher("亚里士多德",c3,c4).start();
        new Philosopher("赫拉克利特",c4,c5).start();
        new Philosopher("阿基米德",c5,c1).start();
    }


}


/**
 * 哲学家
 */
@Slf4j
class Philosopher extends Thread {

    private static final Logger log = LoggerFactory.getLogger(Philosopher.class);

    ChopsLick left;
    ChopsLick right;

    public Philosopher(String name, ChopsLick left, ChopsLick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
//        while(true) {
//            // 尝试获得左手的筷子
//            synchronized (left) {
//                // 尝试获取右手的筷子
//                synchronized (right) {
//                    eat();
//                }
//            }
//        }

        while (true) {
            // 尝试获得左手的筷子
            if (!left.tryLock()) {
                try {
                    // 尝试获取右手的筷子
                    if (right.tryLock()) {
                        try {
                            eat();
                        } finally {
                            right.unlock();  //释放右手的筷子
                        }
                    }
                } finally {
                    left.unlock();  //释放左手的筷子
                }
            }
        }
    }

    Random random = new Random();
    private void eat() {
        log.debug("eating...");
        Sleeper.sleep(1);
    }
}


class ChopsLick extends ReentrantLock{
    String name;

    public ChopsLick(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "筷子{" +
                "name='" + name + '\'' +
                '}';
    }
}