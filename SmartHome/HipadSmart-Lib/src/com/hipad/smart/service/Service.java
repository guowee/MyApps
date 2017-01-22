/**
 * Service.java 2014-11-17
 */
package com.hipad.smart.service;

import android.content.Context;

import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.device.CommonDevice.Group;
import com.hipad.smart.device.Device;
import com.hipad.smart.http.HttpUtil.ResponseResultHandler;
import com.hipad.smart.json.CmdResponse;
import com.hipad.smart.json.CmdResponse.ResponseData;
import com.hipad.smart.json.LoginResponse;
import com.hipad.smart.json.QueryDeviceInfoResponse;
import com.hipad.smart.json.QueryDeviceInfoResponse.DeviceInfo;
import com.hipad.smart.json.QueryDevicesResponse;
import com.hipad.smart.json.QueryGatewayResponse;
import com.hipad.smart.json.QueryGatewayUserResponse;
import com.hipad.smart.json.RegisterResponse;
import com.hipad.smart.json.Response;
import com.hipad.smart.json.UpdateDeviceInfoResponse;
import com.hipad.smart.user.User;

/**
 * core service interface. All results will returned asynchronously in {@linkplain ResponseResultHandler#handle(boolean, Object)}, 
 * and the callback {@linkplain ResponseResultHandler#handle(boolean, Object)} is called in main(UI) thread.
 * @author wangbaoming
 *
 */
public interface Service {
	/**
	 * logout app or cloud. the access token will be cleaned.
	 * @param context
	 * 		app context
	 * @param handler
	 * 		logout result handler
	 */
	public void logout(Context context, ResponseResultHandler<Response> handler);
	/**
	 * login HipadSmart cloud.
	 * @param id
	 * 		the user id used to login
	 * @param passwd
	 * 		login password
	 * @param handler
	 * 		the login result handler
	 */
	public void login(String id, String passwd, ResponseResultHandler<LoginResponse> handler);
	/**
	 * register the user on HipadSmart cloud.
	 * @param user
	 * 		the user info
	 * @param handler
	 * 		result handler
	 */
	public void register(User user, ResponseResultHandler<RegisterResponse> handler);	
	
	/**
	 * update the user info.
	 * @param user
	 * 		the user info
	 * @param handler
	 * 		result handler
	 */
	public void updateUserInfo(User user, ResponseResultHandler<Response> handler);
	
	/**
	 * get the gateway which the user can reach and control the devices that connected to.
	 * @param handler
	 * 		the result handler
	 */
	public void getGateways(ResponseResultHandler<QueryGatewayResponse> handler);	
	/**
	 * add the gateway to the list all of which the user can reach.
	 * @param gatewaySn
	 * 		gateway sn
	 * @param code
	 * 		activation code
	 * @param handler
	 * 		result handler
	 */
	public void addGateway(String gatewaySn, String code, ResponseResultHandler<Response> handler);
	/**
	 * remove the gateway from the list which the user can reach.
	 * @param gatewaySn
	 * 		gateway sn
	 * @param handler
	 * 		result handler
	 */
	public void delGateway(String gatewaySn, ResponseResultHandler<Response> handler);
	/**
	 * set the default gateway.
	 * @param gatewaySn
	 * 		gateway sn
	 * @param handler
	 * 		result handler
	 */
	public void setDefaultGateway(String gatewaySn, ResponseResultHandler<Response> handler);
	
	/**
	 * query all users who can reach the gateway.
	 * @param gatewaySn
	 * 		the gateway sn
	 * @param handler
	 * 		result handler. all user will be returned, as with the result and message.
	 */
	public void getGatewayUsers(String gatewaySn, ResponseResultHandler<QueryGatewayUserResponse> handler);	
	/**
	 * grant the user the privilege to access the gateway.
	 * @param userId
	 * 		user id
	 * @param gatewaySn
	 * 		gateway sn
	 * @param handler
	 * 		result handler
	 */
	public void addUser(String userId, String gatewaySn, ResponseResultHandler<Response> handler);
	/**
	 * revoke the privilege to access the gateway.
	 * @param userId
	 * 		the user id that will have the access to the gateway
	 * @param gatewaySn
	 * 		gateway sn
	 * @param handler
	 * 		result handler
	 */
	public void delUser(String userId, String gatewaySn, ResponseResultHandler<Response> handler);
	
	/**
	 * get the devices that are in the group and connected to the gateway.<br> 
	 * if gatewaySn is null, all devices on the gateways will be returned. And if the group is {@link Group#None} or null, all devices of various group will be returned.  
	 * @param gatewaySn
	 * 		gateway sn
	 * @param group
	 * 		the device(s) group
	 * @param handler
	 * 		result handler, the devices meeting the conditions will be return.
	 */
	public void getDevices(String gatewaySn, CommonDevice.Group group, ResponseResultHandler<QueryDevicesResponse> handler);
	/**
	 * query the device status info.
	 * @param gatewaySn
	 * 		the gateway sn, the device is connected to.
	 * @param deviceId
	 * 		device id
	 * @param handler
	 * 		result handler, the device info will be returned in {@link DeviceInfo}
	 */
	public void getDeviceInfo(String gatewaySn, String deviceId, ResponseResultHandler<QueryDeviceInfoResponse> handler);
	/**
	 * add the device which is directly connected to cloud.
	 * @param deviceId
	 * 		device id
	 * @param code
	 * 		activation code
	 * @param handler
	 * 		result handler
	 */
	public void addDirectDevice(String deviceId, String code, ResponseResultHandler<Response> handler);
	/**
	 * delete the device from the gateway.
	 * @param deviceId
	 * 		device id
	 * @param gatewaySn
	 * 		gateway sn
	 * @param handler
	 * 		result handler
	 */
	public void delDevice(String deviceId, String gatewaySn, ResponseResultHandler<Response> handler);
	/**
	 * update the device info.
	 * @param deviceId
	 * @param gatewaySn
	 * @param name
	 * @param handler
	 */
	public void updateDeviceInfo(String deviceId, String gatewaySn, String name, ResponseResultHandler<UpdateDeviceInfoResponse> handler);	

	/**
	 * send cmd to control the device.
	 * @param deviceId
	 * 		device id
	 * @param gatewaySn
	 * 		the gateway sn, which the device is connected to
	 * @param cmd
	 * 		cmd that will be sent to the device
	 * @param handler
	 * 		retsult handler, the reply will be returned in {@link ResponseData}
	 */
	public void managerDevice(String deviceId, String gatewaySn, Cmd cmd, ResponseResultHandler<CmdResponse> handler);
}
