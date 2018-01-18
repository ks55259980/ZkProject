package com.wemarklinks.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wemarklinks.config.ZKConfig;
import com.wemarklinks.method.ZkemSDK;

public class ZkService {
    
    private static final Logger log = LoggerFactory.getLogger(ZkService.class);
    private ZkemSDK sdk = new ZkemSDK();
    ZKConfig config = new ZKConfig();
    
    private boolean connectNet(int i) {
        boolean b1 = sdk.Connect_Net(config.getIp()[i], config.getPort());
        return b1;
    }
    
    private void disConnect() {
        sdk.Disconnect();
    }
    
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
    public boolean changeUserInfo(String userId, String name, int privilege) {
        for(int i=0;i<config.getIp().length;i++){
            boolean b1 = connectNet(i);
            if (b1 == false) {
                log.warn("连接考勤机失败");
                return false;
            }
            boolean b = sdk.SSR_SetUserInfo(i+1, userId, name, "", privilege, true);
            if(b == false){
                return false;
            }
            disConnect();
        }
        return true;
    }
    
    /**
     * 设置大量用户信息
     * @param users
     * @return
     */
    public boolean changeUseres(User[] users){
        for(int i=0;i<config.getIp().length;i++){
            boolean b1 = connectNet(i);
            if (b1 == false) {
                log.warn("连接考勤机失败");
                return false;
            }
            for(int j=0 ;j <users.length;j++){
                User user = users[j];
                boolean b = sdk.SSR_SetUserInfo(i+1, user.getUserId(), user.getName(), "", user.getPrivilege(), true);
                if(b == false){
                    return false;
                }
            }
            disConnect();
        }
        return true;
    }
    
    /**
     * 启用用户
     * @param userId
     * @return
     */
    public boolean unlockUser(String userId) {
        for(int i=0;i<config.getIp().length;i++){
            boolean b1 = connectNet(i);
            if (b1 == false) {
                log.warn("连接考勤机失败");
                return false;
            }
            boolean b = sdk.SSR_EnableUser(1, userId, true);
            if(b == false){
                return false;
            }
            disConnect();
        }
        return true;
    }
    
    public boolean unlockUsers(String[] userIds){
        for(int i=0;i<config.getIp().length;i++){
            boolean b1 = connectNet(i);
            if (b1 == false) {
                log.warn("连接考勤机失败");
                return false;
            }
            for(int j=0 ;j <userIds.length;j++){
                boolean b = sdk.SSR_EnableUser(1, userIds[j], true);
                if(b == false){
                    return false;
                }
            }
            disConnect();
        }
        return true;
    }
    
    /**
     * 禁用用户
     * @param userId
     * @return
     */
    public boolean lockUser(String userId) {
        for(int i=0;i<config.getIp().length;i++){
            boolean b1 = connectNet(i);
            if (b1 == false) {
                log.warn("连接考勤机失败");
                return false;
            }
            boolean b = sdk.SSR_EnableUser(1, userId, false);
            if(b == false){
                return false;
            }
            disConnect();
        }
        return true;
    }
    
    public boolean lockUsers(String[] userIds){
        for(int i=0;i<config.getIp().length;i++){
            boolean b1 = connectNet(i);
            if (b1 == false) {
                log.warn("连接考勤机失败");
                return false;
            }
            for(int j=0 ;j <userIds.length;j++){
                boolean b = sdk.SSR_EnableUser(1, userIds[j], false);
                if(b == false){
                    return false;
                }
            }
            disConnect();
        }
        return true;
    }
    
    /**
     * 开门
     * @param machineNumber  设备号, 门外设备为1, 门内为2
     * @return
     */
    public boolean openDoor(int machineNumber){
        return sdk.ACUnlock(machineNumber, 80);
    }
}
