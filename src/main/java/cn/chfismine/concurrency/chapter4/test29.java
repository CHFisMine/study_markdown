package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: concurrency
 * @description:
 * 输出内容   等待标记      下一个标记
 * a         1              2
 * b         2              3
 * c         3              1
 *
 * @author: CHF
 * @create: 2021-02-04 14:59
 */
@Slf4j
public class test29 {

    public static void main(String[] args) {
        waitNotify wn = new waitNotify(1,5);

        new Thread(()->{
            wn.print("a",1,2);
        }).start();
        new Thread(()->{
            wn.print("b",2,3);
        }).start();
        new Thread(()->{
            wn.print("c",3,1);
        }).start();
    }
}


class waitNotify {
    // 等待标记
    private int flag;

    // 循环次数
    private int loopNumber;

    // 打印
    public void print(String str, int waitFlag, int nextFlag) {
        for (int i = 0; i < loopNumber;i++){
            synchronized (this) {
                while (flag != waitFlag ) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.print(str);
                flag = nextFlag;
                this.notifyAll();
            }
        }
    }

    public waitNotify(int flag, int loopNumber) {
        this.flag = flag;
        this.loopNumber = loopNumber;
    }
}