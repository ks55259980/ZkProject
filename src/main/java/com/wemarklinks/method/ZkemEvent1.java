package com.wemarklinks.method;

import com.jacob.com.Variant;

/**
 * 中控事件处理类
 * 
 * @author 陈捷
 *
 */
public class ZkemEvent1 extends ZkemEvent {
    
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
//        String seconde = check(vars[9].toString());
        if (!super.checkLong(userId)) {
            return;
        }
        String time = String.format("%s-%s-%s %s:%s", year, month, day,hour,minute);
        System.out.println(time);
        
        
    }
    
    private String check(String in){
        return in.length() == 1 ? "0" + in : in; 
    }
    
}
