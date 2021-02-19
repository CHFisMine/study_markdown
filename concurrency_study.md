# 并发编程

## 1. 概览

## 2. 进程与线程

**本章内容**

### 2.1 进程与线程

### 2.2 并行与并发

### 2.3 应用

* 应用之异步调用（案例1）
* 应用之提高效率（案例2）

## 3. Java线程

**本章内容**

### 3.1 创建和运行线程

**方法一，直接实同Thread**

**方法二，使用Runnable配合Thread**

* 原理之Thread与Runnable的关系  

**方法三，FutureTask 配合 Thread**

### 3.2 观察多个线程同行运行

### 3.3 查看进程线程的方法

* windows
* Linux
* Java

### 3.4 常见方法



## 3.5 start方法详解

调用run

调用start

小结

### 3.6 原理之线程运行基础

栈与栈帧

程序计数器









## 4. 共享模型之管程

**本章内容**

### 4.1 共享带来的问题

小故事

Java的提现

问题分析

临界区 Critical Section

竞态条件 Race Condition

### 4.2 synchronized解决方案

两种手段

synchronized 

如何理解

思考

面向对象改进



### 4.3 方法上的synchronized

#### 不加synchronized的方法

#### 所谓的“线程八锁”

### 4.4 变量的线程安全分析

#### 成员变量和静态变量是否线程安全

##### 局部变量是否线程安全

#### 常见的线程安全类

##### 线程安全类方法的组合

##### 不可变类线程安全性

#### 实例分析

### 4.5 习题

卖票练习

转账练习

###  4.6 Monitor概念

Java对象头

Monitor(锁)

Monitor 被翻译为**监视器**或**管程**

每个Java对象都可以关联一个Monitor对象，如果使用synchronized给对象上锁（重量级）之后，该对象头的Mark Word中就被设置指向Monitor对象的指针

Monitor 结构如下

* ###### 原理之synchronized 

* ###### 原理之synchronized进阶

小故事

#### 原理之synchronized 

#### 原理之synchronized进阶

##### 1.轻量级锁



轻量级锁的使用场景：如果一个对象虽然有多个线程访问，但是多线程访问的时间是错开的（也就是没有竞争），那么可以使用轻量级锁来优化。

轻量级锁对使用者是透明的，即语法任然是synchronized 

假设有两个方法块，利用同一个对象加锁

```java
static final Object obj = new Object();
```

##### 2.锁膨胀





如果在尝试加轻量级锁的过程中，CAS操作无法成功，这时一种情况就是有其他线程为此对象加上了轻量级锁(有竞争)，这时需要进行锁膨胀，将轻量级锁变为重量级锁。



* 这时Thread-1加轻量级锁失败，进入锁膨胀状态
  * 即为Object对象申请Monitor锁，让Object指向重量级锁地址
  * 然后自己进入Monitor的EntryList BLOCKED



* 当 Thread-0退出同步块解锁时，使用CAS将MARK　WORD　的值恢复给对象头，失败。这时会进入重量级解锁流程，即按Monitor地址找到Monitor对象，设置Owner为null,唤醒EntryList中BLOCKED线程。

##### 3.自旋优化

重量级锁竞争的时候，还可以使用自旋来进行优化，如果当前线程自旋成功(即这时候持锁线程已经退出了同步块，释放了锁)，这时当前线程就可以避免阻塞。

自旋重试成功的情况

自旋重试失败的情况

* 在Java 6之后自旋锁是自适应的，比如对象刚刚的一次自旋操作成功过，那么认为这次自旋成功的可能性会高，就多自旋几次；反之，就自旋甚至不自旋，总之，比较智能。
* 自旋会占用CPU时间，单核CPU自旋就是浪费，多核CPU自旋才能发挥优势。
* Java 7之后不能控制是否开启自旋功能。

##### 4.偏向锁

轻量级锁在没有竞争时(就自己这个线程)，每次重入仍需要执行CAS操作。

JAVA 6 中引入了偏向锁来做进一步优化；只有第一次使用CAS将线程ID设置到对象的Mark Word头，之后发现这个线程ID是自己的就表示没有竞争，不用重新CAS。以后只要不发生竞争，这个对象就归线程所有

例如：　

```java
static　final Object obj = new Object(); 
public  static void  m1() {
	synchronized (obj){
	//同步块
	m2();
	}
}
public static void m2() {
    synchronized (obj) {
        // 同步块
        
    }
}
```



##### 偏向状态

对象头格式

