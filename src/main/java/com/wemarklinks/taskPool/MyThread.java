//package com.wemarklinks.taskPool;
//
//import java.lang.management.ManagementFactory;
//import java.lang.management.ThreadInfo;
//import java.lang.management.ThreadMXBean;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ThreadPoolExecutor;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.ui.context.Theme;
//
//import com.jacob.com.STA;
//
//@Configuration
//@EnableAsync
//public class MyThread {
//
//    private int corePoolSize = 2;//线程池维护线程的最少数量
//
//    private int maxPoolSize = 2;//线程池维护线程的最大数量
//
//    private int queueCapacity = 8; //缓存队列
//
//    private int keepAlive = 60;//允许的空闲时间
//    
//    public STA sta1 = new STA();
//    
//    public STA sta2 = new STA(); 
//
//    @Bean
//    public Executor myExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(){
//            @Override
//            public void shutdown() {
//                sta1.quit();
//                sta2.quit();
//                Thread s = findThread(sta1.getId());
//                System.out.println("线程存活状态:"+s.getName()+", "+s.isAlive());
//                s.interrupt();
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("线程存活状态:"+s.getName()+", "+s.isAlive());
//                super.shutdown();
//                s.interrupt();
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("线程存活状态:"+s.getName()+", "+s.isAlive());
//            }
//        };
//        executor.setCorePoolSize(corePoolSize);
//        executor.setMaxPoolSize(maxPoolSize);
//        executor.setQueueCapacity(queueCapacity);
//        executor.setThreadNamePrefix("zksdk-Executor-");
//        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); //对拒绝task的处理策略
//        executor.setKeepAliveSeconds(keepAlive);
//        executor.initialize();
//        return executor;
//    }
//    /**
//     * 通过线程组获得线程
//     *
//     * @param threadId
//     * @return
//     */
//    public static Thread findThread(long threadId) {
//        ThreadGroup group = Thread.currentThread().getThreadGroup();
//        while(group != null) {
//            Thread[] threads = new Thread[(int)(group.activeCount() * 1.2)];
//            int count = group.enumerate(threads, true);
//            System.out.println("线程数量 : "+count);
//            for(int i = 0; i < count; i++) {
//                if(threadId == threads[i].getId()) {
//                    return threads[i];
//                }
//            }
//            group = group.getParent();
//        }
//        return null;
//    }
//}
