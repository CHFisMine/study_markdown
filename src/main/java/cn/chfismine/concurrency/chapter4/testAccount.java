package cn.chfismine.concurrency.chapter4;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: concurrency
 * @description: 账户测试
 * @author: CHF
 * @create: 2021-02-20 13:38
 */
public class testAccount {

    public static void main(String[] args) {
        Account account = new  AccountUnsafe(10000);
        Account.demo(account);

        Account account1 = new AccountCas(10000);
        Account.demo(account1);
    }
}

class AccountCas implements Account {

    private AtomicInteger balance;

    public AccountCas(int balance) {
        this.balance = new AtomicInteger(balance);
    }

    @Override
    public Integer getBalance() {
        return balance.get();
    }

    @Override
    public void withdraw(Integer amount) {
        while(true) {
            // 获取余额的最新值
            int pre = balance.get();
            // 要修改的余额
            int next = pre - amount;
            // 真正修改
            boolean b = balance.compareAndSet(pre, next);
            if (b) {
                break;
            }
        }
    }
}

class AccountUnsafe implements Account {

    private Integer balance;

    public AccountUnsafe(Integer balance) {
        this.balance = balance;
    }

    @Override
    public Integer getBalance() {
        synchronized (this) {
            return this.balance;
        }
    }

    @Override
    public void withdraw(Integer amount) {
        synchronized (this) {
            this.balance -= amount;
        }
    }
}

interface Account {

    //获取余额
    Integer getBalance();

    //取款
    void withdraw(Integer amount);


    /**
     * 方法内会启动1000个线程，每个线程做-10元的操作
     * 如果初始余额为10000 那么正确的结果应该是0
     * @param account
     */
    static void demo(Account account) {
        List<Thread> ts = new ArrayList<>();
        for (int i =0; i<1;i++) {
            ts.add(new Thread(()->{
                account.withdraw(10);
            }));
        }

        long start = System.nanoTime();
        ts.forEach(Thread::start);
        ts.forEach(t->{
            try{
                t.join();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.getBalance()+" cost: "+ (end-start)/1000_000+" ms");
    }


}