|                      Mark Word (64 bit)                      |       State        |
| :----------------------------------------------------------: | :----------------: |
| unused : 25 \|  hashcode : 31 \|  unused : 1 \|  age : 4 \|  biased_lock : 0                                \| 01 |       Normal       |
| thread : 54   \|  epoch : 2  \| unused : 1  \|  age : 4  \| biased_lock : 1                                             \| 01 |       Biased       |
| ptr_to_lock_record : 62                                                                                               \| 00 | Lightweight Locked |
| ptr_to_heavyweight_monitor : 62                                                                                \| 10 | Heavyweight Locked |
|                            \| 11                             |   Marked for GC    |

一个对象创建时：

* 如果开启了偏向锁（默认开启），那么对象创建后，markword 值为 0x05 即最后3位为 101，这时它的 thread 、epoch、age 都为0
* 偏向锁默认是延迟的，不会在程序启动时立即生效，如果想避免延迟，可以加 VM 参数  XX：BiasedLockingStratupDelay = 0 来禁用延迟
* 如果没有开启偏向锁，那么对象创建后，markword的值为 0x01 即最后3位为 001 ，这时 它的 hashcode、age 都为 0,第一次使用到hashCode时才会赋值

1) 测试延时：

3）测试禁用：

在上面测试代码运行时在添加VM参数 -XX：-UseBiasedLocking 禁用偏向锁

4）测试hashCode

##### 撤销---调用对象 hashCode

调用了对象的hashCode ，但偏向锁的对象 MarkWord 中存储的是线程ID，如果调用 hashCode 会导致偏向锁 被撤销

* 轻量级锁会在锁记录中记录hashCode
* 重量级锁会在Monitor中记录hashCode

在调用hashCode后使用偏向锁，记得去掉 -XX：-UseBiasedLocking 

输出



##### 撤销---其他线程使用对象

当有其他线程使用偏向锁对象时，会将偏向锁升级为轻量锁

##### **撤销---调用wait/notify**

重量级锁才有

##### 批量重偏向

如果对象虽然被多个线程访问，但是没有竞争，这时偏向了线程T1的对象仍有机会重新偏向T2，重偏向会重置对象的Thread ID

当撤销偏向锁阈值超过20次之后，JVM会这样觉得，我是不是偏向错了，于是会在给这些对象加锁时重新偏向至加锁线程



##### 批量撤销

当撤销偏向锁阈值超过40次之后，JVM会这样觉得，自己确实偏向错了，根本不该偏向。于是整个类的所有对象都会变为不可偏向的，新建的对象也是不可偏向的

##### 5.锁消除



##### 6.锁粗化

通常情况下，为了保证多线程间的有效并发，会要求每个线程持有锁的时间尽可能短，但是在某些情况下，一个程序对同一个锁不间断、高频地请求、同步与释放，会消耗掉一定的系统资源，因为锁的讲求、同步与释放本身会带来性能损耗，这样高频的锁请求就反而不利于系统性能的优化了，虽然单次同步操作的时间可能很短。**锁粗化就是告诉我们任何事情都有个度，有些情况下我们反而希望把很多次锁的请求合并成一个请求，以降低短时间内大量锁请求、同步、释放带来的性能损耗。**



### 4.7 wait notify

#### 小故事-为什么需要wait

* 由于条件不足，小南不能继续进行计算
* 但小南如果一直占用着锁，其他人就得一直阻塞，效率太低
* 于是老王单开了一间休息室（调用wait方法），让小南到休息室（WaitSet)等着去了，但这时锁释放开，其他人可以由老王随机安排进屋
* 直到小M把烟送过来，大叫一声【你的烟到了！】（调用notify方法）
* 小南是可以离开休息室，重新进入竞争锁的队列

#### * 原理之 wait / notify ![](D:\文档\并发编程素材\原理之 wait _ notify.png)

* Owner线程发现条件不足，调用wait方法，即可进入WaitSet变为WAITING状态
* BLOCKED 和 WAITING的线程都处于阻塞状态，不占用CPU时间片
* BLOCKED线程会在Owner线程释放锁时唤醒
* WAITING线程会在Owner线程调用Notify或者notifyAll时唤醒，但唤醒后并不意味着立刻获得锁，仍需进入entryList重新竞争

#### API介绍

* obj.wait()让进入object 监视器的（获取锁）线程到waitSet等待
* obj.notify() 在object 上正在waitSet等待的线程中挑一个唤醒
* obj.notifyAll() 让object上正在等待的线程全部唤醒

他们都是线程之间进行协作的手段，都属于Object对象的方法，**必须获得此对象的锁，才能调用这几个方法**



### 4.8 wait notify 的正确姿势

开始之前

#### sleep(long n) 和 wait (long n)的区别

1）sleep 是 Thread 方法，而 wait 是Object 的方法

2）sleep 不需要强制和synchronized 配合使用，但 wait 需要和synchronized 一起用

3)  sleep 在睡眠的同时，不会释放对象锁，但wait 在等待的时候会释放对象锁。

