package com.wemarklinks.test;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wemarklinks.config.ZKConfig;
import com.wemarklinks.method.ZkemSDK;

public class ZKTest {
    ZKConfig config;
    ZkemSDK sdk;
    int machineNumber = 1;
    String enrollNumber = "7";
    String name = "左无名";
    String password = "123";
    int privilege = 0;
    boolean enable = false;
    String TZ = "00002359000023590000235900002359000023590000235900002359";
    String TT = "00002359000023590000235900002359000023590000235900002359";
    String TZ1 = "10111223000023590000235900002359000023590000235900002359";
    private static final Logger log = LoggerFactory.getLogger(ZKTest.class);
    
    @Before
    public void before(){
        config = new ZKConfig();
        sdk = new ZkemSDK();
        boolean b = sdk.Connect_Net(config.getIp()[0], config.getPort());
        log.info("连接考勤机 : {}",b);
    }
    
    public static void main(String[] args) {
//        ZKConfig config = new ZKConfig();
//        ZkemSDK sdk = new ZkemSDK();
//        boolean b = sdk.Connect_Net(config.getIp()[0], config.getPort());
//        log.info("连接考勤机 : {}",b);
    }
    
    @Test
    public void GetUserFaceStrTest(){
        Map<String, Object> faceStr = sdk.GetUserFaceStr(1, "1");
        log.info("获取用户脸部模板:{}",faceStr);
    }
    
//    @Test
//    public void SSR_DeleteEnrollDataExtTest(){
//        boolean deleteEnrollData = sdk.SSR_DeleteEnrollDataExt(1, "4", 8);  //可用
//        log.info("删除指纹数据:{}",deleteEnrollData);
//    }
    
    @Test 
    public void GetUserTmpExStrTest(){
        Map<String, Object> tmpExStr = sdk.GetUserTmpExStr(1, "4", 8);
        log.info("获取指纹8:{}",tmpExStr);
//        Map<String, Object> tmpExStr2 = sdk.GetUserTmpExStr(1,"4", 6);
//        log.info("获取指纹7:{}",tmpExStr2);
//        String data1 = tmpExStr.get("tmpdata").toString();
//        String data2 = tmpExStr2.get("tmpdata").toString();
//        System.out.println("data1:"+data1);
//        System.out.println("data2:"+data2);
//        boolean tmp = sdk.SSR_DelUserTmp(1, "4", 7);         //不可用,false
//        log.info("删除指纹:{}",tmp);
//        boolean tmpExt = sdk.SSR_DelUserTmpExt(1, "4", 7); // 不可用
//        log.info("删除指纹ext:{}",tmpExt);
//        boolean exStr = sdk.SetUserTmpExStr(1, "4", 8, 1, tmpExStr.get("tmpdata").toString());
        
        //以字符串设置指纹可用
        boolean exStr = sdk.SetUserTmpExStr(1, "4", 7, 1, "St1TUzIxAAADnpwECAUHCc7QAAAbn2kBAAAAg0Mhn54bAAoOtgDlAImQigAlAAcP+wAtnt4PvAAvAE8P4p5EAJEPlQCNAHqR2QBNABcOlQBQnt8NPQBWAB8P6Z5cACMPlwClAA+RYQBoAOYPXgB7nnQPkACCAMkPQZ6HAEwPpgBVAIOTqgCpAHwMJgConi4OkQC0AMcLkp6+AAsNewAFAMWVhQDAADYLfgDHnqMNoQDaANkPbJ7kADwPyQArAJqQ1ADwAKUOYQD6nhwPzwD+AFcOyZ4GARoOnADOASeRtAAPARYPJAWCHY+CKQCBg7eHYmOvkXZ/5gYXAsxlqACOghp2lX8i7YcLRW6+dTbwTxWI9g6OHQUM+1GcoP269mr+OA4SCf6bMZatmnsBQpUvDv6fbWgAajppWJiNjz4B02vw6gJxOQaZd3QPvmB0fzLcv4wStuQtwfvhzek+8Qv3SfRGvJK5vmDGz+xJLy01iiiHiWUFowASEEp913oZlSIx7PcZCOCCY5EsFC2OtflMCyGeRXvB7KH4+YvamCSCLQllEp4MXmDw9hH7P8p1xQO+MwEB7xcOzACjngg+/8DAwMIAuZ8N/mXBBAAVARDLBQBNAmeNwgBam3HEwMNXBcVhCvPDwcQJAJjffcNdfY8EAI4jxTYEnrsjFsFBwcIAhbh7w8HAkQTFjiuYWxEAQize9f7DtTNMwAMAwPQTw50BOzJXwAvFYTx3//8owP9a0AD1oZ3BwFyJwQTCg1/AwsDABgCTRF0a/g0AkUh6S8P8XMGHwwMAOp9TwpUBml4D/v2F/8PRDwCSYH3DV8KP/P4PAGNk4u/A/WP/wP7/wFrOAJn6CC7//2L/yQCU5nzEoInAR9IA/+OjwP7Cw2mwwJ3vw/7C/wwAUX8DgMH7wT7BE8VEgk4//cD7wPwE//1hwf79gwUASYNzWp4MAJSFBj7BJGFM/gMAPomJwACeRYlJ/wYAZ4+KW8efDACAkJ/DkF/AdMESAMhQpI8GwsHEfGr+0wDzBqjAfpDF/wfCwBjDwcCDBQBXnf5l/f0HANGi6ENNlQGcqHHIxQfCwV7CjgcAkLs4+MP7DQB/vsPAPvr/Yv3+/jUGALnEORnDCACDxTQEwWlcGQDnxbTBQHnBEMTAl8B3QsIAbVg8h34FAJ8MHsL+BQCl2hduwAChQCbDkQQAcCw3wVwCAM3xKf/BAKRiIX9SQgALhgEDngpFUgAAAAAAAAA=");
        log.info("设置指纹:{}",exStr);
    }
    
