/**
 * KettleStatusInfo.java 2014-12-15
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.json.CmdResponse.ResponseData;
import com.hipad.smart.json.QueryDeviceInfoResponse.DeviceInfo;
import com.hipad.smart.util.MsgBodyData;

/**
 * represents the kettle status info. 
 * @author wangbaoming
 *
 */
public class KettleStatusInfo extends MsgBodyData {
	
	/* state */
	public static class KettleState{
		public final static byte STATE_STANDBY = 0;
		public final static byte STATE_HEATING = 1;
		public final static byte STATE_KEEP_TEMPC = 2;
		public final static byte NOTIFY_HUNG = 3;
		public final static byte WARN_NO_WATER = 4;
		public final static byte WARN_BAD_WATER = 5;
		public final static byte STATE_SELFCHECK = 6;
		public final static byte STATE_BOILED_TO_KEEP_TEMPC = 7;
		public final static byte ERROR_NTC = 8;
	}
	
	/* function */
	public final static byte FUNC_STANDBY = 0;
	public final static byte FUNC_BOIL = 1;
	public final static byte FUNC_KEEP_TEMPC = 2;
	public final static byte FUNC_BOIL_THEN_KEEP_TEMPC = 3;
	
	public KettleStatusInfo(byte[] data) {
		super(data);
	}

	/**
	 * Construct a {@link KettleStatusInfo} instance based on the dataBase64, the response body which is the binary data encoded in base64.
	 * @param dataBase64
	 * 		the response body which always comes from {@link ResponseData#getResponseBody()} or {@link DeviceInfo#getResponseBody()}
	 */
	public KettleStatusInfo(String dataBase64) {
		super(dataBase64);
	}		
	
	/**
	 * get kettle state.
	 * @see {@link KettleState}
	 * @return
	 * 		the kettle state
	 */
	public byte getState(){
		return getByte(1);
	}
	
	/**
	 * get the current function.
	 * @see {@link KettleStatusInfo#FUNC_STANDBY} and so on, FUNC_XXX.
	 * @return
	 * 		the current function
	 */
	public byte getCurrFunc(){
		return getByte(2);
	}	
	
	/**
	 * get the remaining time to keep the temperature
	 * @return
	 * 		the remaining time
	 */
	public int getRemainOfTimeToKeepTempC(){		
		return getByte(3);
	}
	
	/**
	 * get the temperature to be kept in centigrade.
	 * @return
	 * 		the temperature to be kept
	 */
	public int getTempCToBeKept(){		
		return getByte(4);
	}
	
	/**
	 * get the current temperature in centigrade
	 * @return
	 */
	public int getCurrentTemperature(){
		return getByte(5);
	}
	
	/**
	 * get the water quality. TDS = n * 10, n = 0 ~ 100.
	 * @return
	 */
	public int getWaterQuality(){
		return getByte(6);
	}
	
	/**
	 * get how long the kettle has been used to boil water totally.
	 * @return
	 * 		how long the kettle has worked to boil water totally
	 */
	public int getWorkedTime(){
		return getByte(7);
	}
	
	/**
	 * get the times the kettle has boiled water.
	 * @return
	 * 		the boiled water times
	 */
	public int getBoiledTimes(){
		byte highByte = getByte(8);
		byte lowByte = getByte(9);
		
		int times = highByte << 8 | lowByte;
		return times;
	}
	
	/**
	 * get the 2 level menus.
	 * @return
	 * 		menus, the 1st byte is 1th level menu, the 2nd is the 2nd level menu
	 */
	public byte[] getMenu(){
		byte[] menu = new byte[2];
		menu[0] = getByte(10);
		menu[1] = getByte(11);
		return menu;
	}
	
	public void dump(){
		StringBuilder sb = new StringBuilder();
		sb.append("\nstate: " + getState())
			.append("\nfunction: " + getCurrFunc())
			.append("\nremaining time of heating: " + getRemainOfTimeToKeepTempC())
			.append("\ntempCToBeKept: " + getTempCToBeKept())
			.append("\ncurrTempC: " + getCurrentTemperature())			
			.append("\nTDS n=" + getWaterQuality())			
			.append("\nhowLongWorked: " + getWorkedTime())			
			.append("\nboiledTimes: " + getBoiledTimes());
		System.out.println("dump:\n" + sb.toString());
	}
}
