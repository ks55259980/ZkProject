package com.wemarklinks.service;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.STA;
import com.jacob.com.Variant;
import com.wemarklinks.config.ZKConfig;
import com.wemarklinks.method.ZkemEvent;
import com.wemarklinks.method.ZkemEvent1;
import com.wemarklinks.method.ZkemEvent2;
import com.wemarklinks.method.ZkemSDK;

@Component
public class SdkListener implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(SdkListener.class);
    @Autowired
    ZKConfig config;
    
    ZkemSDK sdk = new ZkemSDK();
    
    ZkemEvent event1 = new ZkemEvent1();
    
    ZkemEvent event2 = new ZkemEvent2();
    
    @Override
    public void run(String... arg0) {
        findThread(1);
        
        for (int i = 0; i < config.getIp().length; i++) {
            if (i == 0) {
                listen(i, event1,t1);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (i == 1) {
                listen(i, event2,t2);
            }
        }
    }
    Thread t1;
    Thread t2;
    STA sta1 = new STA();
    STA sta2 = new STA();
    private void listen(int i, ZkemEvent event,Thread t){
        Runnable r1 = () -> {
            listenSdk(i,event);
        };
        t=new Thread(r1);
        t.start();
    }
    private void listenSdk(int i, ZkemEvent event) {
        STA sta;
        Thread t;
        if(i==0){
            sta = sta1;
            t = t1;
        }else{
            sta = sta2;
            t = t2;
        }
        System.out.println("开启任务 : " + Thread.currentThread().getName());
        try {
            ComThread.InitMTA();
            boolean b = Dispatch
                    .call(sdk.zkem, "Connect_Net", new Variant(config.getIp()[i]), new Variant(config.getPort()))
                    .getBoolean();
            log.info("连接 : {}", b);
            if (b == false) {
                log.warn("连接失败");
                return;
            }
            new DispatchEvents(sdk.zkem, event);
            Dispatch.call(sdk.zkem, "RegEvent", new Variant(1L), new Variant(65535L));
            sta.doMessagePump();
            log.info("domessage");
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            ComThread.Release();
        }
    }
    private void stopThread(STA sta,Thread t){
        sta.quit();
        ComThread.Release();
        log.info("t is null ?{}",t==null);
//        t.interrupt();
//        t.stop();
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        boolean alive = t.isAlive();
//        log.info(t.getName()+" is alive :"+alive);
    }
    
    @Bean
    public Executor myExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(){
            @Override
            public void shutdown() {
                super.shutdown();
                stopThread(sta1,t1);
                stopThread(sta2,t2);
//                System.out.println("线程id:"+t1.getId());
//                findThread(t1.getId());
            }
        };
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("zksdk-Executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //对拒绝task的处理策略
        executor.initialize();
        return executor;
    }
    private int corePoolSize = 0;//线程池维护线程的最少数量

    private int maxPoolSize = 1;//线程池维护线程的最大数量

    private int queueCapacity = 8; //缓存队列
    
    public static Thread findThread(long threadId) {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        while(group != null) {
            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
            int count = group.enumerate(threads, true);
            System.out.println("thread count : "+count);
//            for(int i = 0; i < count; i++) {
//                if(threadId == threads[i].getId()) {
//                    return threads[i];
//                }
//            }
            group = group.getParent();
        }
        return null;
    }
}