    @Test
    public void SetUserInfoExTest(){
        boolean infoEx = sdk.SetUserInfoEx(1,2, 128);
        log.info("设置验证方式:{}",infoEx);
        Integer ex = sdk.GetUserInfoEx(1, 2);
        log.info("获取用户 {} 的验证方式 :{}",2,ex);
    }
    
    @Test
    public void GetUserInfoExTest(){
        Integer ex = sdk.GetUserInfoEx(1, 2);
        log.info("验证方式:{}",ex);
    }
    
    @Test
    public void SSR_DeleteEnrollDataExtTest(){
//        boolean ext = sdk.SSR_DeleteEnrollDataExt(1, "2", ZkemSDK.DEL_PW);
//        log.info("删除密码 : {}",ext);
//        boolean ext = sdk.SSR_DeleteEnrollDataExt(1, "7",ZkemSDK.DEL_FP);
//        log.info("删除指纹 : {}",ext);
        boolean ext = sdk.SSR_DeleteEnrollDataExt(1, "5", ZkemSDK.DEL_ALL);
        log.info("删除所有 : {}",ext);
    }
    
//    @Test
//    public void DeleteUserInfoExTest(){
//        boolean ex = sdk.DeleteUserInfoEx(1, 2);
//        log.info("删除用户的多种验证方式 : {}",ex);
//    }
    
    @After
    public void after(){
        sdk.Disconnect();
        log.info("断开考勤机 ");
    }
    
    @Test
    public void enableUserTest(){
        boolean b = sdk.SSR_EnableUser(1, "8", false);
        log.info("设置用户:{}",b);
    }
    
    @Test
    public void  userTimeTest(){
//        String str = sdk.GetUserTZStr(machineNumber, 1);
//        log.info("时间段 : {}",str);
    }
    
    @Test  //查询指定编号时间段信息
    public void GetTZInfo(){
        String tzInfo = sdk.GetTZInfo(1, 2);
        log.info("时间段信息 : {}",tzInfo);
    }
    
    @Test  //设置指定时间段信息
    public void SetTZInfo(){
        boolean b = sdk.SetTZInfo(1, 2, TZ1);
        log.info("设置时间段信息 : {}",b);
    }
    
    @Test //设置用户时间段
    public void setTZS(){
    }
    
    @Test
    public void userSettingTest(){
        boolean b = sdk.SSR_SetUserInfo(machineNumber, enrollNumber, name, password, privilege, enable);
        log.info("设置用户 : {}",b);
    }
    @Test
    public void getUserInfo(){
        Map<String, Object> ssr_GetUserInfo = sdk.SSR_GetUserInfo(1, "9");
        log.info("读取用户: {}",ssr_GetUserInfo);
    }
    
    @Test
    public void setUserInfo(){
        boolean userInfo = sdk.SSR_SetUserInfo(1, "3", "小指", "", 0, false);
        log.info("设置用户 :{}",userInfo);
    }
    
//    @Test
//    public void SetUserInfoExTest(){
//        boolean setUserInfoEx = sdk.SetUserInfoEx(1, 2, 130);
//        log.
//        
//    }
    
    @Test //查询门径
    public void getACUnlock(){
        sdk.GetACFun();
    }
    
    @Test
    public void ACUnlockTest(){
        boolean acUnlock = sdk.ACUnlock(1, 1);
        log.info("开门:{}",acUnlock);
    }
    
//    @Test
//    public void enableDeviceTest(){
//        boolean b = sdk.EnableDevice(1, 1);
//        log.info("测试机器是否可用:{}",b);
//    }
    
    @Test
    public void IsTFTMachineTest(){
        boolean b = sdk.isTFTMachine(1);
        log.info("IsTFTMachine : {}",b);
    }
    
    @Test
    public void ReadRTLog(){
        boolean b = sdk.ReadRTLog(1);
        log.info("读取实时事件 : {}",b);
        boolean b1 = sdk.GetRTLog(1);
        log.info("取出实时事件 :{}",b1);
    }
}
