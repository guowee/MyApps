package com.hipad.tracker.config;
/**
 * 
 * @author guowei
 *
 */
public class WebService {
	public final static String SCHEMA = "http://";
	//public final static String IP="www.hzhuagen.com";
	//public final static String IP="tracker.hzhuagen.com";
	public final static String IP = "192.168.1.130";
	public final static String PORT = ":8280";

	public final static String PREFIX_ALL = SCHEMA + IP + PORT;

	public final static String LOGIN = PREFIX_ALL + "/shapi/loginAPP";
	public final static String REGISTER = PREFIX_ALL+ "/shapi/registerAPP";
	public final static String VERIFY=PREFIX_ALL+"/shapi/getSmsCode";
	public final static String BIND=PREFIX_ALL+"/shapi/bind";
	public final static String BINDRESULT=PREFIX_ALL+"/shapi/bindResult";
	public final static String LOC=PREFIX_ALL+"/shapi/loc";
	public final static String LOCRESULT=PREFIX_ALL+"/shapi/locResult";
	public final static String BINDINFO=PREFIX_ALL+"/shapi/getBindInfo";
	public final static String FACTORY=PREFIX_ALL+"/shapi/factory";
	public final static String FACRESULT=PREFIX_ALL+"/shapi/facResult";
	public final static String CURFEW=PREFIX_ALL+"/shapi/curfew";
	public final static String CURFEWRESULT=PREFIX_ALL+"/shapi/curfewResult";
	public final static String SPN=PREFIX_ALL+"/shapi/configSpn2";
	public final static String SPNRESULT=PREFIX_ALL+"/shapi/spn2Result";
	public final static String STEPS=PREFIX_ALL+"/shapi/getStep";
	public final static String RESETPWD=PREFIX_ALL+"/shapi/resetPW";
	public final static String DEVINFO=PREFIX_ALL+"/shapi/getDevInfo";
	
	public static final String UPDATE_SERVER = PREFIX_ALL+"/updates/";
	public static final String UPDATE_APKNAME = "Tracker.apk";
	public static final String UPDATE_VERJSON = "version.xml";
	public static final String UPDATE_SAVENAME = "Tracker";

	public final static class Param {
		public final static String token = "account";
		public final static String imei = "imei";
		public final static String user = "user";
		public final static String mobile="mobile";
		public final static String password = "password";
		public final static String code="code";
		public final static String bizsn="bizSN";
		public final static String sim="devSIM";
		public final static String spn2="spn2";
		public final static String spn1="spn1";
		public final static String apn="apn";
		public final static String curfew="curfewEnable";
		public final static String waketime="wakeTime";
		public final static String sleeptime="sleepTime";
		public final static String steptype="stepType";
	}

}
