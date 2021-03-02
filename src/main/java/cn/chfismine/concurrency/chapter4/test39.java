package cn.chfismine.concurrency.chapter4;

import javax.naming.PartialResultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-22 16:02
 */
public class test39 {

    public static void main(String[] args) {
        demo(
                () -> new int[10],
                (array) -> array.length,
                (array,index) -> array[index]++,
                array -> System.out.println(Arrays.toString(array))
        );
    }

    /**
     * supplier 提供者 无中生有 （）->结果
     * Function 函数  一个参数一个结果
     * BiFunction(参数1，参数2)->结果
     * consumer 消费者 一个参数没结果 （参数）-> void
     * BiConsumer(参数1，参数2) -> void
     * @param arraySupplier 提供数组，可以是线程不安全数组或线程安全数组
     * @param lengthFun 获取数组长度的方法
     * @param putConsumer 自增方法，回传 array index
     * @param printConsumer 打印数组的方法
     * @param <T>
     */
    private static <T> void demo(
            Supplier<T> arraySupplier,
            Function<T,Integer> lengthFun,
            BiConsumer<T,Integer> putConsumer,
            Consumer<T> printConsumer
    ) {
        List<Thread> ts = new ArrayList<>();
        T array = arraySupplier.get();
        int length = lengthFun.apply(array);
        for (int i = 0; i<length; i++) {
            ts.add(new Thread(()->{
                for (int j = 0; j < 10000; j++) {
                    putConsumer.accept(array, j%length);
                }
            }));
        }
        ts.forEach(thread -> thread.start()); // 启动所有线程
        ts.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } ); //等待所有线程结束

        System.out.println();
    }
}
