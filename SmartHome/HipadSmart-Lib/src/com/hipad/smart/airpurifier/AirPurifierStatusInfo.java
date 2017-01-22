/**
 * $(#)AirPurifierStatusInfo.java 2015-1-13
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.json.CmdResponse.ResponseData;
import com.hipad.smart.json.QueryDeviceInfoResponse.DeviceInfo;
import com.hipad.smart.util.Decimal;
import com.hipad.smart.util.MsgBodyData;
import com.hipad.smart.util.Time;

/**
 * represents the air purifier status info. 
 * @author wangbaoming
 *
 */
public class AirPurifierStatusInfo extends MsgBodyData {
	/* mode */
	public final static byte MODE_NORMAL = 0;
	public final static byte MODE_SMART = 1;
	public final static byte MODE_SLEEP = 2;	

	/* speed */
	public final static byte SPEED_LOW = 0;
	public final static byte SPEED_NORMAL = 0;
	public final static byte SPEED_MIDDLE = 0;
	public final static byte SPEED_HIGH = 0;	
	

	/**
	 * Construct a {@link AirPurifierStatusInfo} instance based on the dataBase64, the response body which is the binary data encoded in base64.
	 * @param dataBase64
	 * 		the response body which always comes from {@link ResponseData#getResponseBody()} or {@link DeviceInfo#getResponseBody()}
	 */
	public AirPurifierStatusInfo(String dataBase64) {
		super(dataBase64);
	}
	
	/**
	 * get the power state.
	 * @return
	 * 		true, if power on; false otherwise.
	 */
	public boolean isPowerOn(){
		return 0 != getByte(0);
	}
	
	/**
	 * get the work mode.
	 * @see {@link AirPurifierStatusInfo#MODE_NORMAL} and other MODE_XXX.
	 * @return
	 * 		the work mode.
	 */
	public int getMode(){
		return getByte(1);
	}
	
	/**
	 * 	get the wind speed level.
	 * @see {@link AirPurifierStatusInfo#SPEED_LOW} and other SPEED_XXX.
	 * @return
	 * 		the speed level
	 */
	public int getCurrSpeed(){
		return getByte(2);
	}
	
	/**
	 * if the negative ions function is on or not.
	 * @return
	 * 		true if the negative ions func is on, false otherwise
	 */
	public boolean isNegativeIonOn(){
		return 0 != getByte(3);
	}
	
	/**
	 * if the photocatalytsis is on or not.
	 * @return
	 * 		true if on, false otherwise
	 */
	public boolean isPhotocatalysisOn(){
		return 0 != getByte(4);
	}
	
	/**
	 * if the humidification is on or not.
	 * @return
	 * 		true if on, false otherwise
	 */
	public boolean isHumidificationOn(){
		return 0 != getByte(5);
	}
	
	/**
	 * get the remaining time after which the air purifier will power off.
	 * @return
	 * 		the remaining time
	 */
	public Time getRemainTimeToPowerOff(){
		Time time = new Time();
		time.minute = getByte(6);
		time.hour = getByte(7);
		
		return time;
	}
	
	/**
	 * get how long the filter has worked.
	 * @return
	 * 		the worked hours
	 */
	public short getFilterWorkedHours(){
		return (short) (getByte(8) << 8 | getByte(9) );
	}
	
	/**
	 * get the pm2.5.
	 * @return
	 * 		the pm2.5 value
	 */
	public short getPm25(){
		return (short) (getByte(10) << 8 | getByte(11) );
	}
	
	/**
	 * get the humidity of air.
	 * @return
	 * 		the humidity
	 */
	public Decimal getHumidity(){
		Decimal decimal = new Decimal();
		decimal.integer = getByte(12);
		decimal.decimal = getByte(13);
		
		return decimal; 
	}
	
	/**
	 * get environment temperature in centigrade.
	 * @return
	 * 		the temperature
	 */
	public Decimal getTemperature(){
		Decimal decimal = new Decimal();
		decimal.integer = getByte(14);
		decimal.decimal = getByte(15);
		
		return decimal; 
	}
	
	/**
	 * detect if lacked water or not.
	 * @return
	 * 		true if lack of water, false otherwise
	 */
	public boolean hasLackedWater(){
		return getBit(16, 0);
	}
	
	public boolean isUv1Failed(){
		return getBit(16, 1);
	}
	
	public boolean isUv2Failed(){
		return getBit(16, 2);
	}
	
	public void dump(){
		StringBuilder sb = new StringBuilder();
		sb.append("\npowerOn: " + isPowerOn())
			.append("mode: " + getMode())			
			.append("\nwindSpeed: " + getCurrSpeed())
			.append("\nnegativeIon: " + isNegativeIonOn())
			.append("\nphotocatalysis: " + isPhotocatalysisOn())
			.append("\nhumidification: " + isHumidificationOn())			
			.append("\nremainTimeToPowerOff: " + getRemainTimeToPowerOff())			
			.append("\nfilterWorkedHours: " + getFilterWorkedHours())			
			.append("\nPm25: " + getPm25())
			.append("\nhumidity: " + getHumidity())		
			.append("\ntemperature: " + getTemperature())
			.append("\nlackedWater: " + hasLackedWater())
			.append("\nUv1Failed: " + isUv1Failed())	
			.append("\nUv2Failed: " + isUv2Failed());
		System.out.println("dump:\n" + sb.toString());
	}
}
