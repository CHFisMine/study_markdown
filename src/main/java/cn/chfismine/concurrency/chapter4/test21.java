package cn.chfismine.concurrency.chapter4;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @program: concurrency
 * @description: 保护性暂停
 * @author: CHF
 * @create: 2021-01-31 22:10
 */
public class test21 {

    public static void main(String[] args) {

    }

}



class MailBoxes {

    // hashMap 线程安全
    private static Map<Integer,GuardedObject> boxes = new HashMap<>();

    private static int id = 1;

    // 产生唯一ID
    private static synchronized int generateId() {
        return id++;
    }

    public static GuardedObject createGuardedObject(int id){
        GuardedObject go = new GuardedObject(id);
        boxes.put(go.getId(),go);
        return go;
    }

    public static Set<Integer> getId() {
        return boxes.keySet();
    }
}

class GuardedObject {
    //结果
    private Object response;

    private int id;

    public Integer getId() {
        return id;
    }

    public GuardedObject(int id) {
        this.id = id;
    }

    /**
     * 获取结果
     * @param timeout 超时时间
     * @return
     */
    public Object get(long timeout) {
        synchronized (this) {
            // 没有结果
            // 记录开始时间
            long begin = System.currentTimeMillis();
            // 经历的时间
            long passedTime = 0;
            while(response == null) {
                // 这一轮应该的等待时间
                long waitTime = timeout- passedTime;
                if (waitTime <= 0) {
                    break;
                }
                try {
                    this.wait(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 求取经历时间
            passedTime = System.currentTimeMillis() - begin;
        }
        return response;
    }

    // 产生结果
    public void complete(Object response) {
        synchronized (this) {
            // 给结果成员变量赋值
            this.response = response;
            this.notifyAll();
        }
    }
}
