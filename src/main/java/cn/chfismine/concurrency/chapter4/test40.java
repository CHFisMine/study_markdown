package cn.chfismine.concurrency.chapter4;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-25 12:38
 */
public class test40 {

    public static void main(String[] args) {
        AtomicInteger i = new AtomicInteger(0);
        System.out.println(i.getAndIncrement());

        // 自增并获取（i = 1，结果 i = 2，返回 2）类似于++i
        System.out.println(i.incrementAndGet());

        // 自减并获取（i = 2，结果 i = 1，返回 1），类似于 --i
        System.out.println(i.decrementAndGet());

        // 获取并自减（i = 1,结果 i = 0，返回 1）,类似于 i--
        System.out.println(i.getAndDecrement());

        // 获取并加值
        System.out.println(i.getAndAdd(5));

        // 加值并获取
        System.out.println(i.addAndGet(5));
    }
}
