package cn.chfismine.concurrency.chapter4;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-04 16:46
 */
public class test30 {

    public static void main(String[] args) {

    }
}

class awaitSignal extends ReentrantLock {
    // 循环次数
    private int loopNumber;

    public void print(){
        lock();

    }
}