4) 他们的状态 TIMED-WAITING

#### STEP 1

```java
static final Object room = new Object();
static boolean hasCigarette = false;
static boolean hasTakeout = false;
```

思考下面的解决方案好不好，为什么？

```java
new Thread(()->{
            synchronized (lock) {
                log.debug("有烟没？[{}]",hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        sleep(2);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]",hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        },"小南").start();

        for (int i =0;i<5;i++) {
            new Thread(()->{
                synchronized (lock) {
                    log.debug("可以开始干活了");
                }
            },"其他人").start();
        }

        sleep(7);    // 这个不是例程的sleep,主线程没睡

        new Thread(()->{
            // 这里能不能加 synchronized (lock) ?
//            synchronized (lock) {
                hasCigarette = true;
                log.debug("烟到了奥！");
//            }
        },"送烟的").start();
    }
```

运行结果

![](D:\文档\并发编程素材\image-20210129160845032.png)

* 其他干活的线程，都一直阻塞，效率太低
* 小南线程必须睡足2s后才能醒来，就算烟提前送到，也无法提前醒来
* 加了synchronized（lock）后，就好比小南在里面反锁了门睡觉，烟根本没法送进门，main没加synchronized就好像main线程是翻窗户进来的
* 解决方法，使用wait - notify 机制

#### STEP 2

```java
new Thread(()->{
            synchronized (lock) {
                log.debug("有烟没？[{}]",hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]",hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }
            }
        },"小南").start();

        for (int i =0;i<5;i++) {
            new Thread(()->{
                synchronized (lock) {
                    log.debug("可以开始干活了");
                }
            },"其他人").start();
        }

        sleep(7);    // 这个不是例程的sleep,主线程没睡

        new Thread(()->{
            // 这里能不能加 synchronized (lock) ?
            synchronized (lock) {
                hasCigarette = true;
                log.debug("烟到了奥！");
                lock.notify();
            }
        },"送烟的").start();
    }
```

运行结果 ： 

![](D:\文档\并发编程素材\微信图片_waitnotify.png)

#### STEP 3

```java
 new Thread(()->{
            synchronized (lock) {
                log.debug("有烟没？[{}]",hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]",hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }else {
                    log.debug("没干成活");
                }
            }
        },"小南").start();

        new Thread(()->{
            synchronized (lock) {
                log.debug("外卖到了没？[{}]",hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到了没？[{}]",hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                }else {
                    log.debug("没干成");
                }
            }
        },"小女").start();

        sleep(10);    // 这个不是例程的sleep

        new Thread(()->{
            // 这里能不能加 synchronized (lock) ?
            synchronized (lock) {
                hasTakeout = true;
                log.debug("外卖到了奥！");
                lock.notify();
            }
        },"送外卖的").start();
```

**运行结果**  ： 虚假唤醒，唤醒了错误的对象。 notify()随机唤醒一个线程

![](D:\文档\并发编程素材\微信图片_虚假唤醒.png)

#### STEP 4

```java
 new Thread(()->{
            synchronized (lock) {
                log.debug("有烟没？[{}]",hasCigarette);
                if (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]",hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }else {
                    log.debug("没干成活");
                }
            }
        },"小南").start();

        new Thread(()->{
            synchronized (lock) {
                log.debug("外卖到了没？[{}]",hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到了没？[{}]",hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                }else {
                    log.debug("没干成");
                }
            }
        },"小女").start();

        sleep(10);    // 这个不是例程的sleep

        new Thread(()->{
            // 这里能不能加 synchronized (lock) ?
            synchronized (lock) {
                hasTakeout = true;
                log.debug("外卖到了奥！");
                lock.notifyAll();
            }
        },"送外卖的").start();
```



**运行结果** ：全部唤醒，但是小南还是没有干成活

![](D:\文档\并发编程素材\微信图片_全部唤醒.png)

#### STEP 5

```java
 new Thread(()->{
            synchronized (lock) {
                log.debug("有烟没？[{}]",hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会！");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("有烟没？[{}]",hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                }else {
                    log.debug("没干成活");
                }
            }
        },"小南").start();

        new Thread(()->{
            synchronized (lock) {
                log.debug("外卖到了没？[{}]",hasTakeout);
                if (!hasTakeout) {
                    log.debug("没外卖，先歇会！");
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("外卖送到了没？[{}]",hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                }else {
                    log.debug("没干成");
                }
            }
        },"小女").start();

        sleep(10);    // 这个不是例程的sleep

        new Thread(()->{
            // 这里能不能加 synchronized (lock) ?
            synchronized (lock) {
                hasTakeout = true;
                log.debug("外卖到了奥！");
                lock.notifyAll();
            }
        },"送外卖的").start();
```

**运行结果**

