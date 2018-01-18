package com.wemarklinks.method;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

/**
 * 中控事件处理类
 * @author 陈捷
 *
 */
public class ZkemEvent {
	
	private Map<String,Long> map = new Hashtable<String,Long>();
	
	/**
	 * 当成功连接机器时触发该事件
	 */
	public void OnConnected(){
		System.out.println("事件-->连接设备成功!");
	}
	
	/**
	 * 当断开机器连接时触发该事件
	 */
	public void OnDisConnected(){
		System.out.println("事件-->断开连接!");
	}
	
	/**
	 * 当机器警报的时候触发该事件
	 * 函数原型 OnAlarm(long AlarmType,long EnrollNumber,long Verified)
	 * 函数说明:
	 * AlarmType:警报类型 55为拆机报警，58为错按报警，32为胁迫报警，34为反潜报警
	 * EnrollNumber:当拆机或按错报警或胁迫警报时，该值为0，当为其他胁迫或反潜报警时，该值为用户id号
	 * Verifyed:当为拆机或错按该报警或胁迫按键报警时，该值为0，其余报警该值为1
	 * 
	 * @param vars 
	 */
	public void OnAlarm(Variant[] vars){
		System.out.println("事件-->警报!");
	}
	
	/**
	 * 当机器开门时发生该事件
	 * 以下参数全为返回值
	 * 函数原型:onDoor(long EventType)
	 * EventType:开门类型  4表示门未关好或者门已打开，53表示出门按钮，5表示门已关闭，1表示门被意外打开
	 * 
	 * @param vars
	 */
	public void OnDoor(Variant[] vars){
		System.out.println("事件-->开门事件");
	}
	
	/**
	 * 当验证通过时触发该事件
	 * 以下参数全为返回值
	 * 函数原型:OnAttTransactionEx(BSTR ErollNumber,LONG IsInValid,LONG AttState,LONG VerifyMethod,
	 * 				LONG Year,LONG Month,LONG Day,LONG Hour,LONG Minute,LONG Second,LONG WorkCode)
	 *
	 *EnrollNumber:该用户的UserID
	 *IsInValid:该记录是否为有效记录，1为无效记录，0为有效记录
	 *AttSate:考勤状态  默认0 Check-In, 1 Check-Out, 2 Break-Out, 3 Break-In, 4 OT-In, 5 OT-Out
	 *VerifyMethod:验证方式  0为密码验证，1为指纹验证，2为卡验证 
	 *Year/Month/Day/Hour/Minute/Second:为验证通过的时间
	 *WorkCode:返回验证时WorkCode值，当机器无Workcode功能时，该值返回0
	 *
	 * @param vars
	 */
	public void OnAttTransactionEx(Variant[] vars){
	    System.out.println("Event : OnAttTransactionEx111--->"+Thread.currentThread().getName());
		String userId = vars[0].toString();
		if(!checkLong(userId)){
			return ;
		}
		System.out.println("Event : OnAttTransactionEx111--->"+Thread.currentThread().getName());
	}
	public synchronized boolean checkLong(String userId){
		Long now = new Date().getTime();
		if(map.get(userId) == null){
			map.put(userId, now);
			return true;
		}
		if(now - map.get(userId) < 15000){
			return false;
		}
		map.put(userId, now);
		return true;
	}
	/**
	 * 登记指纹时触发该事件
	 * 以下参数全为返回值
	 * 函数原型:OnEnrollFingerEx(BSTR EnrollNumber,LONG FingerIndex,LONG ActionResult,LONG TemplateLength)
	 * 
	 * EnrollNumber:当前登记指纹的用户ID
	 * FingerIndex:当前指纹的索引号
	 * ActionResult:操作结果，成功为0，失败则返回值大于0
	 * TemplateLength:返回该指纹模板的长度
	 * 
	 * @param vars
	 */
	public void OnEnrollFingerEx(Variant[] vars){
		System.out.println("事件-->登记指纹");
	}
	
	/**
	 * 当机器上指纹头检测到有指纹时触发该消息
	 */
	public void OnFinger(){
		System.out.println("事件-->机器上指纹头检测到有指纹");
	}
	
	/**
	 * 登记用户指纹时，当有指纹按下时触发该消息
	 * 函数原型:OnFingerFeature(LONG Score)
	 * Score:该指纹的质量分数
	 * 
	 * @param vars
	 */
	public void OnFingerFeature(Variant[] vars){
		System.out.println("事件-->登记指纹质量");
	}
	
	/**
	 * 当刷卡时触发该事件
	 * 函数原型:OnHIDNum(LONG CardNumber)
	 * CardNumber:该卡的卡号，卡类型可以是ID卡，HID卡。MIFARE卡需要被作为ID卡使用才会触发该事件
	 * 
	 * @param vars
	 */
	public void OnHIDNum(Variant[] vars){
		System.out.println("事件-->刷卡");
	}
	
	/**
	 * 当成功登记新用户时触发该消息
	 * 函数原型:OnNewUser(LONG EnrollNumber)
	 * EnrollNumber:新登记用户的UserID
	 * 
	 * @param vars
	 */
	public void OnNewUser(Variant[] vars){
		System.out.println("事件-->成功登记新用户");
	}
	
	/**
	 * 当用户验证时触发该消息
	 * 函数原型:OnVerify(LONG UserID)
	 * UserID:当验证成功返回UserID为该用户ID，当卡验证通过时，该值返回卡号，验证失败时返回-1
	 * 
	 * @param vars
	 */
	public void OnVerify(Variant[] vars){
		System.out.println("事件-->验证成功");
	}
	
	/**
	 * 当机器进行写卡操作时触发该事件
	 * 函数原型:OnWriteCard(LONG EnrollNumber,LONG ActionResult,LONG Length)
	 * EnrollNumber:当前需写卡内用户的用户ID
	 * ActionResult:为写卡操作的结果，0为成功，其他值为失败
	 * Length:为写入卡内总的数据大小
	 * 
	 * @param vars
	 */
	public void OnWriteCard(Variant[] vars){
		System.out.println("事件-->机器进行写卡操作");
	}
	
	/**
	 * 当情况MIFARE卡操作时触发该事件
	 * 函数原型:OnEmptyCard(LONG ActionResult)
	 * ActionResult:清卡操作的结果，0为成功，其他值为失败
	 * 
	 * @param vars
	 */
	public void OnEmptyCard(Variant[] vars){
		System.out.println("事件-->清空MIFARE卡操作");
	}
	
	/**
	 * 当机器向SDK发送未知事件时，触发该事件
	 * 函数原型:OnEMData(LONG DataType,LONG DataLen,CHAR* DataBuffer)
	 * DataType:返回事件类型
	 * DataLen:返回整个数据长度
	 * DataBuffer:为实际数据
	 * 
	 * @param vars
	 */
	public void OnEMData(Variant[] vars){
		System.out.println("事件-->机器向SDK发送未知事件");
	}
}
