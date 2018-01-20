package com.wemarklinks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wemarklinks.common.ResultCode;
import com.wemarklinks.method.ZkemSDK;
import com.wemarklinks.service.User;
import com.wemarklinks.service.ZkService;
import com.wemarklinks.util.JsonResult;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/zk")
public class ZKController {
    
    
    private static final Logger log = LoggerFactory.getLogger(ZKController.class);

    @Autowired
    ZkService service;
    ZkemSDK sdk = new ZkemSDK();
    String userId = "1";
    String name = "呆呆";
    String password = "";
    int privilege = 0;  // 0为普通用户
    boolean enabled = true;
    
    @ApiOperation(value = "创建用户", notes = "用户是否重复以userId为key")
    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    public Map<String,Object> createUser(
            @RequestParam String userId,
            @RequestParam String name,
            @ApiParam(value = "密码") @RequestParam(required = false) String password,
            @ApiParam(value = "管理员(3),普通用户(0)",required = true) @RequestParam int privilege,
            @RequestParam boolean enabled){
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setPassword(password);
        user.setPrivilege(privilege);
        user.setEnabled(enabled);
        boolean b = service.changeUserInfo(userId, name, privilege);
        log.info("创建用户 : {}",b);
        if(b == false){
            return JsonResult.RetJsone(ResultCode.UNKNOWN_ERROR, "创建用户失败", "");
        }
        //设置新建用户验证方式为人脸验证
        service.setUserInfoEx(userId, ZkemSDK.FACE);
        return JsonResult.RetJsone(ResultCode.SUCCESS , "", user);
    }
    
    @ApiOperation(value = "更新用户", notes = "用户是否重复以userId为key")
    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    public Map<String,Object> updateUser(
            @RequestParam String userId,
            @RequestParam String name,
            @ApiParam(value = "密码") @RequestParam(required = false) String password,
            @ApiParam(value = "管理员(3),普通用户(0)") @RequestParam int privilege,
            @RequestParam boolean enabled){
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setPassword(password);
        user.setPrivilege(privilege);
        user.setEnabled(enabled);
        boolean b = service.changeUserInfo(userId, name, privilege);
        log.info("更新用户 : {}",b);
        if(b == false){
            return JsonResult.RetJsone(ResultCode.UNKNOWN_ERROR, "创建用户失败", "");
        }
        return JsonResult.RetJsone(ResultCode.SUCCESS , "", user);
    }

    @ApiOperation(value = "批量创建用户",notes = "以json形式传递参数")
    @RequestMapping(value = "/batchCreateUser", method = RequestMethod.POST)
    public Map<String, Object> batchCreateUser(@RequestBody User[] users){
        boolean b = service.changeUseres(users);
        if( b == false){
            return JsonResult.RetJsone(ResultCode.UNKNOWN_ERROR, "创建用户失败", "");
        }
        for(User user : users){
            service.setUserInfoEx(user.getUserId(), ZkemSDK.FACE);
        }
        return JsonResult.RetJsone(ResultCode.SUCCESS , "批量创建用户成功", "");
    }
    
    
    @ApiOperation(value = "获取用户", notes = "")
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.GET)
    public Map<String, Object> getUserInfo(@RequestParam String userId){
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setPassword(password);
        user.setPrivilege(privilege);
        user.setEnabled(enabled);
        Map map = service.SSR_GetUserInfo(userId);
        if(map == null){
            return JsonResult.RetJsone(ResultCode.SYS_ERROR , "查询用户失败", "");
        }
        return JsonResult.RetJsone(ResultCode.SUCCESS , "", map);
    }
    
    @ApiOperation(value = "获取用户列表", notes="获取用户列表")
    @RequestMapping(value = "/listUser", method = RequestMethod.GET)
    public Map<String, Object> listUser(
            @ApiParam(value = "查询页", required = true) @RequestParam Integer page,
            @ApiParam(value = "数据数目", required = true) @RequestParam Integer pageSize){
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setPassword(password);
        user.setPrivilege(privilege);
        user.setEnabled(enabled);
        List<User> list = new ArrayList<User>();
        for(int i=0;i<10;i++){
            list.add(user);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("list", list);
        map.put("page", page);
        map.put("pageSize", pageSize);
        map.put("totalPage", 8);
        map = service.SSR_GetAllUserInfo(page, pageSize);
        
        return JsonResult.RetJsone(ResultCode.SUCCESS , "", map);
    }
    
    @ApiOperation(value = "远程开门", notes = "")
    @RequestMapping(value = "/openDoor", method = RequestMethod.PUT)
    public Map<String, Object> openDoor(){
        return JsonResult.RetJsone(ResultCode.SUCCESS , "", "");
    }
    
    @ApiOperation(value = "激活用户", notes = "激活后 , 该用户可以扫脸并打开门禁")
    @RequestMapping(value = "/enableUser", method = RequestMethod.PUT)
    public Map<String, Object> enableUser(String userId){
        return JsonResult.RetJsone(ResultCode.SUCCESS , "", "");
    }
    
    @ApiOperation(value = "禁用用户", notes = "禁用后 , 该用户无法打开门禁")
    @RequestMapping(value = "/disableUser", method = RequestMethod.PUT)
    public Map<String, Object> disableUser(String userId){
        return JsonResult.RetJsone(ResultCode.SUCCESS , "", "");
    }
    
    
    
    
}
