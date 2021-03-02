package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

import static java.lang.Thread.sleep;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-02 12:57
 */
@Slf4j
public class test22 {

    // lambda表达式要求引用的外部变量必须是final的
    public static void main(String[] args) throws InterruptedException {
        // 定义消息队列
        MessageQueue queue = new MessageQueue(2);

        // 定义三个生产者
        for(int i = 0 ;i < 3 ;i ++) {
            int id = i;
            new Thread(()->{
                queue.put(new Message(id,"消息"+id));
            },"生产者"+i).start();
        }

        // 定义消费者
        new Thread(()->{
            queue.take();
        },"消费者").start();

           sleep(10);


    }


}

@Slf4j
// Java线程之间的通信的消息队列类
class MessageQueue {

    private static final Logger log = LoggerFactory.getLogger(MessageQueue.class);

    // 双向列表(一进一出)
    private LinkedList<Message> list = new LinkedList();

    // 队列的容量
    private int  capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    // 获取消息
    public Message take() {
        //判断队列是否为空,没有消息需要等待，等待需要锁
        synchronized (list) {
            while (list.isEmpty()) {
                log.debug("队列为空，消费者进入等待");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message message = list.removeFirst();
            list.notifyAll();
            return message;
        }
    }

    // 存入消息
    public void put(Message message) {

        // 判断队列是否已满
        synchronized (list) {
            while (list.size()==capacity) {
                log.debug("队列已满，生产者进入等待");
                try {
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 将消息加入队列尾部
            list.addLast(message);
            // 唤醒其他等待的线程
            list.notifyAll();
        }
    }

}

/**
 * final 修饰不可继承 ，保证子类不会覆盖父类
 */
final class  Message {

    private int id;

    private Object msg;

    public Message(int id, Object msg) {
        this.id = id;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public Object getMsg() {
        return msg;
    }
}