package com.wemarklinks.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.wemarklinks.config.ZKConfig;
import com.wemarklinks.method.ZkemSDK;

@Service
public class ZkService {
    
    private static final Logger log = LoggerFactory.getLogger(ZkService.class);
    private static ZkemSDK sdk = new ZkemSDK();
    ZKConfig config = new ZKConfig();
  
    public ZkService() {
        log.info("init ZkService");
        boolean b1 = sdk.Connect_Net(config.getIp()[0], config.getPort());
        if (b1 == false) {
            log.warn("连接考勤机失败");
        }
    }
    
    public static ZkemSDK getSDK(){
        return sdk;
    }
    
    // private boolean connectNet(int i) {
    // boolean b1 = sdk.Connect_Net(config.getIp()[i], config.getPort());
    // return b1;
    // }
    //
    // private void disConnect() {
    // sdk.Disconnect();
    // }
    
    /**
     * 设置指定用户的用户信息，以userId为key , 若机内没有该用户，则会创建该用户
     * 
     * @param userId
     *            用户id
     * @param name
     *            用户姓名
     * @param privilege
     *            用户权限, 3为管理员，0为普通用户
     * @return
     */
    public boolean changeUserInfo(String userId, String name, int privilege, String password,boolean enanbled) {
        password = StringUtils.isEmpty(password) ? "" : password;
        boolean b = sdk.SSR_SetUserInfo(ZkemSDK.machineNumber, userId, name, password, privilege, enanbled);
        if (b == false) {
            return false;
        }
        return true;
    }
    
    /**
     * 设置大量用户信息
     * 
     * @param users
     * @return
     */
    public boolean changeUseres(User[] users) {
        for (int j = 0; j < users.length; j++) {
            User user = users[j];
            boolean b = sdk.SSR_SetUserInfo(ZkemSDK.machineNumber, user.getUserId(), user.getName(),user.getPassword(), user.getPrivilege(), true);
            if (b == false) {
                return false;
            }
        }
        return true;
    }
    
    /** 根据userId查找user */
    public Map<String,Object> SSR_GetUserInfo(String userId){
        Map<String, Object> user = sdk.SSR_GetUserInfo(ZkemSDK.machineNumber, userId);
        return user;
    }
    
    public Map<String,Object> SSR_GetAllUserInfo(Integer page,Integer pageSize){
        List<Map<String,Object>> list = sdk.SSR_GetAllUserInfo(ZkemSDK.machineNumber);
        Collections.sort(list,new Comparator<Map<String,Object>>(){
            @Override
            public int compare(Map<String,Object> m1, Map<String,Object> m2) {
                return    (int)m1.get("userId") - (int)m2.get("userId"); 
            }}
        );
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("notes", list.size());
        map.put("pageSize", pageSize);
        double totalPage = Math.ceil((list.size()*1.0)/pageSize);
        map.put("totalPage", totalPage);
        if(page>totalPage){
            page = (int)totalPage;
        }
        map.put("page", page);
        int start = (page-1)*pageSize;
        int end = page * pageSize -1;
        List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
        for(int i=0;i<list.size();i++){
            if(i<start || i >end){
                continue ;
            }
            list2.add(list.get(i));
        }
        map.put("list", list2);
        return map;
    }
    
    /**
     * 启用用户
     * 
     * @param userId
     * @return
     */
    public boolean unlockUser(String userId) {
        boolean b = sdk.SSR_EnableUser(ZkemSDK.machineNumber, userId, true);
        if (b == false) {
            return false;
        }
        return true;
    }
    
    public boolean unlockUsers(String[] userIds) {
        for (int j = 0; j < userIds.length; j++) {
            boolean b = sdk.SSR_EnableUser(ZkemSDK.machineNumber, userIds[j], true);
            if (b == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 禁用用户
     * 
     * @param userId
     * @return
     */
    public boolean lockUser(String userId) {
        boolean b = sdk.SSR_EnableUser(ZkemSDK.machineNumber, userId, false);
        if (b == false) {
            return false;
        }
        return true;
    }
    
    public boolean lockUsers(String[] userIds) {
        for (int j = 0; j < userIds.length; j++) {
            boolean b = sdk.SSR_EnableUser(ZkemSDK.machineNumber, userIds[j], false);
            if (b == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 设置验证方式
     * @param userId
     * @param verifyStyle
     * @return
     */
    public boolean setUserInfoEx(String userId, int verifyStyle){
        boolean b = sdk.SetUserInfoEx(ZkemSDK.machineNumber, (int)Integer.valueOf(userId), verifyStyle);
        return b;
    }
    
    public boolean deleteUser(String userId){
        boolean ext = sdk.SSR_DeleteEnrollDataExt(ZkemSDK.machineNumber, userId, ZkemSDK.DEL_ALL);
        return ext;
    }
    
    /**
     * 开门
     * 
     * @param machineNumber
     *            设备号, 门外设备为1, 门内为2
     * @return
     */
    public boolean openDoor(int machineNumber) {
        return sdk.ACUnlock(machineNumber, 80);
    }
}
