package com.wemarklinks.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wemarklinks.config.ZKConfig;
import com.wemarklinks.method.ZkemSDK;

@Component
public class ScheduledTasks {
    
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    
//    @Autowired
    private ZkemSDK sdk = new ZkemSDK();
    @Autowired
    private ZKConfig config;
    
    @Scheduled(cron = "0 1 * * * ?")
    public void cheakSDK() {
        for (int i = 0; i < config.getIp().length; i++) {
            boolean b = sdk.EnableDevice(i, 1);
            log.info("device enable : {} ", b);
        }
        
    }
    
}
