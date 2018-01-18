package com.wemarklinks.method;

import com.jacob.com.Variant;

/**
 * 中控事件处理类
 * 
 * @author 陈捷
 *
 */
public class ZkemEvent2 extends ZkemEvent {
    
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
        System.out.println("Event : OnAttTransactionEx222--->" + Thread.currentThread().getName());
        String userId = vars[0].toString();
        if (!super.checkLong(userId)) {
            return;
        }
        System.out.println(userId);
    }
}