![](D:\文档\并发编程素材\微信图片_while替换if.png)



```java
synchronized(lock) {
    while(条件不成立) {
        lock.wait();
    }
    // 干活
}
// 另一个线程
synchronized(lock) {
    lock.notifyAll();
}

```



#### *模式之保护性暂停

即 Guarded Suspension,用在一个线程等待另一个线程的执行结果

要点

* 有一个结果需要从一个线程传递到另一个线程，让他们关联同一个GuardedObject
* 如果有结果从不断一个线程到另一个线程那么可以使用消息队列（见生产者/消费者）
* JDK中，join的实现，Future的实现，采用的就是此模式
* 因为要等待另一方的结果，因此归类到同步模式

扩展1

* 原理之Join

扩展2

图中 Futures就好比居民楼一层的信箱（每个信箱有房间编号），左侧的t0,t2,t4 就好比等待邮件的居民，右侧的t1，t3, t5就好比邮递员

如果需要在多个类之间使用GuardedObject对象，作为参数传递很不是方便，因此设计一个用来解耦的中间类，这样不仅能够解耦[结果等待者]和[结果生产者] ，还能哦支持多个任务的管理

![](D:\文档\并发编程素材\Futures.png)

#### *模式之生产者/消费者定义

要点

* 与前面的保护性暂停中的GuardObject不同，不需要产生结果和消费结果的线程一一对应
* 消费队列可以用来平衡生产和消费的线程资源
* 生产者仅负责产生结果数据，不关心数据该如何处理，而消费者专心处理结果数据
* 消息队列是有容量限制的，满时不会再加入数据，空时不会再消耗数据
* JDK中各种阻塞队列，采用的就是这种模式

![](D:\文档\并发编程素材\消息队列.png) 



### 4.9 Park & Unpark 

#### **基本使用**

它们是 LockSupport 类中的方法

```java
// 暂停当前线程
LockSupport.park();

// 回复某个线程的运行
LockSupport.unpark(暂停线程对象);
```

先 park 再 Unpark

```java
Thread t1 = new Thread (()->{
    log.debug("start ....");
    sleep(1);
    log.debug("park...");
    LockSupport.park();
    log.debug("resume...");
},"t1")
    t1.start();

sleep(2);
log.debug("unpark...");
LockSupport.unpark(t1);
```

#### **特点**

与Object的wait 和 notify 相比

* wait , notify 和 notifyAll  必须配合 Object Monitor 一起使用，而park unpark 不必
* park & unpark 是以线程为单位来【阻塞】和【唤醒】线程，而notify 只能随机唤醒一个等待的线程notifyAll 是唤醒所有等待的线程，就不那么【精确】
* park & unpark 可以先unpark ,而 wait & notify 不能先notify

#### * 原理之park & unpark

每个线程都有自己的一个 Parker对象，由三部分组成_counter， _cond 和 _mutex 打个比喻

* 线程就像一个旅人，Parker 就像他们随身携带的背包，条件变量就好比背包里的帐篷。_counter 就好比背包中的备用干粮（0 为耗尽， 1为充足）
* 调用park 就是要看需不需要停下来歇息
  * 如果备用干粮耗尽，那么钻进帐篷歇息
  * 如果备用干粮充足，那么不需要停留继续前进
* 调用unpark, 就好比令干粮充足
  * 如果这时线程还在等待帐篷，就唤醒让他继续前进
  * 如果这时线程还在运行，那么下次他调用park时，仅是消耗掉备用干粮，不需停留继续前进
    * 因为背包空间有限，多次调用unpark仅会补充一份备用干粮

![](D:\文档\并发编程素材\unpark&park.png)

1.当前线程调用Unsafe.park()方法

2.检查_counter，本情况为0，这时，获取 _mutex互斥锁

3.线程进入_cond条件变量阻塞

4.设置_counter = 0



![](D:\文档\并发编程素材\Unpark.png)

1.调用Unsafe.unpark(Thread_0)方法，设置_counter为1

2.唤醒_cond 条件变量中的Thread_0

3.Thread_0恢复运行

4.设置_counter为0



![](D:\文档\并发编程素材\park.png)

1.调用Unsafe.unpark(Thread_0)方法，设置_counter为1

2.当前线程调用Unsafe.park()方法

3.检查_counter，本情况为1 ，这时线程无需阻塞，继续运行

4.设置_counter为0

### 4.10 重新理解线程状态转换

![](D:\文档\并发编程素材\线程运行状态转变.png)

假设由线程 Thread t 

#### 情况1 NEW--> RUNNABLE

* 当调用t.start()方法时，由NEW　--> RUNNABLE

#### 情况2 RUNNABLE<----> WATING

t 线程用synchronized（obj）获取了对象锁后

