package com.wemarklinks.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wemarklinks.service.ZkService;

@RestController
@RequestMapping("/zk")
public class ZKController {
    
    ZkService service = new ZkService();
    
    @RequestMapping(value = "/userInfo", method = RequestMethod.POST)
    public Map<String,Object> changeUserInfos(){
//        service.changeUseres(users)
        
        return null;
    }
    
}
