/**
 * CoffeeMakerStatusInfo.java 2014年11月21日
 */
package com.hipad.smart.coffeemaker;

import com.hipad.smart.util.MsgBodyData;
import com.hipad.smart.util.Time;

/**
 * represent the coffee maker status info.
 * @author wangbaoming
 *
 */
public class CoffeeMakerStatusInfo extends MsgBodyData{	

	/* function */
	public final static byte FUNC_STANDBY = 0;
	public final static byte FUNC_BIG = 1;
	public final static byte FUNC_SMALL = 2;
	
	/* state */
	public static class CoffeeMakerState{
		public final static byte STATE_STANDBY = 0;
		public final static byte STATE_PREHEATING = 1;
		public final static byte STATE_MAKING = 2;
		public final static byte STATE_PAUSE = 3;
		public final static byte STATE_COMPLETE = 4;
		public final static byte WARN_ABNORMAL_TEMPERATURE = 5;
		public final static byte WARN_NO_WATER = 6;
		public final static byte STATE_SELFCHECK = 7;
		public final static byte STATE_SELFCHECK_FINISH = 8;
		public final static byte STATE_WASH = 9;
		public final static byte STATE_WASH_FINISH = 10;
	}	

	public CoffeeMakerStatusInfo(String dataBase64) {
		super(dataBase64);
	}
	
	public byte getFunc(){
		return getByte(0);
	}
	
	public byte getState(){
		return getByte(1);
	}
	
	public int getTemperature(){
		return getByte(2);
	}
	
	public int getVolume(){
		return getByte(3);
	}
	
	public int getServedTimes(){
		return getByte(4);
	}
	
	public int getWorkedHours(){
		byte highByte = getByte(5);
		byte lowByte = getByte(6);
		
		int hours = highByte << 8 | lowByte;
		
		return hours;
	}
	
	public void dump(){
		StringBuilder sb = new StringBuilder();
		sb.append("func: " + getFunc())
			.append("\nstate: " + getState())
			.append("\ntemperature: " + getTemperature())
			.append("\nvolume: " + getVolume())
			.append("\nservedTimes: " + getServedTimes())
			.append("\nworkedHours: " + getWorkedHours());
		System.out.println("dump:\n" + sb.toString());
	}
}
