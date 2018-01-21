package com.wemarklinks.method;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jacob.com.Variant;
import com.wemarklinks.mapper.RecordMapperExt;
import com.wemarklinks.pojo.Record;
import com.wemarklinks.service.ZkService;

/**
 * 中控事件处理类
 * 
 * @author 陈捷
 *
 */
@Component
public class ZkemEvent1 extends ZkemEvent {
    
    private static final Logger log = LoggerFactory.getLogger(ZkemEvent1.class);

    public static final String status = "进门";
    
    @Autowired
    ZkService service;
    @Autowired
    RecordMapperExt recordMapperExts;
    
    /**
     * 当验证通过时触发该事件 以下参数全为返回值 函数原型:OnAttTransactionEx(BSTR ErollNumber,LONG
     * IsInValid,LONG AttState,LONG VerifyMethod, LONG Year,LONG Month,LONG
     * Day,LONG Hour,LONG Minute,LONG Second,LONG WorkCode)
     *
     * EnrollNumber:该用户的UserID IsInValid:该记录是否为有效记录，1为无效记录，0为有效记录 AttSate:考勤状态
     * 默认0 Check-In, 1 Check-Out, 2 Break-Out, 3 Break-In, 4 OT-In, 5 OT-Out
     * VerifyMethod:验证方式 0为密码验证，1为指纹验证，2为卡验证
     * Year/Month/Day/Hour/Minute/Second:为验证通过的时间
     * WorkCode:返回验证时WorkCode值，当机器无Workcode功能时，该值返回0
     *
     * @param vars
     */
    public void OnAttTransactionEx(Variant[] vars) {
        System.out.println("Event : OnAttTransactionEx111--->" + Thread.currentThread().getName());
        for(Variant var : vars){
            System.out.println(var);
        }
        String userId = vars[0].toString();
        int isInValid = vars[1].getInt();
        int attState = vars[2].getInt();
        int verifyMethod = vars[3].getInt();
        String year = vars[4].toString();
//        String month = check(vars[5].toString());
//        String day = check(vars[6].toString());
//        String hour = check(vars[7].toString());
//        String minute = check(vars[8].toString());
        String month = vars[5].toString();
        String day = vars[6].toString();
        String hour = vars[7].toString();
        String minute = vars[8].toString();
        String second = check(vars[9].toString());
        if (!super.checkLong(userId)) {
            return;
        }
        String time = String.format("%s-%s-%s %s:%s:%s", year, month, day,hour,minute,second);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = sdf.parse(time);
            System.out.println(date);
            System.out.println("userId:"+userId);
            Map<String, Object> userInfo = service.SSR_GetUserInfo(userId);
            if(userInfo == null){
                log.info("查询用户失败 , 事件触发失败");
            }
            Record record = new Record();
            record.setUserid(userId);
            record.setName((String)userInfo.get("name"));
            record.setStatus(status);
            record.setTime(date);
            int insert = recordMapperExts.insert(record);
            if(insert != 1){
                log.info("插入重复或者失败");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    private String check(String in){
        return in.length() == 1 ? "0" + in : in; 
    }
    
}