* 调用 obj.wait() 方法时， t 线程 从RUNNABLE --->WAITING
* 调用object.notify() ,object.notifyAll(),t.interrupt()时
  * 竞争锁成功，t 线程从WAITING--> RUNNABLE 
  * 竞争锁失败，t 线程 从 WAITING--> BLOCKED

#### 情况3 RUNNABLE <----> WAITING 

* 当前线程调用 t.join()方法时，当前线程从RUNNABLE --> WAITING
  * 注意是当前线程在 t线程对象的监视器上等待
* t 线程运行结束，或调用了当前线程的 interrupt()时，当前线程从WAITING --> RUNNABLE

#### 情况4 RUNNABLE <----> WAITING

* 当前线程调用 LockSupport.park() 方法会让当前线程从RUNNABLE --> WAITING
* 调用 LockSupport.unpark(目标线程)或调用了线程的interrupt(),会让目标线程从WAITING -->RUNNABLE

#### 情况5 RUNNABLE　<----> TIMED_WAITING

t 线程 用synchronized(obj)获取对象锁后

* 调用obj.wait(long n) 方法时，t 线程从RUNNABLE -->TIMED_WAITING
* t 线程等待时间超过了n毫秒，或调用obj.notify(),obj.notifyAll(), t.interrupt()时
  * 竞争锁成功，t 线程从TIMED_WAITING --> RUNNABLE
  * 竞争锁失败，t 线程从 TIMED_WAITING --> BLOCKED

#### 情况6 RUNNABLE <---->TIMED_WAITING 

* #### 当前线程调用 t.join(long n)方法时，当前线程从RUNNABLE --> TIMED_WAITING 

  * 注意是当前线程在 t 线程对象的监视器上等待

* 当前线程等待时间超过n毫秒，或 t 线程运行结束，或调用了当前线程的 interrupt()时，当前线程从TIMED_WAITING --> RUNNABLE

#### 情况7 RUNNABLE <----> TIMED_WAITING

* 当前线程调用 Thread.sleep(long n)，当前线程从RUNNABLE -->TIMED_WAITING
* 当前线程等待时间超过了n毫秒，当前线程从TIMED_WAITING -->RUNNABLE

#### 情况8 RUNNABLE<----> TIMED_WAITING

* 当前线程调用 LockSupport.parkNanos(long n) 或 LockSupport.parkUnitl(long millis)时，当前线程从RUNNABLE -->TIMED_WAITING
* 调用LockSupport.unpark(目标线程) 或调用了线程的 interrupt()，或是等待超时，会让目标线  从TIMED_WAITING -->RUNNABLE

#### 情况9 RUNNABLE <---> BLOCKED

* 线程用synchronized(obj) 获取对象锁时如果竞争失败，从RUNNABLE --> BLOCKED
* 持obj锁线程的同步代码块执行完毕，会唤醒该对象上所有BLOCKED线程重新竞争，如果其中t 线程竞争成功，从BLOCKED　--> RUNNABLE ,其他失败的线程仍然BLOCKED

#### 情况10  RUNNABLE　--> TERMINATED

当前线程所有代码运行完毕，进入TERMINATED

### 4.11 多把锁

#### 多把不相干的锁

一间大屋子有两个功能：睡觉 学习 互不相干

现在小南要学习，小女要睡觉，但如果只用一间屋子（一个对象锁）的话，那么并发度很低

解决方法是准备多个房间（多个对象锁）

```java
class BigRoom {

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
```

![image-20210203164424568](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210203164424568.png)

将锁的粒度细分

* 好处，是可以增强并发度
* 坏处，如果一个线程需要同时获得多把锁，就容易发生死锁

### 4.12 活跃性

#### 死锁

有这样得情况：一个线程需要同时获取多把锁，这时就容易发生死锁

t1 线程 获取 A 对象锁，接下来想要获取B 对象锁

t2 线程 获取 B 对象锁，接下来想要获取 A 对象锁

```java
private static void test1() {
        Object A = new Object();
        Object B = new Object();

        Thread t1 = new Thread(()->{
            synchronized (A) {
                log.debug("lock A");
                try {
                    sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (B) {
                log.debug("lock B");
                log.debug("其他操作");
            }
        },"t1");


        Thread t2 = new Thread(()->{
            synchronized (B) {
                log.debug("lock B");
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            synchronized (A) {
                log.debug("lock A");
                log.debug("其他操作");
            }
        },"t2");

        t1.start();
        t2.start();
    }
```



#### 定位死锁

* 检测死锁可以使用jconsole工具，或者使用jps定位进程id,再用jstack定位死锁

#### 哲学家就餐问题

有五位哲学家，围在圆桌旁

