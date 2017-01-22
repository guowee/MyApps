/**
 * SocketStatusInfo.java 2015-3-18
 */
package com.hipad.smart.socket;

import com.hipad.smart.util.MsgBodyData;

/**
 * the response to the command from socket. 
 * @author wangbaoming
 *
 */
public class SocketStatusInfo extends MsgBodyData {

	public final static byte STATE_OFF = 0;
	public final static byte STATE_ON = 1;
	
	public SocketStatusInfo(byte[] data) {
		super(data);
	}
	
	public SocketStatusInfo(String dataBase64) {
		super(dataBase64);
	}	
	
	/**
	 * judge if the plug is on or off.
	 * @param index
	 * 		the plug index[0,2]
	 * @return
	 */
	public boolean isPlugOn(int index){
		return getBit(0, index);
	}
	
	/**
	 * get the power frequency[0,100]Hz,50Hz/60Hz.
	 * @return
	 * 		the frequency
	 */
	public int getPowerFrequency(){
		return getByte(1);
	}
	
	/**
	 * get the power voltage
	 * @return
	 * 		the power voltage
	 */
	public int getVoltage(){
		byte byte2 = getByte(2);
		byte byte3 = getByte(3);
		byte byte4 = getByte(4);
		
		int voltage = byte2 << 16 | byte3 << 8 | byte4;
		return voltage;	
	}
	
	public int getCurrency(){
		byte byte5 = getByte(5);
		byte byte6 = getByte(6);
		byte byte7 = getByte(7);
		
		int currency = byte5 << 16 | byte6 << 8 | byte7;
		return currency;	
	}
	
	public int getReactivePower(){ // average power
		byte byte8 = getByte(8);
		byte byte9 = getByte(9);
		byte byte10 = getByte(10);
		
		int reactivePower = byte8 << 16 | byte9 << 8 | byte10;
		return reactivePower;	
	}
	
	public int getApparentPower(){
		byte byte11 = getByte(11);
		byte byte12 = getByte(12);
		byte byte13 = getByte(13);
		
		int apparentPower = byte11 << 16 | byte12 << 8 | byte13;
		return apparentPower;	
	}

	public void dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("plug 1: " + isPlugOn(0));
		sb.append("plug 2: " + isPlugOn(1));
		sb.append("plug 3: " + isPlugOn(2));
		sb.append("frequency: " + getPowerFrequency());
		sb.append("voltage: " + getVoltage());
		sb.append("currency: " + getCurrency());
		sb.append("reactive power: " + getReactivePower());
		sb.append("apparent power: " + getApparentPower());
		System.out.println("dump:\n" + sb.toString());
	}
}
