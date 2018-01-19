package com.wemarklinks.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.DispatchEvents;
import com.jacob.com.SafeArray;
import com.jacob.com.Variant;

public class ZkemSDK {
	
    private static final Logger log = LoggerFactory.getLogger(ZkemSDK.class);

	//初始化中控插件
	public static String componetName="zkemkeeper.ZKEM";
	public static ActiveXComponent zkem = new ActiveXComponent("zkemkeeper.ZKEM");
	public static final int machineNumber = 1;
	
	/**  设置考勤机验证方式 , 有多种方式 ,这里选五种  */
	public static final int FP = 129;      //指纹
	public static final int PIN = 130;    //工号
	public static final int PW = 131;    //密码
	public static final int RF = 132;      //卡
	public static final int FACE = 143;  //人脸
	
	
	/****************************5.1连接机器相关函数*********************************/
	
	/**
	 * 通过IP地址连接机器，和机器建立一个网络连接
	 * 函数原型:VARIANT_BOOL Connect_Net([in] BSTR IPAdd,[in] long Port1)
	 * 
	 * @param ipAddress 机器的IP地址
	 * @param port 连接机器时使用的端口号，默认为4370
	 * @return 连接成功返回true，连接失败返回false
	 */
	public boolean Connect_Net(String ipAddress,int port){
		return zkem.invoke("Connect_Net",new Variant(ipAddress),new Variant(port)).getBoolean();
	}
	
	
	/**
	 * 通过串口连接机器，即RS232或RS485
	 * 函数原型:VARIANT_BOOL Connect_Com([in] long ComPort,[in] long MachineNumber,[in] long BaudRate)
	 * @param comPort 需要连接机器的PC串口号
	 * @param machineNumber 机器号
	 * @param baudRate 波特率
	 * @return 连接成功返回true，连接失败返回false
	 */
	public boolean Connect_Com(int comPort,int machineNumber,int baudRate){
		return zkem.invoke("Connect_Com",new Variant(comPort),new Variant(machineNumber),new Variant(baudRate)).getBoolean();
	}
	
	
	/**
	 * USB连接考勤机
	 * 函数原型:VARIANT_BOOL Connect_USB([in] long MachineNumber)
	 * 
	 * @param machineNumber 机器号(输入参数)
	 * @return 连接成功返回true,连接失败返回false
	 */
	public boolean Connect_USB(int machineNumber){
		ZkemEvent zkemEvent=new ZkemEvent();
		Object zkemObject=zkem.getObject();
		DispatchEvents dispatchEvents=new DispatchEvents((Dispatch) zkemObject,zkemEvent,componetName);
		return zkem.invoke("Connect_USB",new Variant(machineNumber)).getBoolean();
	}
	
	/**
	 * 断开连接的机器，释放相关资源
	 */
	public void Disconnect(){
		zkem.invoke("Disconnect");
	}
	
	/****************************5.2数据管理相关函数*********************************/
	
	/**
	 * 读取考勤记录到PC的内部缓冲区，同ReadAllGLogData
	 * 函数原型:VARIANT_BOOL ReadGeneralLogData([in] long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号 
	 * @return 缓存成功返回true，缓存失败返回false
	 */
	public boolean ReadGeneralLogData(int machineNumber){
		return zkem.invoke("ReadGeneralLogData",new Variant(machineNumber)).getBoolean();
	}
	
	/**
	 * 读取考勤记录到PC内部缓冲区，同ReadAllGLogData
	 * 函数原型:VARIANT_BOOL ReadAllGLogData([in] long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 缓存成功返回true，缓存失败返回false
	 */
	public boolean ReadAllGLogData(int machineNumber){
		return zkem.invoke("ReadAllGLogData",new Variant(machineNumber)).getBoolean();
	}
	