* 他们只做两件事，思考和吃饭，思考一会吃口饭，吃完饭后接着思考
* 吃饭时要用两根筷子，桌子上共有5根筷子，每位哲学家左右手变各有一根筷子
* 如果筷子被身边的人拿着，自己就等待

![image-20210219123117494](并发编程.assets/image-20210219123117494.png)

#### 活锁

活锁出现再两个线程互相改变对方的结束条件，最后谁也无法结束



#### 饥饿

定义：一个线程由于优先级太低，始终得不到CPU调度执行，也不能结束

使用顺序加锁的方式解决之前的死锁问题

![image-20210203221940572](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210203221940572.png)

顺序加锁的解决方案

![image-20210203222125610](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210203222125610.png)



### 4.13 ReentrantLock

相对于synchronized 它具备如下特点

* 可中断
* 可以设置超时时间
* 可以设置公平锁
* 可以支持多个条件变量

与synchronized一样，都支持可重入

#### 可重入

可重入是指同一个线程如果首次获得了这把锁，那么因为它是这把锁的拥有者，因此有权利再次获取这把锁

基本语法

```
//获取锁
reentrantLock.lock();
try{
	//临界区
} finally{
	//释放锁
	reentrantLock.unlock();
}
```

如果是不可重入锁，那么第二次获得锁时，自己也会被锁住

#### 可打断

```java
private static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            try {
                // 如果没有竞争那么此方法会获得lock对象锁
                // 如果有竞争就进入阻塞队列，可以被其他线程用interrupt方法打断
                log.debug("尝试获得锁");
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                e.printStackTrace();
                log.debug("没有获得锁，返回");
                return;
            }

            try {
                log.debug("获取到锁");
            }finally {
                lock.unlock();
            }
        },"t1");
        lock.lock();
        t1.start();

        try {
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("打断 t1");
        t1.interrupt();
    }
```

#### 锁超时

立刻失败

```

```



#### 公平锁

公平锁定义：在锁上等待时间最长的线程将获得锁的使用权。通俗地讲就是谁排队时间最长谁先执行获取锁。

ReentrantLock 默认是不公平的.new一个ReentrantLock的时候参数为true，表明实现公平锁机制

#### 条件变量

synchronized 中也有条件变量，就是我们讲原理时那个waitSet休息室，当条件不满足时进入waitSet等待

ReentrantLock 的条件比那辆比synchronized强大之处在于，它是支持多个条件变量的，这就好比

* synchronized是哪些不满足条件的线程都在同一间休息室等消息
* 而ReentrantLock支持多间休息室，有专门等烟的休息室，专门等早餐的休息室，唤醒时也是按照休息室来唤醒的

使用流程：

* await 前需要获得锁
* await 执行后，会释放锁，进入conditionObject等待
* await的线程被唤醒（或打断、或超时）去重新竞争lock锁
* 竞争lock锁成功后，从await后继续执行

### 同步模式之顺序控制 

#### 固定运行顺序

比如，必须先2后1 打印

##### **wait notify 版本**

```java
 static final Object lock = new Object();
    // 表示 t2 是否运行过
    static boolean t2runned = false;

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            synchronized (lock) {
                while(!t2runned) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            log.debug("1");
        },"t1");

        Thread t2 = new Thread(()->{
            synchronized (lock) {
                log.debug("2");
                t2runned = true;
                lock.notify();
            }

        },"t2");

        t1.start();
        t2.start();
    }
```



##### LockSupport  park  unpark

```java
Thread t1 = new Thread(()->{
            LockSupport.park();
            log.debug("1");
        },"t1");
      t1.start();

        new Thread(()->{
            log.debug("2");
            LockSupport.unpark(t1);
        },"t2").start();
    }
```



ReentrantLock 



#### 交替输出

线程1 输出a 5次 ，线程2 输出b 5次 ，线程3 输出c 5次，现在要求输出abcabcabcabcabc怎么实现

wait  notify 版本

```java
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
```



本章小结

本章需要掌握地重点是

* 分析多线程访问共享资源时，哪些代码片段属于临界区

* 使用synchronized互斥解决临界区地线程安全问题

  * 掌握synchronized锁对象语法
  * 掌握synchronized加载成员方法和静态方法语法
  * 掌握wait/notify同步方法

* 使用lock互斥解决临界区的线程安全问题

  * 掌握lock的使用细节：可打断、锁超时、公平锁、条件变量

* 学会分析变量的线程安全性、掌握常见线程安全类的使用

* 了解线程活跃性问题：死锁、活锁、饥饿

* 应用方面

  * 互斥：使用synchronized或Lock达到共享资源互斥效果
  * 同步：使用wait/notify或Lock 的条件变量来达到线程间通信效果

* 原理方面

  * monitor synchronized wait/notify 原理
  * synchronized 进阶原理
  * park & unpark 原理

