/**
 * OkBoilCmd.java 2014-12-16
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class OkBoilCmd extends KettleCmd {
	protected OkBoilCmdBody mCmdBody;
	
	public OkBoilCmd() {		
		mCmdBody = new OkBoilCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}

	@Override
	public byte getCmdNo() {
		return CMD_OK_BOIL;
	}
	
	/**
	 * 
	 * @param func
	 * 		work function
	 */
	public void setFunc(int func){
		mCmdBody.setFunc(func);
	}
	/**
	 * set the period when keeping the temperature. 
	 * @param mins
	 * 		minutes
	 */
	public void setMinutesToKeepTempC(int mins){
		mCmdBody.setMinutesToKeepTempC(mins);
	}
	
	/**
	 * set the temperature to be kept.
	 * @param centigrade
	 * 		temperature in centigrade
	 */
	public void setTempCToBeKept(int centigrade){
		mCmdBody.setTempCToBeKept(centigrade);
	}
	
	/**
	 * synchronize the specific choice.
	 * @param level1
	 * 		level 1 menu
	 * @param level2
	 * 		level 2 menu
	 */
	public void syncMenu(int level1, int level2){
		mCmdBody.syncMenu(level1, level2);
	}

	private final static class OkBoilCmdBody extends MsgBodyData{

		protected OkBoilCmdBody() {
			super(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF}); // all bytes is ignored
		}
		
		public void setFunc(int func){
			setByte(2, (byte) func);
		}
		
		public void setMinutesToKeepTempC(int mins){
			setByte(3, (byte) mins);
		}
		
		public void setTempCToBeKept(int centigrade){
			setByte(4, (byte) centigrade);
		}
		
		public void syncMenu(int level1, int level2){
			setByte(5, (byte) level1);
			setByte(6, (byte) level2);
		}
	}
}
