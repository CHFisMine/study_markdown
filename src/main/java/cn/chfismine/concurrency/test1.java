package cn.chfismine.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @program: concurrency
 * @description:
 * @author: CHF
 * @create: 2021-02-23 15:31
 */
public class test1 {

    private static Logger log = LoggerFactory.getLogger(test1.class);

    public static void main(String[] args) {
        DateTimeFormatter stf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                TemporalAccessor parse = stf.parse("1952-01-21");
                log.debug("{}",parse);
            }).start();
        }
    }

    public void test() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                synchronized (sdf) {
                    try{
                        log.debug("{}",sdf.parse("1951-04-21"));
                    }catch (Exception e) {
                        log.error("{}",e);
                    }
                }
            }).start();
        }
    }
}