* 模式方面

  * 同步模式之保护性暂停

  * 异步模式之生产者消费者

  * 同步模式之顺序控制

    



























## 5.共享模型之内存

上一章讲解的Monitor主要关注的是 访问共享变量时，保证临界区代码的原子性      

这一章我们进一步深入学习共享变量在多线程间的【可见性】问题与多条指令执行时的【有序性】问题

### 5.1  Java内存模型

JMM即Java Memory Model , 它定义了主存、工作内存抽象概念，底层对应着CPU寄存器、缓存、硬件内存、CPU指令优化

JMM提现在以下几个方面

* 原子性-保证指令不会受到线程上下文切换的影响
* 可见性-保证指令不会受cpu缓存影响
* 有序性-保证指令不会受cpu指令并行优化的影响

```java
public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread(()->{
            while (run) {
               log.debug("运行中。。。");
            }
        });

        t.start();
        log.debug("停止"); 
        sleep(1000);
        run = false;  // 线程t没有如预想的那样停下来
    }
```



![image-20210204230644284](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210204230644284.png)

![image-20210204230757124](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210204230757124.png)

![image-20210204231027587](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210204231027587.png)

可见性 vs 原子性

前面的例子体现的实际就是可见性，它保证的是多个线程之间，一个线程对volatile变量的修改对另一个线程可见，不能保证原子性，仅用在一个写线程，多个线程的情况：

上例从字节码理解是这样的：

```java
getstatic 		run 	// 线程 t 获取 run true 
getstatic 		run 	// 线程 t 获取 run true   
getstatic 		run 	// 线程 t 获取 run true 
getstatic 		run 	// 线程 t 获取 run true 
putstatic 		run 	// 线程 main 修改 run 为 false， 仅此一次
getstatic 		run 	// 线程 t 获取 run false 
```

比较一下之前我们将线程安全时举的例子：两个线程一个i++ 一个 i--,只能保证看到最新值，不能解决指令交错

```java
// 假设i的初始值为0
getstatic 		i 	// 线程2-获取静态变量i的值	线程内i=0

getstatic 		i  	// 线程1-获取静态变量i的值	线程内i=0
iconst_1 		    // 线程1-准备常量1
iadd 			   //  线程1-自增	线程内i=1
putstatic 		i   // 线程1-将修改后的值存入静态变量i 静态变量i=1

iconst_1 			// 线程2-准备常量1
isub				// 线程2-自减	线程内i=-1
putstatic 		i	// 线程2-将修改后的值存入静态变量i 静态变量i=-1
```

**注意**

synchronized 语句块可以保证代码块的原子性，也同时保证代码块内变量的可见性。但缺点是synchronized是属于重量级操作，性能相对更低

如果在在前面示例的死循环中加入System.out.println() 会发现即使不加volatile修饰符，线程t业能正确看到对run变量的修改

### 终止模式之两阶段终止模式

Two Phase Termination

在一个线程T1中如何 ”优雅“ 终止线程T2 ? 这里的优雅是指给T2一个料理后事的机会

#### 1.错误思路

* 使用线程对象的stop()方法停止线程
  * stop方法会真正杀死线程，如果这时线程锁住了共享资源，那么当它被杀死后就再也没有机会释放锁，其他线程将永远无法获取锁
* 使用System.exit(int)方法停止线程
  * 目的仅是停止一个线程，但是这种做法会让整个程序都停止

同步模式之Balking

Balking（犹豫）模式用在一个线程发现另一个线程或本线程已经做了某一件相同的事，那么本线程就无需再做了，直接结束返回

```java
private volatile boolean stop;
private volatile boolean starting;
private 
```

它还经常用来实现线程安全的单例

对比一下保护性暂停模式：保护性暂停模式用在一个线程等待另一个线程的执行结果，当条件不满足时线程等待。



### 5.3有序性

JVM会在不影响正确性的前提下，可以调整语句的执行顺序，思考下面一段代码

```java
static int i ;
static int j ;

// 在某个线程内执行如下赋值操作
i = .....;
j = .....;
```

可以看到，至于是先执行i还是先执行j，对最终的结果不会产生影响。所以，上面的代码真正执行时，既可以是

```java
i = .....;
j = .....;
```

也可以是

```java
i = ....;
j = ....;
```

这种特性称之为【指令重排】，多线程下【指令重排】会影响正确性。为什么要有重拍指令这项优化呢？从CPU执行指令的原理来理解一下

![image-20210206220533945](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210206220533945.png)

3.指令重排优化

事实上，现代处理器会设计为一个时钟周期完成一条执行时间最长的CPU指令。为什么这么做呢？可以想到指令还可以再划分成为一个个更小的阶段，例如，每条指令都可以划分为：取指令 - 指令译码 - 执行指令 - 内存访问 - 数据写回 这 5 个阶段