	/**
	 * 从内部缓冲区中逐一读取考勤记录，使用该函数前，可是哟个ReadAllGLogData或ReadGeneralLogData将
	 * 考勤记录从机器读到PC内部缓冲区中。该函数每执行一次，指向考勤记录的指针向下一条记录
	 * 函数原型:VARIANT_BOOL SSR_GetGeneralLogData([in] LONG dwMachineNumber,[out] BSTR* dwEnrollNumber,
	 * [out] LONG* dwVerifyMode,[out] LONG* dwInOutMode,[out] LONG* dwYear,[out] LONG* dwMonth,
	 * [out] LONG* dwDay,[out] LONG* dwHour,[out] LONG* dwMinute,[out] LONG* dwSecond,[out]LONG* dwWorkcode)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:指向BSTR型变量的指针，值接收该考勤记录的用户ID号，最大可支持24位
	 * dwVerifyMode:指向long型变量的指针，其值接收记录的验证方式。0为密码验证，1为指纹验证，2为卡验证
	 * dwInOutMode:指向long型变量的指针，其值接收记录的考勤状态。0:CheckIn,1:CheckOut,2:BreakOut,3:BreakIn,4:OTIn,5:OTOut
	 * dwYear/dwMonth/dwDay/dwHour/dwMinute/dwSecond:其值分别接受考勤记录的日期和时间
	 * dwWorkcode:指向long型变量的指针，其值接收记录的Workcode值
	 * 读取成功返回true，读取失败返回false
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回考勤记录列表
	 */
	public List<Map<String,Object>> SSR_GetGeneralLogData(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		boolean status=this.ReadAllGLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant enrollNumber=new Variant("",true);
			Variant verifyMode=new Variant(0,true);
			Variant inoutMode=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			Variant second=new Variant(0,true);
			Variant workCode=new Variant(0,true);
			
			status=zkem.invoke("SSR_GetGeneralLogData",
					new Variant(machineNumber),
					enrollNumber,
					verifyMode,
					inoutMode,
					year,month,day,hour,minute,second,
					workCode).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("enrollnumber", enrollNumber.getIntRef());
				mapLog.put("verifymode", verifyMode.getIntRef());
				mapLog.put("inoutmode", inoutMode.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());
				mapLog.put("second", second.getIntRef());
				mapLog.put("workcode", workCode.getIntRef());
				
				listLog.add(mapLog);
			}
		}
		return listLog;
	}
	
	/**
	 * 清除机器内所有考勤记录
	 * 函数原型:VARIANT_BOOL ClearGLog([in] dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 清除成功返回true，清除失败返回false
	 */
	public boolean ClearGLog(int machineNumber){
		return zkem.invoke("ClearGLog").getBoolean();
	}
	
	/********************5.22 操作记录相关函数************************/
	
	/**
	 * 读取操作记录到PC的内部缓冲区，同ReadAllSLogData
	 * 函数原型:VARIANT_BOOL ReadSuperLogData([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 读取缓存成功返回true，读取缓存失败返回false
	 */
	public boolean ReadSuperLogData(int machineNumber){
		return zkem.invoke("ReadSuperLogData",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 读取操作记录到PC的内部缓冲区，同ReadSuperLogData
	 * 函数原型:VARIANT_BOOL ReadAllSLogData([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器编号
	 * @return 读取缓存成功返回true，读取缓存失败返回false
	 */
	public boolean ReadAllSLogData(int machineNumber){
		return zkem.invoke("ReadAllSLogData",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 从内部缓冲区中逐一读取操作记录，使用该函数前，可使用ReadAllSLogData或ReadSuperLogData将操作
	 * 记录从机器读取到PC内部缓冲区中，该函数每执行一次，指向操作记录的指针指向下一条记录，同GetSuperLogData2,不同
	 * 的是GetSuperLogData2能获取到精确到秒的操作记录时间
	 * 函数原型:VARIANT_BOOL GetSuperLogData([in]long dwMachineNumber,[out]long* dwTMachineNumber,
	 * [out]long* dwSEnrollNumber,[out]long* Params4,[out]long* Params1,[out]long* Params2,
	 * [out]long* dwMainpulation,[out]long* Params3,[out]long* dwYear,[out]long* dwMonth,
	 * [out]long* dwDay,[out]long* dwHour,[out]long* dwMinute)
	 * 
	 * dwMachineNumber:机器号
	 * dwTMachineNumber:指向long型变量的指针，其值接收操作记录的机器号
	 * dwSEnrollNumber:指向long型变量的指针，其值接收操作记录的管理者ID
	 * Params4:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params1:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params2:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwMainpulation:
	 * 指向long型变量的指针，0开机，1关机，3报警，4进入菜单，5更改设置，6登记指纹，7登记密码
	 * 14创建MF卡，20把卡中数据复制到机器内，22恢复出厂设置，30登记新用户，32胁迫报警，34反潜
	 * Params3:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwYear/dwMonth/ dwDay/dwHour/dwMinute:指向long型变量的指针，其值
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回 List<Map<String,Object>>的操作记录
	 */
	public List<Map<String,Object>> GetSuperLogData(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadSuperLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant dwMachineNumber=new Variant(machineNumber);
			Variant tmachineNumber=new Variant(0,true);
			Variant senrollNumber=new Variant(0,true);
			Variant params4=new Variant(0,true);
			Variant params1=new Variant(0,true);
			Variant params2=new Variant(0,true);
			Variant mainpulAction=new Variant(0,true);
			Variant params3=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			
			status=zkem.invoke(
					"GetSuperLogData",
					dwMachineNumber,
					tmachineNumber,
					senrollNumber,
					params4,params1,params2,mainpulAction,params3,
					year,month,day,hour,minute).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("tmachinenumber", tmachineNumber.getIntRef());
				mapLog.put("senrollnumber", senrollNumber.getIntRef());
				mapLog.put("params4", params4.getIntRef());
				mapLog.put("params1", params1.getIntRef());
				mapLog.put("params2", params2.getIntRef());
				mapLog.put("mainpulaction", mainpulAction.getIntRef());
				mapLog.put("params3", params3.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());
				System.out.println(mapLog);
				listLog.add(mapLog);
			}
		}
		
		return listLog;
	}
	
	
	/**
	 * 从内部缓冲区中逐一读取操作记录，使用该函数前，可使用ReadAllSLogData或ReadSuperLogData将操作
	 * 记录从机器读取到PC内部缓冲区中，该函数每执行一次，指向操作记录的指针指向下一条记录.该函数和GetSuperLogData一样
	 * 
	 * 函数原型:VARIANT_BOOL GetAllSLogData([in]long dwMachineNumber,[out]long* dwTMachineNumber,
	 * [out]long* dwSEnrollNumber,[out]long* Params4,[out]long* Params1,[out]long* Params2,
	 * [out]long* dwMainpulation,[out]long* Params3,[out]long* dwYear,[out]long* dwMonth,
	 * [out]long* dwDay,[out]long* dwHour,[out]long* dwMinute)
	 * 
	 * dwMachineNumber:机器号
	 * dwTMachineNumber:指向long型变量的指针，其值接收操作记录的机器号
	 * dwSEnrollNumber:指向long型变量的指针，其值接收操作记录的管理者ID
	 * Params4:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params1:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params2:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwMainpulation:
	 * 指向long型变量的指针，0开机，1关机，3报警，4进入菜单，5更改设置，6登记指纹，7登记密码
	 * 14创建MF卡，20把卡中数据复制到机器内，22恢复出厂设置，30登记新用户，32胁迫报警，34反潜
	 * Params3:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwYear/dwMonth/ dwDay/dwHour/dwMinute:指向long型变量的指针，其值
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回 List<Map<String,Object>>的操作记录
	 */
	public List<Map<String,Object>> GetAllSLogData(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadAllSLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant dwMachineNumber=new Variant(machineNumber);
			Variant tmachineNumber=new Variant(0,true);
			Variant senrollNumber=new Variant(0,true);
			Variant params4=new Variant(0,true);
			Variant params1=new Variant(0,true);
			Variant params2=new Variant(0,true);
			Variant mainpulAction=new Variant(0,true);
			Variant params3=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			
			status=zkem.invoke(
					"GetAllSLogData",
					dwMachineNumber,
					tmachineNumber,
					senrollNumber,
					params4,params1,params2,mainpulAction,params3,
					year,month,day,hour,minute).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("tmachinenumber", tmachineNumber.getIntRef());
				mapLog.put("senrollnumber", senrollNumber.getIntRef());
				mapLog.put("params4", params4.getIntRef());
				mapLog.put("params1", params1.getIntRef());
				mapLog.put("params2", params2.getIntRef());
				mapLog.put("mainpulaction", mainpulAction.getIntRef());
				mapLog.put("params3", params3.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());

				listLog.add(mapLog);
			}
		}
		
		return listLog;
	}
	
	
	/**
	 * 清除机器内所有操作记录
	 * 函数原型:VARIANT_BOOL ClearSLog([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 清除成功返回true，清除失败返回false
	 */
	public boolean ClearSLog(int machineNumber){
		return zkem.invoke("ClearSLog").getBoolean();
	}
	
	
	/**
	 * 从内部缓冲区中逐一读取操作记录，使用该函数前，可使用ReadAllSLogData或ReadSuperLogData将操作
	 * 记录从机器读取到PC内部缓冲区中，该函数每执行一次，指向操作记录的指针指向下一条记录，同GetSuperLogData2,不同
	 * 函数原型:VARIANT_BOOL GetSuperLogData2([in]long dwMachineNumber,[out]long* dwTMachineNumber,
	 * [out]long* dwSEnrollNumber,[out]long* Params4,[out]long* Params1,[out]long* Params2,
	 * [out]long* dwMainpulation,[out]long* Params3,[out]long* dwYear,[out]long* dwMonth,
	 * [out]long* dwDay,[out]long* dwHour,[out]long* dwMinute,[out]long* dwSecs)
	 * 
	 * dwMachineNumber:机器号
	 * dwTMachineNumber:指向long型变量的指针，其值接收操作记录的机器号
	 * dwSEnrollNumber:指向long型变量的指针，其值接收操作记录的管理者ID
	 * Params4:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params1:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * Params2:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwMainpulation:
	 * 指向long型变量的指针，0开机，1关机，3报警，4进入菜单，5更改设置，6登记指纹，7登记密码
	 * 14创建MF卡，20把卡中数据复制到机器内，22恢复出厂设置，30登记新用户，32胁迫报警，34反潜
	 * Params3:指向long型变量的指针，其值含义视dwManipulation不同而不同
	 * dwYear/dwMonth/ dwDay/dwHour/dwMinute:指向long型变量的指针，其值
	 * 
	 * @param machineNumber 机器号
	 * @return 读取成功返回 List<Map<String,Object>>的操作记录
	 */
	public List<Map<String,Object>> GetSuperLogData2(int machineNumber){
		List<Map<String,Object>> listLog=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadSuperLogData(machineNumber);
		
		if(status==false){
			return null;
		}
		
		while(status){
			Variant dwMachineNumber=new Variant(machineNumber);
			Variant tmachineNumber=new Variant(0,true);
			Variant senrollNumber=new Variant(0,true);
			Variant params4=new Variant(0,true);
			Variant params1=new Variant(0,true);
			Variant params2=new Variant(0,true);
			Variant mainpulAction=new Variant(0,true);
			Variant params3=new Variant(0,true);
			Variant year=new Variant(0,true);
			Variant month=new Variant(0,true);
			Variant day=new Variant(0,true);
			Variant hour=new Variant(0,true);
			Variant minute=new Variant(0,true);
			Variant second=new Variant(0,true);
			
			status=zkem.invoke(
					"GetSuperLogData2",
					dwMachineNumber,
					tmachineNumber,
					senrollNumber,
					params4,params1,params2,mainpulAction,params3,
					year,month,day,hour,minute).getBoolean();
			
			if(status==true){
				Map<String,Object> mapLog=new HashMap<String,Object>();
				mapLog.put("tmachinenumber", tmachineNumber.getIntRef());
				mapLog.put("senrollnumber", senrollNumber.getIntRef());
				mapLog.put("params4", params4.getIntRef());
				mapLog.put("params1", params1.getIntRef());
				mapLog.put("params2", params2.getIntRef());
				mapLog.put("mainpulaction", mainpulAction.getIntRef());
				mapLog.put("params3", params3.getIntRef());
				mapLog.put("year", year.getIntRef());
				mapLog.put("month", month.getIntRef());
				mapLog.put("day", day.getIntRef());
				mapLog.put("hour", hour.getIntRef());
				mapLog.put("minute", minute.getIntRef());
				mapLog.put("second", second.getIntRef());
				
				listLog.add(mapLog);
			}
		}
		
		return listLog;
	}
	
	
	
	/************************5.2.3用户信息相关函数**************************/
	
	
	/**
	 * 读取所有用户信息到PC内存中，包括用户编号，密码，姓名，卡号等(指纹模板除外)。该函数执行完成
	 * 之后，可叼哟个GetAllUserID获取用户信息
	 * 函数原型:VARIANT_BOOL ReadAllUserID([in]long dwMachineNumber)
	 * 
	 * @param machineNumber 机器号
	 * @return 缓存成功返回true，缓存失败返回false
	 */
	public boolean ReadAllUserID(int machineNumber){
		return zkem.invoke("ReadAllUserID",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 设置用户是否可用
	 * 函数原型:VARIANT_BOOL SSR_EnableUser([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in] VARIANT_BOOL bFlag)
	 * 
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param flag 用户启用标志，true为启用，false为禁用
	 * @return 成功设置用户返回true，失败返回false
	 */
	public boolean SSR_EnableUser(int machineNumber,String enrollNumber,boolean flag){
		return zkem.invoke("SSR_EnableUser",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(flag)).getBoolean();
	}
	
	
	/**
	 * 上传用户验证方式或组验证方式，只有多种验证方式的机器可支持该函数
	 * 函数原型:VARIANT_BOOL SetUserInfoEx([in]LONG dwMachineNumber,[in]LONG dwEnrollNumber,
	 * [in]LONG VerifyStyle,[in] BYTE* Reserved)
	 * 彩屏门禁指纹机的值为 :128(FP/PW/RF/FACE), 129(FP), 130(PIN), 131(PW), 132(RF), 133(FP/PW),
     * 134(FP/RF), 135(PW/RF), 136(PIN&FP), 137(FP&PW), 138(FP&RF), 139(PW&RF), 140(FP&PW&RF), 141(PIN&FP&PW),
     * 142(FP&RF/PIN),143(FACE),144(FACE&FP),145(FACE&PW),146(FACE&RF),147(FACE&FP&RF),148(F ACE&FP&PW).

	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * VerifyStyle:验证方式
	 * Reserved:保留参数，暂无意义
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param verifyStyle 验证方式,0表示组验证方式，其余参考文档
	 * @return 设置成返回true，设置失败返回false
	 */
	public boolean SetUserInfoEx(int machineNumber,int enrollNumber,int verifyStyle){
		return zkem.invoke("SetUserInfoEx",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(verifyStyle),
				new Variant(null)).getBoolean();
	}
	
	
	/**
	 * 获取用户验证方式，只有具有多种验证方式的机器可支持该函数
	 * 函数原型:VARIANT_BOOL GetUserInfoEx([in]LONG dwMachineNumber,[in]LONG dwEnrollNumber,
	 * [out]LONG* VerifyStyle,[out] BYTE* Reserved)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @return 读取成功返回该用户的验证方式Map<String,Object>
	 */
	public Integer GetUserInfoEx(int machineNumber,int enrollNumber){
		Integer verify=null;
		Variant verifyStyle=new Variant(0,true);
		boolean status=zkem.invoke("GetUserInfoEx",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				verifyStyle,new Variant(null)).getBoolean();
		
		if(status==true){
			verify=verifyStyle.getIntRef();
		}
		
		return verify;
	}
	
	
	/**
	 * 删除指定用户设置的多种验证方式，只有多种验证方式的机器可以支持该函数
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @return 删除用户验证成功返回true，删除用户验证失败返回false
	 */
	public boolean DeleteUserInfoEx(int machineNumber,int enrollNumber){
		return zkem.invoke("DeleteUserInfoEx",new Variant(machineNumber),new Variant(enrollNumber)).getBoolean();
	}
	
	
	/**
	 * 获取所有用户信息 ，在执行该函数之前，可用ReadAllUserID读取所有用户信息到PC内存，SSR_GetAllUserInfo每
	 * 执行一次，指向用户信息指针移动到下一条记录，当读完所有的用户信息后，函数返回false
	 * 函数原型:VARIANT_BOOL SSR_GetAllUserInfo([in]LONG dwMachineNumber,[out]BSTR* dwEnrollNumber,
	 * [out] BSTR* Name,[out]BSTR* Password,[out] LONG* Privilege,[out] VARIANT_BOOL* Enabled)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * Name:用户姓名
	 * Password:用户密码
	 * Privilege:用户权限，3管理员，0普通用户
	 * Enabled:用户启用标志，1为启用，0为禁用
	 * 
	 * @param inMachineNumber 机器编号
	 * @return List<Map<String,Object>>用户信息,读取失败返回null
	 */
	public List<Map<String,Object>> SSR_GetAllUserInfo(int inMachineNumber){
		List<Map<String,Object>> listUser=new ArrayList<Map<String,Object>>();
		
		boolean status=this.ReadAllUserID(inMachineNumber);
		
		if(status==false){
			return null;
		}
		
		Variant machineNumber=new Variant(1,true);
		Variant enrollNumber=new Variant("",true);
		Variant name=new Variant("",true);
		Variant password=new Variant("",true);
		Variant privilege=new Variant(0,true);
		Variant enable=new Variant(false,true);
		
		while(status){
			status=zkem.invoke(
					"SSR_GetAllUserInfo",
					machineNumber,
					enrollNumber,
					name,
					password,
					privilege,
					enable).getBoolean();
			
			//如果没有用户编号则跳过
//			String strEnrollnumber=enrollNumber.getStringRef();
//			if(strEnrollnumber==null || strEnrollnumber.trim().length()==0)
//				continue;
			
//			//如果没有名字则跳过
//			if(strName==null || strName.trim().length()==0)
//				continue;
			
			Map<String,Object> userMap=new HashMap<String,Object>();
			userMap.put("userId", enrollNumber.getStringRef());
			userMap.put("name", cutString(name));
			userMap.put("password", password.getStringRef());
			userMap.put("privilege", privilege.getIntRef());
			userMap.put("enable", enable.getBooleanRef());
			listUser.add(userMap);
		}
		return listUser;
	}
	
	
	/**
	 * 获取指定用户的信息
	 * 函数原型:VARIANT_BOOL SSR_GetUserInfo([in] LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [out]BSTR* Name,[out]BSTR* Password,[out]LONG* Privilege,[out]VARIANT_BOOL* Enabled)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * Name:用户姓名
	 * Password:用户密码
	 * Privilege:用户权限,3管理员，0普通用户
	 * Enabled:用户启用标志，1为启用，0为禁用
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号 
	 * @return 返回Map<String,Object>的用户信息
	 */
	public Map<String,Object> SSR_GetUserInfo(int machineNumber,String enrollNumber){
		Variant name=new Variant("",true);
		Variant password=new Variant("",true);
		Variant privilege=new Variant(0,true);
		Variant enable=new Variant(false,true);
		
		boolean status=zkem.invoke("SSR_GetUserInfo",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				name,password,privilege,enable).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> mapUser=new HashMap<String,Object>();
		mapUser.put("name", cutString(name));
		mapUser.put("password", password.getStringRef());
		mapUser.put("privilege", privilege.getIntRef());
		mapUser.put("enable", enable.getBooleanRef());
		mapUser.put("userId", enrollNumber);
		return mapUser;
	}
	
	private String cutString(Variant name){
        String nameStr = name.getStringRef();
        char[] n = nameStr.toCharArray();
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ; i < n.length ; i++){
            if(n[i] == 0){
                break;
            }
            sb.append(n[i]);
        }
        nameStr = sb.toString();
        return nameStr;
	}
	
	/**
	 * 设置指定用户的用户信息，若机内没有该用户，则会创建该用户
	 * 函数原型:VARIANT_BOOL SSR_SetUserInfo([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]BSTR Name,[in]BSTR Password,[in]LONG Privilege,[in]VARIANT_BOOL Enabled)
	 * 
	 * dwMachineNumber:机器号
	 * dwEnrollNumber:用户号
	 * Name:用户姓名
	 * Password:用户密码
	 * Privilege:用户权限，3为管理员，0为普通用户
	 * Enabled:用户启用标志，1为启用，0为禁用
	 * 
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SSR_SetUserInfo(int machineNumber,String enrollNumber,String name,
										String password,int privilege,boolean enable){
		return zkem.invoke("SSR_SetUserInfo",
							new Variant(machineNumber),
							new Variant(enrollNumber),
							new Variant(name),
							new Variant(password),
							new Variant(privilege),
							new Variant(enable)).getBoolean();
	}
	
	public Map<String, Object> GetUserFaceStr(int machineNumber,String enrollNumber){
	    Variant faceIndex = new Variant(50);
	    StringBuffer str = new StringBuffer();
	    
	    SafeArray array = new SafeArray(Variant.VariantByref,35000);
	    Variant tmpData = new Variant(array,true);
//	    tmpData.putSafeArray(array);
	    Variant tmpLenth = new Variant(0,true);
	    boolean boolean1 = zkem.invoke("GetUserFace",
	            new Variant(machineNumber),
	            new Variant(enrollNumber),
	            faceIndex, tmpData, tmpLenth).getBoolean();
	    log.info("获取人脸模板:{}",boolean1);
//	    if(boolean1 == false){
//	        return null;
//	    }
	    Map<String,Object> map = new HashMap<String, Object>();
	    map.put("userId", enrollNumber);
	    map.put("tmpData",tmpData.toString());
	    map.put("tmpLenth", tmpLenth.getIntRef());
	    return map;
	}
	
	
	/*********************5.2.4登记数据(同时包括用户信息和指纹)**************************/
	
	
	/**
	 * 删除登记数据
	 * 函数原型:VARIANT_BOOL SSR_DeleteEnrollData([in]LONG dwMachineNumber,
	 * 						[in]BSTR dwEnrollNumber,[in]LONG dwBackupNumber)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param backupNumber 指纹索引，一般范围为0-9，同时会查询该用户是否还有其他指纹和密码，如果没有，则删除该用户。
		当前为10代表删除的是密码，同时会查询该用户是否有指纹数据，如果没有，则删除该用户。为11是代表删除该用户所有指纹数据，
		当前为12代表删除该用户(包括所有指纹和卡号、密码数据)
	 * 
	 * @return 删除成功返回true，删除失败返回false
	 */ 
	public boolean SSR_DeleteEnrollData(int machineNumber,String enrollNumber,int backupNumber){
		return zkem.invoke("SSR_DeleteEnrollData",new Variant(machineNumber),
							new Variant(enrollNumber),new Variant(backupNumber)).getBoolean();
	}
	
	
	/**
	 * 删除登记数据，和SSR_DeleteEnrollData不同的是删除所有指纹数据可用参数13实现，该函数具有更高效率
	 * 函数原型:VARIANT_BOOL SSR_DeleteEnrollDataExt([in]LONG machineNumber,[in]BSTR enrollNumber,[in]LONG backupNumber)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param backupNumber 指纹索引，一般范围为0-9，同时会查询该用户是否还有其他指纹和密码，如果没有，则删除该用户。
		当前为10代表删除的是密码，同时会查询该用户是否有指纹数据，如果没有，则删除该用户。为11和13是代表删除该用户所有指纹数据，
		当前为12代表删除该用户(包括所有指纹和卡号、密码数据)
	 * @return 删除成功返回true，删除失败返回false
	 */
	public boolean SSR_DeleteEnrollDataExt(int machineNumber,String enrollNumber,int backupNumber){
		return zkem.invoke("SSR_DeleteEnrollDataExt",new Variant(machineNumber),
				new Variant(enrollNumber),new Variant(backupNumber)).getBoolean();
	}
	
	
	
	/*************************5.2.5指纹模板相关函数****************************/
	
	
	/**
	 * 读取机器内所有指纹模板到PC内存，该函数一次性将所有指纹读到内存
	 * @param machineNumber 机器号
	 * @return 读取成功返回true，读取失败返回false
	 */
	public boolean ReadAllTemplate(int machineNumber){
		return zkem.invoke("ReadAllTemplate",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 以二进制方式获取用户指纹模板，和SSR_GetUserTmpStr不同的仅是模板格式不同而已
	 * 函数原型:VARIANT_BOOL SSR_GetUserTmp([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[out]BYTE* TmpData,[out]LONG* TmpLength)
	 * TmpData:该参数返回指纹模板数据
	 * TmpLength:该参数返回指纹模板数据长度
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引，一般范围为0-9
	 * 
	 * @return Map<String,Object>的用户指纹模板,读取失败返回null
	 */
	public Map<String,Object> SSR_GetUserTmp(int machineNumber,int enrollNumber,int fingerIndex){
		Variant tmpdata=new Variant("",true);
		Variant tmplength=new Variant(0,true);
		boolean status=zkem.invoke("SSR_GetUserTmp",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> userTmp=new HashMap<String,Object>();
		userTmp.put("tmpdata", tmpdata.getByteRef());
		userTmp.put("tmplength", tmplength.getIntRef());
		
		return userTmp;
	}
	
	
	/**
	 * 以字符串方式获取用户指纹模板
	 * 函数原型:VARIANT_BOOL SSR_GetUserTmpStr([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[out]BYTE* TmpData,[out]LONG* TmpLength)
	 * TmpData:该参数返回指纹模板数据
	 * TmpLength:该参数返回指纹模板数据长度
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引，一般范围为0-9
	 * 
	 * @return Map<String,Object>的用户指纹模板,读取失败返回null
	 */
	public Map<String,Object> SSR_GetUserTmpStr(int machineNumber,int enrollNumber,int fingerIndex){
		Variant tmpdata=new Variant("",true);
		Variant tmplength=new Variant(0,true);
		boolean status=zkem.invoke("SSR_GetUserTmpStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> userTmp=new HashMap<String,Object>();
		userTmp.put("tmpdata", tmpdata.getStringRef());
		userTmp.put("tmplength", tmplength.getIntRef());
		
		return userTmp;
	}
	
	
	/**
	 * 以二进制方式上传用户指纹模板，和SSR_SetUserTmpStr不同的是指纹模板格式不同而已
	 * 函数原型:VARIANT_BOOL SSR_SetUserTmp([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[in]BYTE* TmpData)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 指纹模板
	 * @return 上传成功返回true，上传失败返回false
	 */
	public boolean SSR_SetUserTmp(int machineNumber,String enrollNumber,int fingerIndex,byte tmpData){
		return zkem.invoke("SSR_SetUserTmp",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 以字符串方式上传用户指纹模板
	 * 函数原型:VARIANT_BOOL SSR_SetUserTmpStr([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,
	 * [in]LONG dwFingerIndex,[in]BSTR TmpData)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 指纹模板
	 * @return 上传成功返回true，上传失败返回false
	 */
	public boolean SSR_SetUserTmpStr(int machineNumber,String enrollNumber,int fingerIndex,String tmpData){
		return zkem.invoke("SSR_SetUserTmpStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 删除用户指纹模板
	 * 函数原型:VARIANT_BOOL SSR_DelUserTmp([in]LONG dwMachineNumber,[in]BSTR dwEnrollNumber,[in]LONG dwFingerIndex)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @return 删除成功返回true，删除失败返回false
	 * 
	 */
	public boolean SSR_DelUserTmp(int machineNumber,String enrollNumber,int fingerIndex){
		return zkem.invoke("SSR_DelUserTmp",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex)).getBoolean();
	}
	
	
	/**
	 * 上传用户指纹模板，为SSR_SetUserTmp的加强版
	 * 
	 * @param machineNumber 机器号
	 * @param isDeleted 删除标准，即上传时已存在该用户的指定索引号的指纹是否覆盖原指纹，1为覆盖，0为不覆盖
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 指纹模板
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SSR_SetUserTmpExt(int machineNumber,int isDeleted,String enrollNumber,int fingerIndex,byte tmpData){
		return zkem.invoke("SSR_SetUserTmpExt",
				new Variant(machineNumber),
				new Variant(isDeleted),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 删除指定用户的指纹模板，和DelUserTmp的区别在于前者可以支持24位用户号
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @return 删除成功返回true，删除失败返回false
	 */
	public boolean SSR_DelUserTmpExt(int machineNumber,String enrollNumber,int fingerIndex){
		return zkem.invoke("SSR_DelUserTmpExt",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex)).getBoolean();
	}
	
	
	/**
	 * 以二进制方式上传用户普通指纹模板或者胁迫指纹模板，和SetUserTmpExStr不同的仅是指纹模板格式不同而已
	 * 注意:机器上必须已存在该用户或者将用户信息同时上传，相同用户的相同索引号模板如果已经登记，则覆盖。
	 * 注:要求机器固件支持胁迫指纹功能
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号 一般为0-9
	 * @param tmpData 
	 * @return false
	 * 
	 * @deprecated 该函数的接口文档有问题
	 */
	public boolean SetUserTmpEx(int machineNumber,String enrollNumber,int fingerIndex,byte tmpData){
		/********该函数的接口文档有问题*******/
		return false;
	}
	
	
	/**
	 * 以字符串形式上传用户普通指纹模板或者胁迫指纹模板，和SetUserTmpEx不同的仅是指纹模板格式不同而已
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号一般为0-9
	 * @param flag 标识指纹模板是否有效或为胁迫指纹，0表示指纹模板无效，1表示指纹模板有效，3表示为胁迫指纹
	 * @param tmpData 指纹模板数据
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetUserTmpExStr(int machineNumber,String enrollNumber,int fingerIndex,int flag,String tmpData){
		return zkem.invoke("SetUserTmpExStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				new Variant(flag),
				new Variant(tmpData)).getBoolean();
	}
	
	
	/**
	 * 以二进制方式下载用户普通指纹模板或者胁迫指纹模板，和GetUserTmpExStr不同的仅是指纹模板格式不同而已
	 * 注：要求机器固件支持胁迫指纹功能(固件内部版本号Ver6.60及以上)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号一般为0-9
	 * @param flag 标识指纹模板是否有效或者是否为胁迫指纹，0表示指纹模板无效，1表示指纹模板有效，3表示胁迫指纹
	 * @param tmpData 指纹模板数据
	 * @param tmpLength 指纹模板长度
	 * @return Map<String,Object>格式的用户指纹模板数据
	 */
	@Deprecated  //类型不匹配
	public Map<String,Object> GetUserTmpEx(int machineNumber,String enrollNumber,
								int fingerIndex,int flag,byte tmpData,int tmpLength){
		
		Variant v_flag=new Variant(0,true);
		Variant tmpdata=new Variant(0,true);
		Variant tmplength=new Variant(0,true);
		
		boolean status=zkem.invoke("GetUserTmpEx",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				v_flag,
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> mapUsertmp=new HashMap<String,Object>();
		mapUsertmp.put("flag", v_flag.getIntRef());
		mapUsertmp.put("tmpdata", tmpdata.getByteRef());
		mapUsertmp.put("tmplength", tmplength.getIntRef());
		
		return mapUsertmp;
	}
	
	
	/**
	 * 以字符串方式下载用户普通指纹模板或者胁迫指纹模板
	 * 注：要求机器固件支持胁迫指纹功能(固件内部版本号Ver6.60及以上)
	 * 
	 * @param machineNumber 机器号
	 * @param enrollNumber 用户号
	 * @param fingerIndex 指纹索引号一般为0-9
	 * @param flag 标识指纹模板是否有效或者是否为胁迫指纹，0表示指纹模板无效，1表示指纹模板有效，3表示胁迫指纹
	 * @param tmpData 指纹模板数据
	 * @param tmpLength 指纹模板长度
	 * @return Map<String,Object>格式的用户指纹模板数据
	 */
	public Map<String,Object> GetUserTmpExStr(int machineNumber,String enrollNumber,
								int fingerIndex){
		
		Variant v_flag=new Variant(0,true);
		Variant tmpdata=new Variant("",true);
		Variant tmplength=new Variant(0,true);
		
		boolean status=zkem.invoke("GetUserTmpExStr",
				new Variant(machineNumber),
				new Variant(enrollNumber),
				new Variant(fingerIndex),
				v_flag,
				tmpdata,
				tmplength).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> mapUsertmp=new HashMap<String,Object>();
		mapUsertmp.put("flag", v_flag.getIntRef());
		mapUsertmp.put("tmpdata", tmpdata.getStringRef());
		mapUsertmp.put("tmplength", tmplength.getIntRef());
		
		return mapUsertmp;
	}
	
	
	
	/*********************节假日相关函数************************/
	
	/**
	 * 根据节假日编号获取机器上的节假日设置
	 * 函数原型:VARIANT_BOOL SSR_GetHoliday([in]LONG dwMachineNumber,[in]LONG HolidayID,
	 * [out]LONG* BeginMonth,[out]LONG* BeginDay,[out]LONG* EndMonth,[out]LONG EndDay,[out]LONG TimeZoneID)
	 * 
	 * dwMachineNumber:机器号
	 * HolidayID:节假日编号
	 * BeginMonth/BeginDay/EndMonth/EndDay:该参数接收节假日的开始日期结束日期
	 * TimeZoneID:该参数接受节假日的时间段编号
	 * 
	 * @param machineNumber 机器号
	 * @param holidayID 节假日编号
	 * @return Map<String,Object>节假日信息
	 */
	public Map<String,Object> SSR_GetHoliday(int machineNumber,int holidayID){
		Variant beginMonth=new Variant(0,true);
		Variant beginDay=new Variant(0,true);
		Variant endMonth=new Variant(0,true);
		Variant endDay=new Variant(0,true);
		
		boolean status=zkem.invoke("SSR_GetHoliday",
				new Variant(machineNumber),
				new Variant(holidayID),
				beginMonth,beginDay,
				endMonth,endDay).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> mapHoliday=new HashMap<String,Object>();
		mapHoliday.put("beginmonth", beginMonth);
		mapHoliday.put("beginday", beginDay);
		mapHoliday.put("endmonth", endMonth);
		mapHoliday.put("endday", endDay);
		
		return mapHoliday;
	}
	
	
	/**
	 * 设置节假日
	 * 函数原型:VARIANT_BOOL SSR_SetHoliday([in]LONG dwMachineNumber,[in]LONG HolidayID,
	 * [in]LONG BeginMonth,[in]LONG BeginDay,[in]LONG EndMonth,[in]LONG EndDay,[in]LONG TimeZoneID)
	 * 
	 * 
	 * @param machineNumber 机器号
	 * @param holidayID 节假日编号
	 * @param beginMonth 节假日开始时间(月)
	 * @param beginDay 节假日开始时间(日)
	 * @param endMonth 节假日结束时间(月)
	 * @param endDay 节假日结束时间(日)
	 * @param timeZoneID 节假日使用的时间段编号
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SSR_SetHoliday(int machineNumber,int holidayID,int beginMonth,
							int beginDay,int endMonth,int endDay,int timeZoneID){
		return zkem.invoke("SSR_SetHoliday",
				new Variant(machineNumber),
				new Variant(holidayID),
				new Variant(beginMonth),
				new Variant(beginDay),
				new Variant(endMonth),
				new Variant(endDay),
				new Variant(timeZoneID)).getBoolean();
	}
	
	
	/*************************夏时令相关函数****************************/
	
	
	/**
	 * 设置是否使用夏时令功能，以及夏时令开始时间和结束时间
	 * 函数原型:VARIANT_BOOL SetDaylight([in]LONG dwMachineNumber,[in]LONG Support
	 * [in]BSTR BeginTime,[in]BSTR EndTime)
	 * 
	 * @param machineNumber 机器号
	 * @param support 是否使用夏时令功能，1为启用，0为禁用
	 * @param beginTime 夏时令开始时间，日期格式为  mmdd hh:mm
	 * @param endTime 夏时令结束时间，日期格式为  mmdd hh:mm
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetDaylight(int machineNumber,int support,String beginTime,String endTime){
		return zkem.invoke("SetDaylight",
				new Variant(machineNumber),
				new Variant(support),
				new Variant(beginTime),
				new Variant(endTime)).getBoolean();
	}
	
	
	/**
	 * 获取机器的夏时令
	 * 函数原型:VARIANT_BOOL GetDaylight([in] dwMachineNumber,[out]LONG* Support,
	 * [out]BSTR* BeginTime,[out]BSTR* EndTime)
	 * 
	 * @param machineNumber 机器号
	 * @return Map<String,Object>的夏时令信息
	 */
	public Map<String,Object> GetDaylight(int machineNumber){
		Variant support=new Variant(0,true);
		Variant beginTime=new Variant("",true);
		Variant endTime=new Variant("",true);
		boolean status=zkem.invoke("GetDaylight",
						new Variant(machineNumber),
						support,beginTime,endTime).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> dayLight=new HashMap<String,Object>();
		dayLight.put("support", support.getIntRef());
		dayLight.put("beginTime", beginTime.getStringRef());
		dayLight.put("endTime", endTime.getStringRef());
		
		return dayLight;
	}
	
	
	
	
	/************************指纹模板转换相关函数*************************/
	
	
	/**
	 * 计算指定指纹模板长度
	 * 函数原型:VARIANT_BOOL GetFPTempLength([in]BYTE* enrollData,[out]LONG* Len)
	 * 
	 * Len:指纹模板的长度
	 * 
	 * @param enrollData 指向指纹模板的指针
	 * @return 成功返回指纹模板长度，失败返回0
	 */
	public int GetFPTempLength(byte enrollData){
		Variant len=new Variant(0,true);
		boolean status=zkem.invoke("GetFPTempLength",new Variant(enrollData),len).getBoolean();
		if(status==false){
			return 0;
		}
		
		return len.getIntRef();
	}
	
	
	/**
	 * 计算指定指纹模板长度
	 * 函数原型:VARIANT_BOOL GetFPTempLength([in]BSTR* enrollData,[out]LONG* Len)
	 * 
	 * Len:指纹模板的长度
	 * 
	 * @param enrollData 指向指纹模板的指针
	 * @return 成功返回指纹模板长度，失败返回0
	 */
	public int GetFPTempLengthStr(String enrollData){
		Variant len=new Variant(0,true);
		boolean status=zkem.invoke("GetFPTempLengthStr",new Variant(enrollData),len).getBoolean();
		if(status==false){
			return 0;
		}
		
		return len.getIntRef();
	}
	
	
	/**
	 * 脱机指纹模板转换为BIOKEY指纹模板，和FPTempConvertStr的区别在于格式不同而已
	 * 函数原型:VARIANT_BOOL FPTempConvert([in]BYTE* TmpData1,[out]BYTE* TmpData2,[out]LONG* Size)
	 * TmpData2:返回转换后的BIOKEY指纹模板
	 * Size:返回转换后的BIOKEY指纹模板的大小
	 * 
	 * @param tmpData1 要转换的脱机指纹模板
	 * @return 转换成功返回Map<String,Object>的指纹模板信息，转换失败返回null
	 */
	public Map<String,Object> FPTempConvert(byte tmpData1){
		Variant tmpData2=new Variant("",true);
		Variant size=new Variant(0,true);
		boolean status=zkem.invoke("FPTempConvert",new Variant(tmpData1),tmpData2,size).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> fpTemp=new HashMap<String,Object>();
		fpTemp.put("tmpdata2", tmpData2.getByteRef());
		fpTemp.put("size", size.getIntRef());
		
		return fpTemp;
	}

	
	/**
	 * 脱机指纹模板转换为BIOKEY指纹模板，和FPTempConvertStr的区别在于格式不同而已
	 * 函数原型:VARIANT_BOOL FPTempConvertStr([in]BSTR* TmpData1,[out]BSTR* TmpData2,[out]LONG* Size)
	 * TmpData2:返回转换后的BIOKEY指纹模板
	 * Size:返回转换后的BIOKEY指纹模板的大小
	 * 
	 * @param tmpData1 要转换的脱机指纹模板
	 * @return 转换成功返回Map<String,Object>的指纹模板信息，转换失败返回null
	 */
	public Map<String,Object> FPTempConvertStr(String tmpData1){
		Variant tmpData2=new Variant("",true);
		Variant size=new Variant(0,true);
		boolean status=zkem.invoke("FPTempConvertStr",new Variant(tmpData1),tmpData2,size).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> fpTemp=new HashMap<String,Object>();
		fpTemp.put("tmpdata2", tmpData2.getByteRef());
		fpTemp.put("size", size.getIntRef());
		
		return fpTemp;
	}
	
	
	/**
	 * 将BIOKEY指纹模板转换为脱机指纹模板，和FPTempConvertNewStr的区别在与数据格式不同而已
	 * 函数原型:VARIANT_BOOL FPTempConvertNew([in]BYTE* TmpData1,[out]BYTE* TmpData2,[out]LONG* Size)
	 * TmpData2:返回转换后的脱机指纹模板
	 * Size:脱机指纹模板的大小
	 * 
	 * @param tmpData1 要转换的脱机指纹模板
	 * @return Map<String,Object>的指纹模板数据
	 */
	public Map<String,Object> FPTempConvertNew(byte tmpData1){
		Variant tmpData2=new Variant("",true);
		Variant size=new Variant(0,true);
		boolean status=zkem.invoke("FPTempConvertNew",new Variant(tmpData1),tmpData2,size).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> fpTemp=new HashMap<String,Object>();
		fpTemp.put("tmpData2", tmpData2.getByteRef());
		fpTemp.put("size", size.getIntRef());
		
		return fpTemp;
	}

	
	/**
	 * 将BIOKEY指纹模板转换为脱机指纹模板
	 * 函数原型:VARIANT_BOOL FPTempConvertNewStr([in]BSTR* TmpData1,[out]BSTR* TmpData2,[out]LONG* Size)
	 * TmpData2:返回转换后的脱机指纹模板
	 * Size:脱机指纹模板的大小
	 * 
	 * @param tmpData1 要转换的脱机指纹模板
	 * @return Map<String,Object>的指纹模板数据
	 */
	public Map<String,Object> FPTempConvertNewStr(String tmpData1){
		Variant tmpData2=new Variant("",true);
		Variant size=new Variant(0,true);
		boolean status=zkem.invoke("FPTempConvertNew",new Variant(tmpData1),tmpData2,size).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> fpTemp=new HashMap<String,Object>();
		fpTemp.put("tmpData2", tmpData2.getStringRef());
		fpTemp.put("size", size.getIntRef());
		
		return fpTemp;
	}
	
	
	
	/***********************系统数据管理相关函数****************************/
	
	
	/**
	 * 清除机器内所有的数据
	 * @param machineNumber 机器号
	 * @return 清除成功返回true，清除失败返回false
	 */
	public boolean ClearKeeperData(int machineNumber){
		return zkem.invoke("ClearKeeperData",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 清除机器内由DataFlag指定的记录
	 * 
	 * @param machineNumber 机器号
	 * @param dataFlag 该参数指定需清除的记录类型，范围为1-5，具体含义如下：1考勤记录，2指纹模板数据，3无，4操作记录，5用户信息及指纹模板
	 * @return 清除成功返回true，清除失败返回false
	 */
	public boolean ClearData(int machineNumber,int dataFlag){
		return zkem.invoke("ClearData",new Variant(machineNumber),new Variant(dataFlag)).getBoolean();
	}
	
	
	/**
	 * 从机器获取指定数据文件
	 * 
	 * @param machineNumber 机器号
	 * @param dataFlag 需要获取的数据文件类型：1考勤记录数据文件，2指纹模板数据文件，3无，4操作记录数据文件，5用户信息数据文件，6短消息数据文件
						7短消息与用户关系的数据文件，8扩展用户信息数据文件，9Workcode信息数据文件
	 * @param fileName 接收获取到的数据文件存储文件名
	 * @return 获取成功返回true，获取失败返回false
	 */
	public boolean GetDataFile(int machineNumber,int dataFlag,String fileName){
		return zkem.invoke("GetDataFile",new Variant(machineNumber),
					new Variant(dataFlag),new Variant(fileName)).getBoolean();
	}
	
	
	/**
	 * 发送文件到机器，一般发送到/mnt/mtdblock/下，彩屏机如传的是用户照片或者宣传图片，
	 * 需命名为以下格式：图片会自动被转移到相应的目录下
	 * 宣传图片命名方式："ad_"为前缀，后加数字，范围为1-20，后缀为.jpg,如ad_4.jpg
	 * 用户照片的命名方式:"用户ID"+".jpg"，如1.jpg
	 * 
	 * @param machineNumber 机器号
	 * @param fileName 要发送的文件名
	 * @return 发送成功返回true，发送失败返回false
	 */
	public boolean SendFile(int machineNumber,String fileName){
		return zkem.invoke("SendFile",new Variant(machineNumber),new Variant(fileName)).getBoolean();
	}
	
	
	/**
	 * 刷新机器内数据，一般在上传用户信息或者指纹之后调用，这样能使所做的修改立即起作用，起到同步作用
	 * 
	 * @param machineNumber 机器号
	 * @return 刷新成功返回true，刷新失败返回false
	 */
	public boolean RefreshData(int machineNumber){
		return zkem.invoke("RefreshData",new Variant(machineNumber)).getBoolean();
	}
	
	
	
	/*******************5.4机器管理相关*********************/
	
	
	/**
	 * 判断当前机器是否为彩屏机
	 * 
	 * @param machineNumber 机器号
	 * @return 是彩屏机返回true，不是彩屏机返回false
	 */
	public boolean isTFTMachine(int machineNumber){
		return zkem.invoke("isTFTMachine",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 获取机器内数据存储状态，例如管理员数，当前用户数等
	 * 函数原型:VARIANT_BOOL GetDeviceStatus([in]long dwMacineNumber,[in]long dwInfo,[out]long* dwValue)
	 * 
	 * @param machineNumber 机器号
	 * @param status 需获取的数据，范围1-22，含义如下：1管理员数量，2注册用户数量，3机器内指纹模板数量，4密码数量，5操作记录数
	 * 					6考勤记录数，7指纹模板容量，8用户容量，9考勤记录容量，10剩余指纹容量，11剩余用户容量，12剩余考勤记录容量
	 * 					21人脸总数，22人脸容量，其他状态状况返回0
	 * @return 返回值与status取值对应
	 */
	public Integer GetDeviceStatus(int machineNumber,int status){
		Variant value=new Variant(0,true);
		boolean status_b=zkem.invoke("GetDeviceStatus",
				new Variant(machineNumber),new Variant(status),value).getBoolean();
		
		if(status_b==false){
			return null;
		}
		
		return value.getIntRef();
	}
	
	
	/**
	 * 获取机器相关信息，例如语言，波特率等
	 * 函数原型:VARIANT_BOOL GetDeviceInfo([in]long dwMachineNumber,[in]long dwInfo,[out]long* Value)
	 * 
	 * @param machineNumber 机器号
	 * @param info 需获取的信息类型，范围为1-68(注：不能为65)，具体含义如下
		1最大管理员数，总是返回500，2机器号，3语言：(0后缀为E一般代表英文，1其他状况，2语言后缀为T代表繁体，3语言后缀为L代表泰语)
		4空闲时长(分钟)即空闲该时段后机器进入待机或关机，5锁控时长，即锁驱动时长，6考勤记录报警数，当考勤记录数量到达该数量时，机器会报警提示
		7操作记录报警数，即当操作记录达到该数量时，机器会报警以提示用户，8重复记录时间，即同一用户打同一考勤状态的最小时间间隔
		9：232/485通讯波特率，0:1200bps,1:2400,2:4800,3:9600,4:19200,5:38400,6:57600,其他 :115200
		10奇偶校验，总是返回0，11停止位，总是返回0。。。。具体参数请查看脱机开发文档。。。。
	 * 				
	 * @return 具体返回值与info值设置有关
	 */
	public Integer GetDeviceInfo(int machineNumber,int info){
		Variant value=new Variant(0,true);
		boolean status=zkem.invoke("GetDeviceInfo",
				new Variant(machineNumber),new Variant(info),value).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return value.getIntRef();
				
	}
	
	
	/**
	 * 设置机器相关信息，例如语言，重复记录时间等
	 * 
	 * @param machineNumber 机器号
	 * @param info 欲设置的信息类型，范围为1-20，其含义参考GetDeviceInfo函数该参数含义
	 * @param value 要设置的值，请参考info含义
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetDeviceInfo(int machineNumber,int info,int value){
		return zkem.invoke("SetDeviceInfo",
				new Variant(machineNumber),new Variant(info),new Variant(value)).getBoolean();
	}
	
	
	/**
	 * 将本地电脑的时间设置为机器时间，如需要设置指定时间，可参考SetDeviceTime2
	 * 
	 * @param machineNumber 机器号
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetDeviceTime(int machineNumber){
		return zkem.invoke("SetDeviceTime",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 设置机器时间(可指定时间)
	 * 
	 * @param machineNumber 机器号
	 * @param year 年
	 * @param month 月
	 * @param day 日
	 * @param hour 时
	 * @param minute 分
	 * @param second 秒
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetDeviceTime2(int machineNumber,int year,int month,int day,int hour,int minute,int second){
		return zkem.invoke("SetDeviceTime2",
				new Variant(machineNumber),
				new Variant(year),
				new Variant(month),
				new Variant(day),
				new Variant(hour),
				new Variant(minute),
				new Variant(second)).getBoolean();
	}
	
	
	/**
	 * 获取设备时间
	 * 
	 * @param machineNumber 机器号
	 * @return Map<String,Object>的时间信息
	 */
	public Map<String,Object> GetDeviceTime(int machineNumber){
		Variant year =new Variant(0,true);
		Variant month =new Variant(0,true);
		Variant day =new Variant(0,true);
		Variant hour =new Variant(0,true);
		Variant minute =new Variant(0,true);
		Variant second =new Variant(0,true);
		
		boolean status=zkem.invoke("GetDeviceTime",new Variant(machineNumber),
				year,month,day,hour,minute,second).getBoolean();
		
		if(status==false){
			return null;
		}
		
		Map<String,Object> deviceTime=new HashMap<String,Object>();
		deviceTime.put("year", year.getIntRef());
		deviceTime.put("month", month.getIntRef());
		deviceTime.put("day", day.getIntRef());
		deviceTime.put("hour", hour.getIntRef());
		deviceTime.put("minute", minute.getIntRef());
		deviceTime.put("second", second.getIntRef());
		
		return deviceTime;	
	}
	
	
	/**
	 * 获取机器序列号
	 * 
	 * @param machineNumber 机器号
	 * @return 序列号
	 */
	public String GetSerialNumber(int machineNumber){
		Variant serialNumber=new Variant("",true);
		boolean status=zkem.invoke("GetSerialNumber",new Variant(machineNumber),serialNumber).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return serialNumber.getStringRef();
	}
	
	
	/**
	 * 获取机器名称
	 * @param machineNumber 机器号
	 * @return 机器号
	 */
	public String GetProductCode(int machineNumber){
		Variant productCode=new Variant("",true);
		boolean status=zkem.invoke("GetProductCode",new Variant(machineNumber),productCode).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return productCode.getStringRef();
	}
	
	
	/**
	 * 获取机器固件版本
	 * @param machineNumber 机器号
	 * @return 固件版本
	 */
	public String GetFirmwareVersion(int machineNumber){
		Variant version=new Variant("",true);
		boolean status=zkem.invoke("GetFirmwareVersion",new Variant(machineNumber),version).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return version.getStringRef();
	}
	
	
	/**
	 * 获取SDK版本号
	 * @return SDK版本号
	 */
	public String GetSDKVersion(){
		Variant version=new Variant("",true);
		boolean status=zkem.invoke("GetSDKVersion",version).getBoolean();
		
		if(status==false){
			return null;
		}
		return version.getStringRef();
	}
	
	
	/**
	 * 获取机器IP号
	 * @param machineNumber 机器号
	 * @return IP地址
	 */
	public String GetDeviceIP(int machineNumber){
		Variant ipAddr=new Variant("",true);
		boolean status=zkem.invoke("GetDeviceIP",new Variant(machineNumber),ipAddr).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return ipAddr.getStringRef();
	}
	
	
	/**
	 * 设置机器IP地址
	 * @param machineNumber 机器编号
	 * @param ipAddr ip地址
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetDeviceIP(int machineNumber,String ipAddr){
		return zkem.invoke("SetDeviceIP",new Variant(machineNumber),new Variant(ipAddr)).getBoolean();
	}
	
	
	/**
	 * 获取机器的MAC地址
	 * @param machineNumber 机器号
	 * @return MAC地址
	 */
	public String GetDeviceMAC(int machineNumber){
		Variant mac=new Variant("",true);
		boolean status=zkem.invoke("GetDeviceMAC",new Variant(machineNumber),mac).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return mac.getStringRef();
	}
	
	
	/**
	 * 设置机器MAC地址
	 * @param machineNumber 机器编号
	 * @param macAddr MAC地址
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetDeviceMAC(int machineNumber,String macAddr){
		return zkem.invoke("SetDeviceMAC",new Variant(machineNumber),new Variant(macAddr)).getBoolean();
	}
	
	
	/**
	 * 获取机器是否支持射频卡功能
	 * 
	 * @param machineNumber 机器号
	 * @return 返回值1：仅支持射频卡，2：支持射频卡也支持指纹，0：不支持射频卡
	 */
	public Integer GetCardFun(int machineNumber){
		Variant cardFun=new Variant(0,true);
		boolean status=zkem.invoke("GetCardFun",new Variant(machineNumber),cardFun).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return cardFun.getIntRef();
	}
	
	
	/**
	 * 设置机器通讯密码，该函数设置机器通讯密码，该通讯密码会保存在机器内
	 * 
	 * @param machineNumber 机器号
	 * @param commKey 通讯密码
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetDeviceCommPwd(int machineNumber,int commKey){
		return zkem.invoke("SetDeviceCommPwd",new Variant(machineNumber),new Variant(commKey)).getBoolean();
	}
	
	
	/**
	 * 设置PC端通讯密码，只有当PC端通讯密码和机器通讯密码相同才可以建立连接
	 * 
	 * @param commKey 通讯密码
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetCommPassword(int commKey){
		return zkem.invoke("SetCommPassword",new Variant(commKey)).getBoolean();
	}
	
	
	/**
	 * 查询当前机器状态
	 * 
	 * @return 0等待状态，1登记指纹状态，2识别指纹状态，3进入菜单状态，4忙状态(正在处理其他工作),5等待写卡状态
	 */
	public Integer QueryState(){
		Variant state=new Variant(0,true);
		boolean status=zkem.invoke("QueryState",state).getBoolean();
		if(status==false){
			return null;
		}
		
		return state.getIntRef();
	}
	
	
	/**
	 * 获取机器制造商名称
	 * 
	 * @return 机器制造商名称
	 */
	public String GetVendor(){
		Variant vendor=new Variant("",true);
		boolean status=zkem.invoke("GetVendor",vendor).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return vendor.getStringRef();
	}
	
	
	/**
	 * 获取机器的出厂时间
	 * 
	 * @param machineNumber 机器号
	 * @return 出厂时间
	 */
	public String GetDeviceStrInfo(int machineNumber){
		Variant info=new Variant(1);
		Variant value=new Variant("",true);
		boolean status=zkem.invoke("GetDeviceStrInfo",new Variant(machineNumber),info,value).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return value.getStringRef();
	}
	
	
	/**
	 * 获取设备平台的名称
	 * @param machineNumber 机器号
	 * @return  设备平台的名称
	 */
	public String GetPlatform(int machineNumber){
		Variant platForm=new Variant("",true);
		boolean status=zkem.invoke("GetPlatform",new Variant(machineNumber),platForm).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return platForm.getStringRef();
	}
	
	
	/**
	 * 获取机器内的参数配置情况，注：可通过该函数获取机器使用的算法版本
	 * 
	 * @param machineNumber 机器号
	 * @param option 参数名称：当该参数为字符串"~ZKFPVersion"时，由value描述的返回值为10，
	 * 代表当前机器使用的10.0指纹算法，如为空或为9时，代表当前机器使用的算法为9.0
	 * 
	 * @return 返回值参考option描述
	 */
	public String GetSysOption(int machineNumber,String option){
		Variant value=new Variant("",true);
		boolean status=zkem.invoke("GetSysOption",new Variant(machineNumber),new Variant(option),value).getBoolean();
		
		if(status==false){
			return null;
		}
		
		return value.getStringRef();
	}
	
	
	/**
	 * 配置机器内的参数
	 * 
	 * @param machineNumber 机器号
	 * @param option 参数名称
	 * @param value 参数的值
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean SetSysOption(int machineNumber,String option,String value){
		return zkem.invoke("SetSysOption",new Variant(machineNumber),
					new Variant(option),new Variant(value)).getBoolean();
	}
	
	
	
	
	/*****************5.5.1机器控制*********************/
	
	
	/**
	 * 清除机器内所有管理员权限
	 * @param machineNumber 机器号
	 * @return 清除成功返回true，清除失败返回false
	 */
	public boolean ClearAdministrators(int machineNumber){
		return zkem.invoke("ClearAdministrators",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 设置启用或禁用机器，禁用意味着关闭指纹头，键盘，卡模块等
	 * 
	 * @param machineNumber 机器号
	 * @param flag 1为启用，0为禁用
	 * @return 设置成功返回true，设置失败返回false
	 */
	@Deprecated //未连接机器也会返回true
	public boolean EnableDevice(int machineNumber,int flag){
		return zkem.invoke("EnableDevice",new Variant(machineNumber),new Variant(flag)).getBoolean();
	}
	
	
	/**
	 * 启用或禁用机器时钟的":"显示，启用机器显示并刷新到主界面，禁用时不显示":"
	 * 
	 * @param enable 1为启用，0为禁用
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean EnableClock(int enable){
		return zkem.invoke("EnableClock",new Variant(enable)).getBoolean();
	}
	
	
	/**
	 * 禁用机器一段时间
	 * 
	 * @param machineNumber 机器号
	 * @param second 禁用时间
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean DisableDeviceWithTimeOut(int machineNumber,int second){
		return zkem.invoke("DisableDeviceWithTimeOut",new Variant(machineNumber),new Variant(second)).getBoolean();
	}
	
	
	/**
	 * 关机
	 * @param machineNumber 机器号
	 * @return 设置成功返回true
	 */
	public boolean PowerOffDevice(int machineNumber){
		return zkem.invoke("PowerOffDevice",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 重启机器
	 * 
	 * @param machineNumber 机器号
	 * @return 设置成功返回true
	 */
	public boolean RestartDevice(int machineNumber){
		return zkem.invoke("PowerOffDevice",new Variant(machineNumber)).getBoolean();
	}
	
	
	
	/*****************5.5.2连机登记相关操作*******************/
	
	
	/**
	 * 登记用户，让机器进入登记用户状态，等待用户按指纹
	 * 注：使用该函数后，用户按三次指纹完成登记后，可能会出现按指纹不反应的情况，此时可以用StartIdenfity使机器进入等待状态
	 * 
	 * @param userID 需要登记的用户ID
	 * @param fingerID 需要登记的用户指纹索引号，范围为0-9
	 * @param flag 标识指纹模板是否有效或者为胁迫指纹，0表示指纹模板无效，1表示指纹模板有效，3表示为胁迫指纹
	 * 
	 * @return 设置成功返回true ，设置失败返回fasle
	 */
	public boolean StartEnrollEx(String userID,int fingerID,int flag){
		return zkem.invoke("StartEnrollEx",new Variant(userID),
					new Variant(fingerID),new Variant(flag)).getBoolean();
	}
	
	
	/**
	 * 开始1:1比对
	 * @param userID 用户ID
	 * @param fingerID 指纹索引，范围为0-9
	 * @return 验证成功返回true，验证失败返回false
	 */
	public boolean StartVerify(int userID,int fingerID){
		return zkem.invoke("StartVerify",new Variant(userID),new Variant(fingerID)).getBoolean();
	}
	
	
	/**
	 * 开始1：N比对，使机器进入1:N验证状态
	 * 
	 * @return 验证成功返回true，验证失败返回false
	 */
	public boolean StartIdentify(){
		return zkem.invoke("StartIdentify").getBoolean();
	}
	
	
	/**
	 * 取消机器当前的指纹登记状态
	 * 
	 * @return 取消成功返回true，取消失败返回false
	 */
	public boolean CancelOperation(){
		return zkem.invoke("CancelOperation").getBoolean();
	}
	
	
	
	/**********************5.5.3卡操作相关---飘过....*************************/
	
	
	
	
	/**********************5.5.4其他*************************/
	
	
	/**
	 * 获取最后一次错误信息
	 * 
	 * @return 错误返回码:-100不支持或数据不存在，-10传输的数据长度不对，-5数据已经存在，-4空间不足
	 * -3错误的大小，-2文件读写错误，-1SDK未初始化，0找不到数据或数据重复，1操作正确，4参数错误，101分配缓冲区错误
	 */
	public Integer GetLastError(){
		Variant errorCode=new Variant(0,true);
		boolean status=zkem.invoke("GetLastError",errorCode).getBoolean();
		if(status==false){
			return null;
		}
		
		return errorCode.getIntRef();
	}
	
	
	/**
	 * 捕获当前指纹头的指纹图像
	 * @param fullImage 是否获取整个图像，true为整个图像，false只是指纹部分
	 * @param width 指定捕获的图像的宽度
	 * @param height 指定捕获的图像的高度
	 * @param image 该参数接收指定的二进制格式的指纹图像
	 * @param imageFile 该参数指定捕获的指纹图像的保存名(需包含路径)
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean CaptureImage(boolean fullImage,int width,int height,byte image,String imageFile){
		return zkem.invoke("CaptureImage",
				new Variant(fullImage),
				new Variant(width),
				new Variant(height),
				new Variant(image),
				new Variant(imageFile)).getBoolean();
	}
	
	
	/**
	 * 升级固件
	 * @param firmwareFile 需要升级的固件文件名(需包含路径)
	 * @return 设置成功返回true，设置失败返回false
	 */
	public boolean UpdateFirmware(String firmwareFile){
		return zkem.invoke("UpdateFirmware",new Variant(firmwareFile)).getBoolean();
	}
	
	
	/**
	 * 准备以批处理模式上传数据，如在上传用户模板，用户信息等数据前使用该函数，则在上传时SDK将临时地将这些
	 * 数据都存储在缓冲区，然后再执行BatchUpdate将临时数据一起传进机器
	 * @param machineNumber 机器号
	 * @param upateFlag 存在指纹覆盖标志，即上传用户指纹模板时，如该用户指纹所有已经存在，是否
	 * 覆盖致歉的指纹模板，1为强制覆盖，0为不覆盖
	 * 
	 * @return 缓存成返回true，失败返回false
	 */
	public boolean BeginBatchUpdate(int machineNumber,int upateFlag){
		return zkem.invoke("BeginBatchUpdate",
				new Variant(machineNumber),new Variant(upateFlag)).getBoolean();
	}
	
	
	/**
	 * 开始批量上传数据，一般在使用函数BeginBatchUpdate后再上传完相关数据才使用该函数
	 * 
	 * @param machineNumber 机器号
	 * @return 上传成返回true，上传失败返回false
	 */
	public boolean BatchUpdate(int machineNumber){
		return zkem.invoke("BatchUpdate",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 取消批量上传数据模式，一般在使用了BeginBatchUpdate后，使用BatchUpdate之前可以使用
	 * 该函数，该函数释放批处理上传准备的缓冲区
	 * 
	 * @param machineNumber 机器号
	 * @return 取消成功返回true，取消失败返回false
	 * 
	 */
	public boolean CancelBatchUpdate(int machineNumber){
		return zkem.invoke("CancelBatchUpdate",new Variant(machineNumber)).getBoolean();
	}
	
	
	/**
	 * 播放指定的连续号语音，具体序号视机器而定，用户可在机器内声音测试查看到序号，一般为0-11
	 * @param position 开始语音序号
	 * @param length 结束语音序号
	 * @return 播放成功返回true，播放失败返回false
	 */
	public boolean PlayVoice(int position,int length){
		return zkem.invoke("PlayVoice",new Variant(position),new Variant(length)).getBoolean();
	}
	
	
	/**
	 * 播放指定序号语音，具体序号视机器而定，用户可在机器内声音测试查看到序号，一般为0-11
	 * @param index 需要播放的语音序号
	 * @return 播放成功返回true，播放失败返回false
	 */
	public boolean PlayVoiceByIndex(int index){
		return zkem.invoke("PlayVoice",new Variant(index)).getBoolean();
	}
	
	/**
	 * 设置指定时间段编号的时间段信息
	 * @param machineNumber  机器号
	 * @param TZIndex   时间段序号
	 * @param TZ   时间段字符串
	 * @return
	 */
	public boolean SetTZInfo(int machineNumber, int TZIndex, String TZ){
	    return zkem.invoke("SetTZInfo",new Variant(machineNumber),
	            new Variant(TZIndex), new Variant(TZ)).getBoolean();
	}
	
	/**
	 * 获取指定时间段编号的时间段信息
	 * @param machineNumber
	 * @param TZIndex  事件
	 * @return
	 */
	public String GetTZInfo(int machineNumber, int TZIndex){
	    Variant TZ = new Variant("");
	    boolean b = zkem.invoke("GetTZInfo",new Variant(machineNumber),
                new Variant(TZIndex), TZ).getBoolean();
	    log.info("获取时间段信息: {}",b);
	    return TZ.toString();
	}
	
//	public String GetUserTZStr(int machineNumber, int userId){
//	    Variant TZs = new Variant("");
//	    boolean invoke = zkem.invoke("GetUserTZStr",new Variant(machineNumber),
//	            new Variant(userId), TZs).getBoolean();
//	    System.out.println("获取用户时间段 : " + invoke);
//	    return TZs.toString();
//	}
//	
//	public boolean SetUserTZStr(int machineNumber, int userId, String TZs){
//	    boolean b = zkem.invoke("SetUserTZStr", new Variant(machineNumber),
//	            new Variant(userId), new Variant(TZs)).getBoolean();
//	    return b;
//	}

	/**
	 * 查询是否有门禁功能
	 * @return
	 */
	public int GetACFun(){
	    Variant ACFun = new Variant(100,true);
	    boolean boolean1 = zkem.invoke("GetACFun",ACFun).getBoolean();
	    log.info("查询门禁功能成功? :{}",boolean1);
	    log.info("是否有门禁功能 :{}",ACFun.getIntRef());
	    return ACFun.getIntRef();
	}
	/**
	 * 开门
	 * @param machineNumber 设备号
	 * @param delay  延时 delay/10 秒后关门
	 * @return
	 */
	public boolean ACUnlock(int machineNumber, int delay){
	    boolean boolean1 = zkem.invoke("ACUnlock",new Variant(machineNumber),new Variant(delay)).getBoolean();
	    return boolean1;
	}
}

