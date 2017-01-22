/**
\ * Webservice.java 2014-11-17
 */
package com.hipad.smart.config;

/**
 * the webservice url and params.
 * @author wangbaoming
 *
 */
public final class Webservice {	
	
	//
	public final static String SCHEMA = "http://";
	//public final static String IP = "180.166.198.202";
//	public final static String IP = "192.168.1.102"; // ethernet
	public final static String IP = "192.168.1.130"; // dev
	//public final static String IP="10.70.1.35";
	public final static String PORT = ":8280";
	//public final static String PORT = ":8080";
	
	public final static String PREFIX_ALL = SCHEMA + IP + PORT;
	
	// authority
	public final static String LOGIN = PREFIX_ALL + "/shapi/login";
	public final static String REGISTER = PREFIX_ALL + "/shapi/register";
	// gateway
	public final static String GET_GATEWAY_LIST = PREFIX_ALL + "/shapi/gateway/list";
	public final static String ADD_GATEWAY = PREFIX_ALL + "/shapi/gateway/add";
	public final static String DEL_GATEWAY = PREFIX_ALL + "/shapi/gateway/del";
	public final static String SET_DEFAULT_GATEWAY = PREFIX_ALL + "/shapi/gateway/setdefaultgateway";
	// user
	public final static String UPDATE_USER_INFO = PREFIX_ALL + "/shapi/user/modify";
	public final static String GET_GATEWAY_USER_LIST = PREFIX_ALL + "/shapi/gatewayuser/list";
	public final static String ADD_GATEWAY_USER = PREFIX_ALL + "/shapi/gatewayuser/add";
	public final static String DEL_GATEWAY_USER = PREFIX_ALL + "/shapi/gatewayuser/del";
	// device
	public final static String GET_DEVIVCE_LIST = PREFIX_ALL + "/shapi/device/list";
	public final static String GET_DEVIVCE_INFO = PREFIX_ALL + "/shapi/device/info";
	public final static String ADD_DIRECT_DEVICE = PREFIX_ALL + "/shapi/device/add";
	public final static String DEL_DEVICE = PREFIX_ALL + "/shapi/device/del";
	public final static String UPDATE_DEVICE_INFO = PREFIX_ALL + "/shapi/device/upd";
	// control
	public final static String MGR_DEVICE = PREFIX_ALL + "/shapi/device/manager";
	//
	
	/**
	 * webservice params.
	 * @author wangbaoming
	 *
	 */
	public final static class Param {		
		public final static String userId = "userid";
		public final static String user = "user";
		public final static String password = "password";
		

		public final static String name = "name";
		public final static String email = "email";
		public final static String mobile = "mobile";
		public final static String pid = "pid";		

		public final static String nickname = "nickname";
		public final static String birthday = "birthday";
		public final static String gender = "gender";
		public final static String address = "address";
		public final static String job = "job";
		public final static String company = "company";
		public final static String photo = "photo";

		public final static String token = "account";	

		public final static String gateway = "gateway";
		public final static String deviceId = "deviceid";	
		public final static String device = "device";	
		public final static String code = "code";

		public final static String group = "bigclass";

		public final static String flag = "flag";
		public final static String cmdNo = "cmdno";
		public final static String cmd = "cmd";
	}
}
