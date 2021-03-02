package cn.chfismine.concurrency.chapter4;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: concurrency
 * @description: 两阶段中止
 * @author: CHF
 * @create: 2021-02-05 12:31
 */
@Slf4j
public class test34 {

    private static final Logger log = LoggerFactory.getLogger(test34.class);

    public static void main(String[] args) throws InterruptedException {
        TwoPhaseTermination tpt = new TwoPhaseTermination();
        tpt.start();

        Thread.sleep(3500);
        log.debug("停止监控");
        tpt.stop();
    }


}


class TwoPhaseTermination{

    private static final Logger log = LoggerFactory.getLogger(TwoPhaseTermination.class);
    // 监控线程
    private Thread monitorThread;
    //
    private boolean stop = false;

    // 启动监控线程
//    public void start() {
//        monitorThread = new Thread(()->{
//           while(true) {
//               Thread current = Thread.currentThread();
//               // 是否被打断
//               if (current.isInterrupted()) {
//                   log.debug("料理后事");
//                   break;
//               }
//               try {
//                   Thread.sleep(1000);
//               } catch (InterruptedException e) {
//                   // 因为sleep 出现异常后，会清除打断标记
//                   // 需要重置打断标记
//                   current.interrupt();
//                   e.printStackTrace();
//               }
//           }
//        },"monitor");
//        monitorThread.start();
//    }

    public void start() {
        monitorThread = new Thread(()->{
            while(true) {
                Thread current = Thread.currentThread();
                // 是否被打断
                if (stop) {
                    log.debug("料理后事");
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },"monitor");
        monitorThread.start();
    }

    // 停止监控线程
    public void stop() {

        stop = true;
        
        monitorThread.interrupt();
    }
}