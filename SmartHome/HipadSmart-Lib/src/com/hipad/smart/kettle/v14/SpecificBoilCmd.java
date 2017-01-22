/**
 * $(#)SpecificBoilCmd.java 2014-12-16
 */
package com.hipad.smart.kettle.v14;

import com.hipad.smart.util.MsgBodyData;

/**
 * @author wangbaoming
 *
 */
public class SpecificBoilCmd extends KettleCmd {
	protected SpecificBoilCmdBody mCmdBody;
	
	public SpecificBoilCmd() {		
		mCmdBody = new SpecificBoilCmdBody();
	}
	
	@Override
	public MsgBodyData getBody() {
		return mCmdBody;
	}

	@Override
	public byte getCmdNo() {
		return CMD_SPECIFIC_BOIL;
	}
	
	/**
	 * boil water, or stop heating.
	 * @param boil
	 * 		if true, start boiling the water, stop heating otherwise.
	 */
	public void boil(boolean boil){
		mCmdBody.boil(boil);
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
	
	private final static class SpecificBoilCmdBody extends MsgBodyData{

		protected SpecificBoilCmdBody() {
			super(new byte[]{(byte) 0x0, (byte) 0xFF, (byte) 0xFF, (byte) 0x0, (byte) 0x0}); // init all bits
		}
		
		public void boil(boolean boil){
			setByte(0, (byte) (boil ? 1 : 0));
		}
		
		public void setTempCToBeKept(int centigrade){
			setByte(1, (byte) centigrade);
		}
		
		public void setMinutesToKeepTempC(int mins){
			setByte(2, (byte) mins);
		}
		
		public void syncMenu(int level1, int level2){
			setByte(3, (byte) level1);
			setByte(4, (byte) level2);
		}
	}
}
