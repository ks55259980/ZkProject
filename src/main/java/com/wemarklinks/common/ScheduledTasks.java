package com.wemarklinks.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wemarklinks.config.ZKConfig;
import com.wemarklinks.method.ZkemSDK;

@Component
public class ScheduledTasks {
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    
    private ZkemSDK sdk = new ZkemSDK();
    private ZKConfig config = new ZKConfig();
    
    public ScheduledTasks() {
        boolean b = sdk.Connect_Net(config.getIp()[0], config.getPort());
        log.info("ScheduledTasks 连接设备 : {}", b);
    }
    
    @Scheduled(cron = "0 0/8 * * * ?")
    public void cheakSDK() {
        boolean b = sdk.ReadRTLog(1);
        log.info("读取实时事件 : {}",b);
        boolean b1 = sdk.GetRTLog(1);
        log.info("取出实时事件 :{}",b1);
//        boolean b = sdk.isTFTMachine(1);
//        log.info("心跳检测 :{}", b);
        if(b == false){
            throw new RuntimeException("连接设备失败");
        }
    }
    
}