![image-20210207104003580](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207104003580.png)

![image-20210207104301037](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207104301037.png)

在不改变程序结果的前提下，这些指令的各个阶段可以通过**重排序**和**组合**来实现指令级并行，这一技术在80`s中叶到90`s中叶占据了计算架构的重要地位。

​	提示：

​	分阶段，分工是提升效率的关键！

指令重排的前提是，重排指令不能影响结果！例如

```java
// 可以重排的例子
int a = 10; // 指令1
int b = 20; // 指令2
System.out.println(a + b);

// 不能重排的例子
int a = 10;
int b = a - 5;
```



![image-20210207105410584](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207105410584.png)

```java
mvn archetype:generate -DinteractiveMode=false -DarchetypeGroupId=org.openjdk.jcstress 
-DarchetypeArtifacted=jcstress-java-test-archetype -DarchetypeVersion=0.5 -DgroupId=cn.chfismine 
-DartifactId=ordering -Dverison=1.0

```

### volatile原理

volatile的底层实现原理是内存屏障，Memory Barrier (Memory Fence)

* 对 violate 变量的写指令后会加入写屏障
* 对 violate 变量的读指令前会加入读屏障

#### 1.如何保证可见性

* 写屏障（sfence）保证在该屏障之前的，对共享变量的改动，都同步到主存中

* ```java
  public void actor2(I_result r) {
      num = 2;
      ready = true; //ready 是 volatile 赋值带写屏障
      // 写屏障
  }
  ```

* 而读屏障（Ifence）保证在该屏障之后，对共享变量的读取，加载的是主存中最新的数据

* ```java
  public void actor1(I_resulr r) {
      // 读屏障
      // ready 是 volatile 赋值带读屏障
      if (ready) {
          
      }
  }
  ```

  

2.如何保证有序性

* 写屏障会确保指令重排序时，不会将写屏障之前的代码排序在写屏障之后

* ```java
  public void actor2(I_Result r) {
  	num = 2;
  	ready = true; // ready 是 volatile 赋值带写屏障
  	// 写屏障
  }
  ```

* 读屏障会确保指令重排序时，不会将读屏障之后的代码排在读屏障之前

* ```java
  public void actor1(I_Result r) {
      // 读屏障
      // ready 是 volatile 读取值带读屏障
  	if(ready){
          r.r1 = num + num;
      }else {
          r.r1 = 1;
      }
  }
  ```

  还是那句话，不能解决指令交错：

  * 写屏障仅仅是保证之后的读能够读到最新的结果，但不能保证读跑到它前面去
  * 而有序性的保证也只是保证了本线程内的相关代码不被重排序

![](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207155950696.png

![image-20210207155851671](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207155851671.png)

3. double-checked locking 问题

   以著名的 double-checked locking 单例模式为例

   ```java
   public final class Singleton {
       private Singleton() {}
       private static synchronized INSTANCE = null;
       public static synchronized Singleton getInstance() {
           if (INSTANCE == null) {
               INSTANCE = new Singleton();
           }
           return INSTANCE;
       }
   }
   ```

   以上的实现特点是：

   * 懒惰实例化
   * 首次使用getInstance() 才使用synchronized加锁，后续使用时无须加锁
   * 有隐含的，但很关键的一点；第一个 if 使用了INSTANCE变量，是在同步块之外

   但在多线程环境下，上面的代码时有问题的

happens-before 

happens-before 规定了对共享变量的写操作对其他线程的读操作可见，他是可见性与有序性的一套规则总结，抛下一下happens-before规则，JMM并不能保证一个线程对共享变量的写，对于其他线程对该共享变量的读可见

* 线程解锁m之前对变量的写，对于接下来对m加锁的其他线程对该变量的读可见

```java
static int x;
static Object m = new Object();

new Thread(()->{
    synchronized(m) {
        x = 10;
    }
},"t1").start();

new Thread(()->{
    synchronized(m) {
        System.out.println(x);
    }
},"t2").start();
```

* 线程对 volatile 变量的写，对接下来其他线程对该变量的读可见

```java
volatile static int x;
```

![image-20210207163855811](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207163855811.png)

![image-20210207164619547](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207164619547.png)

![image-20210207164647947](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207164647947.png)

![image-20210207164739471](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207164739471.png)

![image-20210207164814158](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207164814158.png)

![image-20210207165128012](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207165128012.png)

![image-20210207213237952](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207213237952.png)

![image-20210207215039445](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207215039445.png)

![image-20210207220123416](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207220123416.png)

![image-20210207221048863](C:\Users\season\AppData\Roaming\Typora\typora-user-images\image-20210207221048863.png)









## 6.共享模型之无锁

## 7.共享模型之不可变
