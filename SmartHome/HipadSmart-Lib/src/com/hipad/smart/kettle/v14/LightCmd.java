/**
 * $(#)LightCmd.java 2015年5月14日
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class LightCmd extends KettleCmd {

	private byte[] mBodyData = new byte[]{(byte) 0xFF};
	
	@Override
	public MsgBodyData getBody() {
		return new MsgBodyData(mBodyData) {};
	}
	
	@Override
	public byte getCmdNo() {
		return CMD_LIGHT;
	}
	
	public void set(boolean query, byte mode){
		mBodyData[0] = query ? 0 : mode;
	}
	
	public static class LightStatus extends MsgBodyData {

		public LightStatus(byte[] data) {
			super(data);
		}

		public LightStatus(String dataBase64) {
			super(dataBase64);
		}
		
		public int getLightMode(){
			return getByte(0);
		}		
	}
}
