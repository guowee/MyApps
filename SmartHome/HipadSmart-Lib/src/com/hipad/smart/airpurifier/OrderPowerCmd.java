/**
 * $(#) PowerCmd.java 2015-1-13
 */
package com.hipad.smart.airpurifier;

import com.hipad.smart.util.MsgBodyData;
import com.hipad.smart.util.Time;

/**
 * @author wangbaoming
 *
 */
public class OrderPowerCmd extends AirPurifierCmd {
	
	private OrderPowerCmdBody mCmdBody;
	
	public OrderPowerCmd() {
		mCmdBody = new OrderPowerCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_POWER;
	}	

	public void setPowerOffTime(Time time){
		mCmdBody.setPowerOffTime(time);
	}
	
	private static class OrderPowerCmdBody extends MsgBodyData{
		private final static int SIZE = 1;

		public OrderPowerCmdBody() {
			super(SIZE);
		}
		
		public void setPowerOffTime(Time time){
			setByte(0, (byte) time.minute);
			setByte(0, (byte) time.hour);
		}
	}

}
